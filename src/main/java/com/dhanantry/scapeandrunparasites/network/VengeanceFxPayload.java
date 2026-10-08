package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/** PacketVengeanceFX: server to client, one of the four Vengeance effects ({@link #SPARKS} ... {@link #LIGHTNING_EXPL}) at a position. */
public record VengeanceFxPayload(byte fxType, double x, double y, double z, float a, int count) implements CustomPacketPayload {
    public static final byte SPARKS = 0;
    public static final byte IMPACT_DUST = 1;
    public static final byte HEAVY_BLEED = 2;
    public static final byte LIGHTNING_EXPL = 3;
    public static final Type<VengeanceFxPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "vengeance_fx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, VengeanceFxPayload> CODEC = StreamCodec.of(
            (buf, p) -> {
                buf.writeByte(p.fxType);
                buf.writeDouble(p.x);
                buf.writeDouble(p.y);
                buf.writeDouble(p.z);
                buf.writeFloat(p.a);
                buf.writeInt(p.count);
            },
            buf -> new VengeanceFxPayload(buf.readByte(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readInt()));

    public VengeanceFxPayload(int type, Vec3 pos, float a, int count) {
        this((byte) type, pos.x, pos.y, pos.z, a, count);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
