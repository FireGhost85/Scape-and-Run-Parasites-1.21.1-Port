package com.dhanantry.scapeandrunparasites.container;

import com.dhanantry.scapeandrunparasites.init.SRPMenus;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfuserFurnace;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Menu of the infuser furnace. Menu slot order as in 1.12: smelt input, fuel, infuse input, infusion output, bottle output,
 * smelt output; then the inventory. The three output slots hand out experience for the items that have a smelting recipe.
 * The shift click merges into the menu slots 0, 1 and 3 exactly like the original (slot 3 is an output, so the infuse
 * input cannot be filled by shift click).
 */
public class ContainerInfuserFurnace extends AbstractContainerMenu {
    private final Container container;
    private final ContainerData data;

    public ContainerInfuserFurnace(int id, Inventory playerInv) {
        this(id, playerInv, new SimpleContainer(6), new SimpleContainerData(4));
    }

    public ContainerInfuserFurnace(int id, Inventory playerInv, Container container, ContainerData data) {
        super(SRPMenus.INFUSER_FURNACE.get(), id);
        checkContainerSize(container, 6);
        checkContainerDataCount(data, 4);
        this.container = container;
        this.data = data;
        this.addSlot(new Slot(container, 0, 56, 17));
        this.addSlot(new Slot(container, 1, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return TileEntityInfuserFurnace.isFuel(stack);
            }
        });
        this.addSlot(new Slot(container, 3, 30, 35));
        this.addSlot(new OutputSlot(playerInv.player, container, 4, 140, 53));
        this.addSlot(new OutputSlot(playerInv.player, container, 5, 116, 35));
        this.addSlot(new OutputSlot(playerInv.player, container, 2, 140, 17));
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
        this.addDataSlots(data);
    }

    public int getBurnTime() {
        return this.data.get(0);
    }

    public int getCurrentBurnTime() {
        return this.data.get(1);
    }

    public int getCookSmelt() {
        return this.data.get(2);
    }

    public int getCookInfuse() {
        return this.data.get(3);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        int invEnd = this.slots.size();
        boolean failed;
        if (index < 6) {
            failed = !this.moveItemStackTo(stack, 6, invEnd, true);
        } else if (TileEntityInfuserFurnace.isFuel(stack)) {
            failed = !this.moveItemStackTo(stack, 1, 2, false);
        } else {
            failed = !this.moveItemStackTo(stack, 0, 1, false) && !this.moveItemStackTo(stack, 3, 4, false);
        }
        if (failed) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    /** 1.12 {@code SlotFurnaceOutput}: nothing can be put in, taking items hands out smelting experience. */
    private static final class OutputSlot extends Slot {
        private final Player player;
        private int removeCount;

        OutputSlot(Player player, Container container, int index, int x, int y) {
            super(container, index, x, y);
            this.player = player;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public ItemStack remove(int amount) {
            if (this.hasItem()) {
                this.removeCount += Math.min(amount, this.getItem().getCount());
            }
            return super.remove(amount);
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            this.checkTakeAchievements(stack);
            super.onTake(player, stack);
        }

        @Override
        protected void onQuickCraft(ItemStack stack, int amount) {
            this.removeCount += amount;
            this.checkTakeAchievements(stack);
        }

        @Override
        protected void checkTakeAchievements(ItemStack stack) {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            if (this.player instanceof ServerPlayer serverPlayer) {
                float xp = smeltingExperience(serverPlayer.serverLevel(), stack);
                float total = this.removeCount * xp;
                int whole = Mth.floor(total);
                if (Mth.frac(total) != 0.0f && Math.random() < Mth.frac(total)) {
                    ++whole;
                }
                if (xp != 0.0f) {
                    ExperienceOrb.award(serverPlayer.serverLevel(), serverPlayer.position(), whole);
                }
            }
            this.removeCount = 0;
        }
    }

    /** {@code FurnaceRecipes.getSmeltingExperience}: the experience of the smelting recipe that makes the item. */
    public static float smeltingExperience(ServerLevel level, ItemStack stack) {
        for (RecipeHolder<? extends AbstractCookingRecipe> holder : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
            AbstractCookingRecipe recipe = holder.value();
            if (ItemStack.isSameItem(recipe.getResultItem(level.registryAccess()), stack)) {
                return recipe.getExperience();
            }
        }
        return 0.0f;
    }
}
