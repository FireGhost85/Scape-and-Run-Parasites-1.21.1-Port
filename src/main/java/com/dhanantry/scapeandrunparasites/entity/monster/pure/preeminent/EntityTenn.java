package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityTenn
extends EntityPPreeminent
implements EntityCanFly {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityTenn.class, EntityDataSerializers.BYTE);

    public EntityTenn(EntityType<? extends EntityTenn> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.folow);
        this.noCulling = true;
        this.borderOrb = -1;
        this.totalP = SRPConfigMobs.alafhaTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.alafhaLimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.alafhaLimit];
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        if (SRPConfigMobs.alafhaMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.alafhaMaxY, true));
        }
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 90;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, 64.0));
        this.goalSelector.addGoal(6, new AIMoveRandom());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPreeminent.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.ALAFHA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.ALAFHA_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.ALAFHA_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.pureFollow);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.ALAFHA_MELLE);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.onGround()) {
                this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
            }
            if (this.srpTicks == 10 && this.getRandom().nextInt(10) == 0) {
                if (!SRPConfigWorld.coloniesActivated) {
                    return;
                }
                AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(16.0);
                List moblist = this.level().getEntitiesOfClass(EntityKol.class, axisalignedbb);
                if (moblist.size() > 3) {
                    return;
                }
                SRPWorldData data = SRPWorldData.get(this.level());
                BlockPos origin = data.nearestColonyPosition(this.blockPosition(), false);
                if (origin != null) {
                    EntityKol aaa = new EntityKol(SRPEntities.WORKER.get(), this.level());
                    aaa.copyPosition((Entity)this);
                    aaa.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false));
                    this.level().addFreshEntity((Entity)aaa);
                }
            }
        }
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.6f;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.ALAFHA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ALAFHA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ALAFHA_DEATH.get();
    }

    protected float getSoundVolume() {
        return 2.0f;
    }

    @Override
    public boolean getCanSpawnHere() {
        BlockState iblockstate = this.level().getBlockState(this.blockPosition().below());
        return true && this.level().getDifficulty() != Difficulty.PEACEFUL && this.isValidLightLevelTwo() && SRPConfig.spawnDays <= (int)this.level().getGameTime();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
    }

    @Override
    public boolean onlySpawnInside() {
        return true;
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

    class AIMoveRandom
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            double d2;
            double d1;
            MoveControl entitymovehelper = EntityTenn.this.getMoveControl();
            if (!entitymovehelper.hasWanted()) {
                return true;
            }
            double d0 = entitymovehelper.getWantedX() - EntityTenn.this.getX();
            double d3 = d0 * d0 + (d1 = entitymovehelper.getWantedY() - EntityTenn.this.getY()) * d1 + (d2 = entitymovehelper.getWantedZ() - EntityTenn.this.getZ()) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            if (EntityTenn.this.getTarget() != null) {
                BlockPos blockpos = EntityTenn.this.blockPosition();
                int flag = 1;
                double speed = 0.11;
                if (EntityTenn.this.distanceToSqr((Entity)EntityTenn.this.getTarget()) > 400.0) {
                    blockpos = EntityTenn.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.11;
                } else if (EntityTenn.this.distanceToSqr((Entity)EntityTenn.this.getTarget()) < 100.0) {
                    blockpos = EntityTenn.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.11;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.offset(EntityTenn.this.getRandom().nextInt(15) - 7, EntityTenn.this.getRandom().nextInt(9) - 5, EntityTenn.this.getRandom().nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.offset(EntityTenn.this.getRandom().nextInt(6) - 2, EntityTenn.this.getRandom().nextInt(7) - 2, EntityTenn.this.getRandom().nextInt(6) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.offset(EntityTenn.this.getRandom().nextInt(4) + 3, EntityTenn.this.getRandom().nextInt(5) + 4, EntityTenn.this.getRandom().nextInt(4) + 3);
                    }
                    if (!EntityTenn.this.level().isEmptyBlock(blockpos1)) continue;
                    EntityTenn.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityTenn.this.getTarget() == null) {
                        EntityTenn.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                RandomSource random = EntityTenn.this.getRandom();
                double d0 = EntityTenn.this.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityTenn.this.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityTenn.this.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityTenn.this.getMoveControl().setWantedPosition(d0, d1, d2, 0.5);
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityTenn vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityTenn.this.getX();
                double d1 = this.getWantedY() - EntityTenn.this.getY();
                double d2 = this.getWantedZ() - EntityTenn.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityTenn.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityTenn.this, 0.5);
                    Mot.mulY(EntityTenn.this, 0.5);
                    Mot.mulZ(EntityTenn.this, 0.5);
                } else {
                    Mot.addX(EntityTenn.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityTenn.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityTenn.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityTenn.this.getTarget() == null) {
                        EntityTenn.this.setYRot(-((float)Mth.atan2((double)EntityTenn.this.getDeltaMovement().x, (double)EntityTenn.this.getDeltaMovement().z)) * 57.295776f);
        EntityTenn.this.yBodyRot = EntityTenn.this.getYRot();
                    } else {
                        double d4 = EntityTenn.this.getTarget().getX() - EntityTenn.this.getX();
                        double d5 = EntityTenn.this.getTarget().getZ() - EntityTenn.this.getZ();
                        EntityTenn.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityTenn.this.yBodyRot = EntityTenn.this.getYRot();
                    }
                }
            }
        }
    }
}

