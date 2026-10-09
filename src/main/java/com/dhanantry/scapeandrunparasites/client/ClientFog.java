package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** The parasite biome fog (FogDensity / FogColors of 1.12): the server sends the density and colour around the player. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientFog {
    private ClientFog() {}

    private static boolean blind() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player == null || mc.player.hasEffect(MobEffects.BLINDNESS);
    }

    @SubscribeEvent
    static void onRenderFog(ViewportEvent.RenderFog event) {
        if (SRPClientState.fog > 0.0f && !blind()) {
            // 1.12 used exponential fog with this density; the visibility there ends at about 3 / density blocks
            float far = Math.max(6.0f, 3.0f / SRPClientState.fog);
            if (far < event.getFarPlaneDistance()) {
                event.setNearPlaneDistance(0.0f);
                event.setFarPlaneDistance(far);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if ((double)SRPClientState.fog > (double)SRPConfigWorld.biomeFogDensity * 0.2 && !blind()) {
            event.setRed(SRPClientState.fogRed);
            event.setGreen(SRPClientState.fogGreen);
            event.setBlue(SRPClientState.fogBlue);
        }
    }
}
