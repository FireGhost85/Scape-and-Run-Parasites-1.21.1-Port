package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** PacketBestiaryRequest: the client asks for its progress. */
public record BestiaryRequestPayload() implements CustomPacketPayload {
    public static final Type<BestiaryRequestPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "bestiary_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BestiaryRequestPayload> CODEC = StreamCodec.unit(new BestiaryRequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BestiaryRequestPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer p) {
                SRPSend.sendToPlayer(p, new BestiarySyncPayload(BestiaryCapability.get(p).serializeNBT()));
            }
        });
    }
}
