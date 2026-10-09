package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** The breathe shader while the player has Distorted Enlightenment. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class DistortedEnlightenmentShaderEvents {
    private static boolean wasActive = false;

    private DistortedEnlightenmentShaderEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.player == null) {
            if (wasActive) {
                BreatheShaderManager.disable();
                wasActive = false;
            }
            return;
        }
        boolean active = mc.player.hasEffect(SRPPotions.DISTORTED_ENLIGHTENMENT_E);
        if (active) {
            BreatheShaderManager.enableFor(8);
            wasActive = true;
        } else if (wasActive) {
            BreatheShaderManager.disable();
            wasActive = false;
        }
    }
}
