package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Block entity of the dermoid cyst: 27 slot container, counts the players that use it. */
public class TileEntityDermoidCyst extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private int numPlayersUsing = 0;
    private int ticksSinceSync = 0;

    public TileEntityDermoidCyst(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.DERMOID_CYST.get(), pos, state);
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
    protected Component getDefaultName() {
        return Component.translatable("container.dermoid_cyst");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return ChestMenu.threeRows(id, inventory, this);
    }

    public boolean hasCustomName() {
        return this.getCustomName() != null && !this.getCustomName().getString().isEmpty();
    }

    public boolean isNamedBoris() {
        if (this.getCustomName() == null) {
            return false;
        }
        String n = this.getCustomName().getString().trim();
        return "Boris".equalsIgnoreCase(n) || "Borris".equalsIgnoreCase(n);
    }

    public int getNumPlayersUsing() {
        return this.numPlayersUsing;
    }

    @Override
    public void startOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        ++this.numPlayersUsing;
        if (!this.level.isClientSide) {
            this.level.playSound(null, this.worldPosition, SRPSounds.FLESH_GROW.get(), SoundSource.BLOCKS, 10.0f, 1.0f);
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
        }
        this.markForRenderUpdate();
    }

    @Override
    public void stopOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        this.numPlayersUsing = Math.max(0, this.numPlayersUsing - 1);
        if (!this.level.isClientSide) {
            this.level.playSound(null, this.worldPosition, SRPSounds.FLESH_GROW.get(), SoundSource.BLOCKS, 10.0f, 1.0f);
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
        }
        this.markForRenderUpdate();
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == 1) {
            this.numPlayersUsing = param;
            return true;
        }
        return super.triggerEvent(id, param);
    }

    public void update() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (++this.ticksSinceSync % 80 == 0) {
            int old = this.numPlayersUsing;
            this.numPlayersUsing = 0;
            double r = 5.0;
            AABB box = new AABB(this.worldPosition).inflate(r);
            for (Player p : this.level.getEntitiesOfClass(Player.class, box)) {
                if (p.containerMenu instanceof ChestMenu chest && chest.getContainer() == this) {
                    ++this.numPlayersUsing;
                }
            }
            if (this.numPlayersUsing != old) {
                this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt)) {
            net.minecraft.world.ContainerHelper.loadAllItems(nbt, this.items, registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (!this.trySaveLootTable(nbt)) {
            net.minecraft.world.ContainerHelper.saveAllItems(nbt, this.items, registries);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private void markForRenderUpdate() {
        if (this.level != null) {
            if (!this.level.isClientSide) {
                this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
            }
        }
        this.setChanged();
    }
}
