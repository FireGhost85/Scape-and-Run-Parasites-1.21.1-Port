package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockAlveoliGrowth extends BlockBase {
    private static final VoxelShape SHAPE = Block.box(3.2, 12.8, 3.2, 12.8, 16.0, 12.8);

    public BlockAlveoliGrowth() {
        super(prop(SRPMaterial.PLANTS.props(0.0f).sound(SRPSoundTypes.FLESH).noOcclusion().replaceable(), true));
    }

    private boolean isSRPBlock(LevelReader level, BlockPos p) {
        return ScapeAndRunParasites.MODID.equals(BuiltInRegistries.BLOCK.getKey(level.getBlockState(p).getBlock()).getNamespace());
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isSRPBlock(level, pos.above());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (context.getClickedFace() != Direction.DOWN) {
            return null;
        }
        return canSurvive(defaultBlockState(), context.getLevel(), context.getClickedPos()) ? defaultBlockState() : null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!isSRPBlock(level, pos.above())) {
            level.destroyBlock(pos, true);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
