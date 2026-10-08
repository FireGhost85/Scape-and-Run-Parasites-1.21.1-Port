package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.RotatedPillarBlock;

/** Hair follicle pillar: wood pillar with hardness 1.0. */
public class BlockHairFolliclePillar extends RotatedPillarBlock {
    public BlockHairFolliclePillar(SRPMaterial material) {
        super(material.props(1.0f));
    }
}
