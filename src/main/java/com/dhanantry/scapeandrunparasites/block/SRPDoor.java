package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

/** Wooden or iron door (hardness 3, iron 5); the door item shares the registry name. */
public class SRPDoor extends DoorBlock {
    public SRPDoor(SRPMaterial material) {
        super(material == SRPMaterial.IRON ? BlockSetType.IRON : BlockSetType.OAK,
                material.props(material == SRPMaterial.IRON ? 5.0f : 3.0f)
                        .sound(material == SRPMaterial.IRON ? SoundType.METAL : SoundType.WOOD).noOcclusion().pushReaction(PushReaction.DESTROY));
    }
}
