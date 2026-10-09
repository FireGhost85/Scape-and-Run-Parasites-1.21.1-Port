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
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPNetwork {
    private static final String VERSION = "1";

    private SRPNetwork() {}

    @SubscribeEvent
    static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToClient(MovingSoundPayload.TYPE, MovingSoundPayload.CODEC, (msg, ctx) -> ClientPayloadHandlers.movingSound(msg, ctx));
        registrar.playToClient(EvoPhaseCancelPayload.TYPE, EvoPhaseCancelPayload.CODEC, (msg, ctx) -> ClientPayloadHandlers.evoPhaseCancel(msg, ctx));
        registrar.playToClient(UpdateEvoPhasePayload.TYPE, UpdateEvoPhasePayload.CODEC, (msg, ctx) -> ClientPayloadHandlers.updateEvoPhase(msg, ctx));
        registrar.playToClient(EscapeOfferPayload.TYPE, EscapeOfferPayload.CODEC, (msg, ctx) -> ClientPayloadHandlers.escapeOffer(msg, ctx));
        registrar.playToServer(RequestEscapePayload.TYPE, RequestEscapePayload.CODEC, (msg, ctx) -> com.dhanantry.scapeandrunparasites.feature.EscapeOnDeathHandler.requestEscape(msg, ctx));
        registrar.playToServer(RequestEvoPhasePayload.TYPE, RequestEvoPhasePayload.CODEC, (msg, ctx) -> RequestEvoPhasePayload.handle(msg, ctx));
        EffectsPayloads.register(registrar);
        BlocksPayloads.register(registrar);
        EntityPayloads.register(registrar);
    }
}
