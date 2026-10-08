package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public final class ClientEvents {
    private ClientEvents() {}

    @SubscribeEvent
    static void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        SRPClientState.reset();
        SRPSaveData.falseLevel = 0;
    }
}
