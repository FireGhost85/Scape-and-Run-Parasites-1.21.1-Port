package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** MsgSyncBlizzardReverse: a Heblu / Kirin is near, the blizzard of the cold star runs backwards. */
public record SyncBlizzardReversePayload(boolean reverse) implements CustomPacketPayload {
    public static final Type<SyncBlizzardReversePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "sync_blizzard_reverse"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncBlizzardReversePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SyncBlizzardReversePayload::reverse,
            SyncBlizzardReversePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
