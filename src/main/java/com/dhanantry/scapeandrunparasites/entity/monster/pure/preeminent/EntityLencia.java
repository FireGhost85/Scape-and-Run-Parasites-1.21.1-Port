package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileLenciaBall;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EntityLencia
extends EntityPPreeminent
implements EntityCanShoot,
EntityCanColony,
EntityCanFly {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityLencia.class, EntityDataSerializers.BYTE);
    private int timer = 0;
    private boolean camu;

    public EntityLencia(EntityType<? extends EntityLencia> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.aiWander);
        this.adaptationCap = 0.95f;
        this.noCulling = true;
    }

    @Override
    public int getParasiteIDRegister() {
        return 86;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.preeminentFollow, true, 3));
        this.goalSelector.addGoal(5, new EntityAIAttackProjectile(this, 60, 30, 3, true));
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, 10, false));
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, 30, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPreeminent.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.LENCIA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.LENCIA_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.preeminentFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.onGround()) {
                this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
            }
            if (this.srpTicks == 10) {
                if (this.getTarget() != null) {
                    for (LivingEntity entitylivingbase : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(3.0, 3.0, 3.0))) {
                        if (entitylivingbase == this || entitylivingbase instanceof EntityParasiteBase) continue;
                        float f = (float)Mth.atan2((double)(entitylivingbase.getZ() - this.getZ()), (double)(entitylivingbase.getX() - this.getX()));
                        EntityDamage damage = new EntityDamage(this.level(), entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ(), f, (LivingEntity)this, 1.0f, false, 2.5f);
                        this.level().addFreshEntity((Entity)damage);
                    }
                }
                if ((this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getTarget() != null) {
                    Mot.setY(this, 0.5);
                }
                float currentH = this.getHealth() / this.getMaxHealth();
                if (this.getSSS()) {
                    this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 25, 1, false, false));
                    if ((double)currentH < SRPConfigMobs.lencianeededhealth) {
                        this.setSSS(false);
                    }
                } else if ((double)currentH >= SRPConfigMobs.lencianeededhealth) {
                    ++this.timer;
                    if (this.timer > 2) {
                        this.setSSS(true);
                        this.particleStatus((byte)6);
                        this.timer = 0;
                    }
                }
            }
        }
    }

    public boolean getSSS() {
        return this.camu;
    }

    public void setSSS(boolean in) {
        this.camu = in;
    }

    @Override
    protected boolean summonFlam(LivingEntity in) {
        boolean flag = super.summonFlam(in);
        return flag;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.setSSS(false);
        this.timer = 0;
        return super.hurt(source, amount);
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public int getHorizontalFaceSpeed() {
        return 3;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.1f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.LENCIA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.LENCIA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.LENCIA_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.lenciaOrbEffects, mobs);
        }
        return flag;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean onlySpawnInside() {
        return false;
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

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        this.setSSS(false);
        this.timer = 0;
        return new EntityProjectileLenciaBall(SRPEntities.BALLMALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ);
    }

    @Override
    public void playProjSound() {
        this.setSSS(false);
        this.timer = 0;
    }

    class AIBomb
    extends Goal {
        private final Mob parent;
        private int ccc;

        public AIBomb(Mob parentIn) {
            this.parent = parentIn;
            this.ccc = 0;
        }

        public boolean canUse() {
            ++this.ccc;
            if (this.ccc >= 100) {
                LivingEntity target;
                this.ccc = 0;
                if (this.parent.getTarget() != null && (target = this.parent.getTarget()).distanceToSqr(this.parent.getX(), target.getY(), this.parent.getZ()) < 256.0) {
                    return true;
                }
            }
            return false;
        }

        public void tick() {
        }
    }

    class AIChargeAttack
    extends Goal {
        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityLencia.this.getTarget() != null && EntityLencia.this.getRandom().nextInt(5) == 0) {
                return EntityLencia.this.distanceToSqr((Entity)EntityLencia.this.getTarget()) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityLencia.this.getMoveControl().hasWanted() && EntityLencia.this.isCharging() && EntityLencia.this.getTarget() != null && EntityLencia.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityLencia.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityLencia.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 0.7);
            EntityLencia.this.setCharging(true);
        }

        public void stop() {
            EntityLencia.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityLencia.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityLencia.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityLencia.this.doHurtTarget((Entity)entitylivingbase);
                    EntityLencia.this.setCharging(false);
                } else {
                    double d0 = EntityLencia.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        if (EntityLencia.this.hasLineOfSight((Entity)entitylivingbase)) {
                            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                            EntityLencia.this.moveControl.setWantedPosition(vec3d.x, vec3d.y + 20.0, vec3d.z, 0.7);
                        } else {
                            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                            EntityLencia.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.1);
                        }
                    } else {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityLencia.this.moveControl.setWantedPosition(vec3d.x, entitylivingbase.getY() + 20.0, vec3d.z, 1.1);
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
            return !EntityLencia.this.getMoveControl().hasWanted() && EntityLencia.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityLencia.this.blockPosition();
            int flag = 1;
            double speed = 0.6;
            if (EntityLencia.this.getTarget() != null) {
                if (EntityLencia.this.distanceToSqr((Entity)EntityLencia.this.getTarget()) > 100.0) {
                    blockpos = EntityLencia.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.1;
                } else if (EntityLencia.this.distanceToSqr((Entity)EntityLencia.this.getTarget()) < 36.0) {
                    blockpos = EntityLencia.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.15;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityLencia.this.getRandom().nextInt(15) - 7, EntityLencia.this.getRandom().nextInt(11) - 5, EntityLencia.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityLencia.this.getRandom().nextInt(6) - 2, EntityLencia.this.getRandom().nextInt(7) - 2, EntityLencia.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityLencia.this.getRandom().nextInt(4) + 3, EntityLencia.this.getRandom().nextInt(5) + 4, EntityLencia.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityLencia.this.level().isEmptyBlock(blockpos1)) continue;
                EntityLencia.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityLencia.this.getTarget() != null) break;
                EntityLencia.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityLencia vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityLencia.this.getX();
                double d1 = this.getWantedY() - EntityLencia.this.getY();
                double d2 = this.getWantedZ() - EntityLencia.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityLencia.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityLencia.this, 0.5);
                    Mot.mulY(EntityLencia.this, 0.5);
                    Mot.mulZ(EntityLencia.this, 0.5);
                } else {
                    Mot.addX(EntityLencia.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityLencia.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityLencia.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityLencia.this.getTarget() == null) {
                        EntityLencia.this.setYRot(-((float)Mth.atan2((double)EntityLencia.this.getDeltaMovement().x, (double)EntityLencia.this.getDeltaMovement().z)) * 57.295776f);
        EntityLencia.this.yBodyRot = EntityLencia.this.getYRot();
                    } else {
                        double d4 = EntityLencia.this.getTarget().getX() - EntityLencia.this.getX();
                        double d5 = EntityLencia.this.getTarget().getZ() - EntityLencia.this.getZ();
                        EntityLencia.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityLencia.this.yBodyRot = EntityLencia.this.getYRot();
                    }
                }
            }
        }
    }
}

