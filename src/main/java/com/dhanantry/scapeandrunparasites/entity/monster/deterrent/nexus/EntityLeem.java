package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPRooter;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.Arrays;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;

public class EntityLeem
extends EntityPRooter {
    public EntityLeem(EntityType<? extends EntityLeem> type, Level worldIn) {
        super(type, worldIn);
        this.buriedT = 2.6;
        this.xpReward = SRPAttributes.XP_INFECTED * 2;
        this.buried = 0.1;
        this.setParasiteStatus(3);
        this.damageCap = SRPConfig.nexussiCap;
        this.pointCap = SRPConfig.nexussiPointCap;
        this.pointReduction = SRPConfig.nexussiPointRed;
        this.chanceLearn = SRPConfig.nexussiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiPointDamCap;
        this.stage = 1;
        this.totalP = SRPConfigMobs.leemlimit;
        this.mobID = new int[SRPConfigMobs.leemlimit];
        this.mobPT = new int[SRPConfigMobs.leemlimit];
        this.leemRange = SRPConfigMobs.leemRange;
        this.leemRangeEffect = SRPConfigMobs.leemRangeEffect;
        this.leemBalls = SRPConfigMobs.leemlimit;
        this.leemCooldownReset = SRPConfigMobs.leemCooldown;
        Arrays.fill(this.mobID, -777);
        this.neededTime = this.setGT(SRPConfig.nexusMinGrowTime, SRPConfig.nexusMaxGrowTime);
        this.valueEvDeath = SRPConfig.nexussiLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 310;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new EntityAINexusGrow(this, 1, 3));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPRooter.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VENKROL_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VENKROL_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VENKROL_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.nexussiFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.4f;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROL_ATTACK_DAMAGE;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        ParasiteEventWorld.setDisloWorldPhase(this.level(), SRPAttributes.EVENTPARANEXUSID, SRPConfigSystems.chanceEventParaNexusID, 0, null);
        return super.onDeathDislo(cause);
    }
}

