package com.dhanantry.scapeandrunparasites.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public final class SRPSmelting {
    private SRPSmelting() {
    }

    private static ItemStack b(String path, int meta, int count) {
        Block blk = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("srparasites", path));
        if (blk == null) {
            throw new IllegalStateException("Missing block: srparasites:" + path);
        }
        return new ItemStack(Item.getItemFromBlock((Block)blk), count, meta);
    }

    private static ItemStack i(String path, int meta, int count) {
        Item it = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", path));
        if (it == null) {
            throw new IllegalStateException("Missing item: srparasites:" + path);
        }
        return new ItemStack(it, count, meta);
    }

    public static void register() {
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("parasiterubble", 11, 1), (ItemStack)SRPSmelting.b("parasiterubble", 13, 1), (float)0.1f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 0, 1), (ItemStack)new ItemStack(Items.COAL, 4), (float)0.1f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 1, 1), (ItemStack)new ItemStack(Items.DIAMOND, 2), (float)1.0f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 2, 1), (ItemStack)new ItemStack(Items.EMERALD, 2), (float)1.0f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 3, 1), (ItemStack)new ItemStack(Items.GOLD_INGOT, 2), (float)0.7f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 4, 1), (ItemStack)new ItemStack(Items.IRON_INGOT, 2), (float)0.7f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 5, 1), (ItemStack)new ItemStack(Items.DYE, 9, 4), (float)0.2f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 6, 1), (ItemStack)new ItemStack(Items.REDSTONE, 7), (float)0.3f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infestedore", 7, 1), (ItemStack)SRPSmelting.i("lurecomponent6", 0, 1), (float)1.0f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.i("bloody_rod", 0, 1), (ItemStack)new ItemStack(Items.BLAZE_ROD), (float)0.1f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.i("bloody_bone", 0, 1), (ItemStack)new ItemStack(Items.BONE), (float)0.1f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.i("bloody_iron_ingot", 0, 1), (ItemStack)new ItemStack(Items.IRON_INGOT), (float)0.1f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("infested_cobblestone", 0, 1), (ItemStack)SRPSmelting.b("infestedrubble", 0, 1), (float)0.1f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.i("parasitestain", 2, 1), (ItemStack)SRPSmelting.i("cooked_flesh", 0, 1), (float)0.35f);
        GameRegistry.addSmelting((ItemStack)SRPSmelting.b("parasiterubble", 3, 2), (ItemStack)SRPSmelting.i("hive_scrap", 0, 1), (float)0.1f);
    }
}

