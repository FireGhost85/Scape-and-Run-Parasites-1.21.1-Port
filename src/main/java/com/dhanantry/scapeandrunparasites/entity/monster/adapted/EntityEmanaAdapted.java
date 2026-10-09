package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
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
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class EntityEmanaAdapted
extends EntityPAdapted
implements EntityCutomAttack,
EntityCanShoot,
EntityCanFly {
    private final EntityHitbox tendril_1;
    private final EntityHitbox tendril_2;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityEmanaAdapted.class, EntityDataSerializers.BYTE);
    private int count;

    public EntityEmanaAdapted(EntityType<? extends EntityEmanaAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.goalSelector.removeGoal(this.folow);
        this.borderOrb = -1;
        if (SRPConfigMobs.emanaMaxY != 256) {
            this.goalSelector.addGoal(3, new EntityAIFlightLimits(this, SRPConfigMobs.emanaMaxY, true));
        }
        this.adaptationCap = 0.95f;
        this.tendril_1 = new EntityHitbox((Mob)this, 0.2f, 1.1f, 0.1f, 0.9f, 2.6f, 1.25f);
        this.tendril_2 = new EntityHitbox((Mob)this, 3.0f, 1.1f, 0.1f, 0.9f, 2.6f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.tendril_1, this.tendril_2};
    }

    @Override
    public int getParasiteIDRegister() {
        return 55;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.adaptedFollow));
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(1, new EntityAIAttackProjectile(this, 60, 20, 2, true));
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeNotGround(this, 2.5, 16.0, 0.04, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.EMANA_HEALTH + SRPAttributes.EMANA_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.EMANA_ARMOR + SRPAttributes.EMANA_A_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.EMANA_KD_RESISTANCE + SRPAttributes.EMANA_A_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.EMANA_A_MELLE);
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
                return;
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

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.doHurtTarget(entityIn);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.1f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityEmana(SRPEntities.PRI_YELLOWEYE.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.AEMANA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.AEMANA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.AEMANA_DEATH.get();
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

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        float pit = 1.0f;
        if (this.count == 3) {
            pit = 1.5f;
        }
        this.playSound(SRPSounds.EMANA_SHOOTING.get(), 2.0f, pit);
        if (this.count >= 3) {
            this.count = 0;
            return new EntityProjectileNade(SRPEntities.NADEBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, 4, 100);
        }
        EntityProjectileSpineball ball = new EntityProjectileSpineball(SRPEntities.SPINEBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, SRPAttributes.EMANA_A_RANGED_DAMAGE);
        ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration * 2, SRPConfigMobs.emanaPoisonAmplifier + 1);
        ball.setGearDamage(SRPConfigMobs.emanaadaptedgeard);
        return ball;
    }

    @Override
    public void playProjSound() {
        ++this.count;
        if (this.count == 3) {
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            float v = this.getRandom().nextFloat() * 0.4f + 1.0f;
            this.playSound(SRPSounds.ATTACKEMANA.get(), 4.0f, v);
            return;
        }
        this.playSound(SRPSounds.AEMANA_SHOOTINGPOST.get(), 2.0f, 1.0f);
    }

    class AIChargeAttack
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityEmanaAdapted.this.getTarget() != null && !EntityEmanaAdapted.this.getMoveControl().hasWanted() && EntityEmanaAdapted.this.getRandom().nextInt(7) == 0) {
                return EntityEmanaAdapted.this.distanceToSqr((Entity)EntityEmanaAdapted.this.getTarget()) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityEmanaAdapted.this.getMoveControl().hasWanted() && EntityEmanaAdapted.this.isCharging() && EntityEmanaAdapted.this.getTarget() != null && EntityEmanaAdapted.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityEmanaAdapted.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityEmanaAdapted.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.5);
            EntityEmanaAdapted.this.setCharging(true);
        }

        public void stop() {
            EntityEmanaAdapted.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityEmanaAdapted.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityEmanaAdapted.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityEmanaAdapted.this.doHurtTarget((Entity)entitylivingbase);
                    EntityEmanaAdapted.this.setCharging(false);
                } else {
                    double d0 = EntityEmanaAdapted.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityEmanaAdapted.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.5);
                    }
                }
            }
        }
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
            return !EntityEmanaAdapted.this.getMoveControl().hasWanted() && EntityEmanaAdapted.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityEmanaAdapted.this.blockPosition();
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityEmanaAdapted.this.getRandom().nextInt(15) - 7, EntityEmanaAdapted.this.getRandom().nextInt(11) - 5, EntityEmanaAdapted.this.getRandom().nextInt(15) - 7);
                if (!EntityEmanaAdapted.this.level().isEmptyBlock(blockpos1)) continue;
                EntityEmanaAdapted.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (EntityEmanaAdapted.this.getTarget() != null) break;
                EntityEmanaAdapted.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityEmanaAdapted vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityEmanaAdapted.this.getX();
                double d1 = this.getWantedY() - EntityEmanaAdapted.this.getY();
                double d2 = this.getWantedZ() - EntityEmanaAdapted.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityEmanaAdapted.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityEmanaAdapted.this, 0.5);
                    Mot.mulY(EntityEmanaAdapted.this, 0.5);
                    Mot.mulZ(EntityEmanaAdapted.this, 0.5);
                } else {
                    Mot.addX(EntityEmanaAdapted.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityEmanaAdapted.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityEmanaAdapted.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityEmanaAdapted.this.getTarget() == null) {
                        EntityEmanaAdapted.this.setYRot(-((float)Mth.atan2((double)EntityEmanaAdapted.this.getDeltaMovement().x, (double)EntityEmanaAdapted.this.getDeltaMovement().z)) * 57.295776f);
        EntityEmanaAdapted.this.yBodyRot = EntityEmanaAdapted.this.getYRot();
                    } else {
                        double d4 = EntityEmanaAdapted.this.getTarget().getX() - EntityEmanaAdapted.this.getX();
                        double d5 = EntityEmanaAdapted.this.getTarget().getZ() - EntityEmanaAdapted.this.getZ();
                        EntityEmanaAdapted.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityEmanaAdapted.this.yBodyRot = EntityEmanaAdapted.this.getYRot();
                    }
                }
            }
        }
    }
}

