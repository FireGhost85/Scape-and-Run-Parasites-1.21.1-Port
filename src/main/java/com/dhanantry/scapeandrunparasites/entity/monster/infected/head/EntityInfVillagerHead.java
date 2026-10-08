package com.dhanantry.scapeandrunparasites.entity.monster.infected.head;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityInfVillagerHead
extends EntityPInfected {
    public EntityInfVillagerHead(EntityType<? extends EntityInfVillagerHead> type, Level worldIn) {
        super(type, worldIn);
        this.killcount = -10.0;
        this.attackSpeedT = 15;
    }

    @Override
    public int getParasiteIDRegister() {
        return 32;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infvillagerCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 40, 100, 3, true, 14));
        this.setskillLeapValues(0.7f, 2.5, 0);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal((Mob)this, 0.4f));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.goalSelector.addGoal(4, new EntityAIAvoidOrAttack(this, 0.5f, 10, 2));
        this.goalSelector.addGoal(5, new EntityAIAvoidEntityStatus<LivingEntity>(this, LivingEntity.class, new Predicate<LivingEntity>(){

            public boolean test(@Nullable LivingEntity e) {
                return !(e instanceof WaterAnimal) && !(e instanceof Creeper) && !(e instanceof EntityParasiteBase) && !(e instanceof Animal);
            }
        }, 8.0f, 1.3));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal((PathfinderMob)this, EntityInhooM.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFVILLAGER_HEADHEALTH);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFVILLAGER_HEADDAMAGE);
        return builder;
    }

    public void setInWeb() {
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.7f;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (entityIn instanceof EntityInhooM && entityIn.isAlive() && this.isAlive()) {
            ParasiteEventEntity.spawnNext(this, new EntityInfVillager(SRPEntities.SIM_VILLAGER.get(), this.level()), true, false);
            ((EntityParasiteBase)entityIn).particleStatus((byte)7);
            entityIn.discard();
            return true;
        }
        return super.doHurtTarget(entityIn);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && SRPConfigSystems.disloGiveBodies && this.isAlive() && this.srpTicks == 10 && SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 20) >= 1) {
            ParasiteEventEntity.spawnNext(this, new EntityInfVillager(SRPEntities.SIM_VILLAGER.get(), this.level()), true, false);
            return;
        }
    }

    @Override
    public boolean canAttackType(EntityType<?> type) {
        if (type == EntityType.PLAYER) {
            return true;
        }
        String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).toString();
        if (name.contains("srparasites") && type != SRPEntities.INCOMPLETEFORM_MEDIUM.get()) {
            return false;
        }
        return !SRPConfig.mobAttackingFull || !ParasiteEventEntity.checkName(name, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        super.causeFallDamage(distance, damageMultiplier * 0.3f, source);
        return false;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDHEAD_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDHEAD_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDHEAD_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.level().random.nextInt(3) == 0) {
            this.setSkin(1);
        }
        return floo;
    }
}

