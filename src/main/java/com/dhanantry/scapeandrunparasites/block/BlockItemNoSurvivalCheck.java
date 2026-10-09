package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.property.Properties;

/** Block item that does not test {@code canSurvive} at placement (the block validates its own placement state). */
public class BlockItemNoSurvivalCheck extends BlockItem {
    public BlockItemNoSurvivalCheck(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean mustSurvive() {
        return false;
    }
}
