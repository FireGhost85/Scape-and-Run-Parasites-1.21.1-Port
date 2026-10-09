package com.dhanantry.scapeandrunparasites.compatibility.jei;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/** JEI view of one brewing recipe of the mod: inputs (bottles), reagents, output. */
public record SRPBrewingJEIRecipe(List<ItemStack> inputs, List<ItemStack> reagents, ItemStack output) {
}
