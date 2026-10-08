package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketMusicTrackCancelUpdateClientEvoPhase: stops current music and sets the client's evolution phase. */
public record EvoPhaseCancelPayload(int phase) implements CustomPacketPayload {
    public static final Type<EvoPhaseCancelPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "evo_phase_cancel"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EvoPhaseCancelPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EvoPhaseCancelPayload::phase,
            EvoPhaseCancelPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
