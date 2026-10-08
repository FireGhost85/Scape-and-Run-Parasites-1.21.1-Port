package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketEntityBodyDead: server to client, marks a body part (tendril) of the target entity as dead. */
public record EntityBodyDeadPayload(int targetId, int partId) implements CustomPacketPayload {
    public static final Type<EntityBodyDeadPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "entity_body_dead"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityBodyDeadPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EntityBodyDeadPayload::targetId,
            ByteBufCodecs.VAR_INT, EntityBodyDeadPayload::partId,
            EntityBodyDeadPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
