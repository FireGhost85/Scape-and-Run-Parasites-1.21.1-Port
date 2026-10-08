package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WorldGenParasiteTreeAbstract
extends WorldGenAbstractTree {
    public WorldGenParasiteTreeAbstract(boolean notify) {
        super(notify);
    }

    protected boolean canGrowInto(Block blockType) {
        BlockState material = blockType.defaultBlockState();
        return material.isAir() || material.is(BlockTags.LEAVES) || blockType == Blocks.GRASS_BLOCK || blockType == Blocks.DIRT || blockType == Blocks.OAK_LOG || blockType == Blocks.ACACIA_LOG || blockType == Blocks.OAK_SAPLING || blockType == Blocks.VINE || blockType == SRPBlocks.ParasiteBush.get();
    }

    protected void setDirtAt(Level worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() != Blocks.DIRT) {
            this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.get().defaultBlockState());
        }
    }

    public boolean isReplaceable(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS) || this.canGrowInto(state.getBlock()) || state.getBlock() == SRPBlocks.ParasiteBush.get();
    }
}

