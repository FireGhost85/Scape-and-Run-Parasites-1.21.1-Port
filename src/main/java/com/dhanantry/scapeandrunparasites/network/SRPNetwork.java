package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.ClientPayloadHandlers;
import com.dhanantry.scapeandrunparasites.network.registration.BlocksPayloads;
import com.dhanantry.scapeandrunparasites.network.registration.EffectsPayloads;
import com.dhanantry.scapeandrunparasites.network.registration.EntityPayloads;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Payload registration (replaces SimpleNetworkWrapper of 1.12.2). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class SRPNetwork {
    private static final String VERSION = "1";

    private SRPNetwork() {}

    @SubscribeEvent
    static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToClient(MovingSoundPayload.TYPE, MovingSoundPayload.CODEC, ClientPayloadHandlers::movingSound);
        registrar.playToClient(EvoPhaseCancelPayload.TYPE, EvoPhaseCancelPayload.CODEC, ClientPayloadHandlers::evoPhaseCancel);
        registrar.playToClient(UpdateEvoPhasePayload.TYPE, UpdateEvoPhasePayload.CODEC, ClientPayloadHandlers::updateEvoPhase);
        EffectsPayloads.register(registrar);
        BlocksPayloads.register(registrar);
        EntityPayloads.register(registrar);
    }
}
