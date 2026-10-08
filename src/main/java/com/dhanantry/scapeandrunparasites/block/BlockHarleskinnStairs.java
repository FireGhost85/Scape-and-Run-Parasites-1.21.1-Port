package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Flesh stairs with hardness 1.5 and resistance 10. */
public class BlockHarleskinnStairs extends StairBlock {
    public BlockHarleskinnStairs(BlockState modelState) {
        super(modelState, SRPMaterial.ROCK.props(1.5f, 10.0f).sound(SRPSoundTypes.FLESH));
    }
}
