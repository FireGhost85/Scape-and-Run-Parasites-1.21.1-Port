package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.property.Properties;

/** Hirsute hair: small plant that grows on any SRP block. */
public class BlockHirsuteHair extends BushBlock {
    public static final MapCodec<BlockHirsuteHair> CODEC = simpleCodec(BlockHirsuteHair::new);
    private static final VoxelShape AABB = Block.box(1.6, 0.0, 1.6, 14.4, 14.4, 14.4);

    public BlockHirsuteHair(Properties properties) {
        super(properties);
    }

    public BlockHirsuteHair() {
        this(SRPMaterial.PLANTS.props(0.0f).sound(SRPSoundTypes.FLESH));
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
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return isSRPBlock(state);
    }

    static boolean isSRPBlock(BlockState state) {
        return ScapeAndRunParasites.MODID.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace());
    }
}
