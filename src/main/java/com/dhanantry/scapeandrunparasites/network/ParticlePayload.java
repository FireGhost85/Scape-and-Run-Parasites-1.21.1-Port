package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * SRPPacketParticle: server to client, spawns the particle burst of the given type around a position, spread by width and height.
 * Types: 1 gore, 2 smoke, 3 happy, 4 green cloud, 5 none, 10 gore burst, 11 large gore burst, 12 rage.
 */
public record ParticlePayload(double x, double y, double z, float width, float height, byte particleType) implements CustomPacketPayload {
    public static final Type<ParticlePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "particle"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ParticlePayload> CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeDouble(p.x);
                buf.writeDouble(p.y);
                buf.writeDouble(p.z);
                buf.writeFloat(p.width);
                buf.writeFloat(p.height);
                buf.writeByte(p.particleType);
            },
            buf -> new ParticlePayload(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readByte()));

    public ParticlePayload(double x, double y, double z, float width, float height, int type) {
        this(x, y, z, width, height, (byte) type);
    }

    /** A burst of the given type around the entity, spread by its bounding box. */
    public static ParticlePayload at(Entity entity, int type) {
        return new ParticlePayload(entity.getX(), entity.getY(), entity.getZ(), entity.getBbWidth(), entity.getBbHeight(), (byte) type);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
