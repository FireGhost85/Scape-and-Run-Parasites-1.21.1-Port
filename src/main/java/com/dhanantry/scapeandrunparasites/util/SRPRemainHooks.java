package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPRemainHooks {
    private SRPRemainHooks() {
    }

    @SubscribeEvent
    public static void onLightningSpawn(EntityJoinWorldEvent e) {
        if (!(e.getEntity() instanceof LightningBolt)) {
            return;
        }
        Level w = e.getWorld();
        if (w.isClientSide) {
            return;
        }
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

