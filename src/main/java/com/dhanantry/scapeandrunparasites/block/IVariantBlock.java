package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Blocks whose 1.12 metadata variants became a {@code variant} blockstate property (one item per variant). */
public interface IVariantBlock<E extends Enum<E> & StringRepresentable> extends IMetaName {
    E[] getVariants();

    EnumProperty<E> getVariantProperty();

    /** The variant item of the state (1.12 {@code getPickBlock} / {@code damageDropped}). */
    default ItemStack variantStack(BlockState state) {
        return VariantBlockItem.stack((Block) this, state.getValue(getVariantProperty()));
    }
}
