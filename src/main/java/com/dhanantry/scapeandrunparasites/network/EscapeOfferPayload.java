package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** S2CSetEscapeOffer: tells the client of a dying player whether the escape button is offered on the death screen. */
public record EscapeOfferPayload(boolean offer) implements CustomPacketPayload {
    public static final Type<EscapeOfferPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "escape_offer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EscapeOfferPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, EscapeOfferPayload::offer,
            EscapeOfferPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
