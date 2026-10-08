package com.dhanantry.scapeandrunparasites.entity.monster.infected;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileWebball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityDorpa
extends EntityPInfected
implements EntityCanShoot {
    public EntityDorpa(EntityType<? extends EntityDorpa> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.canModRender = 1;
        this.type = (byte)14;
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 2;
    }

    @Override
    public int canSpawnByIDData() {
        return 0;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIAttackProjectile(this, 60, 15, 3));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.DORPA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.DORPA_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.27);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.DORPA_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.DORPA_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.infectedFollow);
        return builder;
    }

    public void setInWeb() {
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.75f;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (this.getSkin() == 1) {
            boolean flag = super.doHurtTarget(entityIn);
            if (flag && entityIn instanceof LivingEntity) {
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 40));
            }
            return flag;
        }
        return super.doHurtTarget(entityIn);
    }

    @Override
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.dorpamob}, 0, false, SRPEntityUtil.getCustomNameTag(this));
    }

    @Override
    protected void spawnGore() {
    }

    @Override
    public void spawnEffectsGore() {
        for (int i = 0; i <= 60; ++i) {
            if (i % 5 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 0, 0, 127);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 1, -1, -1);
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDSPIDER_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDSPIDER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDSPIDER_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.INFECTEDSPIDER_STEP.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.level().random.nextInt(3) == 0) {
            this.setSkin(1);
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.DORPA_HEALTH * 0.5);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.DORPA_ATTACK_DAMAGE * 1.25);
            this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        }
        return floo;
    }

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.DORPA_RANGE.get(), 2.0f, 1.0f);
        LivingEntity entitylivingbase = this.getTarget();
        accelY = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 3.0f) - (1.0 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        return new EntityProjectileWebball(SRPEntities.WEBBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ);
    }

    @Override
    public void playProjSound() {
    }
}

