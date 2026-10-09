package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * PacketBestiarySeenCelestial: the client saw a celestial object. The "columbus" and "stolas" advancements of the original are
 * granted once the celestial events and the advancements are ported.
 */
public record BestiarySeenCelestialPayload(String id) implements CustomPacketPayload {
    public static final Type<BestiarySeenCelestialPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "bestiary_seen_celestial"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BestiarySeenCelestialPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, BestiarySeenCelestialPayload::id,
            BestiarySeenCelestialPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(BestiarySeenCelestialPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer p && msg.id() != null && !msg.id().isEmpty()) {
                IBestiaryProgress prog = BestiaryCapability.get(p);
                if (!prog.hasSeenCelestial(msg.id())) {
                    prog.markCelestialSeen(msg.id());
                }
                SRPSend.sendToPlayer(p, new BestiarySyncPayload(prog.serializeNBT()));
            }
        });
    }
}
