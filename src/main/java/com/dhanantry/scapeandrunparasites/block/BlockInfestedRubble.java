package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Infested stone/wood block with an infestation stage. */
public class BlockInfestedRubble extends BlockParasiteSpreading implements IStagedBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);

    public BlockInfestedRubble(SRPMaterial material, float hardness, boolean infested, float resistance) {
        super(material.props(hardness, resistance).sound(SRPSoundTypes.SOIL), infested);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    public IntegerProperty getStageProperty() {
        return STAGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        BlockClientHooks.stageParticles(level, pos, state.getValue(STAGE), rand);
    }
}
