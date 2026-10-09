package com.dhanantry.scapeandrunparasites.network.registration;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockClientHooks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Server to client payload of the block classes that has no counterpart in the shared network package: the flash particle
 * of the biome purifier (spawned directly on the server side in 1.12). {@link #register(PayloadRegistrar)} must be called
 * from {@code SRPNetwork.register}. The block particle bursts use the shared {@code ParticlePayload} and {@code PureParticlesPayload}.
 */
public final class BlocksPayloads {
    private BlocksPayloads() {
    }

    public static void register(PayloadRegistrar registrar) {
        registrar.playToClient(Flash.TYPE, Flash.CODEC, (msg, ctx) -> BlocksPayloads.handleFlash(msg, ctx));
    }

    private static void handleFlash(Flash msg, IPayloadContext ctx) {
        BlockClientHooks.flash(msg.x(), msg.y(), msg.z(), msg.r(), msg.g(), msg.b());
    }

    /** The flash particle of the biome purifier. */
    public record Flash(double x, double y, double z, int r, int g, int b) implements CustomPacketPayload {
        public static final Type<Flash> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block_flash"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Flash> CODEC = StreamCodec.composite(
                ByteBufCodecs.DOUBLE, Flash::x,
                ByteBufCodecs.DOUBLE, Flash::y,
                ByteBufCodecs.DOUBLE, Flash::z,
                ByteBufCodecs.VAR_INT, Flash::r,
                ByteBufCodecs.VAR_INT, Flash::g,
                ByteBufCodecs.VAR_INT, Flash::b,
                Flash::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
