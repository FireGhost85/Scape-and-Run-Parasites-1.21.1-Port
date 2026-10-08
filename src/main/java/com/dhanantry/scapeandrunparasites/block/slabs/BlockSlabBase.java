package com.dhanantry.scapeandrunparasites.block.slabs;

import com.dhanantry.scapeandrunparasites.block.IVariantBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Slab with a variant. The 1.12 half and double slab blocks are one block with the vanilla {@code type} property; a slab
 * only merges with a slab of the same variant, and the pick item is the half slab of the variant.
 */
public abstract class BlockSlabBase<E extends Enum<E> & StringRepresentable> extends SlabBlock implements IVariantBlock<E> {
    protected BlockSlabBase(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.variantStack(state);
    }

    private E heldVariant(ItemStack stack) {
        BlockItemStateProperties props = stack.get(DataComponents.BLOCK_STATE);
        String value = props == null ? null : props.properties().get(this.getVariantProperty().getName());
        return value == null ? null : this.getVariantProperty().getValue(value).orElse(null);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        SlabType slabtype = state.getValue(TYPE);
        ItemStack held = context.getItemInHand();
        if (slabtype == SlabType.DOUBLE || !(held.getItem() instanceof BlockItem bi && bi.getBlock() == this)) {
            return false;
        }
        E variant = this.heldVariant(held);
        if (variant == null ? state.getValue(this.getVariantProperty()) != this.defaultBlockState().getValue(this.getVariantProperty())
                : variant != state.getValue(this.getVariantProperty())) {
            return false;
        }
        if (context.replacingClickedOnBlock()) {
            boolean upperHalf = context.getClickLocation().y - (double) context.getClickedPos().getY() > 0.5;
            Direction direction = context.getClickedFace();
            if (slabtype == SlabType.BOTTOM) {
                return direction == Direction.UP || upperHalf && direction.getAxis().isHorizontal();
            }
            return direction == Direction.DOWN || !upperHalf && direction.getAxis().isHorizontal();
        }
        return true;
    }
}
