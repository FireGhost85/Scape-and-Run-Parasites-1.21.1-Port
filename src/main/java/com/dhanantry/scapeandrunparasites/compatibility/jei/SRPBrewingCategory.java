package com.dhanantry.scapeandrunparasites.compatibility.jei;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SRPBrewingCategory implements IRecipeCategory<SRPBrewingJEIRecipe> {
    public static final RecipeType<SRPBrewingJEIRecipe> TYPE = RecipeType.create(ScapeAndRunParasites.MODID, "srp_brewing", SRPBrewingJEIRecipe.class);
    private static final ResourceLocation VANILLA_BREWING = ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
    private final IDrawable background;
    private final IDrawable icon;

    public SRPBrewingCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(VANILLA_BREWING, 0, 0, 176, 80);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Items.BREWING_STAND));
    }

    @Override
    public RecipeType<SRPBrewingJEIRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("SRP Brewing");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SRPBrewingJEIRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 51).addItemStacks(recipe.inputs());
        builder.addSlot(RecipeIngredientRole.INPUT, 79, 17).addItemStacks(recipe.reagents());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 102, 51).addItemStack(recipe.output());
    }
}
