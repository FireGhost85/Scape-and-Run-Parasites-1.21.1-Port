package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class EntityPAncient
extends EntityPMalleable {
    public EntityPAncient(EntityType<? extends EntityPAncient> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, false, false, null, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, !SRPConfigSystems.useOneMind, !SRPConfigSystems.useOneMind, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
        }
        this.xpReward = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.ancientdespawn;
        this.damageCap = SRPConfig.ancientCap;
        this.canModRender = 1;
        this.killcount = -10.0;
        this.noCulling = true;
        this.pointCap = SRPConfig.ancientPointCap;
        this.pointReduction = SRPConfig.ancientPointRed;
        this.chanceLearn = SRPConfig.ancientChanceLe;
        this.chanceLearnFire = SRPConfig.ancientChanceLeFire;
        this.DamageTypeCap = SRPConfig.ancientPointDamCap;
        this.MiniDamage = SRPConfig.ancientMinDamage;
        this.regen = SRPConfig.ancientRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.ancientOneMindDeathV;
        this.valueEvDeath = SRPConfig.ancientLoosingEPValue;
        this.setScentHPMultiplier(0.25f);
    }

    @Override
    protected void fearPlayer(LivingEntity player) {
        try {
            if (player == null) {
                return;
            }
            if (!this.hasLineOfSight((Entity)player)) {
                return;
            }
            if (!player.hasEffect(SRPPotions.FEAR_E)) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 3, false, false));
            } else if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() < 3) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 3, false, false));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 100.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }
}

