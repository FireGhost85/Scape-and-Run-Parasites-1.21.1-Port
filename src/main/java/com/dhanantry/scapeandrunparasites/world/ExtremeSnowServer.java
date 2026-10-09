package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.network.ExtremeSnowNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** The snow that piles up around the players while the extreme snow storm is on (ExtremeSnowServer of 1.10.9). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class ExtremeSnowServer {
    private ExtremeSnowServer() {
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post e) {
        if (!(e.getLevel() instanceof ServerLevel w)) {
            return;
        }
        ExtremeSnowData data = ExtremeSnowData.get(w);
        if (!data.isEnabled()) {
            return;
        }
        int triesPerPlayer = (int)(12.0f + 48.0f * data.getIntensity());
        int radius = 18;
        for (ServerPlayer p : w.players()) {
            for (int i = 0; i < triesPerPlayer; ++i) {
                int x = (int)(p.getX() + (double)(w.random.nextInt(radius * 2 + 1) - radius));
                int z = (int)(p.getZ() + (double)(w.random.nextInt(radius * 2 + 1) - radius));
                BlockPos hit = new BlockPos(x, w.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
                boolean force = data.isForceAnywhere();
                if (!w.canSeeSky(hit) || !force && !w.getBiome(hit).value().coldEnoughToSnow(hit)) {
                    continue;
                }
                BlockState stateAt = w.getBlockState(hit);
                if (stateAt.getBlock() == Blocks.SNOW) {
                    int layers = stateAt.getValue(SnowLayerBlock.LAYERS);
                    if (layers >= 8) {
                        continue;
                    }
                    w.setBlock(hit, stateAt.setValue(SnowLayerBlock.LAYERS, layers + 1), 2);
                    continue;
                }
                if (!stateAt.isAir()) {
                    continue;
                }
                if (Blocks.SNOW.defaultBlockState().canSurvive(w, hit)) {
                    w.setBlock(hit, Blocks.SNOW.defaultBlockState(), 2);
                }
            }
        }
    }

    /** The storm state is sent again after a login or a dimension change. */
    private static void sync(ServerPlayer p) {
        if (p.level() instanceof ServerLevel w) {
            ExtremeSnowData d = ExtremeSnowData.get(w);
            ExtremeSnowNetwork.send(p, d.isEnabled(), d.getIntensity(), d.isForceAnywhere(), d.getWindDeg(), d.getWindSpeed());
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            sync(p);
        }
    }

    @SubscribeEvent
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            sync(p);
        }
    }
}
