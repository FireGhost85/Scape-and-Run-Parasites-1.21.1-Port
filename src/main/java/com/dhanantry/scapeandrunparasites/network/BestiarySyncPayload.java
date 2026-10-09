package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** PacketBestiarySync: the whole bestiary progress of the player (server to client). */
public record BestiarySyncPayload(CompoundTag progress) implements CustomPacketPayload {
    public static final Type<BestiarySyncPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "bestiary_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, BestiarySyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, BestiarySyncPayload::progress,
            BestiarySyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
