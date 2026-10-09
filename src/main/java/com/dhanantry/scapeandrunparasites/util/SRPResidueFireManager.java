package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPResidueFireManager {
    private static final Map<Integer, Map<BlockPos, Integer>> TRACK = new HashMap<Integer, Map<BlockPos, Integer>>();

    private SRPResidueFireManager() {
    }

    public static void lightAndTrack(Level w, BlockPos pos, int ttl) {
        if (w.isClientSide) {
            return;
        }
        if (ttl <= 0) {
            return;
        }
        if (w.isEmptyBlock(pos)) {
            w.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
        }
        TRACK.computeIfAbsent(DimKeys.of(w), d -> new HashMap()).put(pos.toImmutable(), ttl);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.world.isClientSide) {
            return;
        }
        int dim = DimKeys.of(e.world);
        Map<BlockPos, Integer> m = TRACK.get(dim);
        if (m == null || m.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<BlockPos, Integer>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Integer> en = it.next();
            int left = en.getValue() - 1;
            if (left <= 0) {
                BlockPos p = en.getKey();
                if (e.world.getBlockState(p).getBlock() == Blocks.FIRE) {
                    e.world.removeBlock(p, false);
                }
                it.remove();
                continue;
            }
            en.setValue(left);
        }
        if (m.isEmpty()) {
            TRACK.remove(dim);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        if (e.getLevel().isClientSide) {
            return;
        }
        TRACK.remove(DimKeys.of(e.getLevel()));
    }
}

