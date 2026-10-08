package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
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
import net.minecraft.world.phys.Vec3;

public class EntitySoo
extends EntityPPure
implements EntityCanFly {
    private int scentCool;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntitySoo.class, EntityDataSerializers.BYTE);

    public EntitySoo(EntityType<? extends EntitySoo> type, Level worldIn) {
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
        this.scentCool = 800;
    }

    @Override
    public int getParasiteIDRegister() {
        return 82;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, 64.0));
        this.goalSelector.addGoal(6, new AIMoveRandom());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPure.createAttributes();
        
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
        if (this.onGround() && !this.level().isClientSide) {
            this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
        }
        --this.scentCool;
        if (this.scentCool < 0 && this.srpTicks == 10 && SRPConfigSystems.useScent && this.getLevelCreated() >= SRPConfigSystems.deveScentUse) {
            List serverList = SRPEntityUtil.allEntities(this.level());
            int count = 0;
            for (int x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityParasiticScent)) continue;
                ++count;
            }
            if (count > SRPConfigSystems.scentCap) {
                return;
            }
            if (this.getTarget() != null) {
                EntityParasiticScent sss = new EntityParasiticScent(SRPEntities.SCENT.get(), this.level());
                sss.copyPosition((Entity)this.getTarget());
                sss.setTargetToKill(this.getTarget(), false);
                sss.setDieToE(true);
                sss.setCanFollow(true);
                this.level().addFreshEntity((Entity)sss);
                this.noActionTime = 0;
                this.scentCool = 800;
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
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
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            double d2;
            double d1;
            MoveControl entitymovehelper = EntitySoo.this.getMoveControl();
            if (!entitymovehelper.hasWanted()) {
                return true;
            }
            double d0 = entitymovehelper.getWantedX() - EntitySoo.this.getX();
            double d3 = d0 * d0 + (d1 = entitymovehelper.getWantedY() - EntitySoo.this.getY()) * d1 + (d2 = entitymovehelper.getWantedZ() - EntitySoo.this.getZ()) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            if (EntitySoo.this.getTarget() != null) {
                BlockPos blockpos = EntitySoo.this.blockPosition();
                int flag = 1;
                double speed = 0.11;
                if (EntitySoo.this.distanceToSqr((Entity)EntitySoo.this.getTarget()) > 400.0) {
                    blockpos = EntitySoo.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.11;
                } else if (EntitySoo.this.distanceToSqr((Entity)EntitySoo.this.getTarget()) < 100.0) {
                    blockpos = EntitySoo.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.11;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.offset(EntitySoo.this.getRandom().nextInt(15) - 7, EntitySoo.this.getRandom().nextInt(9) - 5, EntitySoo.this.getRandom().nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.offset(EntitySoo.this.getRandom().nextInt(6) - 2, EntitySoo.this.getRandom().nextInt(7) - 2, EntitySoo.this.getRandom().nextInt(6) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.offset(EntitySoo.this.getRandom().nextInt(4) + 3, EntitySoo.this.getRandom().nextInt(5) + 4, EntitySoo.this.getRandom().nextInt(4) + 3);
                    }
                    if (!EntitySoo.this.level().isEmptyBlock(blockpos1)) continue;
                    EntitySoo.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntitySoo.this.getTarget() == null) {
                        EntitySoo.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                RandomSource random = EntitySoo.this.getRandom();
                double d0 = EntitySoo.this.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntitySoo.this.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntitySoo.this.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntitySoo.this.getMoveControl().setWantedPosition(d0, d1, d2, 0.5);
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntitySoo vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntitySoo.this.getX();
                double d1 = this.getWantedY() - EntitySoo.this.getY();
                double d2 = this.getWantedZ() - EntitySoo.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntitySoo.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntitySoo.this, 0.5);
                    Mot.mulY(EntitySoo.this, 0.5);
                    Mot.mulZ(EntitySoo.this, 0.5);
                } else {
                    Mot.addX(EntitySoo.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntitySoo.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntitySoo.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntitySoo.this.getTarget() == null) {
                        EntitySoo.this.setYRot(-((float)Mth.atan2((double)EntitySoo.this.getDeltaMovement().x, (double)EntitySoo.this.getDeltaMovement().z)) * 57.295776f);
        EntitySoo.this.yBodyRot = EntitySoo.this.getYRot();
                    } else {
                        double d4 = EntitySoo.this.getTarget().getX() - EntitySoo.this.getX();
                        double d5 = EntitySoo.this.getTarget().getZ() - EntitySoo.this.getZ();
                        EntitySoo.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntitySoo.this.yBodyRot = EntitySoo.this.getYRot();
                    }
                }
            }
        }
    }
}

