package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSpawn;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class EntityPHijacked
extends EntityParasiteBase
implements EntityCanSpawn {
    public EntityPHijacked(EntityType<? extends EntityPHijacked> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.hijackedSneakPen, SRPConfig.hijackedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.hijackedSneakPen, SRPConfig.hijackedInviPen));
        }
        this.xpReward = SRPAttributes.XP_HIJACKED;
        this.damageCap = SRPConfig.hijackedCap;
        this.canD = SRPConfig.hijackeddespawn;
        this.MiniDamage = SRPConfig.hijackedMinDamage;
        this.oneMindDeathValue = SRPConfig.hijackedOneMindDeathV;
        this.foodSteal = 0.1f;
        this.canModRender = 0;
        this.valueEvDeath = SRPConfig.hijackedLoosingEPValue;
        this.cothSpread = SRPConfigSystems.cothHijacked;
        this.setScentHPMultiplier(1.75f);
    }

    @Override
    public int getIDSpawn() {
        return this.getParasiteIDRegister();
    }

    @Override
    public void aiStep() {
        super.aiStep();
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
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 0, false, false));
            } else if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() < 0) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 0, false, false));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        return super.hurt(source, amount);
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
    }

    @Override
    protected void spawnGore() {
    }

    @Override
    public void spawnEffectsGore() {
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }
}

