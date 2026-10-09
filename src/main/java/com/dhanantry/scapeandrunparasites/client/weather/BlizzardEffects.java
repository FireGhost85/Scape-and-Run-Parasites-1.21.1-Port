package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * The blizzard of a cold star world (SRPBlizzardRenderer, SRPBlizzardClientEvents and the EntityRenderer mixin of 1.10.9): the
 * overworld effects draw long snow streaks instead of the vanilla rain, the fog closes in, a pale tint covers the screen.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class BlizzardEffects extends DimensionSpecialEffects.OverworldEffects {
    private static final ResourceLocation SNOW_TEXTURE = ResourceLocation.withDefaultNamespace("textures/environment/snow.png");

    @SubscribeEvent
    public static void register(RegisterDimensionSpecialEffectsEvent event) {
        event.register(BuiltinDimensionTypes.OVERWORLD_EFFECTS, new BlizzardEffects());
    }

    @Override
    public boolean renderSnowAndRain(ClientLevel level, int ticks, float partialTick, LightTexture lightTexture, double camX, double camY, double camZ) {
        float intensity = SRPBlizzardClient.getIntensity(partialTick);
        if (intensity <= 0.001f) {
            return false;
        }
        render(Minecraft.getInstance(), partialTick, intensity, camX, camY, camZ);
        return true;
    }

    private static double surfaceY(ClientLevel level, double x, double z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, Mth.floor(x), Mth.floor(z));
    }

    private static float hash01(int x, int z, int salt) {
        long value = (long)x * 341873128712L + (long)z * 132897987541L + (long)salt * 42317861L;
        value ^= value >>> 13;
        value *= 1274126177L;
        value ^= value >>> 16;
        return (float)(value & 0xFFFFFFL) / 1.6777215E7f;
    }

    private static double positiveModulo(double value, double modulus) {
        return value - Math.floor(value / modulus) * modulus;
    }

    private static void render(Minecraft mc, float partialTicks, float intensity, double cameraX, double cameraY, double cameraZ) {
        ClientLevel level = mc.level;
        Entity camera = mc.getCameraEntity();
        if (level == null || camera == null) {
            return;
        }
        intensity = Mth.clamp(intensity, 0.0f, 1.0f);
        float cameraYaw = camera.yRotO + Mth.wrapDegrees(camera.getYRot() - camera.yRotO) * partialTicks;
        double yawRad = Math.toRadians(cameraYaw);
        double cameraRightX = Math.cos(yawRad);
        double cameraRightZ = Math.sin(yawRad);
        float blackBlend = SRPBlizzardDirectionClient.getBlackBlend(partialTicks);
        double time = (float)level.getGameTime() + partialTicks;
        int radius = 8 + Mth.floor(intensity * 6.0f);
        int minX = Mth.floor(cameraX) - radius;
        int maxX = Mth.floor(cameraX) + radius;
        int minZ = Mth.floor(cameraZ) - radius;
        int maxZ = Mth.floor(cameraZ) + radius;
        double windAngle = time * 0.0012 + Math.sin(time * 3.7E-4) * 0.45;
        double gust = 0.72 + Math.sin(time * 0.065) * 0.18 + Math.sin(time * 0.017) * 0.1;
        double windStrength = (0.8 + (double)intensity * 2.2) * gust;
        double windX = Math.cos(windAngle) * windStrength;
        double windZ = Math.sin(windAngle) * windStrength;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, SNOW_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        float daylight = Mth.clamp(1.0f - level.getStarBrightness(partialTicks) * 2.0f, 0.0f, 1.0f);
        float brightness = 0.86f + daylight * 0.14f;
        int minRenderY = Mth.floor(cameraY) - 10;
        int maxRenderY = Mth.floor(cameraY) + 14;
        double snowTime = SRPBlizzardDirectionClient.getMotionPhase(partialTicks);
        boolean any = false;
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                double dx = (double)x + 0.5 - cameraX;
                double dz = (double)z + 0.5 - cameraZ;
                double distance = Math.sqrt(dx * dx + dz * dz);
                if (distance > (double)radius) {
                    continue;
                }
                int groundY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                int startY = Math.max(groundY, minRenderY);
                if (startY >= maxRenderY) {
                    continue;
                }
                float distanceFade = 1.0f - Mth.clamp((float)(distance / (double)radius), 0.0f, 1.0f);
                int laneCount = intensity > 0.7f ? 3 : 2;
                for (int lane = 0; lane < laneCount; ++lane) {
                    float randomA = hash01(x, z, lane * 11);
                    float randomB = hash01(x, z, lane * 11 + 1);
                    float randomC = hash01(x, z, lane * 11 + 2);
                    float randomD = hash01(x, z, lane * 11 + 3);
                    float randomE = hash01(x, z, lane * 11 + 4);
                    float randomF = hash01(x, z, lane * 11 + 5);
                    int baseSpacing = intensity > 0.55f ? 1 : 2;
                    int laneSpacing = baseSpacing + (randomD > 0.82f ? 1 : 0);
                    int phase = Mth.floor(randomC * (float)(laneSpacing + 1));
                    for (int y = startY + phase; y < maxRenderY; y += laneSpacing) {
                        if (randomF > 0.84f) {
                            continue;
                        }
                        double segmentHeight = 1.8 + (double)randomD * 2.1;
                        double fallValue = snowTime * (0.16 + (double)intensity * 0.18) + (double)randomC * 37.0 + (double)lane * 1.73;
                        double fall = positiveModulo(fallValue, (double)laneSpacing + 0.35);
                        double yJitter = ((double)randomE - 0.5) * 0.9;
                        double bottomWorldY = (double)y + yJitter - fall;
                        double topWorldY = bottomWorldY + segmentHeight;
                        double windPhase = time * 0.045 + (double)y * 0.13 + (double)randomA * 8.0 + (double)lane * 0.91;
                        double sideways = Math.sin(windPhase) * 0.14 * (double)intensity;
                        double perpX = -windZ;
                        double perpZ = windX;
                        double perpLength = Math.sqrt(perpX * perpX + perpZ * perpZ);
                        if (perpLength > 0.001) {
                            perpX /= perpLength;
                            perpZ /= perpLength;
                        }
                        double jitterX = ((double)randomA - 0.5) * 0.92 + perpX * sideways + (double)lane * perpX * 0.22;
                        double jitterZ = ((double)randomB - 0.5) * 0.92 + perpZ * sideways + (double)lane * perpZ * 0.22;
                        double baseX = (double)x + 0.5 + jitterX;
                        double baseZ = (double)z + 0.5 + jitterZ;
                        double streakX = -windX * (0.16 + (double)intensity * 0.24);
                        double streakZ = -windZ * (0.16 + (double)intensity * 0.24);
                        double endWorldX = baseX + streakX;
                        double endWorldZ = baseZ + streakZ;
                        double surfY = Math.max(surfaceY(level, baseX, baseZ), surfaceY(level, endWorldX, endWorldZ));
                        if (topWorldY <= surfY + 0.02) {
                            continue;
                        }
                        if (bottomWorldY < surfY + 0.02) {
                            bottomWorldY = surfY + 0.02;
                        }
                        double halfWidth = 0.11 + (double)randomB * 0.12 + (double)intensity * 0.03;
                        double widthX = cameraRightX * halfWidth;
                        double widthZ = cameraRightZ * halfWidth;
                        float alpha = intensity * (0.34f + distanceFade * 0.58f) * (0.74f + randomA * 0.26f);
                        alpha = Math.min(alpha, 0.96f);
                        if (distance < 2.0) {
                            alpha *= 0.92f;
                        }
                        float x1 = (float)(baseX - cameraX);
                        float z1 = (float)(baseZ - cameraZ);
                        float x2 = (float)(endWorldX - cameraX);
                        float z2 = (float)(endWorldZ - cameraZ);
                        float y1 = (float)(bottomWorldY - cameraY);
                        float y2 = (float)(topWorldY - cameraY);
                        float to = (float)(time * 0.09 + (double)randomC * 8.0 + (double)lane * 0.37);
                        float c = brightness * (1.0f - blackBlend);
                        float wx = (float)widthX;
                        float wz = (float)widthZ;
                        buffer.addVertex(x1 - wx, y1, z1 - wz).setUv(0.0f, to).setColor(c, c, c, alpha);
                        buffer.addVertex(x1 + wx, y1, z1 + wz).setUv(1.0f, to).setColor(c, c, c, alpha);
                        buffer.addVertex(x2 + wx, y2, z2 + wz).setUv(1.0f, to + 1.0f).setColor(c, c, c, alpha);
                        buffer.addVertex(x2 - wx, y2, z2 - wz).setUv(0.0f, to + 1.0f).setColor(c, c, c, alpha);
                        any = true;
                    }
                }
            }
        }
        if (any) {
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    /** The EntityRenderer.setupFog mixin: the fog of the blizzard closes in with the intensity. */
    @SubscribeEvent
    public static void onFog(ViewportEvent.RenderFog event) {
        float intensity = SRPBlizzardClient.getIntensity((float)event.getPartialTick());
        if (intensity <= 0.001f || event.getType() != net.minecraft.world.level.material.FogType.NONE) {
            return;
        }
        float fogEnd = 72.0f - intensity * 56.0f;
        event.setNearPlaneDistance(fogEnd * 0.08f);
        event.setFarPlaneDistance(Math.min(event.getFarPlaneDistance(), fogEnd));
        event.setCanceled(true);
    }

    /** The pale tint over the whole screen. */
    @SubscribeEvent
    public static void onOverlay(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        float intensity = SRPBlizzardClient.getIntensity(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        if (intensity <= 0.001f) {
            return;
        }
        float alpha = Mth.clamp(intensity * 0.24f, 0.0f, 0.24f);
        int alphaByte = Mth.clamp((int)(alpha * 255.0f), 0, 255);
        int color = alphaByte << 24 | 205 << 16 | 215 << 8 | 224;
        event.getGuiGraphics().fill(0, 0, event.getGuiGraphics().guiWidth(), event.getGuiGraphics().guiHeight(), color);
    }
}
