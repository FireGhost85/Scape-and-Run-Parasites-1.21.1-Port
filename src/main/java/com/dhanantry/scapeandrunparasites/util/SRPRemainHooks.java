package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPRemainHooks {
    private SRPRemainHooks() {
    }

    @SubscribeEvent
    public static void onLightningSpawn(EntityJoinLevelEvent e) {
        if (!(e.getEntity() instanceof LightningBolt)) {
            return;
        }
        if (e.getLevel().isClientSide()) {
            return;
        }
        Level w = e.getLevel();
        BlockPos strike = e.getEntity().blockPosition();
        int r = 5;
        BlockPos.MutableBlockPos cur = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; ++dx) {
            for (int dy = -r; dy <= r; ++dy) {
                for (int dz = -r; dz <= r; ++dz) {
                    cur.set(strike.getX() + dx, strike.getY() + dy, strike.getZ() + dz);
                    BlockState s = w.getBlockState((BlockPos)cur);
                    if (!(s.getBlock() instanceof BlockInfestedRemain)) continue;
                    w.removeBlock((BlockPos)cur, false);
                }
            }
        }
    }
}

