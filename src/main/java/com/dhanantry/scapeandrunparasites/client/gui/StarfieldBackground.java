package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RandomSource;


public class StarfieldBackground
extends Gui {
    private final Minecraft mc;
    private final RandomSource rand = RandomSource.create();
    private final List<Star> stars = new ArrayList<Star>();
    private final List<ShootingStar> shootingStars = new ArrayList<ShootingStar>();
    private float scrollX = 0.0f;
    private float scrollY = 0.0f;
    private long lastFrameTime = System.currentTimeMillis();
    private static final float STAR_SCROLL_SPEED_X = -4.0f;
    private static final float STAR_SCROLL_SPEED_Y = -2.0f;
    private int lastWidth = -1;
    private int lastHeight = -1;
    private long initTime = System.currentTimeMillis();

    public StarfieldBackground(Minecraft mc) {
        this.mc = mc;
    }

    public void render(int mouseX, int mouseY, float partialTicks, boolean jumbled) {
        this.render(mouseX, mouseY, partialTicks, jumbled, false);
    }

    public void render(int mouseX, int mouseY, float partialTicks, boolean jumbled, boolean darkDays) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        if (width != this.lastWidth || height != this.lastHeight || this.stars.isEmpty()) {
            this.rebuild(width, height, jumbled);
            this.lastWidth = width;
            this.lastHeight = height;
        }
        long nowMs = System.currentTimeMillis();
        float delta = (float)(nowMs - this.lastFrameTime) / 1000.0f;
        this.lastFrameTime = nowMs;
        float scrollMul = jumbled ? 1.35f : 1.0f;
        this.scrollX += -4.0f * delta * scrollMul;
        this.scrollY += -2.0f * delta * scrollMul;
        long now = System.currentTimeMillis();
        float time = (float)(now - this.initTime) / 1000.0f;
        this.drawGradientSky(width, height, jumbled, darkDays);
        if (!darkDays) {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            this.renderStars(width, height, time, jumbled);
            this.updateAndRenderShootingStars(width, height, time, partialTicks, jumbled);
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    private void rebuild(int width, int height, boolean jumbled) {
        this.stars.clear();
        this.shootingStars.clear();
        int starCount = jumbled ? Math.max(85, width * height / 5200) : Math.max(45, width * height / 9000);
        for (int i = 0; i < starCount; ++i) {
            Star s = new Star();
            s.x = this.rand.nextFloat() * (float)Math.max(1, width);
            s.y = this.rand.nextFloat() * (float)Math.max(1, height);
            s.size = this.rand.nextBoolean() ? 1 : 2;
            s.baseAlpha = 0.35f + this.rand.nextFloat() * 0.45f;
            s.twinkleSpeed = 0.6f + this.rand.nextFloat() * 1.4f;
            s.twinkleOffset = this.rand.nextFloat() * 6.28318f;
            this.stars.add(s);
        }
        int shootingCount = jumbled ? 10 : 3;
        for (int i = 0; i < shootingCount; ++i) {
            float stagger = jumbled ? (float)i * 0.22f : (float)i * 0.9f;
            this.shootingStars.add(this.createOffscreenShootingStar(width, height, stagger, jumbled));
        }
    }

    private ShootingStar createOffscreenShootingStar(int width, int height, float extraDelay, boolean jumbled) {
        ShootingStar s = new ShootingStar();
        s.reset(width, height, this.rand, extraDelay, jumbled);
        return s;
    }

    private void drawGradientSky(int width, int height, boolean jumbled, boolean darkDays) {
        int bottom;
        int mid;
        int top;
        if (darkDays) {
            top = -16777216;
            mid = -16777216;
            bottom = -16777216;
        } else if (jumbled) {
            top = -16120560;
            mid = -15595238;
            bottom = -16449017;
        } else {
            top = -16448251;
            mid = -16119284;
            bottom = -16645630;
        }
        this.drawGradientRect(0, 0, width, (int)((float)height * 0.7f), top, mid);
        this.drawGradientRect(0, (int)((float)height * 0.7f), width, height, mid, bottom);
    }

    private void renderStars(int width, int height, float time, boolean jumbled) {
        for (Star s : this.stars) {
            float pulse = 0.5f + 0.5f * (float)Math.sin(time * s.twinkleSpeed + s.twinkleOffset);
            float alpha = s.baseAlpha * (0.55f + pulse * 0.45f);
            int a = this.clamp255((int)(alpha * 255.0f));
            int baseRgb = jumbled ? 12088575 : 0xFFFFFF;
            int glowRgb = jumbled ? 14264319 : 0xFFFFFF;
            int color = a << 24 | baseRgb;
            float sx = this.wrap(s.x + this.scrollX, width);
            float sy = this.wrap(s.y + this.scrollY, height);
            int ix = (int)sx;
            int iy = (int)sy;
            Gui.drawRect((int)ix, (int)iy, (int)(ix + s.size), (int)(iy + s.size), (int)color);
            if (s.size > 1 && a > 60) {
                int glowA = this.clamp255(jumbled ? a / 3 : a / 4);
                int glowColor = glowA << 24 | glowRgb;
                Gui.drawRect((int)(ix - 1), (int)iy, (int)(ix + s.size + 1), (int)(iy + 1), (int)glowColor);
                Gui.drawRect((int)ix, (int)(iy - 1), (int)(ix + 1), (int)(iy + s.size + 1), (int)glowColor);
            }
            if (sx < 2.0f) {
                Gui.drawRect((int)(ix + width), (int)iy, (int)(ix + width + s.size), (int)(iy + s.size), (int)color);
            }
            if (sy < 2.0f) {
                Gui.drawRect((int)ix, (int)(iy + height), (int)(ix + s.size), (int)(iy + height + s.size), (int)color);
            }
            if (!(sx < 2.0f) || !(sy < 2.0f)) continue;
            Gui.drawRect((int)(ix + width), (int)(iy + height), (int)(ix + width + s.size), (int)(iy + height + s.size), (int)color);
        }
    }

    private float wrap(float value, int max) {
        if (max <= 0) {
            return 0.0f;
        }
        if ((value %= (float)max) < 0.0f) {
            value += (float)max;
        }
        return value;
    }

    private void updateAndRenderShootingStars(int width, int height, float time, float partialTicks, boolean jumbled) {
        for (ShootingStar s : this.shootingStars) {
            float progress;
            s.update(width, height, this.rand, jumbled);
            if (!s.active || (progress = s.getProgress()) <= 0.0f || progress >= 1.0f) continue;
            float alphaMul = 1.0f - progress;
            int headAlpha = this.clamp255((int)(alphaMul * (jumbled ? 235.0f : 220.0f)));
            int tailAlpha = this.clamp255((int)(alphaMul * (jumbled ? 120.0f : 90.0f)));
            float headX = s.x;
            float headY = s.y;
            float tailX = s.x - s.dirX * s.length;
            float tailY = s.y - s.dirY * s.length;
            this.drawTrail((int)headX, (int)headY, (int)tailX, (int)tailY, headAlpha, tailAlpha, jumbled);
            int headColor = headAlpha << 24 | (jumbled ? 14264319 : 0xFFFFFF);
            Gui.drawRect((int)((int)headX), (int)((int)headY), (int)((int)headX + 2), (int)((int)headY + 2), (int)headColor);
        }
    }

    private void drawTrail(int x0, int y0, int x1, int y1, int headAlpha, int tailAlpha, boolean jumbled) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps <= 0) {
            return;
        }
        int rgb = jumbled ? 11032055 : 0xFFFFFF;
        for (int i = 0; i <= steps; ++i) {
            float t = (float)i / (float)steps;
            int x = x0 + Math.round((float)dx * t);
            int y = y0 + Math.round((float)dy * t);
            int alpha = (int)((float)headAlpha + (float)(tailAlpha - headAlpha) * t);
            alpha = this.clamp255(alpha);
            int color = alpha << 24 | rgb;
            Gui.drawRect((int)x, (int)y, (int)(x + 1), (int)(y + 1), (int)color);
        }
    }

    private int clamp255(int v) {
        if (v < 0) {
            return 0;
        }
        if (v > 255) {
            return 255;
        }
        return v;
    }

    private static class ShootingStar {
        float x;
        float y;
        float startX;
        float startY;
        float endX;
        float endY;
        float dirX;
        float dirY;
        float length;
        long startTime;
        long durationMs;
        long nextSpawnTime;
        boolean active;

        private ShootingStar() {
        }

        void reset(int width, int height, RandomSource rand, float extraDelaySeconds, boolean jumbled) {
            int side;
            if (jumbled) {
                side = rand.nextInt(4);
                switch (side) {
                    case 0: {
                        this.startX = -30.0f - (float)rand.nextInt(80);
                        this.startY = rand.nextFloat() * (float)height;
                        break;
                    }
                    case 1: {
                        this.startX = (float)width + 30.0f + (float)rand.nextInt(80);
                        this.startY = rand.nextFloat() * (float)height;
                        break;
                    }
                    case 2: {
                        this.startX = rand.nextFloat() * (float)width;
                        this.startY = -30.0f - (float)rand.nextInt(80);
                        break;
                    }
                    default: {
                        this.startX = rand.nextFloat() * (float)width;
                        this.startY = (float)height + 30.0f + (float)rand.nextInt(80);
                    }
                }
                this.endX = rand.nextFloat() * (float)width;
                this.endY = rand.nextFloat() * (float)height;
                for (float tries = 0.0f; this.distanceSq(this.startX, this.startY, this.endX, this.endY) < 12000.0f && tries < 6.0f; tries += 1.0f) {
                    this.endX = rand.nextFloat() * (float)width;
                    this.endY = rand.nextFloat() * (float)height;
                }
            } else {
                side = rand.nextInt(3);
                switch (side) {
                    case 0: {
                        this.startX = -40.0f - (float)rand.nextInt(80);
                        this.startY = rand.nextFloat() * (float)height;
                        this.endX = (float)width + 120.0f;
                        this.endY = this.startY + (-40.0f + rand.nextFloat() * ((float)height * 0.35f + 80.0f));
                        break;
                    }
                    case 1: {
                        this.startX = rand.nextFloat() * (float)width;
                        this.startY = -30.0f - (float)rand.nextInt(60);
                        this.endX = this.startX + ((float)width * 0.35f + rand.nextFloat() * ((float)width * 0.45f));
                        this.endY = (float)height + 80.0f;
                        break;
                    }
                    default: {
                        this.startX = (float)width + 40.0f + (float)rand.nextInt(80);
                        this.startY = rand.nextFloat() * (float)height;
                        this.endX = -120.0f;
                        this.endY = this.startY + (-40.0f + rand.nextFloat() * ((float)height * 0.35f + 80.0f));
                    }
                }
            }
            float dx = this.endX - this.startX;
            float dy = this.endY - this.startY;
            float len = (float)Math.sqrt(dx * dx + dy * dy);
            if (len < 0.001f) {
                dx = 1.0f;
                dy = 0.0f;
                len = 1.0f;
            }
            this.dirX = dx / len;
            this.dirY = dy / len;
            this.length = jumbled ? 32.0f + rand.nextFloat() * 45.0f : 20.0f + rand.nextFloat() * 35.0f;
            this.durationMs = jumbled ? 450L + (long)rand.nextInt(350) : 900L + (long)rand.nextInt(700);
            long now = System.currentTimeMillis();
            this.startTime = now + (long)(extraDelaySeconds * 1000.0f);
            this.nextSpawnTime = now + (jumbled ? 180L + (long)rand.nextInt(420) : 2500L + (long)rand.nextInt(3000));
            this.x = this.startX;
            this.y = this.startY;
            this.active = false;
        }

        void update(int width, int height, RandomSource rand, boolean jumbled) {
            float progress;
            long now = System.currentTimeMillis();
            if (!this.active) {
                if (now >= this.startTime) {
                    this.active = true;
                } else {
                    return;
                }
            }
            if ((progress = this.getProgress()) >= 1.0f) {
                this.active = false;
                this.reset(width, height, rand, jumbled ? 0.12f + rand.nextFloat() * 0.25f : 0.7f + rand.nextFloat() * 2.4f, jumbled);
                return;
            }
            this.x = this.startX + (this.endX - this.startX) * progress;
            this.y = this.startY + (this.endY - this.startY) * progress;
        }

        private float distanceSq(float x1, float y1, float x2, float y2) {
            float dx = x2 - x1;
            float dy = y2 - y1;
            return dx * dx + dy * dy;
        }

        float getProgress() {
            long now = System.currentTimeMillis();
            return (float)(now - this.startTime) / (float)this.durationMs;
        }
    }

    private static class Star {
        float x;
        float y;
        int size;
        float baseAlpha;
        float twinkleSpeed;
        float twinkleOffset;

        private Star() {
        }
    }
}

