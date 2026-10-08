package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class EntityPDerived
extends EntityPCosmical {
    public EntityPDerived(EntityType<? extends EntityPDerived> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.derivedWalls, false, null, SRPConfig.derivedSneakPen, SRPConfig.derivedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.derivedWalls, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.derivedSneakPen, SRPConfig.derivedInviPen));
        }
        this.xpReward = SRPAttributes.XP_DERIVED;
        this.canD = SRPConfig.deriveddespawn;
        this.damageCap = SRPConfig.derivedCap;
        this.canModRender = 1;
        this.type = (byte)71;
        this.fuseOrb = 13;
        this.orbStartTimer = 15;
        this.foodSteal = SRPConfig.derivedFoodSteal;
        this.orbItemCool = SRPConfig.derivedItemOrbCooldown * 20;
        this.pointCap = SRPConfig.derivedPointCap;
        this.pointReduction = SRPConfig.derivedPointRed;
        this.chanceLearn = SRPConfig.derivedChanceLe;
        this.chanceLearnFire = SRPConfig.derivedChanceLeFire;
        this.DamageTypeCap = SRPConfig.derivedPointDamCap;
        this.MiniDamage = SRPConfig.derivedMinDamage;
        this.regen = SRPConfig.derivedRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.derivedOneMindDeathV;
        this.regenEff = 10;
        this.foodRott = SRPConfig.derivedFoodChance;
        this.foodRootNumber = SRPConfig.derivedFoodAmount;
        this.hackHeal = SRPConfig.derivedHackHealing;
        this.hackEffects = SRPConfig.derivedHackingEffects;
        this.valueEvDeath = SRPConfig.derivedLoosingEPValue;
        this.setScentHPMultiplier(1.5f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return super.causeFallDamage(distance, 0.0f, damageSource);
    }
}

