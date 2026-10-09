package com.dhanantry.scapeandrunparasites.network;

import javax.annotation.Nullable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Server to client sends that do nothing when they are called from the client side. 1.12 let the entity code run its
 * {@code sendToAll} on both sides (the client call was dropped); in 1.21 {@link PacketDistributor} throws on a client that is not
 * running a server and would send from the render thread of a single player game. Only the server thread sends.
 */
public final class SRPSend {
    private SRPSend() {}

    private static boolean serverThread() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        return server != null && server.isSameThread();
    }

    public static void sendToAllPlayers(CustomPacketPayload payload, CustomPacketPayload... more) {
        if (serverThread()) {
            PacketDistributor.sendToAllPlayers(payload, more);
        }
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... more) {
        if (serverThread()) {
            PacketDistributor.sendToPlayer(player, payload, more);
        }
    }

    public static void sendToPlayersInDimension(ServerLevel level, CustomPacketPayload payload, CustomPacketPayload... more) {
        if (serverThread()) {
            PacketDistributor.sendToPlayersInDimension(level, payload, more);
        }
    }

    public static void sendToPlayersNear(ServerLevel level, @Nullable ServerPlayer excluded, double x, double y, double z, double radius, CustomPacketPayload payload, CustomPacketPayload... more) {
        if (serverThread()) {
            PacketDistributor.sendToPlayersNear(level, excluded, x, y, z, radius, payload, more);
        }
    }

    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... more) {
        if (serverThread()) {
            PacketDistributor.sendToPlayersTrackingEntity(entity, payload, more);
        }
    }

    public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... more) {
        if (serverThread()) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(entity, payload, more);
        }
    }
}
