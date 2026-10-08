package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** MsgQlipShake: server to client, starts the camera shake / screen darkening (see ClientQlipShake). */
public record QlipShakePayload(int duration, int delay, boolean dark, boolean shake, float shakeValue) implements CustomPacketPayload {
    public static final Type<QlipShakePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "qlip_shake"));
    public static final StreamCodec<RegistryFriendlyByteBuf, QlipShakePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, QlipShakePayload::duration,
            ByteBufCodecs.INT, QlipShakePayload::delay,
            ByteBufCodecs.BOOL, QlipShakePayload::dark,
            ByteBufCodecs.BOOL, QlipShakePayload::shake,
            ByteBufCodecs.FLOAT, QlipShakePayload::shakeValue,
            QlipShakePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
