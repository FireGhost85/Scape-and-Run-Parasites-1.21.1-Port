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
        r.playToClient(ParticlePayload.TYPE, ParticlePayload.CODEC, EffectsClientHandlers::particle);
        r.playToClient(PureParticlesPayload.TYPE, PureParticlesPayload.CODEC, EffectsClientHandlers::pureParticles);
        r.playToClient(VengeanceFxPayload.TYPE, VengeanceFxPayload.CODEC, EffectsClientHandlers::vengeanceFx);
        r.playToClient(ExtremeSnowPayload.TYPE, ExtremeSnowPayload.CODEC, EffectsClientHandlers::extremeSnow);
    }
}
