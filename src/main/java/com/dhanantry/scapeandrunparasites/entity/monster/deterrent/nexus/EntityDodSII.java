package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIDodAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINexusGrow;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.Arrays;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;

public class EntityDodSII
extends EntityPDispatcher {
    public EntityDodSII(EntityType<? extends EntityDodSII> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.xpReward = SRPAttributes.XP_PRIMITIVE * 2;
        this.buried = 0.1;
        this.setParasiteStatus(3);
        this.damageCap = SRPConfig.nexussiiCap;
        this.pointCap = SRPConfig.nexussiiPointCap;
        this.pointReduction = SRPConfig.nexussiiPointRed;
        this.chanceLearn = SRPConfig.nexussiiChanceLe;
        this.chanceLearnFire = SRPConfig.nexussiiChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussiiPointDamCap;
        this.totalP = SRPConfigMobs.dodsiiTotalActiveMobs;
        this.mobID = new int[5];
        this.mobPT = new int[5];
        this.stage = (byte)2;
        Arrays.fill(this.mobID, -777);
        this.neededTime = this.setGT(SRPConfig.nexussiiMinGrowTime, SRPConfig.nexussiiMaxGrowTime);
        this.valueEvDeath = SRPConfig.nexussiiLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 77;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(4, new EntityAINexusGrow(this, 2, 2));
        this.goalSelector.addGoal(2, new EntityAIDodAttack(this, 2, 32, 15.0f));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPDispatcher.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.DODSII_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.DODSII_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.DODSII_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.nexussiiFollow * (double)SRPConfigMobs.dodsiiFollowRangeMult);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.4f;
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.DODSII_ATTACK_DAMAGE;
    }

    @Override
    public boolean storeParasite(EntityParasiteBase in) {
        if (super.storeParasite(in)) {
            return true;
        }
        if (this.storeLodo(in, true)) {
            return true;
        }
        if (this.storeInf(in, true)) {
            return true;
        }
        if (this.storeCrude(in, true)) {
            return true;
        }
        if (this.storeMudo(in, false)) {
            return true;
        }
        if (this.storeMangler(in, false)) {
            return true;
        }
        this.storeAll(in);
        return false;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityDod(SRPEntities.DISPATCHER_SI.get(), this.level()), true, false);
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

