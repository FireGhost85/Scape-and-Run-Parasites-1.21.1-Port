package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.network.BestiarySeenCelestialPayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

/** CelestialSkyRenderer of 1.10.9: the celestial objects of the night and the Dark Days sky dome, drawn after the world (RenderWorldLast). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class CelestialSkyRenderer {
    private static final double SKY_RADIUS = 180.0;
    private static final float ORBIT_PERIOD_TICKS = 12000.0f;
    private static final Map<String, Long> ORBIT_STARTS = new HashMap<String, Long>();
    private static long lastDayTime = 0L;
    private static final Set<String> SENT = new HashSet<String>();

    private CelestialSkyRenderer() {
    }

    private static boolean hasFieldGuide(Player p) {
        if (p == null) {
            return false;
        }
        for (ItemStack st : p.getInventory().items) {
            if (!st.isEmpty() && st.getItem() == SRPItems.SRP_FIELD_GUIDE.get()) {
                return true;
            }
        }
        return p.getOffhandItem().getItem() == SRPItems.SRP_FIELD_GUIDE.get();
    }

    private static void recordSeenOnce(Minecraft mc, String id) {
        if (id == null || mc == null || mc.player == null) {
            return;
        }
        if (!CelestialSkyRenderer.hasFieldGuide(mc.player)) {
            return;
        }
        IBestiaryProgress prog = BestiaryCapability.get(mc.player);
        if (prog != null && prog.hasSeenCelestial(id)) {
            return;
        }
        if (SENT.contains(id)) {
            return;
        }
        SENT.add(id);
        ScapeAndRunParasites.LOGGER.info("[SRP] seen celestial {}", id);
        PacketDistributor.sendToServer(new BestiarySeenCelestialPayload(id));
    }

    public static void clearSeenCacheClient() {
        SENT.clear();
    }

    private static Vec3 dirFromYawPitch(float yawDeg, float pitchDeg) {
        float yaw = (float)Math.toRadians(yawDeg);
        float pitch = (float)Math.toRadians(pitchDeg);
        float x = -Mth.sin(yaw) * Mth.cos(pitch);
        float y = Mth.sin(pitch);
        float z = Mth.cos(yaw) * Mth.cos(pitch);
        return new Vec3((double)x, (double)y, (double)z);
    }

    private static float getCelestialMovementSpeedMultiplier() {
        return Mth.clamp((float)SRPConfigWorld.celestialMovementSpeedMultiplier, 0.01f, 100.0f);
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderLevelStageEvent e) {
        if (e.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }
        if (!SRPConfigWorld.enableCelestialObjects) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ClientLevel world = mc.level;
        if (world == null || mc.player == null) {
            return;
        }
        var dimType = world.dimensionType();
        if (!(dimType.natural() && dimType.hasSkyLight() && !dimType.hasCeiling())) {
            return;
        }
        float partialTicks = e.getPartialTick().getGameTimeDeltaPartialTick(false);
        long totalTime = world.getDayTime();
        long dayTime = totalTime % 24000L;
        // LevelRenderer already multiplied the camera rotation into the global model view matrix that the shader applies on top of the
        // vertices; using the event matrix as well would rotate the sky twice and make the objects swing across the screen with the camera.
        Matrix4f modelView = RenderSystem.getModelViewMatrix().equals(new Matrix4f(), 1.0E-3f) ? new Matrix4f(e.getModelViewMatrix()) : new Matrix4f();
        if (BlackSkyClient.isBlackSkyActive()) {
            CelestialSkyRenderer.recordSeenOnce(mc, "dark_days");
            CelestialSkyRenderer.renderBlackSkyDome(mc, modelView);
            return;
        }
        if (dayTime < lastDayTime) {
            ORBIT_STARTS.clear();
        }
        lastDayTime = dayTime;
        String dim = DimKeys.of(world);
        int phase = CelestialPhaseClient.getPhase(dim);
        float celestialAngle = world.getTimeOfDay(partialTicks);
        float starBrightness = world.getStarBrightness(partialTicks);
        if (starBrightness <= 0.0f) {
            return;
        }
        float rainStrength = world.getRainLevel(partialTicks);
        if (rainStrength > 0.0f || world.isThundering()) {
            return;
        }
        long worldTime = world.getGameTime();
        float movementSpeed = CelestialSkyRenderer.getCelestialMovementSpeedMultiplier();
        double motionTime = ((float)worldTime + partialTicks) * movementSpeed;
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(com.mojang.blaze3d.platform.GlStateManager.SourceFactor.SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, com.mojang.blaze3d.platform.GlStateManager.SourceFactor.ONE, com.mojang.blaze3d.platform.GlStateManager.DestFactor.ZERO);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        try {
            for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
                float alpha;
                float period;
                boolean isForced = CelestialPhaseClient.isForcedTonight(dim, def.id);
                if (!isForced && !def.isPhaseAllowed(phase) || !isForced && !CelestialPhaseClient.isActiveTonight(dim, def.id)) continue;
                CelestialSkyRenderer.recordSeenOnce(mc, def.id);
                float yawDeg = def.yawDeg;
                float pitchDeg = def.pitchDeg;
                period = def.orbitPeriodTicks > 0.0f ? def.orbitPeriodTicks : ORBIT_PERIOD_TICKS;
                if (def.orbitPath != CelestialObjectDefinition.OrbitPath.NONE) {
                    float t;
                    String orbitKey = dim + ":" + def.id;
                    if (def.oneShotOrbit) {
                        long startTime = ORBIT_STARTS.computeIfAbsent(orbitKey, k -> worldTime);
                        t = (float)((double)(((float)(worldTime - startTime) + partialTicks) * movementSpeed) / (double)period);
                        if (t >= 1.0f) {
                            continue;
                        }
                    } else {
                        t = (float)(motionTime % (double)period / (double)period);
                    }
                    switch (def.orbitPath) {
                        case RING: {
                            yawDeg = def.yawDeg + def.orbitYawRangeDeg * t;
                            pitchDeg = def.orbitPitchMinDeg;
                            break;
                        }
                        case ARC: {
                            yawDeg = def.yawDeg + def.orbitYawRangeDeg * t;
                            float arc = (float)Math.sin(Math.PI * (double)t);
                            pitchDeg = def.orbitPitchMinDeg + (def.orbitPitchMaxDeg - def.orbitPitchMinDeg) * arc;
                            break;
                        }
                        default:
                            break;
                    }
                } else if (def.fastStreak) {
                    float t = (float)(motionTime % 12000.0 / 12000.0);
                    yawDeg += t * 360.0f;
                }
                if (def.followsStars) {
                    float starAngleDeg = celestialAngle * 360.0f;
                    yawDeg += starAngleDeg;
                }
                if (def.rotationSpeedDeg != 0.0f) {
                    float spin = (float)(motionTime / 20.0 * (double)def.rotationSpeedDeg);
                    yawDeg += spin;
                }
                Vec3 dir = CelestialSkyRenderer.dirFromYawPitch(yawDeg, pitchDeg);
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                if (def.animated && def.frameCount > 1) {
                    int frame = (int)(worldTime / (long)def.frameTimeTicks % (long)def.frameCount);
                    float frameH = 1.0f / (float)def.frameCount;
                    v0 = (float)frame * frameH;
                    v1 = v0 + frameH;
                }
                alpha = Mth.clamp(def.baseOpacity * starBrightness, 0.0f, 1.0f);
                if (alpha <= 0.001f) continue;
                Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);
                Vec3 side = worldUp.cross(dir);
                if (side.lengthSqr() < 1.0E-4) {
                    worldUp = new Vec3(0.0, 0.0, 1.0);
                    side = worldUp.cross(dir);
                }
                side = side.normalize();
                Vec3 upVec = dir.cross(side).normalize();
                double half = def.size;
                Vec3 center = dir.scale(SKY_RADIUS);
                Vec3 p0 = center.add(side.scale(-half)).add(upVec.scale(-half));
                Vec3 p1 = center.add(side.scale(half)).add(upVec.scale(-half));
                Vec3 p2 = center.add(side.scale(half)).add(upVec.scale(half));
                Vec3 p3 = center.add(side.scale(-half)).add(upVec.scale(half));
                RenderSystem.setShaderTexture(0, def.texture);
                BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                buf.addVertex(modelView, (float)p0.x, (float)p0.y, (float)p0.z).setUv(u0, v1).setColor(1.0f, 1.0f, 1.0f, alpha);
                buf.addVertex(modelView, (float)p1.x, (float)p1.y, (float)p1.z).setUv(u1, v1).setColor(1.0f, 1.0f, 1.0f, alpha);
                buf.addVertex(modelView, (float)p2.x, (float)p2.y, (float)p2.z).setUv(u1, v0).setColor(1.0f, 1.0f, 1.0f, alpha);
                buf.addVertex(modelView, (float)p3.x, (float)p3.y, (float)p3.z).setUv(u0, v0).setColor(1.0f, 1.0f, 1.0f, alpha);
                BufferUploader.drawWithShader(buf.buildOrThrow());
            }
        }
        finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
        }
    }

    private static void renderBlackSkyDome(Minecraft mc, Matrix4f modelView) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        try {
            float renderDistanceBlocks = (float)mc.options.getEffectiveRenderDistance() * 16.0f;
            float r = renderDistanceBlocks - 12.0f;
            if (r < 24.0f) {
                r = 24.0f;
            }
            if (r > 180.0f) {
                r = 180.0f;
            }
            BufferBuilder buf = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            float[][] faces = {
                {-r, -r, -r, r, -r, -r, r, r, -r, -r, r, -r},
                {r, -r, r, -r, -r, r, -r, r, r, r, r, r},
                {-r, -r, r, -r, -r, -r, -r, r, -r, -r, r, r},
                {r, -r, -r, r, -r, r, r, r, r, r, r, -r},
                {-r, r, -r, r, r, -r, r, r, r, -r, r, r}
            };
            for (float[] f : faces) {
                for (int i = 0; i < 4; ++i) {
                    buf.addVertex(modelView, f[i * 3], f[i * 3 + 1], f[i * 3 + 2]).setColor(0.0f, 0.0f, 0.0f, 0.9f);
                }
            }
            BufferUploader.drawWithShader(buf.buildOrThrow());
        }
        finally {
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }
}
