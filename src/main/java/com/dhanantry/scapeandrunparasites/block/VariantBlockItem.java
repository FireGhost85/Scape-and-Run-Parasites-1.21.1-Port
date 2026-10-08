package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * One item per 1.12 metadata variant of a block: places the block with {@code property = value} (the
 * {@code block_state} item component). Translation key is {@code block.srparasites.<base>_<variant>} like the old
 * {@code ItemBlockVariant}.
 */
public class VariantBlockItem extends BlockItem {
    private static final Map<Block, Map<Enum<?>, Item>> BY_VARIANT = new HashMap<>();

    private final String path;
    private final boolean primary;

    public <E extends Enum<E> & StringRepresentable> VariantBlockItem(Block block, EnumProperty<E> property, E value, String path, boolean primary, Item.Properties properties) {
        super(block, properties.component(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(property, value)));
        this.path = path;
        this.primary = primary;
        BY_VARIANT.computeIfAbsent(block, b -> new HashMap<>()).put(value, this);
    }

    /** The item of one variant of the block (1.12 {@code new ItemStack(block, 1, meta)}). */
    public static ItemStack stack(Block block, Enum<?> variant) {
        Map<Enum<?>, Item> items = BY_VARIANT.get(block);
        Item item = items == null ? null : items.get(variant);
        return item == null ? new ItemStack(block) : new ItemStack(item);
    }

    @Override
    public String getDescriptionId() {
        return "block." + ScapeAndRunParasites.MODID + "." + path;
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {
        if (primary) {
            super.registerBlocks(map, item);
        }
    }

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> map, Item item) {
        if (primary) {
            super.removeFromBlockToItemMap(map, item);
        }
    }
}
