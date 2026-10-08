package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketMovingSound: server -> client sound cue / music control code (see ClientPayloadHandlers.playMovingSound). */
public record MovingSoundPayload(int evPhase, float volume) implements CustomPacketPayload {
    public static final Type<MovingSoundPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "moving_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MovingSoundPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MovingSoundPayload::evPhase,
            ByteBufCodecs.FLOAT, MovingSoundPayload::volume,
            MovingSoundPayload::new);

    /** SRPPacketMovingSound(int): full volume. */
    public MovingSoundPayload(int evPhase) {
        this(evPhase, 1.0f);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
