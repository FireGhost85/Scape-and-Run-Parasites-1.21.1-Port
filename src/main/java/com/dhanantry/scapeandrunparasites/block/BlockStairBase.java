package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Stairs copying the properties of the model block state. */
public class BlockStairBase extends StairBlock {
    public BlockStairBase(BlockState modelState) {
        super(modelState, BlockBase.modelProps(modelState.getBlock()));
    }
}
