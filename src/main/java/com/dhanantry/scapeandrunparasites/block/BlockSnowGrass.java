package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.property.Properties;

/**
 * Snow-covered short and tall grass. Turns the grass block below into snow covered grass and back. The box (0.1..0.9, height 1 or
 * 2) is also the collision box like in 1.12, where only the base {@code Block} collision applied. Drops: a grass seed with
 * chance 1/8, the block itself when sheared or with silk touch (loot table).
 */
public class BlockSnowGrass extends Block {
    public static final MapCodec<BlockSnowGrass> CODEC = simpleCodec(p -> new BlockSnowGrass(p, false));
    private static final VoxelShape SHORT_GRASS_AABB = Shapes.box(0.1, 0.0, 0.1, 0.9, 1.0, 0.9);
    private static final VoxelShape TALL_GRASS_AABB = Shapes.box(0.1, 0.0, 0.1, 0.9, 2.0, 0.9);
    private final boolean tallGrass;

    public BlockSnowGrass(BlockBehaviour.Properties properties, boolean tallGrass) {
        super(properties);
        this.tallGrass = tallGrass;
    }

    public BlockSnowGrass(boolean tallGrass) {
        this(BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).replaceable().strength(0.1f).sound(SoundType.GRASS).noOcclusion()
                .pushReaction(PushReaction.DESTROY), tallGrass);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide) {
            return;
        }
        BlockPos below = pos.below();
        if (level.getBlockState(below).is(Blocks.GRASS_BLOCK)) {
            level.setBlock(below, SRPBlocks.SnowCoveredGrass.get().defaultBlockState(), 3);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide) {
            level.playSound(null, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 0.65f, 1.0f);
            BlockPos below = pos.below();
            if (level.getBlockState(below).is(SRPBlocks.SnowCoveredGrass.get())) {
                level.setBlock(below, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.tallGrass ? TALL_GRASS_AABB : SHORT_GRASS_AABB;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return type != PathComputationType.WATER;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    /** Plays the snow place sound at 0.65 volume after placing. */
    public static class ItemSnowGrass extends BlockItem {
        public ItemSnowGrass(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
            boolean placed = super.placeBlock(context, state);
            if (placed && !context.getLevel().isClientSide) {
                context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.SNOW_PLACE, SoundSource.BLOCKS, 0.65f, 1.0f);
            }
            return placed;
        }
    }
}
