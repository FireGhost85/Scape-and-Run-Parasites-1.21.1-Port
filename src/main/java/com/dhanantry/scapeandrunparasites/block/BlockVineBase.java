package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Vine block with its own hardness and optional random ticks (vine growth). */
public class BlockVineBase extends VineBlock {
    public BlockVineBase(float hardness, boolean tickRandom) {
        super(props(hardness, tickRandom));
    }

    private static BlockBehaviour.Properties props(float hardness, boolean tickRandom) {
        BlockBehaviour.Properties p = SRPMaterial.VINE.props(hardness).sound(SoundType.GRASS).noCollission().replaceable();
        return tickRandom ? p.randomTicks() : p;
    }
}
