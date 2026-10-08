package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

/** Blocks with an infestation {@code stage} property (0-5). */
public interface IStagedBlock {
    IntegerProperty getStageProperty();

    default BlockState withStage(BlockState base, int stage) {
        IntegerProperty prop = getStageProperty();
        if (prop != null && base.hasProperty(prop)) {
            return base.setValue(prop, stage);
        }
        return base;
    }
}
