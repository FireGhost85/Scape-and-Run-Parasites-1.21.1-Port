package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockAssimilatedReed extends BlockBushBase {
    public static final MapCodec<BlockAssimilatedReed> CODEC = MapCodec.unit(BlockAssimilatedReed::new);
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 15);
    private static final VoxelShape AABB = Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public BlockAssimilatedReed() {
        super(SRPMaterial.PLANTS.props(0.0f).sound(SoundType.GRASS).randomTicks().noOcclusion().noCollission());
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.is(this)) {
            return true;
        }
        return isValidBaseBlock(below) && hasAdjacentGrowthFluid(level, pos.below());
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        checkAndDrop(level, pos, state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        checkAndDrop(level, pos, state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state;
    }

    private void checkAndDrop(Level level, BlockPos pos, BlockState state) {
        if (!canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
    }

    private static boolean isValidBaseBlock(BlockState state) {
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.COARSE_DIRT) || state.is(Blocks.PODZOL)
                || state.is(Blocks.SAND) || state.is(Blocks.RED_SAND)) {
            return true;
        }
        return ScapeAndRunParasites.MODID.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
    }

    private static boolean hasAdjacentGrowthFluid(LevelReader level, BlockPos base) {
        for (Direction f : Direction.Plane.HORIZONTAL) {
            BlockState st = level.getBlockState(base.relative(f));
            if (st.getFluidState().is(FluidTags.WATER) || st.getFluidState().getType().isSame(SRPFluids.DEADBLOOD_FLUID.get())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }
        if (!canSurvive(state, level, pos)) {
            checkAndDrop(level, pos, state);
            return;
        }
        if (level.isEmptyBlock(pos.above())) {
            int height = 1;
            while (level.getBlockState(pos.below(height)).is(this)) {
                ++height;
            }
            if (height < 7) {
                int age = state.getValue(AGE);
                if (age >= 15) {
                    level.setBlockAndUpdate(pos.above(), defaultBlockState());
                    level.setBlock(pos, state.setValue(AGE, 0), 4);
                } else {
                    level.setBlock(pos, state.setValue(AGE, age + 1), 4);
                }
            }
        }
    }
}
