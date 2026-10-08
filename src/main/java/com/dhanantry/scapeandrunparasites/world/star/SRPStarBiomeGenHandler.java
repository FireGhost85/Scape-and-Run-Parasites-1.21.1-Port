package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPDynamicStar;
import net.neoforged.bus.api.SubscribeEvent;

public class SRPStarBiomeGenHandler {
    @SubscribeEvent
    public void onInitBiomeGens(WorldTypeEvent.InitBiomeGens event) {
        GenLayer[] original = event.getOriginalBiomeGens();
        GenLayer[] replaced = new GenLayer[original.length];
        for (int i = 0; i < original.length; ++i) {
            if (original[i] == null) continue;
            replaced[i] = new GenLayerSRPDynamicStar(7231L + (long)i, original[i]);
            replaced[i].initWorldGenSeed(event.getSeed());
        }
        event.setNewBiomeGens(replaced);
    }
}

