package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketFog: server to client, the parasite biome fog density and colour around the player. */
public record FogPayload(float fog, float red, float green, float blue) implements CustomPacketPayload {
    public static final Type<FogPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "fog"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FogPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, FogPayload::fog,
            ByteBufCodecs.FLOAT, FogPayload::red,
            ByteBufCodecs.FLOAT, FogPayload::green,
            ByteBufCodecs.FLOAT, FogPayload::blue,
            FogPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
