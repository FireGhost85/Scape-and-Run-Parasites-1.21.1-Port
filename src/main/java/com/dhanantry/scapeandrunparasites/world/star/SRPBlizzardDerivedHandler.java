package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDerived;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPBlizzardDerivedHandler {
    private static final double RANGE = 100.0;
    private static final double RANGE_SQ = 10000.0;
    private static final Map<UUID, Boolean> LAST_STATE = new HashMap<UUID, Boolean>();

    private SRPBlizzardDerivedHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!(event.player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)event.player;
        if (player.level().isClientSide) {
            return;
        }
        if (player.tickCount % 10 != 0) {
            return;
        }
        boolean reverse = SRPBlizzardDerivedHandler.shouldReverse(player);
        UUID id = player.getUUID();
        Boolean previous = LAST_STATE.get(id);
        if (previous == null || previous != reverse) {
            LAST_STATE.put(id, reverse);
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)player, new SyncBlizzardReversePayload(reverse));
        }
    }

    private static boolean shouldReverse(ServerPlayer player) {
        Level world = player.level();
        if (world.dimensionType() == null || !DimKeys.of(world).equals(DimKeys.normalize("0"))) {
            return false;
        }
        if (SRPStarWorldData.get(world).getStarType() != 1) {
            return false;
        }
        AABB area = player.getBoundingBox().inflate(100.0);
        for (EntityPDerived entity : world.getEntitiesOfClass(EntityHeblu.class, area)) {
            if (entity.isRemoved() || !(player.distanceToSqr((Entity)entity) <= 10000.0)) continue;
            return true;
        }
        for (EntityPDerived entity : world.getEntitiesOfClass(EntityKirin.class, area)) {
            if (((EntityKirin)entity).isRemoved() || !(player.distanceToSqr((Entity)entity) <= 10000.0)) continue;
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_STATE.remove(event.player.getUUID());
    }
}

