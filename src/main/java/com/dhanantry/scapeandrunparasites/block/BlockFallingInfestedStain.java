package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;

/** Infested sand: a falling infested stain with an infestation stage. */
public class BlockFallingInfestedStain extends BlockFallingBase implements IStagedBlock {
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 5);
    public static final BooleanProperty SNOWY = BooleanProperty.create("snowy");

    @Override
    public IntegerProperty getStageProperty() {
        return STAGE;
    }

    public BlockFallingInfestedStain(SRPMaterial material, float hardness, boolean tickRandom) {
        super(BlockBase.prop(material.props(hardness).sound(SRPSoundTypes.INFEST), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0).setValue(SNOWY, Boolean.FALSE));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean flag = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (level.isClientSide) {
            return flag;
        }
        if (SRPConfigSystems.useEvolution && state.getValue(STAGE) > 0) {
            SRPSaveData data = SRPSaveData.get(level);
            data.setTotalKills(DimKeys.of(level), -SRPConfigSystems.valueLossBlockStain, true, level, false, 33);
        }
        return flag;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        if (rand.nextDouble() < 0.5) {
            if (SRPBlockLinks.isParasiteBiome(level, pos)) {
                level.setBlock(pos, SRPBlocks.ParasiteStain.get().defaultBlockState(), 3);
            } else {
                int heart = ParasiteEventWorld.canBiomeStillExist(level, pos, true);
                if (heart > 0) {
                    BlockParasiteSpreading.SpreadBiome(level, pos, heart, ParasiteEventWorld.canBiomeStillExistType(level, pos, true));
                }
            }
        } else if (rand.nextDouble() <= 0.05) {
            BlockParasiteSpreading.canInfestBlock(level, pos, rand, state.getValue(STAGE), false);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        super.tick(state, level, pos, rand);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE, SNOWY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SNOWY, BlockBase.isSnow(context.getLevel().getBlockState(context.getClickedPos().above())));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState updated = super.updateShape(state, dir, neighbor, level, pos, neighborPos);
        return dir == Direction.UP && updated.is(this) ? updated.setValue(SNOWY, BlockBase.isSnow(neighbor)) : updated;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        super.animateTick(state, level, pos, rand);
        BlockClientHooks.stageParticles(level, pos, state.getValue(STAGE), rand);
    }
}
