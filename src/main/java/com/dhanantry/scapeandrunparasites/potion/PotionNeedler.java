package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class PotionNeedler extends SRPEffectBase {
    public PotionNeedler(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) {
            return true;
        }
        this.effectNeedler(entity, amplifier);
        return true;
    }

    private void effectNeedler(LivingEntity entity, int amplifier) {
        if (amplifier >= SRPConfigSystems.needlerTerminal) {
            String mobName;
            amplifier -= SRPConfigSystems.needlerTerminal;
            entity.removeEffect(SRPPotions.DLER_E);
            try {
                mobName = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
            } catch (Exception e) {
                ScapeAndRunParasites.LOGGER.error("Problem with needler and an entity", e);
                return;
            }
            if (ParasiteEventEntity.checkName(mobName, SRPConfigSystems.needlerImmuneList, SRPConfigSystems.needlerImmuneListWhite)) {
                return;
            }
            entity.addEffect(SRPPotions.effect(SRPPotions.DLER_E, 400, amplifier));
            float f1 = entity.getHealth();
            if (f1 <= 0.0f) {
                return;
            }
            float damageE = entity.getMaxHealth() * SRPConfigSystems.needlerDamage;
            damageE = entity instanceof Player ? Math.min(damageE, SRPConfigSystems.needlerMaxDamPlayer) : Math.min(damageE, SRPConfigSystems.needlerMaxDamMonster);
            entity.setHealth(f1 - damageE);
            entity.level().broadcastEntityEvent(entity, (byte) 2);
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), 0.0f, Level.ExplosionInteraction.NONE);
            if (entity.getHealth() <= 0.0f) {
                ItemStack itemstack = null;
                for (InteractionHand hand : InteractionHand.values()) {
                    ItemStack itemstack1 = entity.getItemInHand(hand);
                    if (itemstack1.getItem() != Items.TOTEM_OF_UNDYING) continue;
                    itemstack = itemstack1.copy();
                    itemstack1.shrink(1);
                    break;
                }
                if (itemstack != null) {
                    if (entity instanceof ServerPlayer serverPlayer) {
                        serverPlayer.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
                        CriteriaTriggers.USED_TOTEM.trigger(serverPlayer, itemstack);
                    }
                    entity.setHealth(1.0f);
                    entity.removeAllEffects();
                    entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
                    entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                    entity.level().broadcastEntityEvent(entity, (byte) 35);
                } else {
                    entity.die(entity.level().damageSources().magic());
                }
            }
        }
    }
}
