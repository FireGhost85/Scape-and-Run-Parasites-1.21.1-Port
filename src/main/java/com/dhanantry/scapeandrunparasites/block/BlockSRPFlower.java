package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Assimilated blossom: grows on grass, ground, sand and clay materials. */
public class BlockSRPFlower extends BushBlock {
    public static final MapCodec<BlockSRPFlower> CODEC = simpleCodec(BlockSRPFlower::new);

    public BlockSRPFlower(Properties properties) {
        super(properties);
    }

    public BlockSRPFlower() {
        this(SRPMaterial.PLANTS.props(0.0f).sound(SRPSoundTypes.FLESH));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return isGrassGroundSandClay(state);
    }

    /** 1.12 material test: {@code Material.grass}, {@code ground}, {@code sand} or {@code clay}. */
    static boolean isGrassGroundSandClay(BlockState state) {
        if (state.is(BlockTags.DIRT) || state.is(BlockTags.SAND)) {
            return true;
        }
        Block b = state.getBlock();
        if (b == Blocks.GRAVEL || b == Blocks.CLAY || b == Blocks.FARMLAND || b == Blocks.DIRT_PATH || b == Blocks.SOUL_SAND) {
            return true;
        }
        return state.is(SRPBlocks.InfestedStain.get()) || state.is(SRPBlocks.ParasiteStain.get()) || state.is(SRPBlocks.ParasiteLoot.get())
                || state.is(SRPBlocks.HarlequinnGrass.get()) || state.is(SRPBlocks.InfestedOre.get()) || state.is(SRPBlocks.InfestedSand.get())
                || state.is(SRPBlocks.ParasiteStainSlabHalf.get());
    }
}
