package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
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
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EntityOmboo
extends EntityPPure
implements EntityCanFly {
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityOmboo.class, EntityDataSerializers.BYTE);

    public EntityOmboo(EntityType<? extends EntityOmboo> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.folow);
        this.borderOrb = -1;
        if (SRPConfigMobs.ombooMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.ombooMaxY, true));
        }
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 47;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(5, new AIBomb(this));
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, 7, false));
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, 20, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPure.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.OMBOO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.OMBOO_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.OMBOO_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.pureFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.srpTicks == 10) {
            if (this.onGround()) {
                this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
            }
            if ((this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getTarget() != null) {
                Mot.setY(this, 0.5);
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
        return 2.4f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.OMBOO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.OMBOO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.OMBOO_DEATH.get();
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

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    class AIBomb
    extends Goal {
        private final EntityParasiteBase parent;
        private int ccc;

        public AIBomb(EntityParasiteBase parentIn) {
            this.parent = parentIn;
            this.ccc = 0;
        }

        public boolean canUse() {
            ++this.ccc;
            if (this.ccc >= 15) {
                this.ccc = 0;
                if (this.parent.getTarget() != null) {
                    LivingEntity target = this.parent.getTarget();
                    if (!target.onGround()) {
                        this.ccc = 7;
                        return false;
                    }
                    if (target.distanceToSqr(this.parent.getX(), target.getY(), this.parent.getZ()) < 25.0) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void tick() {
            EntityBomb out = new EntityBomb(SRPEntities.BOMB.get(), this.parent.level(), this.parent, SRPConfigMobs.ombooGriefing);
            out.copyPosition((Entity)this.parent);
            out.setFuse(80);
            out.setStren(1.0f);
            out.setDamage((float)SRPAttributes.OMBOO_BOMBDAMAGE, 4);
            this.parent.level().addFreshEntity((Entity)out);
            out.updateSTR();
        }
    }

    class AIChargeAttack
    extends Goal {
        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityOmboo.this.getTarget() != null && EntityOmboo.this.getRandom().nextInt(7) == 0) {
                return EntityOmboo.this.distanceToSqr((Entity)EntityOmboo.this.getTarget()) > 3.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityOmboo.this.getMoveControl().hasWanted() && EntityOmboo.this.isCharging() && EntityOmboo.this.getTarget() != null && EntityOmboo.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityOmboo.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityOmboo.this.moveControl.setWantedPosition(vec3d.x, vec3d.y + 10.0, vec3d.z, 1.0);
            EntityOmboo.this.setCharging(true);
        }

        public void stop() {
            EntityOmboo.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityOmboo.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityOmboo.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityOmboo.this.doHurtTarget((Entity)entitylivingbase);
                    EntityOmboo.this.setCharging(false);
                } else {
                    double d0 = EntityOmboo.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityOmboo.this.moveControl.setWantedPosition(vec3d.x, vec3d.y + 10.0, vec3d.z, 1.0);
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
            return !EntityOmboo.this.getMoveControl().hasWanted() && EntityOmboo.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityOmboo.this.blockPosition();
            int flag = 1;
            double speed = 0.2;
            if (EntityOmboo.this.getTarget() != null) {
                if (EntityOmboo.this.distanceToSqr((Entity)EntityOmboo.this.getTarget()) > 100.0) {
                    blockpos = EntityOmboo.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.1;
                } else if (EntityOmboo.this.distanceToSqr((Entity)EntityOmboo.this.getTarget()) < 36.0) {
                    blockpos = EntityOmboo.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.1;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityOmboo.this.getRandom().nextInt(15) - 7, EntityOmboo.this.getRandom().nextInt(11) - 5, EntityOmboo.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityOmboo.this.getRandom().nextInt(6) - 2, EntityOmboo.this.getRandom().nextInt(7) - 2, EntityOmboo.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityOmboo.this.getRandom().nextInt(4) + 3, EntityOmboo.this.getRandom().nextInt(5) + 4, EntityOmboo.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityOmboo.this.level().isEmptyBlock(blockpos1)) continue;
                EntityOmboo.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityOmboo.this.getTarget() != null) break;
                EntityOmboo.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 1.0, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityOmboo vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityOmboo.this.getX();
                double d1 = this.getWantedY() - EntityOmboo.this.getY();
                double d2 = this.getWantedZ() - EntityOmboo.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityOmboo.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityOmboo.this, 0.5);
                    Mot.mulY(EntityOmboo.this, 0.5);
                    Mot.mulZ(EntityOmboo.this, 0.5);
                } else {
                    Mot.addX(EntityOmboo.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityOmboo.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityOmboo.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityOmboo.this.getTarget() == null) {
                        EntityOmboo.this.setYRot(-((float)Mth.atan2((double)EntityOmboo.this.getDeltaMovement().x, (double)EntityOmboo.this.getDeltaMovement().z)) * 57.295776f);
        EntityOmboo.this.yBodyRot = EntityOmboo.this.getYRot();
                    } else {
                        double d4 = EntityOmboo.this.getTarget().getX() - EntityOmboo.this.getX();
                        double d5 = EntityOmboo.this.getTarget().getZ() - EntityOmboo.this.getZ();
                        EntityOmboo.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityOmboo.this.yBodyRot = EntityOmboo.this.getYRot();
                    }
                }
            }
        }
    }
}

