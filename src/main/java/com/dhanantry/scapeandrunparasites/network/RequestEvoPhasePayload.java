package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** SRPPacketRequestEvoPhaseClient: the client asks once a second for the evolution phase (music) and whether it stands in a vector. */
public record RequestEvoPhasePayload() implements CustomPacketPayload {
    public static final Type<RequestEvoPhasePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "request_evo_phase"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestEvoPhasePayload> CODEC = StreamCodec.unit(new RequestEvoPhasePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestEvoPhasePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer p) {
                SRPSaveData data = SRPSaveData.get(p.level());
                if (data == null) {
                    return;
                }
                int phase = data.getEvolutionPhase(DimKeys.of(p.level()));
                boolean vector = SRPWorldData.get(p.level()).nearestInfectionPosition(false, p.blockPosition()) != null;
                SRPSend.sendToPlayer(p, new UpdateEvoPhasePayload(phase, vector));
            }
        });
    }
}
