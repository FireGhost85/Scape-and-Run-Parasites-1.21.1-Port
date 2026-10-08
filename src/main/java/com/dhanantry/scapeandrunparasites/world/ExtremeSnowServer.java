package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.world.ExtremeSnowData;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites", value={Side.SERVER})
public final class ExtremeSnowServer {
    private ExtremeSnowServer() {
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Level w = e.world;
        ExtremeSnowData data = ExtremeSnowData.get(w);
        if (!data.isEnabled()) {
            return;
        }
        List<? extends ServerPlayer> players = w.getMinecraftServer() != null ? w.getMinecraftServer().getPlayerList().getPlayerList() : Collections.emptyList();
        int triesPerPlayer = (int)(12.0f + 48.0f * data.getIntensity());
        int radius = 18;
        for (ServerPlayer p : players) {
            if (p.level() != w) continue;
            for (int i = 0; i < triesPerPlayer; ++i) {
                int x = (int)(p.getX() + (double)(w.random.nextInt(radius * 2 + 1) - radius));
                int z = (int)(p.getZ() + (double)(w.random.nextInt(radius * 2 + 1) - radius));
                BlockPos base = BlockPos.containing(x, (int)p.getY(), z);
                BlockPos hit = w.getPrecipitationHeight(base);
                boolean force = ExtremeSnowData.get(w).isForceAnywhere();
                if (!w.canSeeSky(hit) || !force && !w.canSnowAt(hit, false)) continue;
                BlockState stateAt = w.getBlockState(hit);
                if (stateAt.getBlock() == Blocks.SNOW) {
                    int layers = (Integer)stateAt.getValue((Property)BlockSnow.LAYERS);
                    if (layers >= 8) continue;
                    w.setBlock(hit, stateAt.setValue((Property)BlockSnow.LAYERS, Integer.valueOf(layers + 1)), 2);
                    continue;
                }
                if (!stateAt.isAir()) continue;
                w.setBlock(hit, Blocks.SNOW.defaultBlockState().setValue((Property)BlockSnow.LAYERS, Integer.valueOf(1)), 2);
            }
        }
    }
}

