package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * Grass block below snow grass: always {@code snowy}, reverts to grass when no snow grass is above it. The sound type is the
 * block default (stone) like in 1.12, where this class never set one. Drops dirt, or grass with silk touch (loot table).
 */
public class BlockSnowCoveredGrass extends GrassBlock {
    public BlockSnowCoveredGrass(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(SNOWY, Boolean.TRUE));
    }

    public BlockSnowCoveredGrass() {
        this(BlockBehaviour.Properties.of().mapColor(MapColor.GRASS).strength(0.6f).sound(SoundType.STONE).randomTicks());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BlockState result = super.updateShape(state, direction, neighbor, level, pos, neighborPos);
        return result.is(this) ? result.setValue(SNOWY, Boolean.TRUE) : result;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(SNOWY, Boolean.TRUE);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(Items.GRASS_BLOCK);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide && !this.hasSnowGrassAbove(level, pos)) {
            level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!this.hasSnowGrassAbove(level, pos)) {
            level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            return;
        }
        super.randomTick(state, level, pos, rand);
    }

    private boolean hasSnowGrassAbove(BlockGetter level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(SRPBlocks.SnowTallGrass.get()) || above.is(SRPBlocks.SnowShortGrass.get());
    }
}
