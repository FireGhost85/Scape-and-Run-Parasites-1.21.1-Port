package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.container.ContainerInfuserFurnace;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipe;
import com.dhanantry.scapeandrunparasites.recipes.InfuserFurnaceRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the infuser furnace: a furnace (smelt in, fuel, smelt out) with a second job. The infuse slot and the smelt
 * slot together make an infused item and give back a bottle ({@link InfuserFurnaceRecipes}). Both jobs share the fuel; each
 * cook takes 200 ticks (the infusion recipe's own time for the infusion).
 */
public class TileEntityInfuserFurnace extends BaseContainerBlockEntity implements WorldlyContainer {
    public static final int SLOT_SMELT_IN = 0;
    public static final int SLOT_FUEL = 1;
    public static final int SLOT_SMELT_OUT = 2;
    public static final int SLOT_INFUSE_IN = 3;
    public static final int SLOT_INFUSE_OUT = 4;
    public static final int SLOT_BOTTLE_OUT = 5;
    private NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int burnTime;
    private int currentBurnTime;
    private int cookTimeSmelt;
    private int cookTimeInfuse;
    private static final int COOK_TIME_TOTAL = 200;
    private static final int[] TOP = new int[]{0};
    private static final int[] BOTTOM = new int[]{2, 4, 5};
    private static final int[] SIDES = new int[]{1, 3};
    private final RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> smeltCheck = RecipeManager.createCheck(RecipeType.SMELTING);
    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int id) {
            switch (id) {
                case 0:
                    return TileEntityInfuserFurnace.this.burnTime;
                case 1:
                    return TileEntityInfuserFurnace.this.currentBurnTime;
                case 2:
                    return TileEntityInfuserFurnace.this.cookTimeSmelt;
                case 3:
                    return TileEntityInfuserFurnace.this.cookTimeInfuse;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int id, int value) {
            switch (id) {
                case 0:
                    TileEntityInfuserFurnace.this.burnTime = value;
                    break;
                case 1:
                    TileEntityInfuserFurnace.this.currentBurnTime = value;
                    break;
                case 2:
                    TileEntityInfuserFurnace.this.cookTimeSmelt = value;
                    break;
                case 3:
                    TileEntityInfuserFurnace.this.cookTimeInfuse = value;
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TileEntityInfuserFurnace(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.INFUSER_FURNACE.get(), pos, state);
    }

    private InfuserFurnaceRecipe getCurrentRecipe() {
        return InfuserFurnaceRecipes.find(this.getItem(SLOT_SMELT_IN), this.getItem(SLOT_INFUSE_IN));
    }

    public static int getBurnTime(ItemStack stack) {
        return stack.isEmpty() ? 0 : Math.max(0, stack.getBurnTime(RecipeType.SMELTING));
    }

    public static boolean isFuel(ItemStack stack) {
        return getBurnTime(stack) > 0;
    }

    public void update() {
        boolean wasBurning = this.isBurning();
        boolean dirty = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        InfuserFurnaceRecipe r = this.getCurrentRecipe();
        if (!this.isBurning() && (this.canSmelt() || this.canInfuse(r))) {
            ItemStack fuel = this.items.get(SLOT_FUEL);
            int fuelBurn = getBurnTime(fuel);
            if (fuelBurn > 0) {
                this.burnTime = fuelBurn;
                this.currentBurnTime = fuelBurn;
                dirty = true;
                ItemStack container = fuel.getCraftingRemainingItem();
                fuel.shrink(1);
                if (fuel.isEmpty()) {
                    this.items.set(SLOT_FUEL, container);
                }
            }
        }
        if (this.isBurning() && this.canSmelt()) {
            ++this.cookTimeSmelt;
            if (this.cookTimeSmelt >= COOK_TIME_TOTAL) {
                this.cookTimeSmelt = 0;
                this.doSmelt();
                dirty = true;
            }
        } else {
            this.cookTimeSmelt = 0;
        }
        if (this.isBurning() && this.canInfuse(r)) {
            ++this.cookTimeInfuse;
            if (this.cookTimeInfuse >= (r != null ? r.cookTime : COOK_TIME_TOTAL)) {
                this.cookTimeInfuse = 0;
                this.doInfuse(r);
                dirty = true;
            }
        } else {
            this.cookTimeInfuse = 0;
        }
        if (wasBurning != this.isBurning()) {
            dirty = true;
        }
        if (dirty) {
            this.setChanged();
        }
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    private ItemStack smeltingResult(ItemStack in) {
        if (this.level == null || in.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.smeltCheck.getRecipeFor(new SingleRecipeInput(in), this.level)
                .map(h -> ((RecipeHolder<? extends AbstractCookingRecipe>) h).value().assemble(new SingleRecipeInput(in), this.level.registryAccess()))
                .orElse(ItemStack.EMPTY);
    }

    private boolean canSmelt() {
        ItemStack in = this.items.get(SLOT_SMELT_IN);
        if (in.isEmpty()) {
            return false;
        }
        ItemStack result = this.smeltingResult(in);
        if (result.isEmpty()) {
            return false;
        }
        return this.canOutputStack(this.items.get(SLOT_SMELT_OUT), result);
    }

    private void doSmelt() {
        if (!this.canSmelt()) {
            return;
        }
        ItemStack in = this.items.get(SLOT_SMELT_IN);
        ItemStack result = this.smeltingResult(in).copy();
        ItemStack out = this.items.get(SLOT_SMELT_OUT);
        this.items.set(SLOT_SMELT_OUT, this.pushToOutput(out, result));
        in.shrink(1);
    }

    private boolean canInfuse(InfuserFurnaceRecipe r) {
        if (r == null) {
            return false;
        }
        if (!canMerge(this.getItem(SLOT_INFUSE_OUT), r.infusedOut)) {
            return false;
        }
        return canMerge(this.getItem(SLOT_BOTTLE_OUT), r.bottleOut);
    }

    private void doInfuse(InfuserFurnaceRecipe r) {
        if (!this.canInfuse(r)) {
            return;
        }
        this.getItem(SLOT_SMELT_IN).shrink(1);
        this.getItem(SLOT_INFUSE_IN).shrink(1);
        this.mergeInto(SLOT_INFUSE_OUT, r.infusedOut);
        this.mergeInto(SLOT_BOTTLE_OUT, r.bottleOut);
        this.setChanged();
    }

    private boolean canOutputStack(ItemStack existing, ItemStack toAdd) {
        if (toAdd.isEmpty()) {
            return false;
        }
        if (existing.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItem(existing, toAdd)) {
            return false;
        }
        int total = existing.getCount() + toAdd.getCount();
        return total <= existing.getMaxStackSize() && total <= this.getMaxStackSize();
    }

    private ItemStack pushToOutput(ItemStack existing, ItemStack toAdd) {
        if (existing.isEmpty()) {
            return toAdd;
        }
        existing.grow(toAdd.getCount());
        return existing;
    }

    private static boolean canMerge(ItemStack slotStack, ItemStack add) {
        if (add.isEmpty()) {
            return true;
        }
        if (slotStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(slotStack, add)) {
            return false;
        }
        return slotStack.getCount() + add.getCount() <= slotStack.getMaxStackSize();
    }

    private void mergeInto(int slot, ItemStack add) {
        if (add.isEmpty()) {
            return;
        }
        ItemStack cur = this.getItem(slot);
        if (cur.isEmpty()) {
            this.setItem(slot, add.copy());
        } else {
            cur.grow(add.getCount());
            this.setItem(slot, cur);
        }
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.infuser_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new ContainerInfuserFurnace(id, playerInventory, this, this.dataAccess);
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
        this.setChanged();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == SLOT_SMELT_OUT || index == SLOT_INFUSE_OUT || index == SLOT_BOTTLE_OUT) {
            return false;
        }
        if (index == SLOT_FUEL) {
            return isFuel(stack);
        }
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM;
        }
        if (side == Direction.UP) {
            return TOP;
        }
        return SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
        return this.canPlaceItem(index, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index == SLOT_SMELT_OUT || index == SLOT_INFUSE_OUT || index == SLOT_BOTTLE_OUT;
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        ContainerHelper.saveAllItems(compound, this.items, registries);
        compound.putInt("BurnTime", this.burnTime);
        compound.putInt("CurrentBurnTime", this.currentBurnTime);
        compound.putInt("CookSmelt", this.cookTimeSmelt);
        compound.putInt("CookInfuse", this.cookTimeInfuse);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.items = NonNullList.withSize(6, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(compound, this.items, registries);
        this.burnTime = compound.getInt("BurnTime");
        this.currentBurnTime = compound.getInt("CurrentBurnTime");
        this.cookTimeSmelt = compound.getInt("CookSmelt");
        this.cookTimeInfuse = compound.getInt("CookInfuse");
    }
}
