package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** SRPPacketGuiDistortionState: the player's distortion switches of {@code /srpguidistortion}. */
public record GuiDistortionStatePayload(boolean disabled, boolean creativeOverride) implements CustomPacketPayload {
    public static final Type<GuiDistortionStatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "gui_distortion_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GuiDistortionStatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, GuiDistortionStatePayload::disabled,
            ByteBufCodecs.BOOL, GuiDistortionStatePayload::creativeOverride,
            GuiDistortionStatePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
