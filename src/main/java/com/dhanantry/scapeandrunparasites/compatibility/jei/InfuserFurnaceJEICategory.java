package com.dhanantry.scapeandrunparasites.compatibility.jei;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class InfuserFurnaceJEICategory implements IRecipeCategory<InfuserFurnaceJEIRecipe> {
    public static final RecipeType<InfuserFurnaceJEIRecipe> TYPE = RecipeType.create(ScapeAndRunParasites.MODID, "infuser_furnace", InfuserFurnaceJEIRecipe.class);
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/infuser_furnace.png");
    private static final ResourceLocation VANILLA_FURNACE = ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public InfuserFurnaceJEICategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(BG, 0, 0, 176, 82);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(SRPBlocks.InfuserFurnace.get()));
        IDrawableStatic arrowStatic = guiHelper.createDrawable(VANILLA_FURNACE, 176, 14, 24, 17);
        this.arrow = guiHelper.createAnimatedDrawable(arrowStatic, 200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<InfuserFurnaceJEIRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Infuser Furnace");
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
    public void setRecipe(IRecipeLayoutBuilder builder, InfuserFurnaceJEIRecipe wrapper, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 56, 17).addIngredients(wrapper.recipe().smeltIn);
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 35).addIngredients(wrapper.recipe().infuseIn);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 116, 35).addItemStack(wrapper.recipe().infusedOut);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 140, 53).addItemStack(wrapper.recipe().bottleOut);
    }

    @Override
    public void draw(InfuserFurnaceJEIRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 79, 34);
    }
}
