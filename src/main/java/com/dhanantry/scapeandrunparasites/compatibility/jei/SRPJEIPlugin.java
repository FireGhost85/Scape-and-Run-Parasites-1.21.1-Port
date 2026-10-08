package com.dhanantry.scapeandrunparasites.compatibility.jei;

import com.dhanantry.scapeandrunparasites.compatibility.jei.InfuserFurnaceJEICategory;
import com.dhanantry.scapeandrunparasites.compatibility.jei.InfuserFurnaceJEIRecipe;
import com.dhanantry.scapeandrunparasites.compatibility.jei.SRPBrewingCategory;
import com.dhanantry.scapeandrunparasites.compatibility.jei.SRPBrewingJEIRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipes;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;


@JEIPlugin
public class SRPJEIPlugin
implements IModPlugin {
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new IRecipeCategory[]{new InfuserFurnaceJEICategory(registry.getJeiHelpers().getGuiHelper())});
        registry.addRecipeCategories(new IRecipeCategory[]{new SRPBrewingCategory(registry.getJeiHelpers().getGuiHelper())});
    }

    public void register(IModRegistry registry) {
        ItemStack awkwardPotion;
        ItemStack waterPotion;
        ItemStack infuserStack;
        ArrayList<InfuserFurnaceJEIRecipe> jeiRecipes = new ArrayList<InfuserFurnaceJEIRecipe>();
        for (InfuserFurnaceRecipe r : InfuserFurnaceRecipes.all()) {
            jeiRecipes.add(new InfuserFurnaceJEIRecipe(r));
        }
        registry.addRecipes(jeiRecipes, "srparasites.infuser_furnace");
        Block infuser = (Block)ForgeRegistries.BLOCKS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "infuser_furnace"));
        if (infuser != null && infuser != Blocks.AIR && !(infuserStack = new ItemStack(infuser)).isEmpty()) {
            registry.addRecipeCatalyst(infuserStack, new String[]{"srparasites.infuser_furnace"});
        }
        String MODID = "srparasites";
        Item alveolar = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "alveolar_fluid"));
        Item diseasedSponge = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "diseased_sponge"));
        Item deadblood = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "deadblood_fluid"));
        Item thornshadeBerry = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "thornshade_berry"));
        Item thornshadeDecanter = (Item)ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "thornshade_decanter"));
        Potion FEAR = (Potion)ForgeRegistries.POTION_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("srparasites", "fear"));
        Potion WATER = (Potion)ForgeRegistries.POTION_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "water"));
        Potion AWKWARD = (Potion)ForgeRegistries.POTION_TYPES.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", "awkward"));
        ArrayList<SRPBrewingJEIRecipe> brewing = new ArrayList<SRPBrewingJEIRecipe>();
        if (alveolar != null && FEAR != null) {
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(new ItemStack(alveolar)), Arrays.asList(new ItemStack(Items.FLINT)), PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.POTIONITEM), (Potion)FEAR)));
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.POTIONITEM), (Potion)FEAR)), Arrays.asList(new ItemStack(Items.GUNPOWDER)), PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.SPLASH_POTION), (Potion)FEAR)));
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.SPLASH_POTION), (Potion)FEAR)), Arrays.asList(new ItemStack(Items.DRAGON_BREATH)), PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.LINGERING_POTION), (Potion)FEAR)));
        }
        if (diseasedSponge != null && deadblood != null && WATER != null && AWKWARD != null) {
            waterPotion = PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.POTIONITEM), (Potion)WATER);
            awkwardPotion = PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.POTIONITEM), (Potion)AWKWARD);
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(waterPotion, awkwardPotion), Arrays.asList(new ItemStack(diseasedSponge)), new ItemStack(deadblood)));
        }
        if (thornshadeBerry != null && thornshadeDecanter != null && WATER != null && AWKWARD != null) {
            waterPotion = PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.POTIONITEM), (Potion)WATER);
            awkwardPotion = PotionContents.addPotionToItemStack((ItemStack)new ItemStack((Item)Items.POTIONITEM), (Potion)AWKWARD);
            brewing.add(new SRPBrewingJEIRecipe(Arrays.asList(waterPotion, awkwardPotion), Arrays.asList(new ItemStack(thornshadeBerry)), new ItemStack(thornshadeDecanter)));
        }
        if (!brewing.isEmpty()) {
            registry.addRecipes(brewing, "srparasites.srp_brewing");
            ItemStack brewingStandStack = new ItemStack(Items.BREWING_STAND);
            if (!brewingStandStack.isEmpty()) {
                registry.addRecipeCatalyst(brewingStandStack, new String[]{"srparasites.srp_brewing"});
            }
        }
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "dark_days_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "adapted_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "primitive_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "crude_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "pure_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "hunt_season_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "guerilla_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "ecstasy_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "enemy_of_enemy_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "fog_nullifier_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "self_destruct_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "potion_columbus_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "potion_stolas_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "hellfire_chemical_warfare_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "cosmic_structural_failure_icon");
        SRPJEIPlugin.blacklistAdvancementIcon(registry, "roots_icon");
    }

    private static void blacklistAdvancementIcon(IModRegistry registry, String itemName) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath("srparasites", itemName);
        Item item = (Item)ForgeRegistries.ITEMS.getValue(id);
        if (item == null || item == Items.AIR) {
            System.out.println("[SRP][JEI] Missing advancement icon item: " + id);
            return;
        }
        ItemStack stack = new ItemStack(item);
        if (stack.isEmpty()) {
            System.out.println("[SRP][JEI] Empty advancement icon stack: " + id);
            return;
        }
        try {
            registry.getJeiHelpers().getIngredientBlacklist().addIngredientToBlacklist(stack);
            System.out.println("[SRP][JEI] Blacklisted advancement icon: " + id);
        }
        catch (Throwable t) {
            System.out.println("[SRP][JEI] Failed to blacklist advancement icon: " + id);
            t.printStackTrace();
        }
    }
}

