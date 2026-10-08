package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSwim;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityInfSquid
extends EntityPInfected
implements EntityCanSwim,
EntityCutomAttack {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityInfSquid.class, EntityDataSerializers.BYTE);

    public EntityInfSquid(EntityType<? extends EntityInfSquid> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.canModRender = 0;
        this.type = (byte)15;
        this.goalSelector.removeGoal(this.folow);
        this.goalSelector.removeGoal(this.aiWander);
    }

    @Override
    public int getParasiteIDRegister() {
        return 307;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infsquidCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeNotGround(this, 3.0, SRPConfig.primitiveFollow, 0.05, false, 0, 1));
        this.goalSelector.addGoal(6, new AIMoveRandom());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFSQUID_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INFSQUID_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INFSQUID_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFSQUID_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.infectedFollow);
        return builder;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WaterBoundPathNavigation((Mob)this, worldIn);
    }

    @Override
    public void aiStep() {
        if (this.isNoAi()) {
            return;
        }
        this.liquidLeap = 0;
        super.aiStep();
        this.setNoGravity(this.isInWater());
        this.liquidLeap = 0;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.getBbHeight() * 0.8f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDSQUID_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDSQUID_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDSQUID_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15f, 1.0f);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.doHurtTarget(entityIn);
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.level().getDifficulty() != Difficulty.PEACEFUL && SRPConfig.spawnDays <= (int)this.level().getGameTime();
    }

    public boolean checkSpawnObstruction() {
        return this.level().isUnobstructed((Entity)this);
    }

    public int getAmbientSoundInterval() {
        return 0;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return true;
    }

    protected int getExperiencePoints(Player player) {
        return 1 + this.level().random.nextInt(3);
    }

    public void baseTick() {
        int i = this.getAirSupply();
        super.baseTick();
        if (this.isNoAi()) {
            return;
        }
        if (this.isAlive() && !this.isInWater()) {
            this.setAirSupply(--i);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0f);
            }
        } else {
            this.setAirSupply(300);
        }
    }

    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
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

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return EntityInfSquid.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityInfSquid.this.blockPosition();
            int flag = 1;
            double speed = 0.13;
            if (EntityInfSquid.this.getTarget() != null) {
                if (EntityInfSquid.this.distanceToSqr((Entity)EntityInfSquid.this.getTarget()) > 100.0) {
                    blockpos = EntityInfSquid.this.getTarget().blockPosition();
                    flag = 2;
                } else if (EntityInfSquid.this.distanceToSqr((Entity)EntityInfSquid.this.getTarget()) < 36.0) {
                    blockpos = EntityInfSquid.this.getTarget().blockPosition();
                    flag = 3;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityInfSquid.this.getRandom().nextInt(15) - 7, EntityInfSquid.this.getRandom().nextInt(11) - 5, EntityInfSquid.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityInfSquid.this.getRandom().nextInt(6) - 2, EntityInfSquid.this.getRandom().nextInt(7) - 2, EntityInfSquid.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityInfSquid.this.getRandom().nextInt(4) + 3, EntityInfSquid.this.getRandom().nextInt(5) + 4, EntityInfSquid.this.getRandom().nextInt(4) + 3);
                }
                if (EntityInfSquid.this.level().getBlockState(blockpos1).getFluidState().is(FluidTags.WATER) == false) continue;
                EntityInfSquid.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityInfSquid.this.getTarget() != null) break;
                EntityInfSquid.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityInfSquid vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityInfSquid.this.getX();
                double d1 = this.getWantedY() - EntityInfSquid.this.getY();
                double d2 = this.getWantedZ() - EntityInfSquid.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityInfSquid.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityInfSquid.this, 0.5);
                    Mot.mulY(EntityInfSquid.this, 0.5);
                    Mot.mulZ(EntityInfSquid.this, 0.5);
                } else {
                    Mot.addX(EntityInfSquid.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityInfSquid.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityInfSquid.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityInfSquid.this.getTarget() == null) {
                        EntityInfSquid.this.setYRot(-((float)Mth.atan2((double)EntityInfSquid.this.getDeltaMovement().x, (double)EntityInfSquid.this.getDeltaMovement().z)) * 57.295776f);
        EntityInfSquid.this.yBodyRot = EntityInfSquid.this.getYRot();
                    } else {
                        double d4 = EntityInfSquid.this.getTarget().getX() - EntityInfSquid.this.getX();
                        double d5 = EntityInfSquid.this.getTarget().getZ() - EntityInfSquid.this.getZ();
                        EntityInfSquid.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityInfSquid.this.yBodyRot = EntityInfSquid.this.getYRot();
                    }
                }
            }
        }
    }
}

