package com.dhanantry.scapeandrunparasites.entity.monster.hijacked;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPHijacked;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityHiBlaze
extends EntityPHijacked
implements EntityCanShoot,
EntityCanFly {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityHiBlaze.class, EntityDataSerializers.BYTE);
    private int count;

    public EntityHiBlaze(EntityType<? extends EntityHiBlaze> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.type = (byte)11;
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.folow);
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 302;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infbearCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.primitiveFollow));
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(1, new EntityAIAttackProjectile(this, 80, 20, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPHijacked.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.EMANA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.EMANA_ARMOR);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.EMANA_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.EMANA_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.srpTicks == 10) {
            if (this.onGround() && !this.level().isClientSide) {
                this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
            }
            if ((this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getTarget() != null) {
                Mot.setY(this, 0.1);
            }
            List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, new AABB(this.blockPosition()).inflate(7.0, 3.0, 7.0));
            for (EntityParasiteBase mob : moblist) {
                mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, true));
            }
        }
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.6f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.HI_BLAZE_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HI_BLAZE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.HI_BLAZE_DEATH.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
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

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(ParticleTypes.FLAME);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        float pit = 1.0f;
        if (this.count == 4) {
            pit = 2.0f;
        }
        EntityProjectileSpineball ball = new EntityProjectileSpineball(SRPEntities.SPINEBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, SRPAttributes.EMANA_RANGED_DAMAGE);
        ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
        ball.setGearDamage(SRPConfigMobs.emanaGearD);
        return ball;
    }

    @Override
    public void playProjSound() {
        ++this.count;
        if (this.count == 4) {
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            float v = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4f + 2.0f;
            this.playSound(this.getHurtSound(this.damageSources().generic()), 4.0f, v);
            return;
        }
        this.playSound(SRPSounds.HI_BLAZE_FIRE.get(), 2.0f, 1.0f);
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityHiBlaze.this.getMoveControl().hasWanted() && EntityHiBlaze.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityHiBlaze.this.blockPosition();
            int flag = 1;
            double speed = 0.2;
            if (EntityHiBlaze.this.getTarget() != null) {
                if (EntityHiBlaze.this.distanceToSqr((Entity)EntityHiBlaze.this.getTarget()) > 100.0) {
                    blockpos = EntityHiBlaze.this.getTarget().blockPosition();
                    flag = 2;
                } else if (EntityHiBlaze.this.distanceToSqr((Entity)EntityHiBlaze.this.getTarget()) < 36.0) {
                    blockpos = EntityHiBlaze.this.getTarget().blockPosition();
                    flag = 3;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityHiBlaze.this.getRandom().nextInt(15) - 7, EntityHiBlaze.this.getRandom().nextInt(11) - 5, EntityHiBlaze.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityHiBlaze.this.getRandom().nextInt(6) - 2, EntityHiBlaze.this.getRandom().nextInt(7) - 2, EntityHiBlaze.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityHiBlaze.this.getRandom().nextInt(4) + 3, EntityHiBlaze.this.getRandom().nextInt(5) + 4, EntityHiBlaze.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityHiBlaze.this.level().isEmptyBlock(blockpos1)) continue;
                EntityHiBlaze.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityHiBlaze.this.getTarget() != null) break;
                EntityHiBlaze.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityHiBlaze vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityHiBlaze.this.getX();
                double d1 = this.getWantedY() - EntityHiBlaze.this.getY();
                double d2 = this.getWantedZ() - EntityHiBlaze.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityHiBlaze.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityHiBlaze.this, 0.5);
                    Mot.mulY(EntityHiBlaze.this, 0.5);
                    Mot.mulZ(EntityHiBlaze.this, 0.5);
                } else {
                    Mot.addX(EntityHiBlaze.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityHiBlaze.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityHiBlaze.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityHiBlaze.this.getTarget() == null) {
                        EntityHiBlaze.this.setYRot(-((float)Mth.atan2((double)EntityHiBlaze.this.getDeltaMovement().x, (double)EntityHiBlaze.this.getDeltaMovement().z)) * 57.295776f);
        EntityHiBlaze.this.yBodyRot = EntityHiBlaze.this.getYRot();
                    } else {
                        double d4 = EntityHiBlaze.this.getTarget().getX() - EntityHiBlaze.this.getX();
                        double d5 = EntityHiBlaze.this.getTarget().getZ() - EntityHiBlaze.this.getZ();
                        EntityHiBlaze.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityHiBlaze.this.yBodyRot = EntityHiBlaze.this.getYRot();
                    }
                }
            }
        }
    }
}

