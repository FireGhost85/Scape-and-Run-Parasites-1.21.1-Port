package com.dhanantry.scapeandrunparasites.api;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.entity.player.Player;

public final class SRPBestiaryApi {
    private SRPBestiaryApi() {
    }

    public static boolean hasSeenCelestial(Player player, String celestialId) {
        if (player == null || celestialId == null || celestialId.isEmpty()) {
            return false;
        }
        IBestiaryProgress progress = BestiaryCapability.get(player);
        return progress != null && progress.hasSeenCelestial(celestialId);
    }

    public static Set<String> getSeenCelestials(Player player) {
        if (player == null) {
            return Collections.emptySet();
        }
        IBestiaryProgress progress = BestiaryCapability.get(player);
        if (progress == null || progress.getSeenCelestials() == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<String>(progress.getSeenCelestials()));
    }

    public static int getSeenCelestialCount(Player player) {
        return SRPBestiaryApi.getSeenCelestials(player).size();
    }
}

