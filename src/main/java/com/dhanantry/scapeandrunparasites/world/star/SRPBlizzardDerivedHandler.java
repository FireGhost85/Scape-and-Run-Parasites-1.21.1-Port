package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import com.dhanantry.scapeandrunparasites.network.SyncBlizzardReversePayload;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** SRPBlizzardDerivedHandler of 1.10.9: in a cold star world a Heblu / Kirin within 100 blocks reverses the blizzard for the player. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPBlizzardDerivedHandler {
    private static final double RANGE = 100.0;
    private static final Map<UUID, Boolean> LAST_STATE = new HashMap<>();

    private SRPBlizzardDerivedHandler() {
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.tickCount % 10 != 0) {
            return;
        }
        boolean reverse = shouldReverse(player);
        UUID id = player.getUUID();
        Boolean previous = LAST_STATE.get(id);
        if (previous == null || previous != reverse) {
            LAST_STATE.put(id, reverse);
            SRPSend.sendToPlayer(player, new SyncBlizzardReversePayload(reverse));
        }
    }

    private static boolean shouldReverse(ServerPlayer player) {
        Level world = player.level();
        if (world.dimension() != Level.OVERWORLD || SRPStarWorldData.get(player.getServer()).getStarType() != 1) {
            return false;
        }
        AABB area = player.getBoundingBox().inflate(RANGE);
        for (LivingEntity e : world.getEntitiesOfClass(LivingEntity.class, area, x -> x instanceof EntityHeblu || x instanceof EntityKirin)) {
            if (!e.isRemoved() && player.distanceToSqr(e) <= RANGE * RANGE) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        LAST_STATE.remove(event.getEntity().getUUID());
    }
}
