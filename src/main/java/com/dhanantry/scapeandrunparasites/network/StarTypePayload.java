package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** MsgSyncStarType: the star type of the world (0 normal, 1 cold, 2 warm) for the client shaders. */
public record StarTypePayload(int starType) implements CustomPacketPayload {
    public static final Type<StarTypePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "star_type"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StarTypePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, StarTypePayload::starType,
            StarTypePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
