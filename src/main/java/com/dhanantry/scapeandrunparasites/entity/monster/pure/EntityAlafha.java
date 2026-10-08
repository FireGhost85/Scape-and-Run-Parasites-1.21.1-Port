package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeNotGround;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.EnumSet;
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

public class EntityAlafha
extends EntityPPure
implements EntityBodyParts,
EntityCutomAttack,
EntityCanShoot,
EntityCanFly {
    private EntityBody head;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityAlafha.class, EntityDataSerializers.BYTE);

    public EntityAlafha(EntityType<? extends EntityAlafha> type, Level worldIn) {
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
        this.head = new EntityBody(this, 1.2f, 1.2f, 1.0f, 3.0f, 0.0f, -1, 1, false, 0.2f);
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 9;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.pureFollow));
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(5, new EntityAIAirVomitSummon(this, SRPConfigMobs.alafhaSummoningCooldown, SRPConfigMobs.alafhaLimit, SRPConfigMobs.alafhaMobList));
        this.goalSelector.addGoal(1, new EntityAIAttackProjectile(this, 20, 10, 4));
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeNotGround(this, 4.5, 16.0, 0.045, true));
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
        if ((this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getTarget() != null) {
            Mot.setY(this, 0.5);
        }
        this.head.tick();
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        return this.hurt(source, amount * 3.0f);
    }

    @Override
    public void setBodyPartDead(int id) {
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
        return 1.6f;
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
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

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.ALAFHA_SHOOTING.get(), 2.0f, 1.0f);
        return new EntityProjectileAlafhaBall(SRPEntities.SALIVABALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ);
    }

    @Override
    public void playProjSound() {
        this.playSound(SRPSounds.ALAFHA_SHOOTINGPOST.get(), 2.0f, 1.0f);
    }

    static class EntityAIAirVomitSummon
    extends Goal {
        private final EntityParasiteBase parentEntity;
        private int limit = 0;
        private int sCooldown;
        private int sLimit;
        private String[] sMobs;
        private int attackTimer = 0;
        private int attacking = 0;
        private double targetX;
        private double targetY;
        private double targetZ;

        public EntityAIAirVomitSummon(EntityParasiteBase summoner, int cooldown, int limit, String[] mobList) {
            this.parentEntity = summoner;
            this.sCooldown = cooldown;
            this.sLimit = limit;
            this.sMobs = mobList;
        }

        public boolean canUse() {
            if (this.attacking >= 1) {
                return true;
            }
            if (this.parentEntity.getTarget() != null) {
                return this.parentEntity.getTarget().onGround();
            }
            return false;
        }

        public void start() {
        }

        public void stop() {
        }

        public void tick() {
            if (this.attacking >= 1) {
                ++this.attacking;
                this.parentEntity.setParasiteStatus(10);
                this.parentEntity.getNavigation().stop();
                if (this.attacking == 2) {
                    // empty if block
                }
                if (this.attacking % 20 == 0 && this.attacking >= 40) {
                    EntityCanSummon parent = (EntityCanSummon)(this.parentEntity);
                    parent.checkID();
                    if (parent.getActualParasites() < parent.getTotalParasites() && this.limit < this.sLimit && ParasiteEventEntity.spawnBiomassFromProjectile(this.parentEntity, this.sMobs, this.parentEntity.getTarget())) {
                        ++this.limit;
                        this.parentEntity.particleStatus((byte)8);
                    }
                }
                if (this.attacking >= 80 || this.limit >= this.sLimit) {
                    this.attacking = 0;
                    this.attackTimer = 0;
                    this.limit = 0;
                    this.parentEntity.setParasiteStatus(2);
                }
            } else if (this.parentEntity.getTarget() == null) {
                if (this.attackTimer > 0) {
                    --this.attackTimer;
                }
            } else if (this.parentEntity.getTarget().isRemoved()) {
                if (this.attackTimer > 0) {
                    --this.attackTimer;
                }
            } else {
                LivingEntity entitylivingbase = this.parentEntity.getTarget();
                if (this.parentEntity.hasLineOfSight((Entity)entitylivingbase) && this.parentEntity.distanceToSqr((Entity)entitylivingbase) < 256.0 && entitylivingbase.onGround()) {
                    ++this.attackTimer;
                    if (this.attackTimer >= this.sCooldown && this.attacking == 0) {
                        ++this.attacking;
                        this.targetX = this.parentEntity.getX();
                        this.targetY = this.parentEntity.getY();
                        this.targetZ = this.parentEntity.getZ();
                    }
                } else if (this.attackTimer > 0 && this.attackTimer > 0) {
                    --this.attackTimer;
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
            double d2;
            double d1;
            MoveControl entitymovehelper = EntityAlafha.this.getMoveControl();
            if (!entitymovehelper.hasWanted()) {
                return true;
            }
            double d0 = entitymovehelper.getWantedX() - EntityAlafha.this.getX();
            double d3 = d0 * d0 + (d1 = entitymovehelper.getWantedY() - EntityAlafha.this.getY()) * d1 + (d2 = entitymovehelper.getWantedZ() - EntityAlafha.this.getZ()) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            if (EntityAlafha.this.getTarget() != null) {
                BlockPos blockpos = EntityAlafha.this.blockPosition();
                int flag = 1;
                double speed = 0.11;
                if (EntityAlafha.this.distanceToSqr((Entity)EntityAlafha.this.getTarget()) > 400.0) {
                    blockpos = EntityAlafha.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.11;
                } else if (EntityAlafha.this.distanceToSqr((Entity)EntityAlafha.this.getTarget()) < 100.0) {
                    blockpos = EntityAlafha.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.11;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.offset(EntityAlafha.this.getRandom().nextInt(15) - 7, EntityAlafha.this.getRandom().nextInt(9) - 5, EntityAlafha.this.getRandom().nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.offset(EntityAlafha.this.getRandom().nextInt(6) - 2, EntityAlafha.this.getRandom().nextInt(7) - 2, EntityAlafha.this.getRandom().nextInt(6) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.offset(EntityAlafha.this.getRandom().nextInt(4) + 3, EntityAlafha.this.getRandom().nextInt(5) + 4, EntityAlafha.this.getRandom().nextInt(4) + 3);
                    }
                    if (!EntityAlafha.this.level().isEmptyBlock(blockpos1)) continue;
                    EntityAlafha.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityAlafha.this.getTarget() == null) {
                        EntityAlafha.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                RandomSource random = EntityAlafha.this.getRandom();
                double d0 = EntityAlafha.this.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityAlafha.this.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityAlafha.this.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityAlafha.this.getMoveControl().setWantedPosition(d0, d1, d2, 0.5);
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityAlafha vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityAlafha.this.getX();
                double d1 = this.getWantedY() - EntityAlafha.this.getY();
                double d2 = this.getWantedZ() - EntityAlafha.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityAlafha.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityAlafha.this, 0.5);
                    Mot.mulY(EntityAlafha.this, 0.5);
                    Mot.mulZ(EntityAlafha.this, 0.5);
                } else {
                    Mot.addX(EntityAlafha.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityAlafha.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityAlafha.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityAlafha.this.getTarget() == null) {
                        EntityAlafha.this.setYRot(-((float)Mth.atan2((double)EntityAlafha.this.getDeltaMovement().x, (double)EntityAlafha.this.getDeltaMovement().z)) * 57.295776f);
        EntityAlafha.this.yBodyRot = EntityAlafha.this.getYRot();
                    } else {
                        double d4 = EntityAlafha.this.getTarget().getX() - EntityAlafha.this.getX();
                        double d5 = EntityAlafha.this.getTarget().getZ() - EntityAlafha.this.getZ();
                        EntityAlafha.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityAlafha.this.yBodyRot = EntityAlafha.this.getYRot();
                    }
                }
            }
        }
    }
}

