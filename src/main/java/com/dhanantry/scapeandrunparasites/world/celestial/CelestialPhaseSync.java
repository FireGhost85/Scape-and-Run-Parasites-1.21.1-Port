package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.network.CelestialNightStatePayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** CelestialPhaseSyncHandler + CelestialNightJoinSync + CelestialEffectHooks of 1.10.9: rolls the events of the night at nightfall, runs their effects and syncs the clients. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class CelestialPhaseSync {
    private static final Random RAND = new Random();
    private static final Map<String, Boolean> WAS_NIGHT = new HashMap<String, Boolean>();

    private static long getNightIndex(Level world) {
        return world.getGameTime() / 24000L;
    }

    private static boolean isNight(Level world) {
        long dayTime = world.getDayTime() % 24000L;
        return dayTime >= 13000L && dayTime <= 23000L;
    }

    private static void rollNight(Level world, int phase, CelestialNightData.DimState state) {
        state.active.clear();
        long nightIndex = state.nightIndex;
        long baseSeed = ((ServerLevel)world).getSeed() ^ nightIndex * 918273L;
        for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
            if ("dark_days".equals(def.id) || !def.isPhaseAllowed(phase)) continue;
            long seed = baseSeed + (long)def.id.hashCode() * 31L;
            RAND.setSeed(seed);
            if (!(RAND.nextFloat() <= def.chancePerNight)) continue;
            state.active.add(def.id);
        }
    }

    private static void callNightStart(Level world, String dim, int phase, long nightIndex, Set<String> started) {
        for (String id : started) {
            ICelestialEventEffect fx = CelestialEffectRegistry.get(id);
            if (fx == null) continue;
            fx.onNightStart(world, dim, phase, nightIndex);
        }
    }

    private static void callNightEnd(Level world, String dim, int phase, long nightIndex, Set<String> ended) {
        for (String id : ended) {
            ICelestialEventEffect fx = CelestialEffectRegistry.get(id);
            if (fx == null) continue;
            fx.onNightEnd(world, dim, phase, nightIndex);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        Level world = event.getLevel();
        if (world.isClientSide || world.getServer() == null) {
            return;
        }
        if (!SRPConfigWorld.enableCelestialObjects) {
            return;
        }
        if (!CelestialEventManager.isSurface(world)) {
            return;
        }
        String dim = DimKeys.of(world);
        SRPSaveData data = SRPSaveData.get(world);
        if (data == null) {
            return;
        }
        int phase = data.getEvolutionPhase(dim);
        CelestialNightData nightData = CelestialNightData.get(world);
        CelestialNightData.DimState state = nightData.getOrCreate(dim);
        boolean nightNow = CelestialPhaseSync.isNight(world);
        boolean nightPrev = WAS_NIGHT.getOrDefault(dim, false);
        if (nightNow && !nightPrev) {
            Set<String> prev = new HashSet<String>(state.active);
            prev.addAll(state.forced);
            state.nightIndex = CelestialPhaseSync.getNightIndex(world);
            CelestialPhaseSync.rollNight(world, phase, state);
            Set<String> next = new HashSet<String>(state.active);
            next.addAll(state.forced);
            Set<String> started = new HashSet<String>(next);
            started.removeAll(prev);
            Set<String> ended = new HashSet<String>(prev);
            ended.removeAll(next);
            CelestialPhaseSync.callNightEnd(world, dim, phase, state.nightIndex, ended);
            CelestialPhaseSync.callNightStart(world, dim, phase, state.nightIndex, started);
            nightData.markDirty();
            CelestialPhaseSync.syncDim(world, dim, phase, state.nightIndex, state.active, state.forced);
        }
        if (!nightNow && nightPrev) {
            Set<String> prev = new HashSet<String>(state.active);
            prev.addAll(state.forced);
            CelestialPhaseSync.callNightEnd(world, dim, phase, state.nightIndex, prev);
        }
        WAS_NIGHT.put(dim, nightNow);
    }

    public static void syncDim(Level world, String dim, int phase, long nightIndex, Set<String> activeSet, Set<String> forcedSet) {
        if (world.getServer() == null) {
            return;
        }
        CelestialNightStatePayload pkt = CelestialNightStatePayload.of(dim, phase, nightIndex, activeSet, forcedSet);
        for (ServerPlayer player : world.getServer().getPlayerList().getPlayers()) {
            if (!DimKeys.of(player.level()).equals(dim)) continue;
            SRPSend.sendToPlayer(player, pkt);
        }
    }

    private static void syncPlayer(ServerPlayer player) {
        Level world = player.level();
        String dim = DimKeys.of(world);
        SRPSaveData save = SRPSaveData.get(world);
        if (save == null) {
            return;
        }
        int phase = save.getEvolutionPhase(dim);
        CelestialNightData.DimState state = CelestialNightData.get(world).getOrCreate(dim);
        SRPSend.sendToPlayer(player, CelestialNightStatePayload.of(dim, phase, CelestialPhaseSync.getNightIndex(world), state.active, state.forced));
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            CelestialPhaseSync.syncPlayer(p);
        }
    }

    @SubscribeEvent
    public static void onDimChange(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            CelestialPhaseSync.syncPlayer(p);
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent e) {
        Level world = e.getLevel();
        if (world == null || world.isClientSide) {
            return;
        }
        long dayTime = world.getDayTime() % 24000L;
        if (dayTime < 13000L || dayTime > 23000L) {
            return;
        }
        Entity ent = e.getEntity();
        if (!(ent instanceof EntityParasiteBase parasite)) {
            return;
        }
        for (String id : CelestialEffectRegistry.getActiveIds(world)) {
            ICelestialEventEffect fx = CelestialEffectRegistry.get(id);
            if (fx == null) continue;
            fx.onParasiteSpawn(parasite, null);
        }
    }

    static {
        CelestialEffectRegistry.register("twenty_seven", new com.dhanantry.scapeandrunparasites.world.celestial.effects.EffectTwentySeven());
    }
}
