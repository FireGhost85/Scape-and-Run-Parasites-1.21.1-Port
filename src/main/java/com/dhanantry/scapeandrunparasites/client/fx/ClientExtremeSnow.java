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
        double radius = 12.0;
        ParticleStatus setting = mc.options.particles().get();
        double budget = setting == ParticleStatus.MINIMAL ? 0.25 : (setting == ParticleStatus.DECREASED ? 0.55 : 1.0);
        int base = 140 + (int) (360.0f * intensity);
        int count = (int) ((double) base * 1.8 * budget);
        for (int i = 0; i < count; ++i) {
            int z;
            int x = (int) (p.getX() + w.random.nextGaussian() * radius);
            BlockPos ground = w.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(x, (int) p.getY(), z = (int) (p.getZ() + w.random.nextGaussian() * radius)));
            if (!w.canSeeSky(ground) || !forceAnywhere && !w.getBiome(ground).value().shouldSnow(w, ground)) continue;
            double spawnY = Math.max((double) (ground.getY() + 16 + w.random.nextInt(8)), p.getY() + 16.0 + (double) w.random.nextInt(8));
            double sx = (double) x + 0.5 + (w.random.nextDouble() - 0.5);
            double sz = (double) z + 0.5 + (w.random.nextDouble() - 0.5);
            double jitter = 0.03;
            double vx = windX + (w.random.nextDouble() - 0.5) * jitter;
            double vz = windZ + (w.random.nextDouble() - 0.5) * jitter;
            double vy = -0.22 - 0.06 * (double) intensity - w.random.nextDouble() * 0.04;
            mc.particleEngine.add(new ParticleBlizzard(w, sx, spawnY, sz, vx, vy, vz, p));
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
