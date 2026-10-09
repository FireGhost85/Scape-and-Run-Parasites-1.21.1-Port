package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Slab of the SRP building blocks. The 1.12 half and double slab blocks are one block with the vanilla {@code type}
 * property now; the sound is always flesh like the original, whatever sound type was passed.
 */
public class BlockHarleskinnSlab extends SlabBlock {
    public BlockHarleskinnSlab(float hardness, float resistance) {
        super(SRPMaterial.WOOD.props(hardness, resistance).sound(SRPSoundTypes.FLESH).randomTicks());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        BlockInfestationTouch.schedule(this, level, pos);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        BlockInfestationTouch.schedule(this, level, pos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        this.tick(state, level, pos, rand);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        BlockInfestationTouch.tick(this, level, pos, rand);
    }
}
