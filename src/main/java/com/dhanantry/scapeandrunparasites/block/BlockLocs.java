package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Locs block; {@code snowy} was the 1.12 actual state (snow layer or snow block above). */
public class BlockLocs extends BlockBase {
    public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");

    public BlockLocs(SRPMaterial material, float hardness, boolean tickRandom) {
        super(material, hardness, tickRandom);
        this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SNOWY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SNOWY, isSnow(context.getLevel().getBlockState(context.getClickedPos().above())));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return dir == Direction.UP ? state.setValue(SNOWY, isSnow(neighbor)) : state;
    }
}
