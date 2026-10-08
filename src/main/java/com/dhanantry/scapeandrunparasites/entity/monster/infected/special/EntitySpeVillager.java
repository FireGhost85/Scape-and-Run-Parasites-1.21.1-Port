package com.dhanantry.scapeandrunparasites.entity.monster.infected.special;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAssimara;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntitySpeVillager
extends EntityPAssimara
implements RangedAttackMob {
    private int host;

    public EntitySpeVillager(EntityType<? extends EntitySpeVillager> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 1;
        this.type = (byte)11;
    }

    @Override
    public int getIDSpawn() {
        return 27;
    }

    @Override
    public int getParasiteIDRegister() {
        return 323;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infvillagerCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 0.0f));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.2, 40, (float)SRPConfig.assimaraFollow / 2.0f, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAssimara.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.MARVILLAGER_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.MARVILLAGER_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.225);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.MARVILLAGER_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.MARVILLAGER_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.assimaraFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.973f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDHUMAN_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDHUMAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDHUMAN_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            this.setSkin(1);
        }
        return floo;
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = target.getX() - (this.getX() + vec3d.x);
        double d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 2.0f) - (0.5 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        double d4 = target.getZ() - (this.getZ() + vec3d.z);
        this.playSound(SRPSounds.EMANA_SHOOTING.get(), 2.0f, 1.0f);
        d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 4.0f) - (0.0 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        EntityProjectileSpineball entitylargefireball = new EntityProjectileSpineball(SRPEntities.SPINEBALL.get(), this.level(), (LivingEntity)this, d2, d3, d4, 3.0f);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
        Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
        this.level().addFreshEntity((Entity)entitylargefireball);
    }

    public void setAggressive(boolean swingingArms) {
    }
}

