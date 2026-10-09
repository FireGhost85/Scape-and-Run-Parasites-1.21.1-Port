package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityViin
extends EntityParasiteBase
implements EntityCanFly {
    private int lifespan = 0;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityViin.class, EntityDataSerializers.BYTE);

    public EntityViin(EntityType<? extends EntityViin> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.type = (byte)5;
        this.lifespan = 0;
        this.goalSelector.removeGoal(this.folow);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.attackSpeedT = 6;
    }

    @Override
    public int getParasiteIDRegister() {
        return 334;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.adaptedFollow));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        ++this.lifespan;
        if (this.lifespan > 1200) {
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 11));
            this.discard();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VIIN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VIIN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.34559);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VIIN_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.VIIN_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (entityIn == this.getTarget()) {
            LivingEntity target = (LivingEntity)entityIn;
            if (target.getHealth() <= target.getMaxHealth() * SRPConfigSystems.hijackHealth) {
                if (ParasiteEventEntity.convertEntityFeral((LivingEntity)entityIn, entityIn.getPersistentData(), true, SRPConfigSystems.COTHVictimParasite) || ParasiteEventEntity.hijackEntity((LivingEntity)entityIn, SRPConfigSystems.HIJACKVictimParasite)) {
                    com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 11));
                    com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 11));
                } else {
                    entityIn.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
                }
            } else {
                entityIn.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            }
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 120, 2);
            this.playSound(SRPSounds.BUTHOL_BOOM.get(), 0.4f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
            this.discard();
        }
    }

    @Override
    protected void tickDeath() {
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
        this.discard();
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public double getMountedYOffset() {
        return this.getBbHeight() * 0.5f;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8f;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        return false;
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        this.checkInsideBlocks();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VEX_FLAGS, (byte) (0));
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)this.entityData.get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        this.entityData.set(VEX_FLAGS, (byte) (((byte)(i & 0xFF))));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    class AIChargeAttack
    extends Goal {
        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityViin.this.getTarget() != null && !EntityViin.this.getMoveControl().hasWanted() && EntityViin.this.getRandom().nextInt(7) == 0) {
                return EntityViin.this.distanceToSqr((Entity)EntityViin.this.getTarget()) > 1.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityViin.this.getMoveControl().hasWanted() && EntityViin.this.isCharging() && EntityViin.this.getTarget() != null && EntityViin.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityViin.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityViin.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 2.0);
            EntityViin.this.setCharging(true);
        }

        public void stop() {
            EntityViin.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityViin.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityViin.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityViin.this.doHurtTarget((Entity)entitylivingbase);
                    EntityViin.this.setCharging(false);
                } else {
                    double d0 = EntityViin.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityViin.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 2.0);
                    }
                }
            }
        }
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityViin.this.getMoveControl().hasWanted() && EntityViin.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityViin.this.blockPosition();
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityViin.this.getRandom().nextInt(15) - 7, EntityViin.this.getRandom().nextInt(11) - 5, EntityViin.this.getRandom().nextInt(15) - 7);
                if (!EntityViin.this.level().isEmptyBlock(blockpos1)) continue;
                EntityViin.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (EntityViin.this.getTarget() != null) break;
                EntityViin.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityViin vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityViin.this.getX();
                double d1 = this.getWantedY() - EntityViin.this.getY();
                double d2 = this.getWantedZ() - EntityViin.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityViin.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityViin.this, 0.5);
                    Mot.mulY(EntityViin.this, 0.5);
                    Mot.mulZ(EntityViin.this, 0.5);
                } else {
                    Mot.addX(EntityViin.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityViin.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityViin.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityViin.this.getTarget() == null) {
                        EntityViin.this.setYRot(-((float)Mth.atan2((double)EntityViin.this.getDeltaMovement().x, (double)EntityViin.this.getDeltaMovement().z)) * 57.295776f);
        EntityViin.this.yBodyRot = EntityViin.this.getYRot();
                    } else {
                        double d4 = EntityViin.this.getTarget().getX() - EntityViin.this.getX();
                        double d5 = EntityViin.this.getTarget().getZ() - EntityViin.this.getZ();
                        EntityViin.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityViin.this.yBodyRot = EntityViin.this.getYRot();
                    }
                }
            }
        }
    }
}

