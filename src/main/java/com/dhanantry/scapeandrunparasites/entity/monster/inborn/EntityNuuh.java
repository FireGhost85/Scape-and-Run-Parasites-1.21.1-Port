package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityNuuh
extends EntityPMalleable {
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityNuuh.class, EntityDataSerializers.BYTE);

    public EntityNuuh(EntityType<? extends EntityNuuh> type, Level worldIn) {
        super(type, worldIn);
        this.borderOrb = -1;
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.xpReward = SRPAttributes.XP_PURE;
        this.type = (byte)51;
        this.foodSteal = SRPConfig.pureFoodSteal;
        this.pointCap = SRPConfig.purePointCap;
        this.pointReduction = SRPConfig.purePointRed;
        this.chanceLearn = SRPConfig.pureChanceLe;
        this.chanceLearnFire = SRPConfig.pureChanceLeFire;
        this.DamageTypeCap = SRPConfig.purePointDamCap;
        this.MiniDamage = SRPConfigMobs.nuuhMinDamage;
        this.regen = SRPConfig.pureRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.pureOneMindDeathV;
        this.regenEff = 3;
        this.adaptationCap = 0.95f;
        this.attackSpeedT = 6;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 20, 100, 5, true, 14));
        this.setskillLeapValues(0.8f, 2.0, 0);
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.12));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal((Mob)this, 0.4f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new EntityAIEvadeDash(this, 10, 2, 1, 1.0, 15));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public int getParasiteIDRegister() {
        return 76;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        LivingEntity src = source.getEntity() instanceof LivingEntity ? (LivingEntity)source.getEntity() : null;
        boolean took = super.hurt(source, amount);
        if (this.level().isClientSide) {
            return took;
        }
        if (src instanceof Player && this.isAlive()) {
            Player player = (Player)src;
            this.setLastHurtByMob((LivingEntity)player);
            this.setTarget((LivingEntity)player);
        }
        return took;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPMalleable.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.NUUH_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.NUUH_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.37);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.NUUH_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.NUUH_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity target) {
        return super.doHurtTarget(target);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 200.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    public void setInWeb() {
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation((Mob)this, worldIn);
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (this.level().isClientSide) {
            return;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, (byte) (0));
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(CLIMBING);
        if (this.getTarget() != null) {
            if (!this.hasLineOfSight((Entity)this.getTarget())) {
                if (this.distanceToSqr((Entity)this.getTarget()) < 100.0) {
                    b0 = (byte)(b0 & 0xFFFFFFFE);
                    this.entityData.set(CLIMBING, (byte) (b0));
                    return;
                }
            } else if (this.getTarget().getY() + 1.0 < this.getY()) {
                b0 = (byte)(b0 & 0xFFFFFFFE);
                this.entityData.set(CLIMBING, (byte) (b0));
                return;
            }
        }
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.entityData.set(CLIMBING, (byte) (b0));
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.9f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityMudo(SRPEntities.RUPTER.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(2)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                }
            }
        }
        return floo;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.NUUH_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.NUUH_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.NUUH_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }
}

