package com.dhanantry.scapeandrunparasites.client.fog;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.ClientHooks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** SRPFogManager + SRPFogHandler of 1.10.9: a grey, close fog switched by /srp_breathe. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class SRPFogManager {
    private static boolean enabled = false;

    private SRPFogManager() {
    }

    public static void enable() {
        enabled = true;
    }

    public static void disable() {
        enabled = false;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    @SubscribeEvent
    public static void onFogColors(ViewportEvent.ComputeFogColor e) {
        if (!enabled) {
            return;
        }
        float r = e.getRed();
        float g = e.getGreen();
        float b = e.getBlue();
        float gray = (r + g + b) / 3.0f;
        r = r * 0.75f + gray * 0.25f;
        g = g * 0.75f + gray * 0.25f;
        b = b * 0.75f + gray * 0.25f;
        e.setRed(r * 0.9f);
        e.setGreen(g * 0.9f);
        e.setBlue(b * 0.9f);
    }

    @SubscribeEvent
    public static void onFogRender(ViewportEvent.RenderFog e) {
        if (!enabled) {
            return;
        }
        e.setNearPlaneDistance(12.0f);
        e.setFarPlaneDistance(64.0f);
        e.setCanceled(true);
    }
}
