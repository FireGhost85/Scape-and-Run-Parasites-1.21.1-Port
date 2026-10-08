package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

/** Wooden pressure plate triggered by every entity ({@code Sensitivity.EVERYTHING}). */
public class BlockSRPPressurePlate extends PressurePlateBlock {
    public BlockSRPPressurePlate() {
        super(BlockSetType.OAK, SRPMaterial.WOOD.props(0.5f).sound(SoundType.WOOD).noCollission().forceSolidOn().pushReaction(PushReaction.DESTROY));
    }
}
