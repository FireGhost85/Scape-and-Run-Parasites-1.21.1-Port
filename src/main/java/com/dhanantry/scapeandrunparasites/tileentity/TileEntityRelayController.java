package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.item.ItemModule;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

/**
 * Block entity of the relay controller: the formed flag, the 38 node blocks of the multiblock, the module slot and the scan
 * cooldown. The scanning itself (reports on vectors, phase, dislodgements and the module scans, the scan registry) is ported
 * with the Relay in M6 together with the module and report items; see PORTING_NOTES.
 */
public class TileEntityRelayController extends BlockEntity {
    public boolean formed = false;
    private long nextScanTick = 0L;
    private static final String NBT_NEXT_SCAN = "NextScanTick";
    private final List<BlockPos> childPositions = new ArrayList<>();
    private boolean dismantling = false;
    private static final int SCAN_RADIUS = 8;
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack != null && !stack.isEmpty() && stack.getItem() instanceof ItemModule;
        }

        @Override
        protected void onContentsChanged(int slot) {
            TileEntityRelayController.this.setChanged();
        }
    };

    public TileEntityRelayController(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.RELAY_CONTROLLER.get(), pos, state);
    }

    public void setChildPositions(List<BlockPos> positions) {
        this.childPositions.clear();
        if (positions != null) {
            this.childPositions.addAll(positions);
        }
        this.setChanged();
    }

    private int scannerCooldownTicks() {
        return SRPConfigSystems.getScannerCooldownTicks();
    }

    public boolean canScan() {
        return this.level == null || this.level.getGameTime() >= this.nextScanTick;
    }

    public int getCooldownRemainingTicks() {
        if (this.level == null) {
            return 0;
        }
        long t = this.nextScanTick - this.level.getGameTime();
        return t > 0L ? (int) t : 0;
    }

    public int getCooldownTotalTicks() {
        return this.scannerCooldownTicks();
    }

    public void startCooldown() {
        if (this.level == null) {
            return;
        }
        this.nextScanTick = this.level.getGameTime() + this.scannerCooldownTicks();
        this.setChanged();
        if (!this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
        }
    }

    public void addChild(BlockPos p) {
        if (p != null && !this.childPositions.contains(p)) {
            this.childPositions.add(p);
            this.setChanged();
        }
    }

    public ItemStackHandler getHandler() {
        return this.itemHandler;
    }

    public void dropContents() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        ItemStack stack = this.itemHandler.getStackInSlot(0);
        if (!stack.isEmpty()) {
            Containers.dropItemStack(this.level, this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, stack.copy());
            this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            this.setChanged();
        }
    }

    public void setFormed(boolean val) {
        if (this.formed == val) {
            return;
        }
        this.formed = val;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState s = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        nbt.putBoolean("Formed", this.formed);
        nbt.putLong(NBT_NEXT_SCAN, this.nextScanTick);
        ListTag list = new ListTag();
        for (BlockPos p : this.childPositions) {
            if (p != null) {
                list.add(NbtUtils.writeBlockPos(p));
            }
        }
        nbt.put("Children", list);
        nbt.put("Inv", this.itemHandler.serializeNBT(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.nextScanTick = nbt.getLong(NBT_NEXT_SCAN);
        this.formed = nbt.getBoolean("Formed");
        this.childPositions.clear();
        ListTag list = nbt.getList("Children", Tag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); ++i) {
            int[] a = list.getIntArray(i);
            if (a.length == 3) {
                this.childPositions.add(new BlockPos(a[0], a[1], a[2]));
            }
        }
        if (nbt.contains("Inv", Tag.TAG_COMPOUND)) {
            this.itemHandler.deserializeNBT(registries, nbt.getCompound("Inv"));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        this.relinkChildrenToMe();
        if (this.nextScanTick < 0L) {
            this.nextScanTick = 0L;
            this.setChanged();
        }
        BlockState s = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, s, s, 3);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** Removes the node blocks, drops the module and breaks the controller (re-entry safe). */
    public void dismantle() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.dismantling) {
            return;
        }
        this.dismantling = true;
        try {
            Level level = this.level;
            List<BlockPos> targets = new ArrayList<>(this.childPositions);
            if (targets.isEmpty()) {
                targets = this.findChildrenByScan();
            }
            this.formed = false;
            this.dropContents();
            for (BlockPos p : targets) {
                if (p == null || !level.isLoaded(p) || !level.getBlockState(p).is(SRPBlocks.NODE_RELAY.get())) {
                    continue;
                }
                level.removeBlock(p, false);
            }
            this.childPositions.clear();
            this.setChanged();
            if (level.isLoaded(this.worldPosition) && level.getBlockState(this.worldPosition).is(SRPBlocks.RELAY_CONTROLLER.get())) {
                level.destroyBlock(this.worldPosition, true);
            }
        } finally {
            this.dismantling = false;
        }
    }

    private void relinkChildrenToMe() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        for (BlockPos p : this.childPositions) {
            if (p != null && this.level.isLoaded(p) && this.level.getBlockEntity(p) instanceof TileEntityNodeRelay node) {
                node.setControllerPos(this.worldPosition);
            }
        }
    }

    private List<BlockPos> findChildrenByScan() {
        List<BlockPos> found = new ArrayList<>();
        if (this.level == null) {
            return found;
        }
        BlockPos origin = this.worldPosition;
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; ++dx) {
            for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; ++dy) {
                for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; ++dz) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (!p.equals(origin) && this.level.isLoaded(p) && this.level.getBlockState(p).is(SRPBlocks.NODE_RELAY.get())) {
                        found.add(p);
                    }
                }
            }
        }
        return found;
    }
}
