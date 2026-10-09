package com.dhanantry.scapeandrunparasites.bestiary.blocks;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class BlockBestiaryEntry {
    public final ResourceLocation id;
    public final Block block;
    public final ItemStack icon;
    public final String nameKey;
    public final String loreKey;

    public BlockBestiaryEntry(Block block, String nameKey, String loreKey) {
        this.block = block;
        this.id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        this.icon = new ItemStack(block);
        this.nameKey = nameKey;
        this.loreKey = loreKey;
    }
}

