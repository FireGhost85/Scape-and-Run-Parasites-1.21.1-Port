package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Infestation behaviour shared by the SRP fences, slabs and walls of 1.12 (each class held an identical copy). */
final class BlockInfestationTouch {
    private BlockInfestationTouch() {
    }

    /** {@code updateTick}: spreads the infestation while an infested neighbour exists and keeps ticking. */
    static void tick(Block self, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (touchingAnyInfestation(level, pos)) {
            BeckonBlockInfestation.beckonInfestation(level, pos, rand, 1, false);
            level.scheduleTick(pos, self, 20);
        }
    }

    static void schedule(Block self, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, self, 10);
        }
    }

    static boolean touchingAnyInfestation(Level level, BlockPos pos) {
        for (int dir = 0; dir <= 5; ++dir) {
            BlockPos helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            Block b = level.getBlockState(helper).getBlock();
            if (b instanceof IStagedBlock) {
                return true;
            }
            ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(b);
            if (rl == null || !ScapeAndRunParasites.MODID.equals(rl.getNamespace())) {
                continue;
            }
            if (rl.getPath().toLowerCase(Locale.ROOT).contains("infest")) {
                return true;
            }
        }
        return false;
    }
}
