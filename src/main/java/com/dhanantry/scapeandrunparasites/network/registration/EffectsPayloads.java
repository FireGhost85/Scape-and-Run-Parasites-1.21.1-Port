package com.dhanantry.scapeandrunparasites.network.registration;

import com.dhanantry.scapeandrunparasites.client.EffectsClientHandlers;
import com.dhanantry.scapeandrunparasites.network.ExtremeSnowPayload;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.network.PureParticlesPayload;
import com.dhanantry.scapeandrunparasites.network.VengeanceFxPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Payloads of the effects and particles: call from the RegisterPayloadHandlersEvent handler with the shared registrar. */
public final class EffectsPayloads {
    private EffectsPayloads() {
    }

    public static void register(PayloadRegistrar r) {
        r.playToClient(ParticlePayload.TYPE, ParticlePayload.CODEC, (msg, ctx) -> EffectsClientHandlers.particle(msg, ctx));
        r.playToClient(PureParticlesPayload.TYPE, PureParticlesPayload.CODEC, (msg, ctx) -> EffectsClientHandlers.pureParticles(msg, ctx));
        r.playToClient(VengeanceFxPayload.TYPE, VengeanceFxPayload.CODEC, (msg, ctx) -> EffectsClientHandlers.vengeanceFx(msg, ctx));
        r.playToClient(ExtremeSnowPayload.TYPE, ExtremeSnowPayload.CODEC, (msg, ctx) -> EffectsClientHandlers.extremeSnow(msg, ctx));
    }
}
