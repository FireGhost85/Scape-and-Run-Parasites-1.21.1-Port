package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;

public class BlockSolidAlveoli extends BlockBase {
    public BlockSolidAlveoli() {
        super(prop(SRPMaterial.CLAY.props(1.0f).sound(SRPSoundTypes.FLESH).noOcclusion(), true));
    }
}
