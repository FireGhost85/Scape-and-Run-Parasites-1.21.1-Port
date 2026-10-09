package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** The "breathe" screen warp (shaders/post/notch_tweaked.json): alveolar fluid, dead blood, Distorted Enlightenment and /srp_breathe. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class BreatheShaderManager {
    private static final SRPPostEffect EFFECT = new SRPPostEffect("notch_tweaked");
    private static boolean wantEnabled = false;
    private static int ticksRemaining = 0;

    private BreatheShaderManager() {
    }

    public static void enable() {
        wantEnabled = true;
        if (ticksRemaining <= 0) {
            EFFECT.timeSeconds();
        }
    }

    public static void enableFor(int ticks) {
        if (ticks <= 0) {
            return;
        }
        wantEnabled = true;
        if (ticks > ticksRemaining) {
            ticksRemaining = ticks;
        }
    }

    public static void disable() {
        wantEnabled = false;
        ticksRemaining = 0;
    }

    public static boolean isWanted() {
        return wantEnabled;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (wantEnabled && ticksRemaining > 0) {
            --ticksRemaining;
            if (ticksRemaining <= 0) {
                wantEnabled = false;
            }
        }
        if (!wantEnabled && !EFFECT.isApplied()) {
            return;
        }
        EFFECT.update(wantEnabled);
        if (wantEnabled && EFFECT.isApplied()) {
            EFFECT.setUniform("SRP_Time", EFFECT.timeSeconds());
            EFFECT.setUniform("TintStrength", 0.1f);
        }
    }
}
