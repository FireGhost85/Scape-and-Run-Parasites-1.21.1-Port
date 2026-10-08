package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.fx.ClientSRPParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class EffectsClientEvents {
    private EffectsClientEvents() {
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post e) {
        ClientSRPParticles.resetTick();
    }
}
