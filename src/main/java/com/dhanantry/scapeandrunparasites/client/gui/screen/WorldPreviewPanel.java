package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * GuiSRPWorldPreview of 1.10.9: the panel next to the buttons of the world settings screen. A slowly drifting star field, the earth
 * of the chosen difficulty (or of the chosen star type), the moon on its orbit and, with the meteor infection on, the meteor orbit.
 */
public final class WorldPreviewPanel {
    private static final ResourceLocation EARTH_EASY = tex("earth_easy");
    private static final ResourceLocation EARTH_NORMAL = tex("earth_normal");
    private static final ResourceLocation EARTH_HARD = tex("earth_hard");
    private static final ResourceLocation EARTH_IMPOSSIBLE = tex("earth_impossible");
    private static final ResourceLocation EARTH_COLD = tex("earth_cold");
    private static final ResourceLocation EARTH_WARM = tex("earth_warm");
    private static final ResourceLocation MOON = tex("moon");
    private static final ResourceLocation METEOR = tex("meteor_orbit");

    private WorldPreviewPanel() {
    }

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/worldsettings/" + name + ".png");
    }

    public static void draw(GuiGraphics g, int x, int y, int width, int height, int difficulty, boolean meteorEnabled, int starType) {
        float time = (float) (Util.getMillis() % 100000000L) / 1000.0f;
        g.fill(x, y, x + width, y + height, -16513262);
        g.fill(x - 1, y - 1, x + width + 1, y, -11511957);
        g.fill(x - 1, y + height, x + width + 1, y + height + 1, -15394525);
        g.fill(x - 1, y, x, y + height, -11511957);
        g.fill(x + width, y, x + width + 1, y + height, -15394525);
        g.enableScissor(x, y, x + width, y + height);
        drawStars(g, x, y, width, height, time);
        drawPlanetSystem(g, x, y, width, height, difficulty, meteorEnabled, starType, time);
        g.disableScissor();
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void drawStars(GuiGraphics g, int x, int y, int width, int height, float time) {
        Random rand = new Random(923847L);
        for (int i = 0; i < 70; ++i) {
            int baseX = rand.nextInt(Math.max(1, width));
            int baseY = rand.nextInt(Math.max(1, height));
            float depth = 0.35f + rand.nextFloat() * 0.65f;
            float speed = 0.05f + depth * 0.18f;
            float phase = rand.nextFloat() * ((float) Math.PI * 2);
            boolean big = rand.nextInt(12) == 0;
            int brightness = 185 + rand.nextInt(45);
            float twinkle = 0.6f + 0.4f * (float) Math.sin(time * (0.2f + depth * 0.18f) + phase);
            int alpha = (int) (6.0f + depth * 12.0f + twinkle * 14.0f);
            int sx = x + wrap((int) ((float) baseX - time * speed * 3.0f), width);
            int sy = y + baseY;
            drawPrettyStar(g, sx, sy, brightness, alpha, big);
        }
        for (int i = 0; i < 3; ++i) {
            int baseX = rand.nextInt(Math.max(1, width));
            int baseY = rand.nextInt(Math.max(1, height));
            float speed = 0.2f + rand.nextFloat() * 0.15f;
            int sx = x + wrap((int) ((float) baseX - time * speed * 4.0f), width);
            int sy = y + baseY;
            int alpha = 4 + rand.nextInt(4);
            int len = 6 + rand.nextInt(5);
            g.fill(sx - len / 2, sy, sx + len / 2, sy + 1, argb(alpha, 190, 205, 235));
        }
    }

    private static void drawPrettyStar(GuiGraphics g, int x, int y, int brightness, int alpha, boolean big) {
        int outerAlpha = Math.max(2, alpha / 6);
        int glowAlpha = Math.max(4, alpha / 3);
        int coreAlpha = Math.min(255, alpha);
        int outer = argb(outerAlpha, brightness, brightness, 255);
        int glow = argb(glowAlpha, brightness, brightness, 255);
        int core = argb(coreAlpha, 255, 255, 255);
        g.fill(x - 1, y, x + 2, y + 1, outer);
        g.fill(x, y - 1, x + 1, y + 2, outer);
        if (big) {
            g.fill(x - 1, y - 1, x + 2, y + 2, glow);
        }
        g.fill(x, y, x + 1, y + 1, core);
    }

    private static void drawTextured(GuiGraphics g, ResourceLocation tex, int x, int y, int w, int h, float alpha) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.setColor(1.0f, 1.0f, 1.0f, alpha);
        g.blit(tex, x, y, 0.0f, 0.0f, w, h, w, h);
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private static void drawTexturedRotated(GuiGraphics g, ResourceLocation tex, float centerX, float centerY, int w, int h, float alpha, float angleDeg) {
        g.pose().pushPose();
        g.pose().translate(centerX, centerY, 0.0f);
        g.pose().mulPose(Axis.ZP.rotationDegrees(angleDeg));
        g.pose().translate((float) (-w) / 2.0f, (float) (-h) / 2.0f, 0.0f);
        drawTextured(g, tex, 0, 0, w, h, alpha);
        g.pose().popPose();
    }

    private static void drawPlanetSystem(GuiGraphics g, int x, int y, int width, int height, int difficulty, boolean meteorEnabled, int starType, float time) {
        float cx = (float) x + (float) width / 2.0f;
        float cy = (float) y + (float) height / 2.0f + 4.0f;
        int earthSize = Math.min(width, height) / 2;
        earthSize = Math.max(44, Math.min(earthSize, 70));
        drawTextured(g, earthTexture(difficulty, starType), (int) (cx - (float) earthSize / 2.0f), (int) (cy - (float) earthSize / 2.0f), earthSize, earthSize, 0.96f);
        float moonAngle = time * 0.65f;
        float moonOrbitX = (float) earthSize / 2.0f + 22.0f;
        float moonOrbitY = (float) earthSize / 3.0f + 12.0f;
        float moonX = cx + (float) Math.cos(moonAngle) * moonOrbitX;
        float moonY = cy + (float) Math.sin(moonAngle) * moonOrbitY;
        int moonSize = Math.max(10, earthSize / 5);
        float moonRotationDeg = (float) Math.toDegrees(Math.atan2(cy - moonY, cx - moonX));
        drawTexturedRotated(g, MOON, moonX, moonY, moonSize, moonSize, 0.82f, moonRotationDeg);
        if (meteorEnabled) {
            float meteorAngle = time * -1.15f + 1.7f;
            float meteorOrbitX = (float) earthSize / 2.0f + 34.0f;
            float meteorOrbitY = (float) earthSize / 3.0f + 20.0f;
            float meteorX = cx + (float) Math.cos(meteorAngle) * meteorOrbitX;
            float meteorY = cy + (float) Math.sin(meteorAngle) * meteorOrbitY;
            int meteorSize = Math.max(8, earthSize / 7);
            float meteorRotationDeg = (float) Math.toDegrees(Math.atan2(cy - meteorY, cx - meteorX));
            drawTexturedRotated(g, METEOR, meteorX, meteorY, meteorSize, meteorSize, 0.72f, meteorRotationDeg);
        }
    }

    private static ResourceLocation earthTexture(int difficulty, int starType) {
        if (starType == 1) {
            return EARTH_COLD;
        }
        if (starType == 2) {
            return EARTH_WARM;
        }
        switch (difficulty) {
            case 0:
                return EARTH_EASY;
            case 2:
                return EARTH_HARD;
            case 3:
                return EARTH_IMPOSSIBLE;
            default:
                return EARTH_NORMAL;
        }
    }

    private static int argb(int a, int r, int g, int b) {
        return clamp(a) << 24 | clamp(r) << 16 | clamp(g) << 8 | clamp(b);
    }

    private static int clamp(int v) {
        return Math.max(0, Math.min(255, v));
    }

    private static int wrap(int value, int max) {
        if (max <= 0) {
            return 0;
        }
        value %= max;
        if (value < 0) {
            value += max;
        }
        return value;
    }
}
