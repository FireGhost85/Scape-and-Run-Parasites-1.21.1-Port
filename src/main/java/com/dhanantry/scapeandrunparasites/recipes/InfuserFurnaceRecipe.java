package com.dhanantry.scapeandrunparasites.recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/** One recipe of the infuser furnace: a smelt input and an infuse input give an infused stack and a returned bottle. */
public final class InfuserFurnaceRecipe {
    public final Ingredient smeltIn;
    public final Ingredient infuseIn;
    public final ItemStack infusedOut;
    public final ItemStack bottleOut;
    public final int cookTime;

    public InfuserFurnaceRecipe(Ingredient smeltIn, Ingredient infuseIn, ItemStack infusedOut, ItemStack bottleOut, int cookTime) {
        this.smeltIn = smeltIn;
        this.infuseIn = infuseIn;
        this.infusedOut = infusedOut.copy();
        this.bottleOut = bottleOut.copy();
        this.cookTime = cookTime;
    }

    public boolean matches(ItemStack smeltStack, ItemStack infuseStack) {
        return this.smeltIn.test(smeltStack) && this.infuseIn.test(infuseStack);
    }
}
