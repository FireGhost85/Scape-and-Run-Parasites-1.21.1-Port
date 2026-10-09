package com.dhanantry.scapeandrunparasites.compatibility.jei;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipes;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;

/** JEI: the infuser furnace recipes, the brewing recipes of the mod, and the hidden advancement icon items. */
@JeiPlugin
public class SRPJEIPlugin implements IModPlugin {
    private static final String[] HIDDEN_ICONS = {"dark_days_icon", "adapted_icon", "primitive_icon", "crude_icon", "pure_icon", "hunt_season_icon",
            "guerilla_icon", "ecstasy_icon", "enemy_of_enemy_icon", "fog_nullifier_icon", "self_destruct_icon", "potion_columbus_icon",
            "potion_stolas_icon", "hellfire_chemical_warfare_icon", "cosmic_structural_failure_icon", "roots_icon"};

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new InfuserFurnaceJEICategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new SRPBrewingCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    private static Item item(String name) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, name));
    }

    private static ItemStack potion(Item base, Holder<Potion> potion) {
        return PotionContents.createItemStack(base, potion);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry) {
        List<InfuserFurnaceJEIRecipe> infuser = new ArrayList<>();
        for (InfuserFurnaceRecipe r : InfuserFurnaceRecipes.all()) {
            infuser.add(new InfuserFurnaceJEIRecipe(r));
        }
        registry.addRecipes(InfuserFurnaceJEICategory.TYPE, infuser);

        Item alveolar = item("alveolar_fluid");
        Item diseasedSponge = item("diseased_sponge");
        Item deadblood = item("deadblood_fluid");
        Item thornshadeBerry = item("thornshade_berry");
        Item thornshadeDecanter = item("thornshade_decanter");
        Holder<Potion> fear = SRPPotions.FEAR_P;
        List<SRPBrewingJEIRecipe> brewing = new ArrayList<>();
        if (alveolar != Items.AIR) {
            ItemStack fearBottle = potion(Items.POTION, fear);
            ItemStack fearSplash = potion(Items.SPLASH_POTION, fear);
            brewing.add(new SRPBrewingJEIRecipe(List.of(new ItemStack(alveolar)), List.of(new ItemStack(Items.FLINT)), fearBottle));
            brewing.add(new SRPBrewingJEIRecipe(List.of(fearBottle), List.of(new ItemStack(Items.GUNPOWDER)), fearSplash));
            brewing.add(new SRPBrewingJEIRecipe(List.of(fearSplash), List.of(new ItemStack(Items.DRAGON_BREATH)), potion(Items.LINGERING_POTION, fear)));
        }
        ItemStack water = potion(Items.POTION, Potions.WATER);
        ItemStack awkward = potion(Items.POTION, Potions.AWKWARD);
        if (diseasedSponge != Items.AIR && deadblood != Items.AIR) {
            brewing.add(new SRPBrewingJEIRecipe(List.of(water, awkward), List.of(new ItemStack(diseasedSponge)), new ItemStack(deadblood)));
        }
        if (thornshadeBerry != Items.AIR && thornshadeDecanter != Items.AIR) {
            brewing.add(new SRPBrewingJEIRecipe(List.of(water, awkward), List.of(new ItemStack(thornshadeBerry)), new ItemStack(thornshadeDecanter)));
        }
        if (!brewing.isEmpty()) {
            registry.addRecipes(SRPBrewingCategory.TYPE, brewing);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
        registry.addRecipeCatalyst(new ItemStack(SRPBlocks.InfuserFurnace.get()), InfuserFurnaceJEICategory.TYPE);
        registry.addRecipeCatalyst(new ItemStack(Items.BREWING_STAND), SRPBrewingCategory.TYPE);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        List<ItemStack> hidden = new ArrayList<>();
        for (String name : HIDDEN_ICONS) {
            Item item = item(name);
            if (item != Items.AIR) {
                hidden.add(new ItemStack(item));
            }
        }
        if (!hidden.isEmpty()) {
            runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden);
        }
    }
}
