package com.dhanantry.scapeandrunparasites.world.celestial;

import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.SRPNetwork;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData;
import com.dhanantry.scapeandrunparasites.world.celestial.PacketCelestialNightState;
import java.util.Random;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class CelestialEventManager {
    private static final Random RAND = new Random();
    public static final int DARK_DAYS_DURATION_TICKS = 6000;
    public static final int DARK_DAYS_END_WARNING_TICKS = 200;
    public static final int DARK_DAYS_INTRO_DELAY_TICKS = 160;
    public static final int DARK_DAYS_OUTRO_DELAY_TICKS = 200;
    public static final long DARK_DAYS_ROLL_TIME = 800L;
    public static final long DARK_DAYS_ACTIVATION_TIME = 1000L;

    public static boolean isActive(Level world, String id) {
        if (world == null || world.isClientSide) {
            return false;
        }
        int dim = DimKeys.of(world);
        CelestialNightData.DimState s = CelestialNightData.get(world).getState(dim);
        if (s == null) {
            return false;
        }
        return s.forced.contains(id) || s.active.contains(id);
    }

    public static void force(Level world, String id, boolean enabled) {
        if (world == null || world.isClientSide) {
            return;
        }
        int dim = DimKeys.of(world);
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
        if (world == null) {
            return;
        }
        world.getWorldInfo().setRaining(false);
        world.getWorldInfo().setThundering(false);
        world.getWorldInfo().setRainTime(0);
        world.getWorldInfo().setThunderTime(0);
        world.getWorldInfo().setCleanWeatherTime(6400);
    }

    public static void clearForced(Level world) {
        if (world == null || world.isClientSide) {
            return;
        }
        int dim = DimKeys.of(world);
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
        if (!world.dimensionType().isSurfaceWorld()) {
            return;
        }
        CelestialEventManager.clearWeatherForDarkDays(world);
        int dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        s.active.clear();
        s.forced.clear();
        s.darkDaysStartTime = world.getWorldTime() + 160L;
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
        if (!world.dimensionType().isSurfaceWorld()) {
            return;
        }
        int dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        boolean bl = active = s.forced.contains("dark_days") || s.active.contains("dark_days");
        if (!active) {
            s.darkDaysStartTime = -1L;
            s.darkDaysEndTime = -1L;
            s.darkDaysEndingSoundPlayed = false;
            data.markDirty();
            CelestialEventManager.syncDim(world, dim);
            return;
        }
        s.darkDaysStartTime = -1L;
        s.darkDaysEndTime = world.getWorldTime() + 200L;
        s.darkDaysEndingSoundPlayed = true;
        data.markDirty();
        CelestialEventManager.syncDim(world, dim);
        CelestialEventManager.playToDimension(world, SRPSounds.DARK_DAYS_ENDING.get(), 1.0f);
    }

    public static void syncDim(Level world, int dim) {
        if (world == null || world.isClientSide) {
            return;
        }
        CelestialNightData.DimState s = CelestialNightData.get(world).getState(dim);
        if (s == null) {
            return;
        }
        PacketCelestialNightState pkt = new PacketCelestialNightState(dim, s.phase, s.nightIndex, s.active, s.forced);
        for (ServerPlayer p : world.getMinecraftServer().getPlayerList().getPlayerList()) {
            if (DimKeys.of(p.level()) != dim) continue;
            SRPNetwork.CHANNEL.sendTo((IMessage)pkt, p);
        }
    }

    private static void rollIfNeeded(Level world) {
        if (world == null || world.isClientSide) {
            return;
        }
        if (!world.dimensionType().isSurfaceWorld()) {
            return;
        }
        int dim = DimKeys.of(world);
        long nightIndex = world.getWorldTime() / 24000L;
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
        long baseSeed = world.getSeed() ^ nightIndex * 918273L;
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
        if (!world.dimensionType().isSurfaceWorld()) {
            return;
        }
        int dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        long now = world.getWorldTime();
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
        boolean bl = active = s.forced.contains("dark_days") || s.active.contains("dark_days");
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
        if (world == null || sound == null || world.getMinecraftServer() == null) {
            return;
        }
        int dim = DimKeys.of(world);
        for (ServerPlayer p : world.getMinecraftServer().getPlayerList().getPlayerList()) {
            if (DimKeys.of(p.level()) != dim) continue;
            p.level().playSound(null, p.getX(), p.getY(), p.getZ(), sound, SoundSource.AMBIENT, 10000.0f, pitch);
        }
    }

    private static void rollDarkDaysIfNeeded(Level world) {
        boolean activeOrQueued;
        if (world == null || world.isClientSide) {
            return;
        }
        if (!world.dimensionType().isSurfaceWorld()) {
            return;
        }
        int dim = DimKeys.of(world);
        CelestialNightData data = CelestialNightData.get(world);
        CelestialNightData.DimState s = data.getOrCreate(dim);
        boolean bl = activeOrQueued = s.forced.contains("dark_days") || s.active.contains("dark_days") || s.darkDaysStartTime > 0L || s.darkDaysEndTime > 0L;
        if (activeOrQueued) {
            return;
        }
        long worldTime = world.getWorldTime();
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
        long seed = world.getSeed() ^ day * 918273L ^ (long)"dark_days".hashCode() * 31L;
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
    public static void onWorldTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END) {
            return;
        }
        Level w = e.world;
        if (w == null || w.isClientSide) {
            return;
        }
        CelestialEventManager.rollIfNeeded(w);
        CelestialEventManager.rollDarkDaysIfNeeded(w);
        CelestialEventManager.tickDarkDays(w);
    }

    private static void grantDarkDaysAdvancement(Level world) {
        if (world == null || world.getMinecraftServer() == null) {
            return;
        }
        Advancement adv = world.getMinecraftServer().getAdvancementManager().getAdvancement(ResourceLocation.fromNamespaceAndPath("srparasites", "dark_days"));
        if (adv == null) {
            return;
        }
        int dim = DimKeys.of(world);
        for (ServerPlayer p : world.getMinecraftServer().getPlayerList().getPlayerList()) {
            AdvancementProgress progress;
            if (DimKeys.of(p.level()) != dim || (progress = p.getAdvancements().getProgress(adv)).isDone()) continue;
            for (String criterion : progress.getRemaningCriteria()) {
                p.getAdvancements().grantCriterion(adv, criterion);
            }
        }
    }
}

