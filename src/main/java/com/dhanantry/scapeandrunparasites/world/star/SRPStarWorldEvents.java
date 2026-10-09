package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import com.dhanantry.scapeandrunparasites.network.StarTypePayload;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

/**
 * Star world setup (SRPStarWorldEvents + SRPStarTypeSyncHandler of 1.10.9). The world creation screen stores the choice with
 * {@link #markCreatingWorld}; when the overworld loads for the first time the choice (or the config default) is written to the
 * world data. {@link SRPWorldEntitySpawner#starType} is the live value the biome mixin reads.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPStarWorldEvents {
    private static boolean creatingWorld = false;
    private static int pendingStarType = 0;
    private static boolean pendingMushroomTrees = false;
    private static boolean pendingFracturedTerrain = false;

    private SRPStarWorldEvents() {
    }

    public static void markCreatingWorld(int starType, boolean mushroomTreesEnabled, boolean fracturedTerrainEnabled) {
        creatingWorld = true;
        pendingStarType = sanitize(starType);
        pendingMushroomTrees = pendingStarType == 1 && mushroomTreesEnabled;
        pendingFracturedTerrain = pendingStarType == 1 && fracturedTerrainEnabled;
    }

    public static void clearPending() {
        creatingWorld = false;
        pendingStarType = 0;
        pendingMushroomTrees = false;
        pendingFracturedTerrain = false;
    }

    private static int sanitize(int starType) {
        return starType < 0 || starType > 2 ? 0 : starType;
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        MinecraftServer server = level.getServer();
        SRPStarWorldData data = SRPStarWorldData.get(server);
        if (data.isFresh()) {
            if (creatingWorld) {
                data.setStarType(pendingStarType);
                data.setMushroomTreesEnabled(pendingStarType == 1 && pendingMushroomTrees);
                data.setFracturedTerrainEnabled(pendingFracturedTerrain);
            } else {
                data.setStarType(SRPConfigWorld.defaultStarWorldType);
                data.setMushroomTreesEnabled(SRPConfigWorld.defaultStarWorldType == 1 && SRPConfigWorld.defaultStarWorldMushroomTrees);
                data.setFracturedTerrainEnabled(SRPConfigWorld.defaultStarWorldFracturedTerrain);
            }
            data.markUsed();
        }
        clearPending();
        SRPWorldEntitySpawner.starType = data.getStarType();
        SRPWorldEntitySpawner.mushroomTrees = data.getStarType() == 1 && data.isMushroomTreesEnabled();
        SRPWorldEntitySpawner.fracturedTerrain = data.isFracturedTerrainEnabled();
        StarBiomeMapper.clearCache();
    }

    private static void send(ServerPlayer p) {
        if (p.getServer() == null) {
            return;
        }
        SRPSend.sendToPlayer(p, new StarTypePayload(SRPStarWorldData.get(p.getServer()).getStarType()));
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            send(p);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            send(p);
        }
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            send(p);
        }
    }
}
