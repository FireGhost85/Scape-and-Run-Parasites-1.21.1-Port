package com.dhanantry.scapeandrunparasites.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

/**
 * Mobility armor pieces. In 1.12 they sit in the vanilla combat tab, use the leather texture as fallback and a custom model
 * (ModelMobilityArmor, a client extension registered in the client milestone); durability multiplier 28.
 */
public class ItemMobilityArmor extends ArmorItem {
    public ItemMobilityArmor(String registryName, Holder<ArmorMaterial> material, ArmorItem.Type type) {
        super(material, type, new Item.Properties().durability(type.getDurability(28)));
    }
}
