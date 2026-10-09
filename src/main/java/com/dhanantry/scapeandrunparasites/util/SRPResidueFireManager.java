package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPResidueFireManager {
    private static final Map<String, Map<BlockPos, Integer>> TRACK = new HashMap<String, Map<BlockPos, Integer>>();

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
        TRACK.computeIfAbsent(DimKeys.of(w), d -> new HashMap<>()).put(pos.immutable(), ttl);
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post e) {
        if (e.getLevel().isClientSide) {
            return;
        }
        String dim = DimKeys.of(e.getLevel());
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
                if (e.getLevel().getBlockState(p).getBlock() == Blocks.FIRE) {
                    e.getLevel().removeBlock(p, false);
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
    public static void onWorldUnload(LevelEvent.Unload e) {
        if (e.getLevel().isClientSide() || !(e.getLevel() instanceof Level lvl)) {
            return;
        }
        TRACK.remove(DimKeys.of(lvl));
    }
}

