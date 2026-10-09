package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import net.minecraft.client.Minecraft;

public final class BlackSkyClient {
    private BlackSkyClient() {
    }

    public static boolean isDarkDaysActive() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null) {
            return false;
        }
        var dimType = mc.level.dimensionType();
        if (!(dimType.natural() && dimType.hasSkyLight() && !dimType.hasCeiling())) {
            return false;
        }
        String dim = DimKeys.of(mc.level);
        return CelestialPhaseClient.getActiveIds(dim).contains("dark_days") || CelestialPhaseClient.getForcedIds(dim).contains("dark_days");
    }

    public static boolean isBlackSkyActive() {
        return BlackSkyClient.isDarkDaysActive();
    }
}
