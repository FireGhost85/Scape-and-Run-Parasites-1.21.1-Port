package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Infested ground block with an infestation stage; {@code snowy} was the 1.12 actual state. */
public class BlockInfestedStain extends BlockParasiteSpreading implements IStagedBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);
    public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");

    @Override
    public IntegerProperty getStageProperty() {
        return STAGE;
    }

    public BlockInfestedStain(SRPMaterial material, float hardness, boolean infested) {
        super(material.props(hardness).sound(SRPSoundTypes.BECKON), infested);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0).setValue(SNOWY, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE, SNOWY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SNOWY, isSnow(context.getLevel().getBlockState(context.getClickedPos().above())));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return dir == Direction.UP ? state.setValue(SNOWY, isSnow(neighbor)) : state;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        BlockClientHooks.stainDot(level, pos, rand);
        BlockClientHooks.stageParticles(level, pos, state.getValue(STAGE), rand);
    }
}
