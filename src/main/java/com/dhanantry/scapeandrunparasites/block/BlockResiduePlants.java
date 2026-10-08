package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Residue plants: cling to residue and residue bricks on the face opposite to {@code facing}. The 1.12 spread code looks for
 * residue below the new position, which is the plant itself, so the plants never spread; it is kept as is.
 */
public class BlockResiduePlants extends BlockBase {
    public static final DirectionProperty FACING = DirectionProperty.create("facing");
    private static final VoxelShape AABB_CENTER = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);

    public BlockResiduePlants() {
        super(prop(SRPMaterial.PLANTS.props(0.0f, 0.0f).sound(SRPSoundTypes.VOMIT).noOcclusion(), true));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    private static boolean isResidueSupport(BlockState state) {
        return state.is(SRPBlocks.ResidueBlock.get()) || state.is(SRPBlocks.ResidueBricks.get());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB_CENTER;
    }

    boolean canAttach(LevelReader level, BlockPos pos, Direction facing) {
        return isResidueSupport(level.getBlockState(pos.relative(facing.getOpposite())));
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return this.canAttach(level, pos, state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(FACING, context.getClickedFace());
        return state.canSurvive(context.getLevel(), context.getClickedPos()) ? state : null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!this.canAttach(level, pos, state.getValue(FACING))) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!this.canAttach(level, pos, state.getValue(FACING))) {
            level.destroyBlock(pos, false);
            return;
        }
        if (rand.nextInt(8) != 0) {
            return;
        }
        for (int tries = 0; tries < 3; ++tries) {
            Direction f = Direction.values()[rand.nextInt(6)];
            BlockPos newPos = pos.relative(f);
            if (!level.isEmptyBlock(newPos) || !this.canAttach(level, newPos, f)) {
                continue;
            }
            level.setBlock(newPos, this.defaultBlockState().setValue(FACING, f), 2);
            break;
        }
    }
}
