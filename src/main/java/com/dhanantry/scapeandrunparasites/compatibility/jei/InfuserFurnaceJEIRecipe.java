package com.dhanantry.scapeandrunparasites.compatibility.jei;

import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipe;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.world.item.ItemStack;


public class InfuserFurnaceJEIRecipe
implements IRecipeWrapper {
    private final InfuserFurnaceRecipe recipe;

    public InfuserFurnaceJEIRecipe(InfuserFurnaceRecipe recipe) {
        this.recipe = recipe;
    }

    public InfuserFurnaceRecipe getRecipe() {
        return this.recipe;
    }

    public void getIngredients(@Nonnull IIngredients ingredients) {
        List<ItemStack> smeltInputs = Arrays.asList(this.recipe.smeltIn.getMatchingStacks());
        List<ItemStack> infuseInputs = Arrays.asList(this.recipe.infuseIn.getMatchingStacks());
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(smeltInputs, infuseInputs));
        ingredients.setOutputs(VanillaTypes.ITEM, Arrays.asList(this.recipe.infusedOut, this.recipe.bottleOut));
    }
}

