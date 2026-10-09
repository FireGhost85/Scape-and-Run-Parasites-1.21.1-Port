package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** PacketCelestialNightState / MsgSyncCelestialPhase / MessageSyncCelestialPhase of 1.10.9 in one payload: phase, night index and the active / forced events of a dimension. */
public record CelestialNightStatePayload(String dim, int phase, long nightIndex, List<String> active, List<String> forced) implements CustomPacketPayload {
    public static final Type<CelestialNightStatePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "celestial_night_state"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CelestialNightStatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, CelestialNightStatePayload::dim,
            ByteBufCodecs.VAR_INT, CelestialNightStatePayload::phase,
            ByteBufCodecs.VAR_LONG, CelestialNightStatePayload::nightIndex,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), CelestialNightStatePayload::active,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), CelestialNightStatePayload::forced,
            CelestialNightStatePayload::new);

    public static CelestialNightStatePayload of(String dim, int phase, long nightIndex, Collection<String> active, Collection<String> forced) {
        return new CelestialNightStatePayload(dim, phase, nightIndex, new ArrayList<>(active), new ArrayList<>(forced));
    }

    public Set<String> activeSet() {
        return new HashSet<>(this.active);
    }

    public Set<String> forcedSet() {
        return new HashSet<>(this.forced);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
