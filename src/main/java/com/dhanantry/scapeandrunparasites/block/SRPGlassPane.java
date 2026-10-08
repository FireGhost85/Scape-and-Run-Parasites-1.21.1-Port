package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SRPGlassPane extends IronBarsBlock {
    public SRPGlassPane() {
        super(BlockBehaviour.Properties.of().strength(0.3f).sound(SoundType.GLASS).noOcclusion());
    }
}
