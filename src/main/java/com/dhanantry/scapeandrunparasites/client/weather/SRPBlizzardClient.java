package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.client.world.SRPClientStarWorldState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

/** The blizzard of a cold star world: the rain of the overworld is drawn as a blizzard (SRPBlizzardClient of 1.10.9). */
public final class SRPBlizzardClient {
    private SRPBlizzardClient() {
    }

    public static boolean isColdWorld() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.level.dimension() != Level.OVERWORLD) {
            return false;
        }
        return SRPClientStarWorldState.isCold();
    }

    public static float getIntensity(float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (!isColdWorld()) {
            return 0.0f;
        }
        return Mth.clamp(mc.level.getRainLevel(partialTicks), 0.0f, 1.0f);
    }
}
