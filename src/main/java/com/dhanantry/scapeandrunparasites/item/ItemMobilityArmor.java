package com.dhanantry.scapeandrunparasites.item;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
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

    /** The armor layer draws the custom model (ModelMobilityArmor, see MobilityArmorClient) with this texture, one for all four pieces. */
    @Override
    public ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        return ResourceLocation.fromNamespaceAndPath("srparasites", "textures/models/armor/mobility_armor.png");
    }
}
