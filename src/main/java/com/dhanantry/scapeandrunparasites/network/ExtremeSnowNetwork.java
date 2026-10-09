package com.dhanantry.scapeandrunparasites.network;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;

public final class ExtremeSnowNetwork {
    private ExtremeSnowNetwork() {
    }

    /** Sends the storm state to every player in the level. */
    public static void broadcast(ServerLevel world, boolean en, float inten, boolean any, float windDeg, float windSpeed) {
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersInDimension(world, new ExtremeSnowPayload(en, inten, any, windDeg, windSpeed));
    }
}
