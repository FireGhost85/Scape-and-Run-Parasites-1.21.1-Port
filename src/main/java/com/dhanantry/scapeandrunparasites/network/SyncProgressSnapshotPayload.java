package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** MsgSyncProgressSnapshot: the numbers of the "Current Progress" page. */
public record SyncProgressSnapshotPayload(CompoundTag tag) implements CustomPacketPayload {
    public static final Type<SyncProgressSnapshotPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "sync_progress_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncProgressSnapshotPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, SyncProgressSnapshotPayload::tag,
            SyncProgressSnapshotPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
