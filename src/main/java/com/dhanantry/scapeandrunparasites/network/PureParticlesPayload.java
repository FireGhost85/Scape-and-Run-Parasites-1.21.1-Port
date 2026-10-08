package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** MsgSpawnPureParticles: server to client, spawns {@code count} purification particles of the kind (0 wave, 1 pulse) at a position. */
public record PureParticlesPayload(double x, double y, double z, int count, int kind) implements CustomPacketPayload {
    public static final Type<PureParticlesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "pure_particles"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PureParticlesPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, PureParticlesPayload::x,
            ByteBufCodecs.DOUBLE, PureParticlesPayload::y,
            ByteBufCodecs.DOUBLE, PureParticlesPayload::z,
            ByteBufCodecs.INT, PureParticlesPayload::count,
            ByteBufCodecs.INT, PureParticlesPayload::kind,
            PureParticlesPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
