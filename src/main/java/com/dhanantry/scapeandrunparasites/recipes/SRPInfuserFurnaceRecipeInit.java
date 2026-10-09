package com.dhanantry.scapeandrunparasites.recipes;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * The ten infuser furnace recipes of the original. Counts of the ingredients were ignored by the original ({@code Ingredient}
 * matched the item only) and one of each input is used per cook, so only the items are listed. Dead blood bottle plus iron ingot
 * make a semiorganic ingot; dead blood bottle plus infested sand makes infested glass; sixteen-block swaps are listed as in the
 * original (the counts there never mattered).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPInfuserFurnaceRecipeInit {
    private SRPInfuserFurnaceRecipeInit() {
    }

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SRPInfuserFurnaceRecipeInit::init);
    }

    private static void addGlassSwap(ItemLike y16, ItemStack x32) {
        InfuserFurnaceRecipes.add(new InfuserFurnaceRecipe(Ingredient.of(SRPBlocks.InfestedGlass.get()), Ingredient.of(y16), x32, ItemStack.EMPTY, 200));
    }

    public static void init() {
        InfuserFurnaceRecipes.clear();
        Item deadBlood = SRPItems.DEADBLOOD_FLUID.get();
        InfuserFurnaceRecipes.add(new InfuserFurnaceRecipe(Ingredient.of(Items.IRON_INGOT), Ingredient.of(deadBlood),
                new ItemStack(SRPItems.semiorganicingot.get(), 1), new ItemStack(Items.GLASS_BOTTLE, 1), 200));
        InfuserFurnaceRecipes.add(new InfuserFurnaceRecipe(Ingredient.of(SRPBlocks.InfestedSand.get()), Ingredient.of(deadBlood),
                new ItemStack(SRPBlocks.InfestedGlass.get(), 1), new ItemStack(Items.GLASS_BOTTLE, 1), 200));
        addGlassSwap(SRPBlocks.CookedFlesh.get(), new ItemStack(SRPBlocks.BloodyGlass.get(), 2));
        addGlassSwap(SRPBlocks.InfestedTerracotta.get(), new ItemStack(SRPBlocks.AshenGlass.get(), 2));
        addGlassSwap(SRPBlocks.PolandSkinBlock.get(), new ItemStack(SRPBlocks.SepiaGlass.get(), 2));
        addGlassSwap(SRPBlocks.HarleskinnBlock.get(), new ItemStack(SRPBlocks.HarlequinnGlass.get(), 2));
        addGlassSwap(Blocks.ICE, new ItemStack(SRPBlocks.ShroudedGlass.get(), 2));
        addGlassSwap(SRPBlocks.ResidueBlock.get(), new ItemStack(SRPBlocks.MoodyGlass.get(), 2));
        addGlassSwap(SRPBlocks.gothShroom.get(), new ItemStack(SRPBlocks.ShadeGlass.get(), 2));
    }
}
