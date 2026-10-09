package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.CelestialNightStatePayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import java.util.Random;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class CelestialEventManager {
    private static final Random RAND = new Random();
    public static final int DARK_DAYS_DURATION_TICKS = 6000;
    public static final int DARK_DAYS_END_WARNING_TICKS = 200;
    public static final int DARK_DAYS_INTRO_DELAY_TICKS = 160;
    public static final int DARK_DAYS_OUTRO_DELAY_TICKS = 200;
    public static final long DARK_DAYS_ROLL_TIME = 800L;
    public static final long DARK_DAYS_ACTIVATION_TIME = 1000L;

    /** 1.12 WorldProvider.isSurfaceWorld: a natural dimension with sky light and no ceiling. */
    public static boolean isSurface(Level world) {
        return world.dimensionType().natural() && world.dimensionType().hasSkyLight() && !world.dimensionType().hasCeiling();
    }

    public static boolean isActive(Level world, String id) {
        if (world == null || world.isClientSide) {
            return false;
        }
        CelestialNightData.DimState s = CelestialNightData.get(world).getState(DimKeys.of(world));
        if (s == null) {
            return false;
        }
        return s.forced.contains(id) || s.active.contains(id);
    }

    public static void force(Level world, String id, boolean enabled) {
        if (world == null || world.isClientSide) {
            return;
        }
        String dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        if (enabled) {
            s.forced.add(id);
        } else {
            s.forced.remove(id);
            s.active.remove(id);
            if ("dark_days".equals(id)) {
                s.darkDaysStartTime = -1L;
                s.darkDaysEndTime = -1L;
                s.darkDaysEndingSoundPlayed = false;
            }
        }
        if (s.forced.contains("dark_days")) {
            s.active.clear();
            s.forced.clear();
            s.forced.add("dark_days");
        }
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    private static void clearWeatherForDarkDays(Level world) {
        if (world instanceof ServerLevel server) {
            server.setWeatherParameters(6400, 0, false, false);
        }
    }

    public static void clearForced(Level world) {
        if (world == null || world.isClientSide) {
            return;
        }
        String dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        s.forced.clear();
        s.active.remove("dark_days");
        s.darkDaysStartTime = -1L;
        s.darkDaysEndTime = -1L;
        s.darkDaysEndingSoundPlayed = false;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    public static void startDarkDays(Level world) {
        if (world == null || world.isClientSide) {
            return;
        }
        if (!CelestialEventManager.isSurface(world)) {
            return;
        }
        CelestialEventManager.clearWeatherForDarkDays(world);
        String dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        s.active.clear();
        s.forced.clear();
        s.darkDaysStartTime = world.getDayTime() + 160L;
        s.darkDaysEndTime = -1L;
        s.darkDaysEndingSoundPlayed = false;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
        CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_START.get(), 1.0f);
    }

    public static void stopDarkDays(Level world) {
        boolean active;
        if (world == null || world.isClientSide) {
            return;
        }
        if (!CelestialEventManager.isSurface(world)) {
            return;
        }
        String dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        active = s.forced.contains("dark_days") || s.active.contains("dark_days");
        if (!active) {
            s.darkDaysStartTime = -1L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
            return;
        }
        s.darkDaysStartTime = -1L;
        s.darkDaysEndTime = world.getDayTime() + 200L;
        s.darkDaysEndingSoundPlayed = true;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
        CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_ENDING.get(), 1.0f);
    }

    public static void syncDim(Level world, String dim) {
        if (world == null || world.isClientSide || world.getServer() == null) {
            return;
        }
        CelestialNightData.DimState s = CelestialNightData.get(world).getState(dim);
        if (s == null) {
            return;
        }
        CelestialNightStatePayload pkt = CelestialNightStatePayload.of(dim, s.phase, s.nightIndex, s.active, s.forced);
        for (ServerPlayer p : world.getServer().getPlayerList().getPlayers()) {
            if (!DimKeys.of(p.level()).equals(dim)) continue;
            SRPSend.sendToPlayer(p, pkt);
        }
    }

    private static void rollIfNeeded(Level world) {
        if (world == null || world.isClientSide) {
            return;
        }
        if (!CelestialEventManager.isSurface(world)) {
            return;
        }
        String dim = DimKeys.of(world);
        long nightIndex = world.getDayTime() / 24000L;
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        if (s.forced.contains("dark_days")) {
            return;
        }
        if (s.nightIndex == nightIndex) {
            return;
        }
        s.nightIndex = nightIndex;
        s.active.clear();
        long baseSeed = ((ServerLevel)world).getSeed() ^ nightIndex * 918273L;
        for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
            if ("dark_days".equals(def.id) || SRPConfigWorld.isCelestialEventBlacklisted(def.id) || !def.isPhaseAllowed(s.phase)) continue;
            long seed = baseSeed + (long)def.id.hashCode() * 31L;
            RAND.setSeed(seed);
            if (!(RAND.nextFloat() <= def.chancePerNight)) continue;
            s.active.add(def.id);
        }
        if (s.active.contains("dark_days")) {
            s.active.clear();
            s.active.add("dark_days");
        }
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    private static void tickDarkDays(Level world) {
        boolean active;
        if (world == null || world.isClientSide) {
            return;
        }
        if (!CelestialEventManager.isSurface(world)) {
            return;
        }
        String dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        long now = world.getDayTime();
        if (s.darkDaysStartTime > 0L && now >= s.darkDaysStartTime) {
            s.darkDaysStartTime = -1L;
            s.active.clear();
            s.forced.clear();
            s.forced.add("dark_days");
            s.darkDaysEndTime = now + 6000L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
        }
        active = s.forced.contains("dark_days") || s.active.contains("dark_days");
        if (!active) {
            return;
        }
        if (s.darkDaysEndTime <= 0L) {
            s.darkDaysEndTime = now + 6000L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
            return;
        }
        if (!s.darkDaysEndingSoundPlayed && now >= s.darkDaysEndTime - 200L) {
            s.darkDaysEndingSoundPlayed = true;
            data.markDirty();
            CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_ENDING.get(), 1.0f);
        }
        if (now >= s.darkDaysEndTime) {
            CelestialEventManager.grantDarkDaysAdvancement(world);
            s.forced.remove("dark_days");
            s.active.remove("dark_days");
            s.darkDaysStartTime = -1L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
        }
    }

    private static void playToDimension(Level world, SoundEvent sound, float pitch) {
        if (world == null || sound == null || world.getServer() == null) {
            return;
        }
        String dim = DimKeys.of(world);
        for (ServerPlayer p : world.getServer().getPlayerList().getPlayers()) {
            if (!DimKeys.of(p.level()).equals(dim)) continue;
            p.level().playSound(null, p.getX(), p.getY(), p.getZ(), sound, SoundSource.AMBIENT, 10000.0f, pitch);
        }
    }

    private static void rollDarkDaysIfNeeded(Level world) {
        boolean activeOrQueued;
        if (world == null || world.isClientSide) {
            return;
        }
        if (!CelestialEventManager.isSurface(world)) {
            return;
        }
        String dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        activeOrQueued = s.forced.contains("dark_days") || s.active.contains("dark_days") || s.darkDaysStartTime > 0L || s.darkDaysEndTime > 0L;
        if (activeOrQueued) {
            return;
        }
        long worldTime = world.getDayTime();
        long dayStart = worldTime - worldTime % 24000L;
        long day = dayStart / 24000L;
        long dayTime = worldTime % 24000L;
        if (s.darkDaysLastRollDay == day) {
            return;
        }
        if (dayTime < 800L || dayTime >= 1000L) {
            return;
        }
        s.darkDaysLastRollDay = day;
        CelestialObjectDefinition def = CelestialObjectRegistry.getById("dark_days");
        if (def == null) {
            data.markDirty();
            return;
        }
        long seed = ((ServerLevel)world).getSeed() ^ day * 918273L ^ (long)"dark_days".hashCode() * 31L;
        RAND.setSeed(seed);
        if (RAND.nextFloat() <= def.chancePerNight) {
            s.active.clear();
            s.forced.clear();
            CelestialEventManager.clearWeatherForDarkDays(world);
            s.darkDaysStartTime = dayStart + 1000L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_START.get(), 1.0f);
        }
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post e) {
        Level w = e.getLevel();
        if (w == null || w.isClientSide) {
            return;
        }
        CelestialEventManager.rollIfNeeded(w);
        CelestialEventManager.rollDarkDaysIfNeeded(w);
        CelestialEventManager.tickDarkDays(w);
    }

    private static void grantDarkDaysAdvancement(Level world) {
        if (world == null || world.getServer() == null) {
            return;
        }
        AdvancementHolder adv = world.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "dark_days"));
        if (adv == null) {
            return;
        }
        String dim = DimKeys.of(world);
        for (ServerPlayer p : world.getServer().getPlayerList().getPlayers()) {
            AdvancementProgress progress;
            if (!DimKeys.of(p.level()).equals(dim) || (progress = p.getAdvancements().getOrStartProgress(adv)).isDone()) continue;
            for (String criterion : progress.getRemainingCriteria()) {
                p.getAdvancements().award(adv, criterion);
            }
        }
    }
}
