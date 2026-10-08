package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityRanrac
extends EntityPPrimitive
implements EntityCanPullMobs {
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityRanrac.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int pulling;
    private boolean canPull;
    private EntityHitbox head;
    private EntityHitbox abdomen;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityRanrac.class, EntityDataSerializers.BYTE);
    private int border;
    private boolean skillpulling;

    public EntityRanrac(EntityType<? extends EntityRanrac> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.canPull = true;
        this.head = new EntityHitbox((Mob)this, 1.6f, 0.8f, 1.0f, 0.7f, 0.6f, 1.25f);
        this.abdomen = new EntityHitbox((Mob)this, -1.6f, 0.8f, 1.3f, 0.9f, 1.5f, 0.5f);
        this.hitboxes = new EntityHitbox[]{this.head, this.abdomen};
    }

    @Override
    public int getParasiteIDRegister() {
        return 38;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.095));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, 300, 7, true, 1));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0f, true));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 2, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPrimitive.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.RANRAC_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.RANRAC_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.33);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.RANRAC_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.RANRAC_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ENTITY, 0);
        builder.define(CLIMBING, (byte) (0));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityRanracAdapted(SRPEntities.ADA_ARACHNIDA.get(), this.level()), true, true);
        }
        if (!this.level().isClientSide) {
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getTarget() != null) {
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.hasLineOfSight((Entity)this.getTarget()) && this.distanceToSqr((Entity)this.getTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3, false, false));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 3, false, false));
                    this.getLookControl().setLookAt((Entity)this.getTargetedEntity(), 30.0f, 30.0f);
                    if (this.srpTicks == 5 || this.srpTicks == 15) {
                        this.lookAt((Entity)this.getTargetedEntity());
                    }
                    this.setParasiteStatus(3);
                    if (this.srpTicks == 10 && this.getTarget() instanceof ServerPlayer) {
                        PacketDistributor.sendToPlayer((ServerPlayer)this.getTarget(), new QlipShakePayload(250, 0, true, false, 4.0f));
                    }
                    ++this.pulling;
                    if (this.pulling > 200) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && this.distanceToSqr((Entity)this.getTargetedEntity()) > 0.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.4;
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            str = 0.15;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(target, (deltaX /= distance) * str);
            Mot.addY(target, (deltaY /= distance) * str);
            Mot.addZ(target, (deltaZ /= distance) * str);
        }
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getTarget() != null) {
                    if (!this.getTarget().isAlive()) {
                        this.setTarget(null);
                        this.setParasiteStatus(0);
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (this.level().isClientSide) {
            return;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
        }
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(CLIMBING);
        if (this.getTarget() != null) {
            if (!this.hasLineOfSight((Entity)this.getTarget())) {
                if (this.distanceToSqr((Entity)this.getTarget()) < 100.0) {
                    b0 = (byte)(b0 & 0xFFFFFFFE);
                    this.entityData.set(CLIMBING, (byte) (b0));
                    return;
                }
            } else if (this.getTarget().getY() + 1.0 < this.getY()) {
                b0 = (byte)(b0 & 0xFFFFFFFE);
                this.entityData.set(CLIMBING, (byte) (b0));
                return;
            }
        }
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.entityData.set(CLIMBING, (byte) (b0));
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new PathNavigateClimberStatus((Mob)this, worldIn);
    }

    @Override
    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    @Override
    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)this.entityData.get(TARGET_ENTITY) != 0;
    }

    @Override
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

    @Override
    public void setPStatus(int in) {
        this.setParasiteStatus(in);
    }

    @Override
    public void setPullingMobEffects(LivingEntity mob) {
        mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3, false, false));
    }

    @Override
    public int getAcceleration() {
        return 2;
    }

    @Override
    public boolean checkAttackTarget(LivingEntity check) {
        return this.getTarget() == check;
    }

    public void push(Entity entityIn) {
        if (this.getTargetedEntity() == entityIn) {
            return;
        }
        super.push(entityIn);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
                    break;
                }
                case 6: {
                    SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 40, 0);
                }
            }
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.5f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.level().isClientSide && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityRanracAdapted(SRPEntities.ADA_ARACHNIDA.get(), this.level()), true, true);
        }
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.arachnidaOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(3)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                    break;
                }
                case 2: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillpulling;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillpulling = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.pullingE();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void pullingE() {
        if (!this.onGround() || !this.canPull) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (this.hasTargetedEntity()) {
            this.skillpulling = true;
            this.border = 0;
            return;
        }
        this.setParasiteStatus(11);
        this.getNavigation().stop();
        if (this.border == 0) {
            // empty if block
        }
        if (this.border <= 2) {
            // empty if block
        }
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.getTarget() == null) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (!this.hasLineOfSight((Entity)this.getTarget())) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        this.lookAt((Entity)this.getTarget());
        LivingEntity entitylivingbase = this.getTarget();
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = entitylivingbase.getX() - (this.getX() + vec3d.x);
        double d3 = entitylivingbase.getBoundingBox().maxY + (double)(entitylivingbase.getBbHeight() + 0.8f) - (0.5 + this.getY() + (double)(this.getBbHeight() / 1.0f));
        double d4 = entitylivingbase.getZ() - (this.getZ() + vec3d.z);
        EntityProjectilePullball entitylargefireball = new EntityProjectilePullball(SRPEntities.PULLINGBALL.get(), this.level(), this, d2, d3, d4);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
        Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
        this.level().addFreshEntity((Entity)entitylargefireball);
        if (this.border > 5) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.pulling = 60;
        }
    }

    @Override
    public void resetPullSkill() {
        this.skillpulling = true;
        this.border = 0;
    }
}

