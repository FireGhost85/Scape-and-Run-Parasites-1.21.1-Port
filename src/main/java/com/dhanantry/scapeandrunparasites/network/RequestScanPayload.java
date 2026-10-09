package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** MsgRequestScan: the Scan button of the relay scanner screen. */
public record RequestScanPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<RequestScanPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "request_scan"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestScanPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RequestScanPayload::pos,
            RequestScanPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestScanPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) {
                return;
            }
            if (player.distanceToSqr(msg.pos().getX() + 0.5, msg.pos().getY() + 0.5, msg.pos().getZ() + 0.5) > 64.0) {
                return;
            }
            if (player.level().getBlockEntity(msg.pos()) instanceof TileEntityRelayController rc && rc.canScan()) {
                rc.performScan(player);
            }
        });
    }
}
