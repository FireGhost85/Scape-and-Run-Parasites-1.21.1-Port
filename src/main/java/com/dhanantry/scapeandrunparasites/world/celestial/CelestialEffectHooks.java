package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialEffectRegistry;
import com.dhanantry.scapeandrunparasites.world.celestial.ICelestialEventEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;

public class CelestialEffectHooks {
    private static boolean isNight(Level world) {
        long dayTime = world.getWorldTime() % 24000L;
        return dayTime >= 13000L && dayTime <= 23000L;
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        Level world = e.getWorld();
        if (world == null || world.isClientSide) {
            return;
        }
        if (!CelestialEffectHooks.isNight(world)) {
            return;
        }
        Entity ent = e.getEntity();
        if (!(ent instanceof EntityParasiteBase)) {
            return;
        }
        EntityParasiteBase parasite = (EntityParasiteBase)ent;
        for (String id : CelestialEffectRegistry.getActiveIds(world)) {
            ICelestialEventEffect fx = CelestialEffectRegistry.get(id);
            if (fx == null) continue;
            fx.onParasiteSpawn(parasite, null);
        }
    }
}

