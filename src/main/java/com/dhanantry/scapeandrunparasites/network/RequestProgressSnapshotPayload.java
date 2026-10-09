package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** MsgRequestProgressSnapshot: the "Current Progress" page asks the server for its numbers. */
public record RequestProgressSnapshotPayload() implements CustomPacketPayload {
    public static final Type<RequestProgressSnapshotPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "request_progress_snapshot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestProgressSnapshotPayload> CODEC = StreamCodec.unit(new RequestProgressSnapshotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestProgressSnapshotPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer p) {
                SRPSend.sendToPlayer(p, new SyncProgressSnapshotPayload(SRPProgressSnapshot.collect(p.level(), p).toNBT()));
            }
        });
    }
}
