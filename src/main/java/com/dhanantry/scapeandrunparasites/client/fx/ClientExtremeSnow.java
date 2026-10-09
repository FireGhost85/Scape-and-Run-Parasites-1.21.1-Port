package com.dhanantry.scapeandrunparasites.client.fx;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.mojang.blaze3d.shaders.FogShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FogType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/** Client side of the extreme snow storm: close fog, whitened fog colour and a dense snowfall around the player. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientExtremeSnow {
    /** Distance at which the 1.10.9 exponential fog (density sqrt(ln 50) / 10) left 2 percent visibility; the linear fog ends there. */
    private static final float FOG_END = 10.0f;
    /** Height above the player's feet at which a flake is over its nominal spawn column (see onClientTick). */
    private static final double COMPENSATE_HEIGHT = 7.0;
    /** Distance at which the closed-in fog hides the flakes. */
    private static final double SEE_RANGE = 13.0;
    private static boolean enabled = false;
    private static float intensity = 1.0f;
    private static boolean forceAnywhere = true;
    private static float windDeg = 30.0f;
    private static float windSpeed = 0.5f;
    private static double windX = 0.0;
    private static double windZ = 0.0;

    private ClientExtremeSnow() {
    }

    public static void setState(boolean on, float i, boolean any, float deg, float spd) {
        enabled = on;
        intensity = Mth.clamp(i, 0.0f, 1.0f);
        forceAnywhere = any;
        windDeg = deg % 360.0f;
        windSpeed = Mth.clamp(spd, 0.0f, 1.0f);
        double rad = Math.toRadians(windDeg);
        double base = 0.15 + 0.55 * (double) windSpeed;
        windX = Math.cos(rad) * base;
        windZ = Math.sin(rad) * base;
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            if (mc.levelRenderer != null) {
                mc.levelRenderer.allChanged();
            }
        }
    }

    /** The exponential fog of 1.12 has no 1.21 counterpart, so the terrain fog is the linear fog ending at {@link #FOG_END}. */
    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog e) {
        if (!enabled || e.getMode() != FogRenderer.FogMode.FOG_TERRAIN || e.getType() != FogType.NONE) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (!ClientExtremeSnow.isOutdoors(mc.level, mc.player)) {
            return;
        }
        e.setNearPlaneDistance(0.0f);
        e.setFarPlaneDistance(FOG_END);
        e.setFogShape(FogShape.SPHERE);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColors(ViewportEvent.ComputeFogColor e) {
        if (!enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        if (!ClientExtremeSnow.isOutdoors(mc.level, mc.player)) {
            return;
        }
        float push = 0.75f;
        e.setRed(e.getRed() * (1.0f - push) + push);
        e.setGreen(e.getGreen() * (1.0f - push) + push);
        e.setBlue(e.getBlue() * (1.0f - push) + push);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        if (!enabled) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ClientLevel w = mc.level;
        if (w == null) {
            return;
        }
        LocalPlayer p = mc.player;
        if (p == null) {
            return;
        }
        // Spawn area: a uniform disc around the player instead of 1.10.9's gaussian (sigma 12) cut by a 20 block sphere. The wind shears the
        // falling snow sideways, so a concentrated cloud always leaned downwind of the player (about 3 times as many flakes in front when
        // looking downwind as when looking upwind, measured with a simulation of the particles). The disc is wide enough to cover everything
        // the fog lets the player see (13 blocks) plus the wind shift, and the number of flakes is scaled to the disc so the density the
        // player sees stays that of the original.
        double windBase = Math.sqrt(windX * windX + windZ * windZ);
        double radius = SEE_RANGE + 8.0 * windBase;
        ParticleStatus setting = mc.options.particles().get();
        double budget = setting == ParticleStatus.MINIMAL ? 0.25 : (setting == ParticleStatus.DECREASED ? 0.55 : 1.0);
        int base = 140 + (int) (360.0f * intensity);
        int count = (int) (1.135 * radius * radius * ((double) base / 500.0) * budget);
        for (int i = 0; i < count; ++i) {
            double dist = radius * Math.sqrt(w.random.nextDouble());
            double angle = w.random.nextDouble() * (Math.PI * 2.0);
            double nominalX = p.getX() + dist * Math.cos(angle);
            double nominalZ = p.getZ() + dist * Math.sin(angle);
            int x = Mth.floor(nominalX);
            int z = Mth.floor(nominalZ);
            BlockPos ground = w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, (int) p.getY(), z));
            if (!w.canSeeSky(ground) || !forceAnywhere && !w.getBiome(ground).value().shouldSnow(w, ground)) continue;
            double spawnY = Math.max((double) (ground.getY() + 16 + w.random.nextInt(8)), p.getY() + 16.0 + (double) w.random.nextInt(8));
            double jitter = 0.03;
            double vx = windX + (w.random.nextDouble() - 0.5) * jitter;
            double vz = windZ + (w.random.nextDouble() - 0.5) * jitter;
            double vy = -0.22 - 0.06 * (double) intensity - w.random.nextDouble() * 0.04;
            // The flake drifts downwind while it falls (1.10.9 had the same shear, the cloud sat downwind of the player). Run its own fall
            // forward to COMPENSATE_HEIGHT above the player's feet (same integrator as ParticleBlizzard) and start it upwind by the drift it
            // gains until then: it passes that height exactly over its nominal point.
            double fall = spawnY - p.getY();
            double fallSpeed = vy;
            int ticks = 0;
            while (fall > COMPENSATE_HEIGHT && ticks < 200) {
                fallSpeed -= 0.05;
                fall += fallSpeed;
                fallSpeed *= 0.99;
                ++ticks;
            }
            double drift = (1.0 - Math.pow(0.99, ticks)) / 0.01;
            double sx = nominalX - vx * drift;
            double sz = nominalZ - vz * drift;
            mc.particleEngine.add(new ParticleBlizzard(w, sx, spawnY, sz, vx, vy, vz, p, nominalX, nominalZ, radius + 2.0));
        }
    }

    // WORLD_SURFACE, not MOTION_BLOCKING: a snow layer of height 8 does not count as motion blocking, the sampled position was then inside the snow, where the sky light is 0, and the storm switched itself off
    private static boolean isOutdoors(Level w, LocalPlayer p) {
        BlockPos[] samples;
        for (BlockPos s : samples = new BlockPos[]{BlockPos.containing(p.getX(), p.getY(), p.getZ()), BlockPos.containing(p.getX() + 4.0, p.getY(), p.getZ()), BlockPos.containing(p.getX() - 4.0, p.getY(), p.getZ()), BlockPos.containing(p.getX(), p.getY(), p.getZ() + 4.0), BlockPos.containing(p.getX(), p.getY(), p.getZ() - 4.0)}) {
            BlockPos h = w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, s);
            if (!w.canSeeSky(h)) continue;
            return true;
        }
        return false;
    }
}
