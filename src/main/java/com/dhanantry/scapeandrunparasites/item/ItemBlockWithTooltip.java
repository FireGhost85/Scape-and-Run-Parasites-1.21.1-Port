package com.dhanantry.scapeandrunparasites.item;

import java.util.Arrays;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** Block item with an optional tooltip: the lang key {@code tooltip.<block name>}, lines separated by a literal {@code \n}. */
public class ItemBlockWithTooltip extends BlockItem {
    public ItemBlockWithTooltip(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        String key = "tooltip." + BuiltInRegistries.BLOCK.getKey(this.getBlock()).getPath();
        Language language = Language.getInstance();
        if (!language.has(key)) {
            return;
        }
        String translated = language.getOrDefault(key);
        if (translated.isEmpty()) {
            return;
        }
        if (translated.contains("\\n")) {
            Arrays.stream(translated.split("\\\\n")).forEach(line -> tooltip.add(Component.literal(line)));
        } else {
            tooltip.add(Component.literal(translated));
        }
    }
}
