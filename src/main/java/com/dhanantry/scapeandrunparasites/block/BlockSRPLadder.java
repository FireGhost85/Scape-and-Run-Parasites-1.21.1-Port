package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;

/** Ladder with hardness 0.4. */
public class BlockSRPLadder extends LadderBlock {
    public BlockSRPLadder() {
        super(SRPMaterial.CIRCUITS.props(0.4f).sound(SoundType.LADDER).noOcclusion().pushReaction(PushReaction.DESTROY));
    }
}
