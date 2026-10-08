package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.network.MovingSoundPayload;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

/** Entity/world event helpers of 1.10.9 (ParasiteEventEntity). Ported incrementally; see PORTING_NOTES for open items. */
public final class ParasiteEventEntity {
    private ParasiteEventEntity() {}

    /** Sound cue to every player in the world's dimension, optional chat message to its players. */
    public static void alertAllPlayerDim(@Nullable Level level, String message, int warning) {
        if (!(level instanceof ServerLevel server)) return;
        PacketDistributor.sendToPlayersInDimension(server, new MovingSoundPayload(warning, 1.0f));
        if (!message.isEmpty()) {
            Component text = Component.literal(message);
            for (ServerPlayer player : server.players()) player.sendSystemMessage(text);
        }
        if (warning == -7 && message.equals("Phase decreased")) {
            // TODO(M4): give every parasite in this level SRPPotions.RAGE_E (2400 ticks, amplifier 1, no particles) once
            // mob effects and EntityParasiteBase exist.
        }
    }

    /**
     * Blacklist test of 1.10.9: true when the element contains any entry of the list (blacklist), inverted for a whitelist.
     * Entries are substrings, not exact names.
     */
    public static boolean checkName(@Nullable String potentialElement, String[] blacklist, boolean isWhitelist) {
        if (potentialElement == null) {
            return false;
        }
        return Arrays.stream(blacklist).anyMatch(potentialElement::contains) != isWhitelist;
    }

    /** Chat message to every player on the server. */
    public static void alertAllPlayerSer(@Nullable Level level, String message) {
        if (level == null) return;
        MinecraftServer server = level.getServer();
        if (server == null) return;
        Component text = Component.literal(message);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) player.sendSystemMessage(text);
    }

    /** Dislodgment 2 (summon by death) end effect. TODO(M5): needs jugg effect + spawnUnitFromRof. */
    public static boolean disloNumber2(ServerLevel level) {
        return false;
    }

    /** Dislodgment 5 end effect (empty in 1.10.9 as well). */
    public static boolean disloNumber5(@Nullable LivingEntity target, Level level) {
        return false;
    }
}
