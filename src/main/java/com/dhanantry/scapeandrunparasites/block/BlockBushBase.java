package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** 1.12 {@code BlockBushBase}: plain bush with hardness and optional random ticks. */
public class BlockBushBase extends BushBlock {
    public static final MapCodec<BlockBushBase> CODEC = simpleCodec(BlockBushBase::new);

    public BlockBushBase(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public BlockBushBase(float hardness, boolean tickRandom) {
        this(tickRandom ? SRPMaterial.PLANTS.props(hardness).randomTicks() : SRPMaterial.PLANTS.props(hardness));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }
}
