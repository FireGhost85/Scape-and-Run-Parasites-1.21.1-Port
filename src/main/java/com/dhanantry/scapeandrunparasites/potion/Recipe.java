package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

/** Brewing recipes of SRP (Recipe.init() of 1.10.9 registered through BrewingRecipeRegistry). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class Recipe {
    private Recipe() {
    }

    private static Item item(String path) {
        return BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path)).orElse(null);
    }

    @SubscribeEvent
    public static void onRegisterBrewingRecipes(RegisterBrewingRecipesEvent event) {
        Item alveolar = item("alveolar_fluid");
        Item diseasedSponge = item("diseased_sponge");
        Item deadblood = item("deadblood_fluid");
        Holder<Potion> FEAR = SRPPotions.FEAR_P;
        Holder<Potion> WATER = Potions.WATER;
        Holder<Potion> AWKWARD = Potions.AWKWARD;
        Item thornshadeBerry = item("thornshade_berry");
        Item thornshadeDecanter = item("thornshade_decanter");
        if (alveolar == null) {
            ScapeAndRunParasites.LOGGER.error("[SRPBrewing] Missing item: srparasites:alveolar_fluid");
            return;
        }
        if (diseasedSponge == null) {
            ScapeAndRunParasites.LOGGER.error("[SRPBrewing] Missing item: srparasites:diseased_sponge");
            return;
        }
        if (deadblood == null) {
            ScapeAndRunParasites.LOGGER.error("[SRPBrewing] Missing item: srparasites:deadblood_fluid");
            return;
        }
        if (thornshadeBerry == null) {
            ScapeAndRunParasites.LOGGER.error("[SRPBrewing] Missing item: srparasites:thornshade_berry");
            return;
        }
        if (thornshadeDecanter == null) {
            ScapeAndRunParasites.LOGGER.error("[SRPBrewing] Missing item: srparasites:thornshade_decanter");
            return;
        }
        event.getBuilder().addRecipe(new BaseToFearRecipe(alveolar, FEAR));
        event.getBuilder().addRecipe(new FearToSplashRecipe(FEAR));
        event.getBuilder().addRecipe(new FearSplashToLingeringRecipe(FEAR));
        event.getBuilder().addRecipe(new SpongeToDeadbloodRecipe(diseasedSponge, deadblood, WATER, AWKWARD));
        event.getBuilder().addRecipe(new BerryToThornshadeDecanterRecipe(thornshadeBerry, thornshadeDecanter, WATER, AWKWARD));
    }

    private static boolean hasPotion(ItemStack stack, Holder<Potion> potion) {
        return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).is(potion);
    }

    private static final class FearSplashToLingeringRecipe implements IBrewingRecipe {
        private final Holder<Potion> fear;

        private FearSplashToLingeringRecipe(Holder<Potion> fear) {
            this.fear = fear;
        }

        @Override
        public boolean isInput(ItemStack input) {
            return !input.isEmpty() && input.getItem() == Items.SPLASH_POTION && hasPotion(input, this.fear);
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == Items.DRAGON_BREATH;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return PotionContents.createItemStack(Items.LINGERING_POTION, this.fear);
            }
            return ItemStack.EMPTY;
        }
    }

    private static final class FearToSplashRecipe implements IBrewingRecipe {
        private final Holder<Potion> fear;

        private FearToSplashRecipe(Holder<Potion> fear) {
            this.fear = fear;
        }

        @Override
        public boolean isInput(ItemStack input) {
            return !input.isEmpty() && input.getItem() == Items.POTION && hasPotion(input, this.fear);
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == Items.GUNPOWDER;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return PotionContents.createItemStack(Items.SPLASH_POTION, this.fear);
            }
            return ItemStack.EMPTY;
        }
    }

    private static final class BaseToFearRecipe implements IBrewingRecipe {
        private final Item baseItem;
        private final Holder<Potion> fear;

        private BaseToFearRecipe(Item baseItem, Holder<Potion> fear) {
            this.baseItem = baseItem;
            this.fear = fear;
        }

        @Override
        public boolean isInput(ItemStack input) {
            return !input.isEmpty() && input.getItem() == this.baseItem;
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == Items.FLINT;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return PotionContents.createItemStack(Items.POTION, this.fear);
            }
            return ItemStack.EMPTY;
        }
    }

    /** Shared by the sponge and berry recipes: a water or awkward potion plus one SRP item yields the item. */
    private abstract static class PotionToItemRecipe implements IBrewingRecipe {
        private final Item ingredientItem;
        private final Item outItem;
        private final Holder<Potion> water;
        private final Holder<Potion> awkward;

        private PotionToItemRecipe(Item ingredientItem, Item outItem, Holder<Potion> water, Holder<Potion> awkward) {
            this.ingredientItem = ingredientItem;
            this.outItem = outItem;
            this.water = water;
            this.awkward = awkward;
        }

        @Override
        public boolean isInput(ItemStack input) {
            if (input.isEmpty()) {
                return false;
            }
            if (input.getItem() != Items.POTION) {
                return false;
            }
            return hasPotion(input, this.water) || hasPotion(input, this.awkward);
        }

        @Override
        public boolean isIngredient(ItemStack ingredient) {
            return !ingredient.isEmpty() && ingredient.getItem() == this.ingredientItem;
        }

        @Override
        public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
            if (this.isInput(input) && this.isIngredient(ingredient)) {
                return new ItemStack(this.outItem, 1);
            }
            return ItemStack.EMPTY;
        }
    }

    private static final class BerryToThornshadeDecanterRecipe extends PotionToItemRecipe {
        private BerryToThornshadeDecanterRecipe(Item ingredientItem, Item outItem, Holder<Potion> water, Holder<Potion> awkward) {
            super(ingredientItem, outItem, water, awkward);
        }
    }

    private static final class SpongeToDeadbloodRecipe extends PotionToItemRecipe {
        private SpongeToDeadbloodRecipe(Item ingredientItem, Item outItem, Holder<Potion> water, Holder<Potion> awkward) {
            super(ingredientItem, outItem, water, awkward);
        }
    }
}
