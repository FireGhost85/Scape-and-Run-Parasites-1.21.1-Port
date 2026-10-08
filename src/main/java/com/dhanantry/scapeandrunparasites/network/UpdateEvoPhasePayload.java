package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketUpdateEvoPhaseClient: current phase and whether the player is inside an Evolution Infection Vector. */
public record UpdateEvoPhasePayload(int phase, boolean vector) implements CustomPacketPayload {
    public static final Type<UpdateEvoPhasePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "update_evo_phase"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateEvoPhasePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, UpdateEvoPhasePayload::phase,
            ByteBufCodecs.BOOL, UpdateEvoPhasePayload::vector,
            UpdateEvoPhasePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
