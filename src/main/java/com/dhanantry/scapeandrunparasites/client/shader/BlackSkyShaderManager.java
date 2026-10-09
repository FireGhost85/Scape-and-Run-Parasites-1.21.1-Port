package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.celestial.BlackSkyClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** The darkness / vignette screen effect of Dark Days (shaders/post/black_sky_darkness.json). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class BlackSkyShaderManager {
    private static final SRPPostEffect EFFECT = new SRPPostEffect("black_sky_darkness");

    private BlackSkyShaderManager() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        boolean wanted = BlackSkyClient.isBlackSkyActive();
        if (!wanted && !EFFECT.isApplied()) {
            return;
        }
        EFFECT.update(wanted);
        if (wanted && EFFECT.isApplied()) {
            EFFECT.setUniform("SRP_Time", EFFECT.timeSeconds());
            EFFECT.setUniform("Darkness", 0.85f);
        }
    }
}
