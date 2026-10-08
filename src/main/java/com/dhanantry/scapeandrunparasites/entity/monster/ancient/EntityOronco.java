package com.dhanantry.scapeandrunparasites.entity.monster.ancient;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAncientSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAncient;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOroncoTen;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAncientball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityOronco
extends EntityPAncient
implements EntityCanShoot {
    private static final EntityDataAccessor<Boolean> URTEN = SynchedEntityData.defineId(EntityOronco.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ULTEN = SynchedEntityData.defineId(EntityOronco.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RATEN = SynchedEntityData.defineId(EntityOronco.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> LATEN = SynchedEntityData.defineId(EntityOronco.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityOronco.class, EntityDataSerializers.BYTE);
    private boolean health80;
    private boolean health60;
    private boolean health40;
    private boolean health20;
    private float damageR = SRPAttributes.ORONCO_ATTACK_DAMAGE;
    private int maxY = 20;
    private int minY = 7;
    private EntityAIFlightLimits flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);
    private EntityBodyModel leftTendril;
    private EntityBodyModel rightTendril;

    public EntityOronco(EntityType<? extends EntityOronco> type, Level worldIn) {
        super(type, worldIn);
        this.type = (byte)62;
        this.borderOrb = -1;
        this.goalSelector.removeGoal(this.folow);
        this.goalSelector.removeGoal(this.aiWander);
        this.noCulling = true;
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.health80 = true;
        this.health60 = true;
        this.health40 = true;
        this.health20 = true;
        this.leftTendril = new EntityBodyModel(this, 0.6f, 2.0f, 1.0f, 2.17f, 1.4f, 1, 1, true);
        this.rightTendril = new EntityBodyModel(this, 0.6f, 2.0f, 1.0f, 2.17f, 1.4f, -1, 2, true);
        this.leftTendril.setSkin(1);
        this.rightTendril.setSkin(1);
    }

    @Override
    public int getParasiteIDRegister() {
        return 24;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(2, new AIMoveRandom());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(5, new EntityAIAttackProjectile(this, 60, 20, 3, true));
        this.goalSelector.addGoal(4, new EntityAIAncientSummon(this, 20 * SRPConfigMobs.oroncoPodCooldown, SRPConfigMobs.oroncoPodNumber, new String[]{"srparasites:ancientpod;1;1"}));
        this.goalSelector.addGoal(4, new EntityAIFlightLimits(this, this.minY, false));
        this.goalSelector.addGoal(5, new EntityAIFlightAttack(this, 64.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAncient.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.ORONCO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.ORONCO_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.ancientFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(URTEN, true);
        builder.define(ULTEN, true);
        builder.define(RATEN, true);
        builder.define(LATEN, true);
        builder.define(VEX_FLAGS, (byte) (0));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.rightTendril != null) {
            this.rightTendril.tick();
        }
        if (this.leftTendril != null) {
            this.leftTendril.tick();
        }
        if (this.onGround() && !this.level().isClientSide) {
            this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
        }
        if (!this.level().isClientSide && this.getTarget() != null && this.tickCount % 30 == 0) {
            for (LivingEntity entitylivingbase : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(3.0, 3.0, 3.0))) {
                if (entitylivingbase == this || entitylivingbase instanceof EntityParasiteBase) continue;
                float f = (float)Mth.atan2((double)(entitylivingbase.getZ() - this.getZ()), (double)(entitylivingbase.getX() - this.getX()));
                EntityDamage damage = new EntityDamage(this.level(), entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ(), f, (LivingEntity)this, 1.0f, false, 2.5f);
                this.level().addFreshEntity((Entity)damage);
            }
        }
    }

    public void tick() {
        super.tick();
        this.setNoGravity(true);
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.leftTendril.discard();
            this.leftTendril = null;
        }
        if (this.rightTendril != null) {
            this.rightTendril.discard();
            this.rightTendril = null;
        }
        super.discard();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(ServerPlayer player) {
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && !source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (source.getEntity() instanceof ServerPlayer) {
                this.bossInfo.addPlayer((ServerPlayer)source.getEntity());
            }
            if ((double)this.getMaxHealth() * 0.8 >= (double)this.getHealth() && this.health80) {
                this.damageBody(this.getRandom().nextInt(4));
                this.health80 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            } else if ((double)this.getMaxHealth() * 0.6 >= (double)this.getHealth() && this.health60) {
                this.damageBody(this.getRandom().nextInt(4));
                this.health60 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            } else if ((double)this.getMaxHealth() * 0.4 >= (double)this.getHealth() && this.health40) {
                this.damageBody(this.getRandom().nextInt(4));
                this.health40 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            } else if ((double)this.getMaxHealth() * 0.2 >= (double)this.getHealth() && this.health20) {
                this.damageBody(this.getRandom().nextInt(4));
                this.health20 = false;
                this.setParasiteStatus(77);
                this.setWait(30);
            }
        }
        return flag;
    }

    private void damageBody(int in) {
        if (!(this.livingTENUR() || this.livingTENUL() || this.livingTENRA() || this.livingTENLA())) {
            return;
        }
        if (in == 0 && this.livingTENUR()) {
            this.livingTENUR(false);
            EntityOroncoTen ten = new EntityOroncoTen(SRPEntities.ANC_DREADNAUT_TEN.get(), this.level());
            ten.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)ten);
            ten.setSkin(1);
        } else if (in == 1 && this.livingTENUL()) {
            this.livingTENUL(false);
            EntityOroncoTen ten = new EntityOroncoTen(SRPEntities.ANC_DREADNAUT_TEN.get(), this.level());
            ten.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)ten);
            ten.setSkin(1);
        } else if (in == 2 && this.livingTENRA()) {
            this.livingTENRA(false);
            EntityOroncoTen ten = new EntityOroncoTen(SRPEntities.ANC_DREADNAUT_TEN.get(), this.level());
            ten.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)ten);
        } else if (in == 3 && this.livingTENLA()) {
            this.livingTENLA(false);
            EntityOroncoTen ten = new EntityOroncoTen(SRPEntities.ANC_DREADNAUT_TEN.get(), this.level());
            ten.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)ten);
        } else {
            in = (in + 1) % 4;
            this.damageBody(in);
        }
    }

    public int getHorizontalFaceSpeed() {
        return 3;
    }

    public float getDamageR() {
        return this.damageR;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.1f;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.ORONCO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ORONCO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ORONCO_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    public boolean livingTENUR() {
        return (Boolean)this.entityData.get(URTEN);
    }

    public boolean livingTENUL() {
        return (Boolean)this.entityData.get(ULTEN);
    }

    public boolean livingTENRA() {
        return (Boolean)this.entityData.get(RATEN);
    }

    public boolean livingTENLA() {
        return (Boolean)this.entityData.get(LATEN);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("urten", ((Boolean)this.entityData.get(URTEN)).booleanValue());
        compound.putBoolean("ulten", ((Boolean)this.entityData.get(ULTEN)).booleanValue());
        compound.putBoolean("raten", ((Boolean)this.entityData.get(RATEN)).booleanValue());
        compound.putBoolean("laten", ((Boolean)this.entityData.get(LATEN)).booleanValue());
        compound.putBoolean("healtheight", this.health80);
        compound.putBoolean("healthsix", this.health60);
        compound.putBoolean("healthfour", this.health40);
        compound.putBoolean("healthtwo", this.health20);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("urten", 99)) {
            this.livingTENUR(compound.getBoolean("urten"));
        }
        if (compound.contains("ulten", 99)) {
            this.livingTENUL(compound.getBoolean("ulten"));
        }
        if (compound.contains("raten", 99)) {
            this.livingTENRA(compound.getBoolean("raten"));
        }
        if (compound.contains("laten", 99)) {
            this.livingTENLA(compound.getBoolean("laten"));
        }
        if (compound.contains("healtheight", 99)) {
            this.health80 = compound.getBoolean("healtheight");
        }
        if (compound.contains("healthsix", 99)) {
            this.health60 = compound.getBoolean("healthsix");
        }
        if (compound.contains("healthfour", 99)) {
            this.health40 = compound.getBoolean("healthfour");
        }
        if (compound.contains("healthtwo", 99)) {
            this.health20 = compound.getBoolean("healthtwo");
        }
    }

    public void livingTENUR(boolean in) {
        this.entityData.set(URTEN, in);
    }

    public void livingTENUL(boolean in) {
        this.entityData.set(ULTEN, in);
    }

    public void livingTENRA(boolean in) {
        this.entityData.set(RATEN, in);
    }

    public void livingTENLA(boolean in) {
        this.entityData.set(LATEN, in);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    public boolean onClimbable() {
        return false;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        this.checkInsideBlocks();
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
        this.playSound(SRPSounds.ORONCO_SHOOTINGREAL.get(), 2.0f, 1.0f);
        return new EntityProjectileAncientball(SRPEntities.ANCIENTBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ);
    }

    @Override
    public void playProjSound() {
        this.playSound(SRPSounds.ORONCO_SHOOTING.get(), 4.0f, 1.0f);
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            double d2;
            double d1;
            MoveControl entitymovehelper = EntityOronco.this.getMoveControl();
            if (!entitymovehelper.hasWanted()) {
                return true;
            }
            double d0 = entitymovehelper.getWantedX() - EntityOronco.this.getX();
            double d3 = d0 * d0 + (d1 = entitymovehelper.getWantedY() - EntityOronco.this.getY()) * d1 + (d2 = entitymovehelper.getWantedZ() - EntityOronco.this.getZ()) * d2;
            return d3 < 1.0 || d3 > 3600.0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            if (EntityOronco.this.getTarget() != null) {
                BlockPos blockpos = EntityOronco.this.blockPosition();
                int flag = 1;
                double speed = 0.3;
                if (EntityOronco.this.distanceToSqr((Entity)EntityOronco.this.getTarget()) > 400.0) {
                    blockpos = EntityOronco.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.1;
                } else if (EntityOronco.this.distanceToSqr((Entity)EntityOronco.this.getTarget()) < 100.0) {
                    blockpos = EntityOronco.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.15;
                }
                for (int i = 0; i < 3; ++i) {
                    BlockPos blockpos1 = blockpos.offset(EntityOronco.this.getRandom().nextInt(15) - 7, EntityOronco.this.getRandom().nextInt(11) - 5, EntityOronco.this.getRandom().nextInt(15) - 7);
                    if (flag == 2) {
                        blockpos1 = blockpos.offset(EntityOronco.this.getRandom().nextInt(12) - 2, EntityOronco.this.getRandom().nextInt(10) - 2, EntityOronco.this.getRandom().nextInt(12) - 2);
                    } else if (flag == 3) {
                        blockpos1 = blockpos.offset(EntityOronco.this.getRandom().nextInt(8) + 3, EntityOronco.this.getRandom().nextInt(7) + 4, EntityOronco.this.getRandom().nextInt(8) + 3);
                    }
                    if (!EntityOronco.this.level().isEmptyBlock(blockpos1)) continue;
                    EntityOronco.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                    if (EntityOronco.this.getTarget() == null) {
                        EntityOronco.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                    }
                    break;
                }
            } else {
                RandomSource random = EntityOronco.this.getRandom();
                double d0 = EntityOronco.this.getX() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d1 = EntityOronco.this.getY() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                double d2 = EntityOronco.this.getZ() + (double)((random.nextFloat() * 2.0f - 1.0f) * 16.0f);
                EntityOronco.this.getMoveControl().setWantedPosition(d0, d1, d2, 0.3);
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityOronco vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityOronco.this.getX();
                double d1 = this.getWantedY() - EntityOronco.this.getY();
                double d2 = this.getWantedZ() - EntityOronco.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityOronco.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityOronco.this, 0.5);
                    Mot.mulY(EntityOronco.this, 0.5);
                    Mot.mulZ(EntityOronco.this, 0.5);
                } else {
                    Mot.addX(EntityOronco.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityOronco.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityOronco.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityOronco.this.getTarget() == null) {
                        EntityOronco.this.setYRot(-((float)Mth.atan2((double)EntityOronco.this.getDeltaMovement().x, (double)EntityOronco.this.getDeltaMovement().z)) * 57.295776f);
        EntityOronco.this.yBodyRot = EntityOronco.this.getYRot();
                    } else {
                        double d4 = EntityOronco.this.getTarget().getX() - EntityOronco.this.getX();
                        double d5 = EntityOronco.this.getTarget().getZ() - EntityOronco.this.getZ();
                        EntityOronco.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityOronco.this.yBodyRot = EntityOronco.this.getYRot();
                    }
                }
            }
        }
    }
}

