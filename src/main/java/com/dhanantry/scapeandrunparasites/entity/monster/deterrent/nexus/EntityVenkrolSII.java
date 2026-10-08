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
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
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

public class EntityVenkrolSII
extends EntityPBeckon {
    public EntityAIVenkrolSummon summonV = new EntityAIVenkrolSummon(this, SRPConfigMobs.venkrolsiilimit, 20 * SRPConfigMobs.venkrolsiiCooldown, 2, SRPConfigMobs.venkrolsiiCAMinimumV, SRPConfigMobs.venkrolsiiCAExtraM);

    public EntityVenkrolSII(EntityType<? extends EntityVenkrolSII> type, Level worldIn) {
        super(type, worldIn);
        this.buriedT = 4.4;
        this.totalP = SRPConfigMobs.venkrolsiiTotalActiveMobs;
        this.mobID = new int[this.totalP + SRPConfigMobs.venkrolsiilimit];
        this.mobPT = new int[this.totalP + SRPConfigMobs.venkrolsiilimit];
        this.stage = (byte)2;
        this.xpReward = SRPAttributes.XP_PRIMITIVE * 2;
        if (SRPAttributes.rsBlockI) {
            this.goalSelector.addGoal(3, new EntityAIBlockInfest(this, 2));
        }
        Arrays.fill(this.mobID, -777);
        this.setBODY(1.0f);
        this.goalSelector.addGoal(2, this.summonV);
        this.damageCap = SRPConfig.nexussiiCap;
        this.pointCap = SRPConfig.nexussiiPointCap;
        this.pointReduction = SRPConfig.nexussiiPointRed;
        this.chanceLearn = SRPConfig.nexussiiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiiPointDamCap;
        this.neededTime = this.setGT(SRPConfig.nexussiiMinGrowTime, SRPConfig.nexussiiMaxGrowTime);
        this.valueEvDeath = SRPConfig.nexussiiLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 18;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new EntityAINexusGrow(this, 2));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPBeckon.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VENKROLSII_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VENKROLSII_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VENKROLSII_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.nexussiiFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && this.topParticles > 0 && this.tickCount % 5 == 0) {
            for (int i = 0; i <= 2; ++i) {
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
        return 2.7f;
    }

    public void setBODY(float in) {
        if ((in += this.getBODY()) > 0.5f) {
            in = 0.5f;
        }
        if (in < 0.0f) {
            in = 0.0f;
        }
        this.body = in;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.VENKROLSII_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.VENKROLSII_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.VENKROLSII_DEATH.get();
    }

    protected float getSoundVolume() {
        return 1.0f;
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROLSII_ATTACK_DAMAGE;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityVenkrol(SRPEntities.BECKON_SI.get(), this.level()), true, false);
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
        ParasiteEventWorld.setDisloWorldPhase(this.level(), SRPAttributes.EVENTPARANEXUSIID, SRPConfigSystems.chanceEventParaNexusIID, 0, null);
        return super.onDeathDislo(cause);
    }
}

