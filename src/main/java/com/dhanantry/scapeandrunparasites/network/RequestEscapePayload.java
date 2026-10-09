package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** C2SRequestEscape: the dead player pressed the escape button; the next respawn is moved away from the death spot. */
public record RequestEscapePayload() implements CustomPacketPayload {
    public static final Type<RequestEscapePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "request_escape"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestEscapePayload> CODEC = StreamCodec.unit(new RequestEscapePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
