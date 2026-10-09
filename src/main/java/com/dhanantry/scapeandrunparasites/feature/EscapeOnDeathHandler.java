package com.dhanantry.scapeandrunparasites.feature;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.network.EscapeOfferPayload;
import com.dhanantry.scapeandrunparasites.network.RequestEscapePayload;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Death loop prevention: more than 5 deaths by parasites within 60 seconds make the death screen offer an escape that moves the
 * next respawn 200 to 300 blocks away (see {@link EscapeRespawnHandler}).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class EscapeOnDeathHandler {
    static final String PERSIST_TAG = "PlayerPersisted";
    private static final String OFFER_TAG = "srp_offer_escape";
    private static final String WINDOW_TAG = "srp_death_window";
    static final String PENDING_TAG = "srp_escape_pending";
    private static final long WINDOW_MS = 60000L;
    private static final int THRESHOLD = 5;
    private static final Map<UUID, Deque<Long>> streaks = new HashMap<>();

    private static boolean isSrparasitesCause(LivingDeathEvent e) {
        if (e.getSource().getEntity() != null && e.getSource().getEntity().getClass().getName().startsWith("com.dhanantry.scapeandrunparasites")) {
            return true;
        }
        if (e.getSource().getDirectEntity() != null && e.getSource().getDirectEntity().getClass().getName().startsWith("com.dhanantry.scapeandrunparasites")) {
            return true;
        }
        return e.getSource().getMsgId().contains("srparasites");
    }

    @SubscribeEvent
    public static void die(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) {
            return;
        }
        if (!SRPConfigWorld.escapeEnabled) {
            return;
        }
        if (!isSrparasitesCause(e)) {
            return;
        }
        long nowMs = p.level().getGameTime() * 50L;
        Deque<Long> q = streaks.computeIfAbsent(p.getUUID(), k -> new ArrayDeque<>());
        q.addLast(nowMs);
        while (!q.isEmpty() && nowMs - q.peekFirst() > WINDOW_MS) {
            q.removeFirst();
        }
        boolean offer = q.size() > THRESHOLD;
        CompoundTag persisted = p.getPersistentData().getCompound(PERSIST_TAG);
        persisted.putBoolean(OFFER_TAG, offer);
        persisted.putLong(WINDOW_TAG, nowMs);
        p.getPersistentData().put(PERSIST_TAG, persisted);
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer(p, new EscapeOfferPayload(offer));
    }

    public static void clearOffer(ServerPlayer p) {
        CompoundTag persisted = p.getPersistentData().getCompound(PERSIST_TAG);
        persisted.putBoolean(OFFER_TAG, false);
        p.getPersistentData().put(PERSIST_TAG, persisted);
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer(p, new EscapeOfferPayload(false));
    }

    /** C2SRequestEscape: marks the next respawn of the player as an escape. */
    public static void requestEscape(RequestEscapePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer p) {
                CompoundTag persisted = p.getPersistentData().getCompound(PERSIST_TAG);
                persisted.putBoolean(PENDING_TAG, true);
                p.getPersistentData().put(PERSIST_TAG, persisted);
            }
        });
    }
}
