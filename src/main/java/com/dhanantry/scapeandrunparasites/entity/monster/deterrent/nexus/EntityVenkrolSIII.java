package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockInfest;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIVenkrolSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
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

public class EntityVenkrolSIII
extends EntityPBeckon {
    public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsiiilimit, 20 * SRPConfigMobs.venkrolsiiiCooldown, 3, SRPConfigMobs.venkrolsiiiCAMinimumV, SRPConfigMobs.venkrolsiiiCAExtraM);

    public EntityVenkrolSIII(EntityType<? extends EntityVenkrolSIII> type, Level worldIn) {
        super(type, worldIn);
        this.buriedT = 5.8;
        this.totalP = SRPConfigMobs.venkrolsiiiTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsiiilimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsiiilimit];
        this.stage = (byte)3;
        this.xpReward = SRPAttributes.XP_ADAPTED * 2;
        if (SRPAttributes.rsBlockI) {
            this.goalSelector.addGoal(3, new EntityAIBlockInfest(this, 3));
        }
        Arrays.fill(this.mobID, -777);
        this.setBODY(1.0f);
        this.goalSelector.addGoal(2, this.summonV);
        this.damageCap = SRPConfig.nexussiiiCap;
        this.pointCap = SRPConfig.nexussiiiPointCap;
        this.pointReduction = SRPConfig.nexussiiiPointRed;
        this.chanceLearn = SRPConfig.nexussiiiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiiiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiiiPointDamCap;
        this.neededTime = this.setGT(SRPConfig.nexussiiiMinGrowTime, SRPConfig.nexussiiiMaxGrowTime);
        this.valueEvDeath = SRPConfig.nexussiiiLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 19;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new EntityAINexusGrow(this, 3));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPBeckon.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VENKROLSIII_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VENKROLSIII_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VENKROLSIII_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.nexussiiiFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.topParticles > 0 && this.tickCount % 3 == 0) {
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
        return 4.9f;
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

    protected SoundEvent getAmbientSound() {
        return SRPSounds.VENKROLSIII_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.VENKROLSIII_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.VENKROLSIII_DEATH.get();
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROLSIII_ATTACK_DAMAGE;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityVenkrolSII(SRPEntities.BECKON_SII.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        ParasiteEventWorld.setDisloWorldPhase(this.level(), SRPAttributes.EVENTPARANEXUSIIID, SRPConfigSystems.chanceEventParaNexusIIID, 0, null);
        return super.onDeathDislo(cause);
    }
}

