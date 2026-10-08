package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Tall hanging deadhead grass: the placed block is the TOP part (hanging from the support above), the BOTTOM part is
 * placed below it. Its block item skips the survival check at placement (see {@link BlockItemNoSurvivalCheck}).
 */
public class BlockDeadheadGrassTall extends BushBlock {
    public static final MapCodec<BlockDeadheadGrassTall> CODEC = simpleCodec(BlockDeadheadGrassTall::new);
    public static final EnumProperty<EnumPart> PART = EnumProperty.create("part", EnumPart.class);
    private static final VoxelShape TALL_TOP_AABB = Block.box(0.0, -16.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape TALL_BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 32.0, 16.0);

    public BlockDeadheadGrassTall(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, EnumPart.TOP));
    }

    public BlockDeadheadGrassTall() {
        this(SRPMaterial.VINE.props(0.0f).sound(SoundType.GRASS).noCollission().replaceable());
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(PART) == EnumPart.TOP ? TALL_TOP_AABB : TALL_BOTTOM_AABB;
    }

    /** 1.12 {@code canBlockStay}. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        EnumPart part = state.getValue(PART);
        if (part == EnumPart.TOP) {
            BlockState below = level.getBlockState(pos.below());
            return BlockDeadheadGrassShort.isValidDeadheadSupport(level, pos.above()) && below.getBlock() == this && below.getValue(PART) == EnumPart.BOTTOM;
        }
        BlockState above = level.getBlockState(pos.above());
        return above.getBlock() == this && above.getValue(PART) == EnumPart.TOP;
    }

    /** 1.12 {@code canPlaceBlockAt}: support above and air below. */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        if (BlockDeadheadGrassShort.isValidDeadheadSupport(context.getLevel(), pos.above()) && context.getLevel().isEmptyBlock(pos.below())) {
            return this.defaultBlockState().setValue(PART, EnumPart.TOP);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide && level.isEmptyBlock(pos.below())) {
            level.setBlock(pos.below(), this.defaultBlockState().setValue(PART, EnumPart.BOTTOM), 3);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return !state.canSurvive(level, pos) ? net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() : state;
    }

    public enum EnumPart implements StringRepresentable {
        TOP,
        BOTTOM;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }
    }
}
