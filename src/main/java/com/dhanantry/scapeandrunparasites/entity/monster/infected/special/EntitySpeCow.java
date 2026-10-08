package com.dhanantry.scapeandrunparasites.entity.monster.infected.special;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAssimara;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
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
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntitySpeCow
extends EntityPAssimara
implements RangedAttackMob {
    int vomit;
    private int lastSkin = -1;

    public EntitySpeCow(EntityType<? extends EntitySpeCow> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 1;
        this.type = (byte)11;
        this.fuseTime = 40;
    }

    @Override
    public int getIDSpawn() {
        return 13;
    }

    @Override
    public int getParasiteIDRegister() {
        return 322;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infcowCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 3.0f));
        this.goalSelector.addGoal(2, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.0, 200, 7.0f, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAssimara.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.MARCOW_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.MARCOW_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, (double)0.2f);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.MARCOW_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.MARCOW_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.assimaraFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            int skin = this.getSkin();
            if (skin != this.lastSkin) {
                if (skin == 1) {
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.MARCOW_HEALTH * 0.75);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.26000000298023224);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.MARCOW_ATTACK_DAMAGE * 1.25);
                    if (this.getHealth() > this.getMaxHealth()) {
                        this.setHealth(this.getMaxHealth());
                    }
                } else {
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.MARCOW_HEALTH);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)0.2f);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.MARCOW_ATTACK_DAMAGE);
                    if (this.hasEffect(SRPPotions.RAGE_E)) {
                        this.removeEffect(SRPPotions.RAGE_E);
                    }
                }
                this.lastSkin = skin;
            }
            if (!(skin != 1 || this.hasEffect(SRPPotions.RAGE_E) && this.getEffect(SRPPotions.RAGE_E).getDuration() >= 40)) {
                this.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 400, 0, false, false));
            }
        }
        if (this.level().isClientSide && this.vomit > 0) {
            --this.vomit;
            for (int i = 0; i < 6; ++i) {
                Vec3 vec3d = this.getViewVector(1.0f);
                double bon = 1.2;
                double offsetX = this.getX() + vec3d.x * bon;
                double offsetY = this.getY() + (double)this.getEyeHeight() - 0.2;
                double offsetZ = this.getZ() + vec3d.z * bon;
                double motionX = (double)(-Mth.sin((float)(this.getYRot() * (float)Math.PI / 180.0f))) * 0.2;
                double motionZ = (double)Mth.cos((float)(this.getYRot() * (float)Math.PI / 180.0f)) * 0.2;
                double motionY = 0.01 + this.getRandom().nextDouble() * 0.1;
                double spreadFactor = 0.25;
                this.spawnParticles(SRPEnumParticle.GCLOUD, 123, 0, 196, offsetX, offsetY, offsetZ, motionX += (this.getRandom().nextDouble() - 0.5) * spreadFactor, motionY, motionZ += (this.getRandom().nextDouble() - 0.5) * spreadFactor);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        livingdata = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (!this.level().isClientSide) {
            boolean desert = this.level().getBiome(this.blockPosition()).is(Biomes.DESERT);
            double chance = desert ? 0.8 : 0.01;
            this.canChangeVariant = true;
            if (this.getRandom().nextDouble() < chance) {
                this.setSkin(1);
            } else {
                this.setSkin(0);
            }
            this.canChangeVariant = false;
        }
        return livingdata;
    }

    @Override
    public int getMaxManualVariants() {
        return 2;
    }

    @Override
    protected void selfExplode() {
        super.selfExplode();
        ParasiteSummon.spawnM(this, new String[]{SRPConfigMobs.infcowmob}, 0, false, SRPEntityUtil.getCustomNameTag(this));
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.3f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDCOW_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDCOW_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDCOW_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.COW_STEP, 0.15f, 1.0f);
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        this.level().broadcastEntityEvent((Entity)this, (byte)100);
        Vec3 vec3d = this.getViewVector(1.0f);
        double bon = 4.5;
        AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(this.level(), this.getX() + vec3d.x * bon, this.getY(), this.getZ() + vec3d.z * bon);
        entityareaeffectcloud.setRadius(3.0f);
        entityareaeffectcloud.setDuration(100);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VOMIT_E, 300, 0, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 300, 20, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 20, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 20, false, true));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, 300, 20, false, true));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
        this.lookAt((Entity)target);
        this.setWait(60);
    }

    public void setAggressive(boolean swingingArms) {
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 100) {
            this.vomit = 40;
        } else {
            super.handleEntityEvent(id);
        }
    }
}

