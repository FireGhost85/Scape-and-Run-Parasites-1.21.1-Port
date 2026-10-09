package com.dhanantry.scapeandrunparasites.feature;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.network.msg.S2CSetEscapeOffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public class EscapeOnDeathHandler {
    private static final String PERSIST_TAG = "PlayerPersisted";
    private static final String OFFER_TAG = "srp_offer_escape";
    private static final String WINDOW_TAG = "srp_death_window";
    private static final long WINDOW_MS = 60000L;
    private static final int THRESHOLD = 5;
    private static final Map<UUID, Deque<Long>> streaks = new HashMap<UUID, Deque<Long>>();

    private static boolean isSrparasitesCause(LivingDeathEvent e) {
        if (e.getSource() == null) {
            return false;
        }
        if (e.getSource().getEntity() != null && e.getSource().getEntity().getClass().getName().startsWith("com.dhanantry.scapeandrunparasites")) {
            return true;
        }
        if (e.getSource().getDirectEntity() != null && e.getSource().getDirectEntity().getClass().getName().startsWith("com.dhanantry.scapeandrunparasites")) {
            return true;
        }
        String dmg = e.getSource().damageType == null ? "" : e.getSource().damageType;
        return dmg.contains("srparasites");
    }

    @SubscribeEvent
    public void die(LivingDeathEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer)) {
            return;
        }
        if (!SRPConfigWorld.escapeEnabled) {
            return;
        }
        if (!EscapeOnDeathHandler.isSrparasitesCause(e)) {
            return;
        }
        ServerPlayer p = (ServerPlayer)e.getEntity();
        long nowMs = p.level().getGameTime() * 50L;
        Deque q = streaks.computeIfAbsent(p.getUUID(), k -> new ArrayDeque());
        q.addLast(nowMs);
        while (!q.isEmpty() && nowMs - (Long)q.peekFirst() > 60000L) {
            q.removeFirst();
        }
        boolean offer = q.size() > 5;
        CompoundTag persisted = p.getPersistentData().getCompound(PERSIST_TAG);
        persisted.putBoolean(OFFER_TAG, offer);
        persisted.putLong(WINDOW_TAG, nowMs);
        p.getPersistentData().put(PERSIST_TAG, (Tag)persisted);
        SRPNetwork.CHANNEL.sendTo((IMessage)new S2CSetEscapeOffer(offer), p);
    }

    public static void clearOffer(ServerPlayer p) {
        CompoundTag persisted = p.getPersistentData().getCompound(PERSIST_TAG);
        persisted.putBoolean(OFFER_TAG, false);
        p.getPersistentData().put(PERSIST_TAG, (Tag)persisted);
        SRPNetwork.CHANNEL.sendTo((IMessage)new S2CSetEscapeOffer(false), p);
    }
}

