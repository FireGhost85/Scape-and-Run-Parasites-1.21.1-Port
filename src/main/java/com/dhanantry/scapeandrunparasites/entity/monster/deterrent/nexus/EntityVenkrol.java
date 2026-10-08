package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockInfest;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityVenkrol
extends EntityPBeckon {
    public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrollimit, 20 * SRPConfigMobs.venkrolCooldown, 1, SRPConfigMobs.venkrolCAMinimumV, SRPConfigMobs.venkrolCAExtraM);

    public EntityVenkrol(EntityType<? extends EntityVenkrol> type, Level worldIn) {
        super(type, worldIn);
        this.buriedT = 2.6;
        this.totalP = SRPConfigMobs.venkrolTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.venkrollimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.venkrollimit];
        this.stage = 1;
        this.xpReward = SRPAttributes.XP_INFECTED * 2;
        if (SRPAttributes.rsBlockI) {
            this.goalSelector.addGoal(3, new EntityAIBlockInfest(this, 1));
        }
        Arrays.fill(this.mobID, -777);
        this.setBODY(1.0f);
        this.goalSelector.addGoal(2, this.summonV);
        this.damageCap = SRPConfig.nexussiCap;
        this.pointCap = SRPConfig.nexussiPointCap;
        this.pointReduction = SRPConfig.nexussiPointRed;
        this.chanceLearn = SRPConfig.nexussiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiPointDamCap;
        this.neededTime = this.setGT(SRPConfig.nexusMinGrowTime, SRPConfig.nexusMaxGrowTime);
        this.valueEvDeath = SRPConfig.nexussiLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 16;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new EntityAINexusGrow(this, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPBeckon.createAttributes();
        
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
        if (this.level().isClientSide && this.topParticles > 0 && this.tickCount % 10 == 0) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticlesTop(SRPEnumParticle.BIOMASS, 0, 0, 0);
            }
        }
        if (this.getParasiteStatus() == 0) {
            this.setBODY(0.04f);
        } else {
            this.setBODY(-0.04f);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.4f;
    }

    public void setBODY(float in) {
        if ((in += this.getBODY()) > 0.6f) {
            in = 0.6f;
        }
        if (in < 0.0f) {
            in = 0.0f;
        }
        this.body = in;
    }

    @Override
    public boolean getCanSpawnHere() {
        BlockState iblockstate = this.level().getBlockState(this.blockPosition().below());
        return this.getWalkTargetValue(BlockPos.containing(this.getX(), this.getBoundingBox().minY, this.getZ())) >= 0.0f && true && this.level().getDifficulty() != Difficulty.PEACEFUL;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.VENKROLSI_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.VENKROLSI_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.VENKROLSI_DEATH.get();
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

