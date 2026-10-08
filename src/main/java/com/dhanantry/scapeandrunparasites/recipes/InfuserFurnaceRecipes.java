package com.dhanantry.scapeandrunparasites.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.ItemStack;

/** Registry of the infuser furnace recipes (code registered like the 1.12 original, see {@link SRPInfuserFurnaceRecipeInit}). */
public final class InfuserFurnaceRecipes {
    private static final List<InfuserFurnaceRecipe> RECIPES = new ArrayList<>();

    private InfuserFurnaceRecipes() {
    }

    public static void add(InfuserFurnaceRecipe recipe) {
        RECIPES.add(recipe);
    }

    @Nullable
    public static InfuserFurnaceRecipe find(ItemStack smelt, ItemStack infuse) {
        if (smelt == null || smelt.isEmpty() || infuse == null || infuse.isEmpty()) {
            return null;
        }
        for (InfuserFurnaceRecipe r : RECIPES) {
            if (r.matches(smelt, infuse)) {
                return r;
            }
        }
        return null;
    }

    public static List<InfuserFurnaceRecipe> all() {
        return Collections.unmodifiableList(RECIPES);
    }

    public static void clear() {
        RECIPES.clear();
    }
}
