package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.property.Properties;

/** Tresses hair: two block tall plant that grows on any SRP block. */
public class BlockTressesHair extends DoublePlantBlock {
    /** 1.12 {@code BlockBush.BUSH_AABB}, inherited through {@code BlockDoublePlant}. */
    private static final VoxelShape BUSH_AABB = Block.box(0.30000001192092896 * 16.0, 0.0, 0.30000001192092896 * 16.0, 0.699999988079071 * 16.0, 0.6000000238418701 * 16.0, 0.699999988079071 * 16.0);

    public BlockTressesHair(Properties properties) {
        super(properties);
    }

    public BlockTressesHair() {
        this(SRPMaterial.PLANTS.props(0.0f).sound(SoundType.GRASS));
    }


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BUSH_AABB;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return BlockHirsuteHair.isSRPBlock(state);
    }
}
