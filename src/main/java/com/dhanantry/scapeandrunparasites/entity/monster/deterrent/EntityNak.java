package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntitySRPProjectile;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityNak
extends EntityPStationary {
    private float attackTimer;
    private boolean up2;
    private EntityPDispatcher father;
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityNak.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int tak;

    public EntityNak(EntityType<? extends EntityNak> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 0;
        this.type = (byte)40;
        this.buriedT = 4.0;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
        this.valueEvDeath = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 72;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ENTITY, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPStationary.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.NAK_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.NAK_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.NAK_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 6.0);
        return builder;
    }

    @Override
    public void aiStep() {
        double dis;
        super.aiStep();
        if (this.isNoAi()) {
            return;
        }
        if (this.up2) {
            this.attackTimer += 0.15f;
            if (this.attackTimer > 1.0f) {
                this.up = false;
            }
        } else {
            this.attackTimer -= 0.2f;
        }
        if (this.up || this.buried()) {
            return;
        }
        if (!this.level().isClientSide) {
            if (this.getTarget() != null) {
                if (this.getTarget() instanceof Player) {
                    Player pa = (Player)this.getTarget();
                    if (pa.getAbilities().instabuild || pa.getAbilities().invulnerable) {
                        this.setTarget(null);
                        this.setTargetedEntity(0);
                        this.setParasiteStatus(0);
                        return;
                    }
                }
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                    this.setTargetedEntity(0);
                    this.setParasiteStatus(0);
                } else {
                    this.tak = 0;
                    if (this.hasLineOfSight((Entity)this.getTarget()) && this.distanceToSqr((Entity)this.getTarget()) < 25.0) {
                        this.setParasiteStatus(3);
                        this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 2, false, false));
                        this.setTargetedEntity(this.getTarget().getId());
                        this.getNavigation().moveTo((Entity)this.getTarget(), 0.0);
                        this.lookAt((Entity)this.getTarget());
                        if (this.srpTicks == 10 && this.getTarget() instanceof ServerPlayer) {
                            PacketDistributor.sendToPlayer((ServerPlayer)this.getTarget(), new QlipShakePayload(250, 0, true, false, 4.0f));
                        }
                    } else {
                        this.setParasiteStatus(2);
                        this.setTargetedEntity(0);
                    }
                }
            } else {
                ++this.tak;
                if (this.tak >= 60) {
                    this.level().broadcastEntityEvent((Entity)this, (byte)51);
                    this.up = true;
                }
                this.setParasiteStatus(0);
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && (dis = this.distanceToSqr((Entity)this.getTargetedEntity())) < 25.0 && dis > 1.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.5;
            Mot.addX(target, (Math.signum(this.getX() - target.getX()) * str - target.getDeltaMovement().x) * str);
            Mot.addZ(target, (Math.signum(this.getZ() - target.getZ()) * str - target.getDeltaMovement().z) * str);
            if (!this.level().isClientSide) {
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 2, false, false));
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (source.getDirectEntity() instanceof EntitySRPProjectile) {
            LivingEntity target = this.getTargetedEntity();
            if (target != null) {
                target.hurt(source, amount * 2.0f);
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Item wea = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if (wea != SRPItems.itembase.get()) {
            // empty if block
        }
        if (this.father != null) {
            this.father.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 1, false, false));
        }
        return super.mobInteract(player, hand);
    }

    private void setTargetedEntity(int entityId) {
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        return (Integer)this.entityData.get(TARGET_ENTITY) != 0;
    }

    public EntityPDispatcher getFather() {
        return this.father;
    }

    public void setFather(EntityPDispatcher in) {
        this.father = in;
    }

    @Nullable
    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.level().isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(TARGET_ENTITY)).intValue());
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getTarget();
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.8f;
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else {
            super.handleEntityEvent(id);
        }
    }
}

