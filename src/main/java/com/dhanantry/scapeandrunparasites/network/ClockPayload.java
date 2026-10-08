package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketClock: server to client, refreshes the values shown by the evolution / development clocks. */
public record ClockPayload(int cooldown, int phase, int development, int type) implements CustomPacketPayload {
    public static final Type<ClockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "clock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClockPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClockPayload::cooldown,
            ByteBufCodecs.INT, ClockPayload::phase,
            ByteBufCodecs.INT, ClockPayload::development,
            ByteBufCodecs.INT, ClockPayload::type,
            ClockPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
