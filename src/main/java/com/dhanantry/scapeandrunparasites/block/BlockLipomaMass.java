package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Lipoma mass: hangs from the underside of an SRP block. */
public class BlockLipomaMass extends BushBlock {
    public static final MapCodec<BlockLipomaMass> CODEC = simpleCodec(BlockLipomaMass::new);
    /** {@code BlockBush.BUSH_AABB} of 1.12, the bounding box the original inherited. */
    private static final VoxelShape BUSH_AABB = net.minecraft.world.level.block.Block.box(0.30000001192092896 * 16.0, 0.0, 0.30000001192092896 * 16.0, 0.699999988079071 * 16.0, 0.6000000238418701 * 16.0, 0.699999988079071 * 16.0);

    public BlockLipomaMass(Properties properties) {
        super(properties);
    }

    public BlockLipomaMass() {
        this(SRPMaterial.PLANTS.props(0.0f).sound(SRPSoundTypes.FLESH));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BUSH_AABB;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos up = pos.above();
        BlockState above = level.getBlockState(up);
        return BlockHirsuteHair.isSRPBlock(above) && above.isFaceSturdy(level, up, Direction.DOWN);
    }
}
