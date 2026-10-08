package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.container.ContainerParasiteLoot;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Inventory (27 slots) of the parasite loot block; accepts only non-block items of the mod. */
public class TileEntityParasiteLoot extends BaseContainerBlockEntity {
    private static final int INV_SIZE = 27;
    private NonNullList<ItemStack> items = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);

    public TileEntityParasiteLoot(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.PARASITE_LOOT.get(), pos, state);
    }

    public float getFullness() {
        int total = this.getTotalSlots();
        if (total <= 0) {
            return 0.0f;
        }
        return (float) this.getUsedSlotCount() / (float) total;
    }

    public int getFullnessField() {
        return (int) Math.round((double) this.getFullness() * 1000.0);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInventory) {
        return new ContainerParasiteLoot(id, playerInventory, this);
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
    public boolean isEmpty() {
        for (ItemStack s : this.items) {
            if (s.isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        ItemStack s = ContainerHelper.removeItem(this.items, index, count);
        this.setChanged();
        return s;
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack s = ContainerHelper.takeItem(this.items, index);
        this.setChanged();
        return s;
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        if (!stack.isEmpty() && !this.canPlaceItem(index, stack)) {
            stack = ItemStack.EMPTY;
        }
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
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        return isValidParasiteLootItem(stack);
    }

    public static boolean isValidParasiteLootItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        if (stack.getItem() instanceof BlockItem) {
            return false;
        }
        if (BuiltInRegistries.ITEM.getKey(stack.getItem()) == null) {
            return false;
        }
        return "srparasites".equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
    }

    public int getField(int id) {
        return id == 0 ? this.getFullnessField() : 0;
    }

    public void setField(int id, int value) {
    }

    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.srparasites.parasite_loot");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, this.items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(INV_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, registries);
    }

    public int getTotalSlots() {
        return this.getContainerSize();
    }

    public int getFreeSlotCount() {
        int free = 0;
        for (int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                continue;
            }
            ++free;
        }
        return free;
    }

    public int getUsedSlotCount() {
        return this.getTotalSlots() - this.getFreeSlotCount();
    }
}
