package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityHull
extends EntityPPrimitive {
    private int timer = 0;
    private static final EntityDataAccessor<Boolean> CAM = SynchedEntityData.defineId(EntityHull.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityHull.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int pulling;
    private boolean canPull;

    public EntityHull(EntityType<? extends EntityHull> type, Level worldIn) {
        super(type, worldIn);
        this.attackSpeedT = 6;
    }

    @Override
    public int getParasiteIDRegister() {
        return 7;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CAM, false);
        builder.define(TARGET_ENTITY, 0);
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.095));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 40, 5, 8.0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPrimitive.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.HULL_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.HULL_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.35);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.HULL_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.HULL_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.srpTicks == 10) {
                float currentH = this.getHealth() / this.getMaxHealth();
                if (this.getSSS()) {
                    this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 25, 0, false, false));
                    this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 2, false, false));
                    if (this.tickCount % 2 == 0) {
                        this.playSound(SRPSounds.HULL_C.get(), 0.2f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
                    }
                    if ((double)currentH < SRPConfigMobs.hullNeededHealth) {
                        this.setSSS(false);
                    }
                } else if ((double)currentH >= SRPConfigMobs.hullNeededHealth) {
                    ++this.timer;
                    if ((float)this.timer > SRPConfigMobs.hullNeededTime) {
                        this.setSSS(true);
                        this.particleStatus((byte)6);
                        this.timer = 0;
                    }
                }
                if (this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
                    ParasiteEventEntity.spawnNext(this, new EntityHullAdapted(SRPEntities.ADA_MANDUCATER.get(), this.level()), true, true);
                }
            }
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getTarget() != null) {
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.hasLineOfSight((Entity)this.getTarget()) && this.distanceToSqr((Entity)this.getTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 1, false, false));
                    this.lookAt((Entity)this.getTargetedEntity());
                    this.attackEntityAsMobMinimum(this.getTarget(), 0.02f);
                    this.setParasiteStatus(3);
                    ++this.pulling;
                    if (this.pulling > 200 || this.distanceToSqr((Entity)this.getTarget()) > 9.0) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        if (this.getTargetedEntity() != null && this.distanceToSqr((Entity)this.getTargetedEntity()) > 0.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.3;
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            str = 0.13;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(target, (deltaX /= distance) * str);
            Mot.addY(target, (deltaY /= distance) * str);
            Mot.addZ(target, (deltaZ /= distance) * str);
        }
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getTarget() != null) {
                    if (!this.getTarget().isAlive()) {
                        this.setTarget(null);
                        this.setParasiteStatus(0);
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)this.entityData.get(TARGET_ENTITY) != 0;
    }

    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.level().isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(TARGET_ENTITY)).intValue());
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getTarget();
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.setSSS(false);
        this.timer = 0;
        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (this.getSSS()) {
                float f = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * SRPConfigMobs.hullStealthDamageMultiplier;
                if (entityIn instanceof LivingEntity) {
                    if (this.level() instanceof ServerLevel serverLevel) {
                        f = EnchantmentHelper.modifyDamage(serverLevel, this.getMainHandItem(), entityIn, this.damageSources().mobAttack(this), f);
                    }
                }
                entityIn.hurt(this.damageSources().mobAttack(this), f);
                this.setSSS(false);
                this.timer = 0;
            }
            if (!this.hasTargetedEntity()) {
                this.setTargetedEntity(entityIn.getId());
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3, false, false));
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobMinimum(LivingEntity entityIn, float damage) {
        boolean flag = super.attackEntityAsMobMinimum(entityIn, damage);
        if (flag && this.getSSS()) {
            this.setSSS(false);
            this.timer = 0;
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.0f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    public boolean getSSS() {
        return (Boolean)this.entityData.get(CAM);
    }

    public void setSSS(boolean in) {
        this.entityData.set(CAM, in);
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.level().isClientSide && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityHullAdapted(SRPEntities.ADA_MANDUCATER.get(), this.level()), true, true);
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0 || this.hasEffect(MobEffects.INVISIBILITY)) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.HULL_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.HULL_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.HULL_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.hullOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP.get(), 0.15f, 1.0f);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    public void push(Entity entityIn) {
        if (this.getTargetedEntity() != null && this.getTargetedEntity() == entityIn) {
            return;
        }
        super.push(entityIn);
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
}

