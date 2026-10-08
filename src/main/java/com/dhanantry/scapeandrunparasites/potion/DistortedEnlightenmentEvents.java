package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class DistortedEnlightenmentEvents {
    private DistortedEnlightenmentEvents() {
    }

    /** LivingHurtEvent of 1.12 fired before armor and effect reductions, which is the position of LivingIncomingDamageEvent. */
    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim == null || victim.level().isClientSide) {
            return;
        }
        Entity trueSource = event.getSource().getEntity();
        if (!(trueSource instanceof LivingEntity attacker)) {
            return;
        }
        boolean victimHasEffect = victim.hasEffect(SRPPotions.DISTORTED_ENLIGHTENMENT_E);
        boolean attackerHasEffect = attacker.hasEffect(SRPPotions.DISTORTED_ENLIGHTENMENT_E);
        float amount = event.getAmount();
        if (victimHasEffect && isSRPParasite(attacker)) {
            amount *= isDraconite(attacker) ? 5.0f : 0.8f;
        }
        if (attackerHasEffect && isSRPParasite(victim)) {
            amount *= 0.8f;
        }
        event.setAmount(amount);
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        clearGlowing(event.getEntity(), event.getEffect().value());
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        clearGlowing(event.getEntity(), event.getEffectInstance().getEffect().value());
    }

    private static void clearGlowing(LivingEntity entity, Object effect) {
        if (entity.level().isClientSide || effect != SRPPotions.DISTORTED_ENLIGHTENMENT_E.get()) {
            return;
        }
        entity.setGlowingTag(false);
    }

    private static boolean isSRPParasite(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity instanceof EntityParasiteBase;
    }

    private static boolean isDraconite(Entity entity) {
        if (entity == null) {
            return false;
        }
        return entity instanceof EntityHeblu;
    }
}
