package com.dhanantry.scapeandrunparasites.bestiary;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/** Damage dealt to and taken from parasites and deaths by parasites, for the stats page of the bestiary. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class BestiaryCombatStatsHandler {
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        float amount = event.getNewDamage();
        if (target.level().isClientSide || amount <= 0.0f) {
            return;
        }
        Entity trueAttacker = getTrueAttacker(event.getSource());
        if (isParasite(target) && trueAttacker instanceof Player player) {
            BestiaryCapability.get(player).addDamageToParasites(amount);
            return;
        }
        if (target instanceof Player player && isParasite(trueAttacker)) {
            BestiaryCapability.get(player).addDamageFromParasites(amount);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide) {
            return;
        }
        if (isParasite(getTrueAttacker(event.getSource()))) {
            IBestiaryProgress prog = BestiaryCapability.get(player);
            prog.addDeathsByParasites(1);
        }
    }

    private static Entity getTrueAttacker(DamageSource source) {
        if (source == null) {
            return null;
        }
        Entity trueSource = source.getEntity();
        return trueSource != null ? trueSource : source.getDirectEntity();
    }

    private static boolean isParasite(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof EntityParasiteBase) {
            return true;
        }
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return key != null && ScapeAndRunParasites.MODID.equals(key.getNamespace());
    }
}
