package com.dhanantry.scapeandrunparasites.entity.monster.derived;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDerived;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHebluLight;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
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
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityHeblu
extends EntityPDerived
implements EntityCutomAttack,
EntityBodyParts,
RangedAttackMob,
EntityCanFly {
    protected static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BOOLEAN);
    private int flying;
    private float aaa;
    private float sss;
    private int hebluLightBarrageSoundTimer = 0;
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private EntityBody head;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private float headlHealth;
    public int vomit;
    private BlockPos vomitPos;
    public boolean raining;
    private int rainingOrbs = 0;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(EntityHeblu.class, EntityDataSerializers.BOOLEAN);
    private int limit;
    private boolean skillFlame;
    private double tttX;
    private double tttY;
    private double tttZ;
    private double tttH;
    private double tttHH;

    public EntityHeblu(EntityType<? extends EntityHeblu> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.canModRender = 0;
        this.type = (byte)14;
        this.killcount = -10.0;
        this.goalSelector.removeGoal(this.folow);
        this.flying = 0;
        this.skillFlame = false;
        this.leftTendril = new EntityBody(this, 3.3f, 2.5f, 1.0f, 4.1f, 3.3f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 3.3f, 2.5f, 1.0f, 4.1f, 3.3f, -1, 2, true);
        this.head = new EntityBody(this, 2.2f, 2.2f, 1.0f, 4.0f, 2.0f, -1, 3, false, 0.2f);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.headlHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.noCulling = true;
        this.moveControl = new AIMoveControl(this);
    }

    @Override
    public int getParasiteIDRegister() {
        return 309;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 9.0));
        this.goalSelector.addGoal(5, new AIMoveRandom());
        this.goalSelector.addGoal(6, new AIFireballAttack(this));
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, SRPConfig.derivedFollow, true, 3));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 12.0f));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.3, 100, 40.0f, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPDerived.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.HEBLU_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.HEBLU_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.27);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.HEBLU_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.HEBLU_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.derivedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        if (this.isNoAi()) {
            return;
        }
        super.aiStep();
        this.killcount = -10.0;
        if (!this.level().isClientSide && this.hebluLightBarrageSoundTimer > 0) {
            --this.hebluLightBarrageSoundTimer;
            if (this.hebluLightBarrageSoundTimer == 0) {
                this.level().playSound(null, this.getX(), this.getY() + (double)this.getBbHeight() * 0.6, this.getZ(), SRPSounds.HEBLU_LIGHT_IMPACT.get(), SoundSource.HOSTILE, 1.2f, 1.45f + this.getRandom().nextFloat() * 0.18f);
            }
        }
        if (this.headlHealth > 0.0f) {
            this.head.tick();
        }
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.tick();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.tick();
        }
        if (!this.level().isClientSide && this.srpTicks == 10) {
            if ((this.level().getBlockState(this.blockPosition().below(1)).getBlock() != Blocks.AIR || this.level().getBlockState(this.blockPosition().below(2)).getBlock() != Blocks.AIR) && this.getFlyingState() && this.getRandom().nextInt(3) == 0) {
                Mot.setY(this, 0.5);
            }
            if (this.rainingOrbs > 0) {
                --this.rainingOrbs;
                if (this.rainingOrbs <= 15) {
                    double radius = 10.0;
                    double x = this.getX() + (this.getRandom().nextDouble() * 2.0 - 1.0) * radius;
                    double y = this.getY() + 20.0;
                    double z = this.getZ() + (this.getRandom().nextDouble() * 2.0 - 1.0) * radius;
                    if (this.getTarget() != null && this.getRandom().nextBoolean()) {
                        x = this.getTarget().getX() + (this.getRandom().nextDouble() * 2.0 - 1.0) * radius;
                        z = this.getTarget().getZ() + (this.getRandom().nextDouble() * 2.0 - 1.0) * radius;
                    }
                    BlockPos pos = BlockPos.containing(x, y, z);
                    EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(SRPEntities.SALIVABALL.get(), this.level(), (LivingEntity)this, 0.0, -10.0, 0.0);
                    Mot.setPosX(entitylargefireball, x);
                    Mot.setPosY(entitylargefireball, y);
                    Mot.setPosZ(entitylargefireball, z);
                    this.level().addFreshEntity((Entity)entitylargefireball);
                }
            }
            if (this.getRandom().nextInt(25) == 0 && !this.getFlyingState() && this.vomit <= 0) {
                this.changeStateTo(true);
                return;
            }
        }
        if (this.vomit > 0) {
            if (!this.level().isClientSide) {
                this.lookAt(this.vomitPos.getX(), this.vomitPos.getY(), this.vomitPos.getZ());
            }
            --this.vomit;
            if (this.level().isClientSide) {
                for (int i = 0; i < 19; ++i) {
                    Vec3 vec3d = this.getViewVector(1.0f);
                    double bon = 8.2;
                    double offsetX = this.getX() + vec3d.x * bon;
                    double offsetY = this.getY() + (double)this.getEyeHeight() + 2.2;
                    double offsetZ = this.getZ() + vec3d.z * bon;
                    if (this.raining) {
                        if (this.getFlyingState()) {
                            bon = 4.3;
                            offsetX = this.getX() + vec3d.x * bon;
                            offsetY = this.getY() + (double)this.getEyeHeight() + 7.5;
                            offsetZ = this.getZ() + vec3d.z * bon;
                        } else {
                            bon = 6.1;
                            offsetX = this.getX() + vec3d.x * bon;
                            offsetY = this.getY() + (double)this.getEyeHeight() + 7.2;
                            offsetZ = this.getZ() + vec3d.z * bon;
                        }
                    }
                    double motionX = (double)(-Mth.sin((float)(this.getYRot() * (float)Math.PI / 180.0f))) * 1.4;
                    double motionZ = (double)Mth.cos((float)(this.getYRot() * (float)Math.PI / 180.0f)) * 1.4;
                    double motionY = -0.55 + this.getRandom().nextDouble() * 0.5;
                    double spreadFactor = 0.55;
                    motionX += (this.getRandom().nextDouble() - 0.5) * spreadFactor;
                    motionZ += (this.getRandom().nextDouble() - 0.5) * spreadFactor;
                    double rain = 1.0;
                    if (this.raining) {
                        motionY = 4.5 + this.getRandom().nextDouble() * 0.3;
                        spreadFactor = 0.2;
                        motionX += (this.getRandom().nextDouble() - 0.5) * spreadFactor;
                        motionZ += (this.getRandom().nextDouble() - 0.5) * spreadFactor;
                        rain = 0.2;
                    }
                    this.spawnParticles(ParticleTypes.FLAME, offsetX, offsetY, offsetZ, motionX * rain, motionY * rain, motionZ * rain);
                    this.spawnParticles(SRPEnumParticle.GCLOUD, -255, 0, 0, offsetX, offsetY, offsetZ, motionX * rain, motionY * rain, motionZ * rain);
                }
            }
        } else {
            this.raining = false;
        }
        if (this.flying >= 1) {
            ++this.flying;
        }
        if (this.getFlyingState()) {
            this.aaa += 0.08f;
            this.sss += 0.782f;
            if (this.sss >= 24.0f) {
                this.playSound(SoundEvents.ENDER_DRAGON_FLAP, 5.0f, 0.8f + this.getRandom().nextFloat() * 0.3f);
                this.sss = 0.0f;
            }
            if (this.onGround() && !this.level().isClientSide && this.flying > 40) {
                this.changeStateTo(false);
            }
        } else {
            this.aaa = 0.08f;
            this.sss = 0.0f;
        }
    }

    public void tick() {
        super.tick();
        if (this.getFlyingState()) {
            this.setNoGravity(true);
        } else {
            this.setNoGravity(false);
        }
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.noActionTime = 0;
        this.vomitPos = target.blockPosition();
        if (target.getY() > this.getY() + 5.0 || target.getY() < this.getY()) {
            double d1 = 4.0;
            Vec3 vec3d = this.getViewVector(1.0f);
            double d2 = target.getX() - (this.getX() + vec3d.x * 4.0);
            double d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 2.0f) - (0.5 + this.getY() + (double)(this.getBbHeight() / 2.0f));
            double d4 = target.getZ() - (this.getZ() + vec3d.z * 4.0);
            this.level().levelEvent((Player)null, 1016, this.blockPosition(), 0);
            EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(SRPEntities.SALIVABALL.get(), this.level(), (LivingEntity)this, d2, d3, d4);
            Mot.setPosX(entitylargefireball, this.getX() + vec3d.x * 4.0);
            Mot.setPosY(entitylargefireball, this.getY() + (double)(this.getBbHeight() / 2.0f) + 0.5);
            Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z * 4.0);
            this.level().addFreshEntity((Entity)entitylargefireball);
            for (int i = 0; i <= 2; ++i) {
                this.level().addParticle(ParticleTypes.FLAME, this.getX() + vec3d.x * 4.0, this.getY() + (double)(this.getBbHeight() / 2.0f) + 0.5, this.getZ() + vec3d.z * 4.0, 0.0, -1.0, 0.0);
            }
            return;
        }
        this.vomit = 40;
        if (this.getRandom().nextBoolean()) {
            this.raining = true;
            this.rainingOrbs = 19;
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            this.playSound(SRPSounds.HEBLU_SHOOT.get(), this.getSoundVolume() * 2.0f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
        } else {
            this.level().broadcastEntityEvent((Entity)this, (byte)101);
        }
        if (!this.raining) {
            Vec3 vec3d = this.getViewVector(1.0f);
            double bon = 12.5;
            float rad = 2.0f;
            for (int i = 0; i < 3; ++i) {
                EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX() + vec3d.x * bon, Math.max(this.getY(), target.getY()), this.getZ() + vec3d.z * bon);
                entityareaeffectcloud.setRadius(rad + 1.0f, 0.9f);
                entityareaeffectcloud.setDuration(100);
                entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
                entityareaeffectcloud.setOwner(this);
                this.level().broadcastEntityEvent((Entity)entityareaeffectcloud, (byte)77);
                entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 300, 0, false, true));
                this.level().addFreshEntity((Entity)entityareaeffectcloud);
                if (i == 1) {
                    bon += 4.0;
                }
                bon += 7.5 + (double)i;
                rad += 2.0f;
            }
            this.vomitPos = BlockPos.containing(this.getX() + vec3d.x * bon, this.getY(), this.getZ() + vec3d.z * bon);
        }
        this.setWait(80);
    }

    public void spawnLightBarrage(LivingEntity target) {
        this.spawnLightBarrage(target, false);
    }

    public void spawnLightBarrage(LivingEntity target, boolean forceTarget) {
        if (this.level().isClientSide) {
            return;
        }
        if (!forceTarget && target == null) {
            return;
        }
        if (!forceTarget && EntityHeblu.isInvalidLightBarrageTarget(target)) {
            return;
        }
        Vec3 look = this.getViewVector(1.0f);
        double startX = this.getX() + look.x * 2.5;
        double startY = this.getY() + (double)this.getBbHeight() * 0.7 + 1.2;
        double startZ = this.getZ() + look.z * 2.5;
        this.level().playSound(null, this.getX(), this.getY() + (double)this.getBbHeight() * 0.6, this.getZ(), SRPSounds.HEBLU_BELL_CHARGEUP.get(), SoundSource.HOSTILE, 5.0f, 0.75f + this.getRandom().nextFloat() * 0.08f);
        this.hebluLightBarrageSoundTimer = 10;
        int count = Math.max(1, SRPConfigMobs.hebluLightBarrageCount);
        for (int i = 0; i < count; ++i) {
            double x = startX + (this.getRandom().nextDouble() - 0.5) * 3.5;
            double y = startY + (this.getRandom().nextDouble() - 0.5) * 2.5;
            double z = startZ + (this.getRandom().nextDouble() - 0.5) * 3.5;
            EntityProjectileHebluLight shard = new EntityProjectileHebluLight(SRPEntities.HEBLU_LIGHT.get(), this.level(), (LivingEntity)this, target, x, y, z, forceTarget);
            this.level().addFreshEntity((Entity)shard);
        }
    }

    public void setAggressive(boolean swingingArms) {
    }

    public float getaaa() {
        return this.aaa;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && this.getRandom().nextInt(12) == 0 && !this.getFlyingState()) {
            this.changeStateTo(true);
        }
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.level().isClientSide) {
            return false;
        }
        boolean flag = this.hurt(source, amount);
        if (!flag) {
            return false;
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getPartId() == id) {
            this.leftTendril.discard();
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendril.discard();
        } else if (this.head.getPartId() == id) {
            this.head.discard();
        }
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        if (this.leftTendril != null) {
            this.leftTendril.discard();
        }
        if (this.rightTendril != null) {
            this.rightTendril.discard();
        }
        super.discard();
    }

    @Override
    protected void spawnCloneCosmical(EntityPCosmical entityout) {
        entityout.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        if (this.hasCustomName()) {
            SRPEntityUtil.setCustomNameTag(entityout, "--" + SRPEntityUtil.getCustomNameTag(this) + "--");
            entityout.setCustomNameVisible(this.isCustomNameVisible());
        }
        this.level().addFreshEntity((Entity)entityout);
        entityout.particleStatus((byte)7);
        this.limitClones = entityout.getId();
        entityout.limitClones = this.getId();
        this.setShadowStatus(false);
        entityout.setCloneC();
        entityout.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(entityout.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 1.33);
        entityout.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(entityout.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 0.5);
        if (this.getFlyingState()) {
            ((EntityHeblu)entityout).changeStateTo(true);
        }
    }

    public void changeStateTo(boolean fly) {
        if (this.limit >= 1) {
            return;
        }
        if (fly) {
            if (!this.getFlyingState()) {
                if (this.leftTendrilHealth <= 0.0f || this.rightTendrilHealth <= 0.0f) {
                    return;
                }
                this.moveControl = new AIMoveControl(this);
                this.setParasiteStatus(3);
                this.entityData.set(FLYING, true);
                Mot.setY(this, 0.5);
                this.aaa += 0.08f;
                this.flying = 1;
                this.sss = 19.85f;
            }
        } else if (this.getFlyingState()) {
            this.moveControl = new MoveControl((Mob)this);
            this.setParasiteStatus(0);
            this.entityData.set(FLYING, false);
            this.flying = 0;
            this.aaa = 0.0f;
            this.sss = 0.0f;
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.75f;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        return super.doHurtTarget(entityIn);
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        return this.doHurtTarget(entityIn);
    }

    @Override
    protected void selfExplode() {
    }

    @Override
    protected void spawnGore() {
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.HEBLU_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HEBLU_HURT.get();
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.HEBLU_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.HEAVY_STEPS.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    public boolean getFlyingState() {
        return (Boolean)this.entityData.get(FLYING);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.putFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
        }
        if (compound.contains("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
            }
        }
    }

    public float getLeft() {
        return this.leftTendrilHealth;
    }

    public float getRight() {
        return this.rightTendrilHealth;
    }

    public float getHead() {
        return this.headlHealth;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
        } else if (id == 33) {
            this.headlHealth = 0.0f;
        } else if (id == 100) {
            this.vomit = 40;
            this.raining = true;
        } else if (id == 101) {
            this.vomit = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected EntityPCosmical getThis() {
        return new EntityHeblu(SRPEntities.DRACONITE.get(), this.level());
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
        builder.define(FLYING, true);
        builder.define(ATTACKING, false);
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

    public boolean isAttacking() {
        return (Boolean)this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }

    private static boolean isInvalidLightBarrageTarget(LivingEntity target) {
        if (target instanceof Player) {
            Player player = (Player)target;
            return player.isCreative() || player.isSpectator();
        }
        return false;
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillFlame;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillFlame = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.flame();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void flame() {
        if (this.getFlyingState() || this.headlHealth <= 0.0f) {
            this.skillFlame = true;
            this.limit = 0;
            return;
        }
        if (this.limit == 0) {
            LivingEntity entitylivingbase = this.getTarget();
            if (entitylivingbase == null) {
                this.skillFlame = true;
                this.limit = 0;
                return;
            }
            this.tttX = entitylivingbase.getX();
            this.tttY = entitylivingbase.getY();
            this.tttZ = entitylivingbase.getZ();
            this.tttH = entitylivingbase.getBoundingBox().minY;
            this.tttHH = entitylivingbase.getBbHeight();
        }
        ++this.limit;
        this.setParasiteStatus(10);
        this.getNavigation().moveTo(this.tttX, this.tttY, this.tttZ, 0.0);
        this.resetIdleTime();
        if (this.tickCount % 10 != 0) {
            return;
        }
        double d1 = 4.0;
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = this.tttX - (this.getX() + vec3d.x * 4.0);
        double d3 = this.tttH + this.tttHH / 4.0 - (0.5 + this.getY() + (double)(this.getBbHeight() / 4.0f));
        double d4 = this.tttZ - (this.getZ() + vec3d.z * 4.0);
        this.level().levelEvent((Player)null, 1016, this.blockPosition(), 0);
        EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(SRPEntities.SALIVABALL.get(), this.level(), (LivingEntity)this, d2, d3, d4);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x * 4.0);
        Mot.setPosY(entitylargefireball, this.getY() + (double)(this.getBbHeight() / 2.0f) + 0.5);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z * 4.0);
        this.level().addFreshEntity((Entity)entitylargefireball);
        if (this.limit >= 60) {
            this.skillFlame = true;
            this.setParasiteStatus(0);
            this.limit = 0;
        }
    }

    static class AIFireballAttack
    extends Goal {
        private final EntityHeblu parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityHeblu ghast) {
            this.parentEntity = ghast;
        }

        public boolean canUse() {
            return this.parentEntity.getTarget() != null && this.parentEntity.getFlyingState() && this.parentEntity.headlHealth > 0.0f;
        }

        public void start() {
            this.attackTimer = 0;
        }

        public void stop() {
            this.parentEntity.setAttacking(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            double d0 = 64.0;
            if (entitylivingbase == null) {
                return;
            }
            if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < 4096.0 && this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                Level world = this.parentEntity.level();
                ++this.attackTimer;
                if (this.parentEntity.hasEffect(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                this.parentEntity.resetIdleTime();
                if (this.attackTimer == 10) {
                    // empty if block
                }
                if (this.attackTimer == 20) {
                    int specialAttack = this.parentEntity.level().random.nextInt(2);
                    if (specialAttack == 0 && entitylivingbase.onGround()) {
                        this.parentEntity.vomitPos = entitylivingbase.blockPosition();
                        this.parentEntity.vomit = 40;
                        this.parentEntity.raining = true;
                        this.parentEntity.rainingOrbs = 19;
                        world.broadcastEntityEvent((Entity)this.parentEntity, (byte)100);
                        this.parentEntity.playSound(SRPSounds.HEBLU_SHOOT.get(), this.parentEntity.getSoundVolume() * 2.0f, (this.parentEntity.level().random.nextFloat() - this.parentEntity.level().random.nextFloat()) * 0.2f + 1.0f);
                        this.attackTimer = -60;
                        return;
                    }
                    if (specialAttack == 1 && !EntityHeblu.isInvalidLightBarrageTarget(entitylivingbase)) {
                        this.parentEntity.spawnLightBarrage(entitylivingbase);
                        this.attackTimer = -85;
                        return;
                    }
                    double d1 = 4.0;
                    Vec3 vec3d = this.parentEntity.getViewVector(1.0f);
                    double d2 = entitylivingbase.getX() - (this.parentEntity.getX() + vec3d.x * 4.0);
                    double d3 = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 2.0f) - (0.5 + this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f));
                    double d4 = entitylivingbase.getZ() - (this.parentEntity.getZ() + vec3d.z * 4.0);
                    world.levelEvent((Player)null, 1016, this.parentEntity.blockPosition(), 0);
                    EntityProjectileAlafhaBall entitylargefireball = new EntityProjectileAlafhaBall(SRPEntities.SALIVABALL.get(), world, (LivingEntity)this.parentEntity, d2, d3, d4);
                    Mot.setPosX(entitylargefireball, this.parentEntity.getX() + vec3d.x * 4.0);
                    Mot.setPosY(entitylargefireball, this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f) + 0.5);
                    Mot.setPosZ(entitylargefireball, this.parentEntity.getZ() + vec3d.z * 4.0);
                    world.addFreshEntity((Entity)entitylargefireball);
                    this.parentEntity.noActionTime = 0;
                    this.attackTimer = -45;
                    for (int i = 0; i <= 2; ++i) {
                        this.parentEntity.level().addParticle(ParticleTypes.FLAME, this.parentEntity.getX() + vec3d.x * 4.0, this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f) + 0.5, this.parentEntity.getZ() + vec3d.z * 4.0, 0.0, -1.0, 0.0);
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
            this.parentEntity.setAttacking(this.attackTimer > 10);
        }
    }

    class AIMoveRandom
    extends Goal {
        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityHeblu.this.getMoveControl().hasWanted() && EntityHeblu.this.getRandom().nextInt(5) == 0 && EntityHeblu.this.getFlyingState();
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityHeblu.this.blockPosition();
            int flag = 1;
            double speed = 0.5;
            if (EntityHeblu.this.getTarget() != null) {
                if (EntityHeblu.this.distanceToSqr((Entity)EntityHeblu.this.getTarget()) > 100.0) {
                    blockpos = EntityHeblu.this.getTarget().blockPosition();
                    flag = 2;
                    speed += 0.25;
                } else if (EntityHeblu.this.distanceToSqr((Entity)EntityHeblu.this.getTarget()) < 36.0) {
                    blockpos = EntityHeblu.this.getTarget().blockPosition();
                    flag = 3;
                    speed += 0.25;
                }
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityHeblu.this.getRandom().nextInt(15) - 7, EntityHeblu.this.getRandom().nextInt(11) - 5, EntityHeblu.this.getRandom().nextInt(15) - 7);
                if (flag == 2) {
                    blockpos1 = blockpos.offset(EntityHeblu.this.getRandom().nextInt(6) - 2, EntityHeblu.this.getRandom().nextInt(7) - 2, EntityHeblu.this.getRandom().nextInt(6) - 2);
                } else if (flag == 3) {
                    blockpos1 = blockpos.offset(EntityHeblu.this.getRandom().nextInt(4) + 3, EntityHeblu.this.getRandom().nextInt(5) + 4, EntityHeblu.this.getRandom().nextInt(4) + 3);
                }
                if (!EntityHeblu.this.level().isEmptyBlock(blockpos1)) continue;
                EntityHeblu.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, speed);
                if (EntityHeblu.this.getTarget() != null) break;
                EntityHeblu.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityHeblu vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityHeblu.this.getX();
                double d1 = this.getWantedY() - EntityHeblu.this.getY();
                double d2 = this.getWantedZ() - EntityHeblu.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityHeblu.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityHeblu.this, 0.5);
                    Mot.mulY(EntityHeblu.this, 0.5);
                    Mot.mulZ(EntityHeblu.this, 0.5);
                } else {
                    Mot.addX(EntityHeblu.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityHeblu.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityHeblu.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityHeblu.this.getTarget() == null) {
                        EntityHeblu.this.setYRot(-((float)Mth.atan2((double)EntityHeblu.this.getDeltaMovement().x, (double)EntityHeblu.this.getDeltaMovement().z)) * 57.295776f);
        EntityHeblu.this.yBodyRot = EntityHeblu.this.getYRot();
                    } else {
                        double d4 = EntityHeblu.this.getTarget().getX() - EntityHeblu.this.getX();
                        double d5 = EntityHeblu.this.getTarget().getZ() - EntityHeblu.this.getZ();
                        EntityHeblu.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityHeblu.this.yBodyRot = EntityHeblu.this.getYRot();
                    }
                }
            }
        }
    }
}

