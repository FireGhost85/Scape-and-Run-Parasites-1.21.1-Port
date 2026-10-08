package com.dhanantry.scapeandrunparasites.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** The Sign charm (the effect itself is handled by the The Sign potion). */
public class ItemTheSignCharm extends ItemBase {
    public ItemTheSignCharm(String name) {
        super(name, 1, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.the_sign_charm.red").withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.srparasites.the_sign_charm.white").withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("tooltip.srparasites.the_sign_charm.gray").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
}
