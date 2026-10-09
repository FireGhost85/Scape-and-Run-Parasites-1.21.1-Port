package com.dhanantry.scapeandrunparasites.container;

import com.dhanantry.scapeandrunparasites.init.SRPMenus;
import com.dhanantry.scapeandrunparasites.item.ItemModule;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

/** ScannerContainer of 1.10.9: the module slot of the relay controller. The cooldown and the formed flag are synced as menu data (replaces MsgScanCooldown). */
public class ScannerContainer extends AbstractContainerMenu {
    @Nullable
    private final TileEntityRelayController te;
    private final ContainerData data;

    private BlockPos pos = BlockPos.ZERO;

    public ScannerContainer(int id, Inventory playerInv, net.minecraft.network.FriendlyByteBuf buf) {
        this(id, playerInv, new ItemStackHandler(1), null, new SimpleContainerData(3));
        this.pos = buf.readBlockPos();
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ScannerContainer(int id, Inventory playerInv, TileEntityRelayController te) {
        this(id, playerInv, te.getHandler(), te, new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> te.isFormed() ? 1 : 0;
                    case 1 -> te.getCooldownRemainingTicks();
                    default -> te.getCooldownTotalTicks();
                };
            }

            @Override
            public void set(int index, int value) {
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    private ScannerContainer(int id, Inventory playerInv, IItemHandler handler, @Nullable TileEntityRelayController te, ContainerData data) {
        super(SRPMenus.SCANNER.get(), id);
        this.te = te;
        this.data = data;
        this.addSlot(new SlotItemHandler(handler, 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty() && stack.getItem() instanceof ItemModule;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        int xStart = 8;
        int yStart = 84;
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, xStart + col * 18, yStart + row * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInv, i, xStart + i * 18, yStart + 58));
        }
        this.addDataSlots(data);
    }

    public boolean isFormed() {
        return this.data.get(0) != 0;
    }

    public int getCooldownRemaining() {
        return this.data.get(1);
    }

    public int getCooldownTotal() {
        return this.data.get(2);
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.te == null) {
            return true;
        }
        if (this.te.getLevel() == null || this.te.getLevel().getBlockEntity(this.te.getBlockPos()) != this.te) {
            return false;
        }
        return player.distanceToSqr(this.te.getBlockPos().getX() + 0.5, this.te.getBlockPos().getY() + 0.5, this.te.getBlockPos().getZ() + 0.5) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack in = slot.getItem();
            ret = in.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(in, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (in.getItem() instanceof ItemModule) {
                if (!this.moveItemStackTo(in, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
            if (in.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return ret;
    }
}
