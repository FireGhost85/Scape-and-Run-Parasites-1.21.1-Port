package com.dhanantry.scapeandrunparasites.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** The 1.12 {@code WorldGenAbstractTree} base (the parasite trees override the growth checks). */
public abstract class WorldGenAbstractTree
extends WorldGenerator {
    public WorldGenAbstractTree(boolean notify) {
        super(notify);
    }

    protected boolean canGrowInto(Block blockType) {
        BlockState state = blockType.defaultBlockState();
        return state.isAir() || state.is(BlockTags.LEAVES) || blockType == Blocks.GRASS_BLOCK || blockType == Blocks.DIRT || state.is(BlockTags.LOGS) || state.is(BlockTags.SAPLINGS) || blockType == Blocks.VINE;
    }

    public boolean isReplaceable(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || this.canGrowInto(state.getBlock());
    }

    protected void setDirtAt(Level worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() != Blocks.DIRT) {
            this.setBlockAndNotifyAdequately(worldIn, pos, Blocks.DIRT.defaultBlockState());
        }
    }
}
