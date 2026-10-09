package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** Dark Days on the client: black fog colour, music silenced and the rumble loop. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class BlackSkyClientEvents {
    private static DarkDaysRumbleSound rumbleSound = null;

    private BlackSkyClientEvents() {
    }

    @SubscribeEvent
    public static void onFogColors(ViewportEvent.ComputeFogColor e) {
        if (!SRPConfigWorld.enableCelestialObjects) {
            return;
        }
        if (!BlackSkyClient.isDarkDaysActive()) {
            return;
        }
        e.setRed(0.0f);
        e.setGreen(0.0f);
        e.setBlue(0.0f);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null) {
            return;
        }
        boolean active = mc.level != null && mc.player != null && SRPConfigWorld.enableCelestialObjects && BlackSkyClient.isDarkDaysActive();
        if (active) {
            if (rumbleSound == null || !mc.getSoundManager().isActive(rumbleSound)) {
                rumbleSound = new DarkDaysRumbleSound();
                mc.getSoundManager().play(rumbleSound);
            }
            mc.getMusicManager().stopPlaying();
            return;
        }
        rumbleSound = null;
    }
}
