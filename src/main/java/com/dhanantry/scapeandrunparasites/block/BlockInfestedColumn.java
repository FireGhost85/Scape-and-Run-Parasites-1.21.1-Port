package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;

/** Infested column / goth stem: rock pillar. */
public class BlockInfestedColumn extends RotatedPillarBlock {
    public BlockInfestedColumn() {
        super(SRPMaterial.ROCK.props(1.5f, 10.0f).sound(SoundType.STONE));
    }
}
