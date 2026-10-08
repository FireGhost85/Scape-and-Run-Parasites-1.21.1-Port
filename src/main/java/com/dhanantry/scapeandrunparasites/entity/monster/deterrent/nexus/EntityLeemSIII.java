package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPRooter;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSII;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.Arrays;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;

public class EntityLeemSIII
extends EntityPRooter
implements EntityBodyParts {
    private EntityBody head;

    public EntityLeemSIII(EntityType<? extends EntityLeemSIII> type, Level worldIn) {
        super(type, worldIn);
        this.buriedT = 2.6;
        this.noCulling = true;
        this.xpReward = SRPAttributes.XP_ADAPTED * 2;
        this.buried = 0.1;
        this.setParasiteStatus(3);
        this.damageCap = SRPConfig.nexussiCap;
        this.pointCap = SRPConfig.nexussiiiPointCap;
        this.pointReduction = SRPConfig.nexussiiiPointRed;
        this.chanceLearn = SRPConfig.nexussiiiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiiiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiiiPointDamCap;
        this.stage = (byte)3;
        this.totalP = SRPConfigMobs.leemsiiilimit;
        this.mobID = new int[SRPConfigMobs.leemsiiilimit];
        this.mobPT = new int[SRPConfigMobs.leemsiiilimit];
        this.leemRange = SRPConfigMobs.leemsiiiRange;
        this.leemRangeEffect = SRPConfigMobs.leemsiiiRangeEffect;
        this.leemBalls = SRPConfigMobs.leemsiiilimit;
        this.leemCooldownReset = SRPConfigMobs.leemsiiiCooldown;
        Arrays.fill(this.mobID, -777);
        this.neededTime = this.setGT(SRPConfig.nexusMinGrowTime, SRPConfig.nexusMaxGrowTime);
        this.head = new EntityBody(this, 3.2f, 2.5f, 1.0f, 0.0f, 5.25f, -1, 1, false, 0.2f);
        this.valueEvDeath = SRPConfig.nexussiiiLoosingEPValue;
        this.rangeB = 3;
    }

    @Override
    public int getParasiteIDRegister() {
        return 312;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new EntityAINexusGrow(this, 3, 3));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPRooter.createAttributes();
        
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
        this.head.tick();
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.getRandom().nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 80, 0);
        }
        return this.hurt(source, amount * 3.0f);
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
    }

    @Override
    public void setBodyPartDead(int id) {
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
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLeemSII(SRPEntities.ROOTER_SII.get(), this.level()), true, false);
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

