package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** Wooden (opens by hand) or iron (redstone only) trapdoor with hardness 3. */
public class SRPTrapDoor extends TrapDoorBlock {
    public SRPTrapDoor(SRPMaterial material) {
        super(material == SRPMaterial.WOOD ? BlockSetType.OAK : BlockSetType.IRON,
                material.props(3.0f).sound(material == SRPMaterial.WOOD ? SoundType.WOOD : SoundType.METAL).noOcclusion());
    }
}
