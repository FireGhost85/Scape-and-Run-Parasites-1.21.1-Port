package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** S2CExtremeSnow: server to client, state of the extreme snow storm. */
public record ExtremeSnowPayload(boolean enabled, float intensity, boolean anywhere, float windDeg, float windSpeed) implements CustomPacketPayload {
    public static final Type<ExtremeSnowPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "extreme_snow"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExtremeSnowPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ExtremeSnowPayload::enabled,
            ByteBufCodecs.FLOAT, ExtremeSnowPayload::intensity,
            ByteBufCodecs.BOOL, ExtremeSnowPayload::anywhere,
            ByteBufCodecs.FLOAT, ExtremeSnowPayload::windDeg,
            ByteBufCodecs.FLOAT, ExtremeSnowPayload::windSpeed,
            ExtremeSnowPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
