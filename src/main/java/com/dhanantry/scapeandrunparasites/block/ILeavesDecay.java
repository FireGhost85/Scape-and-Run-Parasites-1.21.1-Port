package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Leaves that start their decay check on request (Forge 1.12 {@code Block.beginLeavesDecay}). */
public interface ILeavesDecay {
    void beginLeavesDecay(BlockState state, Level level, BlockPos pos);
}
