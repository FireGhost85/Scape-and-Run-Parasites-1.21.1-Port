package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPStarTypeSyncHandler {
    private SRPStarTypeSyncHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        SRPStarTypeSyncHandler.send(event.player);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        SRPStarTypeSyncHandler.send(event.player);
    }

    @SubscribeEvent
    public static void onDimensionChanged(PlayerEvent.PlayerChangedDimensionEvent event) {
        SRPStarTypeSyncHandler.send(event.player);
    }

    private static void send(Player player) {
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        if (serverPlayer.getServer() == null) {
            return;
        }
        ServerLevel overworld = serverPlayer.getServer().worldServerForDimension(0);
        if (overworld == null) {
            return;
        }
        int starType = SRPStarWorldData.get((Level)overworld).getStarType();
        PacketDistributor.sendToPlayer((ServerPlayer)serverPlayer, new SyncStarTypePayload(starType));
    }
}

