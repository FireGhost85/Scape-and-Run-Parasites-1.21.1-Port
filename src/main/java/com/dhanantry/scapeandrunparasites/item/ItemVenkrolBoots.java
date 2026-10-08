package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPArmorMaterials;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Venkrol boots (armor layer "venkrol_boot"). */
public class ItemVenkrolBoots extends ArmorItem {
    public ItemVenkrolBoots(String name) {
        super(SRPArmorMaterials.VENKROL_BOOT, ArmorItem.Type.BOOTS, new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(40)));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.item.venkrol_boots.0"));
    }
}
