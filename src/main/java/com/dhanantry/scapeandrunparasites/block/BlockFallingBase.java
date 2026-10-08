package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** Gravity block of the mod; raises the block-break dislodgment event without the mob cap test. */
public class BlockFallingBase extends FallingBlock {
    public static final MapCodec<BlockFallingBase> CODEC = simpleCodec(BlockFallingBase::new);

    public BlockFallingBase(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockFallingBase(SRPMaterial material, float hardness, boolean tickRandom) {
        this(BlockBase.prop(material.props(hardness), tickRandom));
    }

    public BlockFallingBase(SRPMaterial material, float hardness, boolean tickRandom, float resistance) {
        this(BlockBase.prop(material.props(hardness, resistance), tickRandom));
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getMapColor(level, pos).col;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        BlockBase.parasiteBlockBreak(level, pos, false);
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
}
