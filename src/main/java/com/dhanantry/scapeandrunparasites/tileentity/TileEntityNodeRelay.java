package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Block entity of a relay node block: remembers the position of its relay controller. */
public class TileEntityNodeRelay extends BlockEntity {
    @Nullable
    private BlockPos controllerPos;

    public TileEntityNodeRelay(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.NODE_RELAY.get(), pos, state);
    }

    public void setControllerPos(@Nullable BlockPos pos) {
        this.controllerPos = pos;
        this.setChanged();
        if (this.level != null && !this.level.isClientSide) {
            BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
    }

    @Nullable
    public BlockPos getControllerPos() {
        return this.controllerPos;
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (this.controllerPos != null) {
            nbt.put("ControllerPos", NbtUtils.writeBlockPos(this.controllerPos));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.controllerPos = nbt.contains("ControllerPos") ? NbtUtils.readBlockPos(nbt, "ControllerPos").orElse(null) : null;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
