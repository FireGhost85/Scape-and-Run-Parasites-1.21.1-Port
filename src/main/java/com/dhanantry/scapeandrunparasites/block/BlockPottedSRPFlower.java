package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Pot with an assimilated plant; a plain small block that drops itself. */
public class BlockPottedSRPFlower extends Block {
    public static final MapCodec<BlockPottedSRPFlower> CODEC = simpleCodec(BlockPottedSRPFlower::new);
    private static final VoxelShape AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public BlockPottedSRPFlower(Properties properties) {
        super(properties);
    }

    public BlockPottedSRPFlower() {
        this(SRPMaterial.CIRCUITS.props(0.0f).sound(SoundType.STONE).noOcclusion());
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }
}
