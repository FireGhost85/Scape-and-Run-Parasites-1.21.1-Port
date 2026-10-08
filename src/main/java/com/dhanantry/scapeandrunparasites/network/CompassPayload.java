package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketCompass: server to client, the target position of a compass type (1 node, 2 colony, 3 origin). */
public record CompassPayload(int x, int y, int z, int type) implements CustomPacketPayload {
    public static final Type<CompassPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "compass"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CompassPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, CompassPayload::x,
            ByteBufCodecs.INT, CompassPayload::y,
            ByteBufCodecs.INT, CompassPayload::z,
            ByteBufCodecs.INT, CompassPayload::type,
            CompassPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
