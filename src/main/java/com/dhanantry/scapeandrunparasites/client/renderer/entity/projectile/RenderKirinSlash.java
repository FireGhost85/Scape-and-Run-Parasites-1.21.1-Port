package com.dhanantry.scapeandrunparasites.client.renderer.entity.projectile;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileKirinSlash;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class RenderKirinSlash
extends Render<EntityProjectileKirinSlash> {
    public RenderKirinSlash(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0f;
    }

    public void doRender(EntityProjectileKirinSlash entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float visibleAge = (float)entity.tickCount + partialTicks - (float)entity.getDelayTicks();
        if (visibleAge <= 0.0f) {
            return;
        }
        float life = Math.max(1.0f, (float)entity.getLife());
        float growth = entity.getGrowth(partialTicks);
        if (growth <= 0.0f) {
            return;
        }
        float fadeInTicks = 5.0f;
        float fadeOutTicks = 18.0f;
        float fadeIn = Math.min(visibleAge / fadeInTicks, 1.0f);
        float fadeOut = 1.0f;
        if (visibleAge > life - fadeOutTicks) {
            fadeOut = 1.0f - Math.min((visibleAge - (life - fadeOutTicks)) / fadeOutTicks, 1.0f);
        }
        float alphaBase = Math.max(0.0f, Math.min(fadeIn, fadeOut));
        float hitPopScale = 1.0f;
        float hitPopWidthScale = 1.0f;
        float hitPopAlphaBoost = 1.0f;
        if (entity.isHitPopping()) {
            float p = Math.min(((float)entity.getHitPopAge() + partialTicks) / Math.max(1.0f, (float)entity.getHitPopTicks()), 1.0f);
            hitPopScale = 1.0f - p * 0.82f;
            hitPopWidthScale = 1.0f - p * 0.92f;
            float flash = 1.0f - Math.abs(p - 0.18f) / 0.18f;
            flash = Math.max(0.0f, Math.min(flash, 1.0f));
            hitPopAlphaBoost = (1.0f - p) * (1.0f + flash * 1.8f);
        }
        if (entity.isFading()) {
            alphaBase *= 0.35f;
        }
        if ((alphaBase *= hitPopAlphaBoost) <= 0.01f || hitPopScale <= 0.02f || hitPopWidthScale <= 0.02f) {
            return;
        }
        float length = entity.getSlashLength() * growth * hitPopScale;
        float r = 0.42f;
        float g = 0.86f;
        float b = 1.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x), (float)((float)y), (float)((float)z));
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        GlStateManager.rotate((float)entity.getSlashYaw(), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)entity.getSlashPitch(), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.rotate((float)entity.getSlashRoll(), (float)0.0f, (float)0.0f, (float)1.0f);
        this.drawForwardCrossSlash(0.52f * hitPopWidthScale, length, r, g, b, 0.13f * alphaBase);
        this.drawForwardCrossSlash(0.2f * hitPopWidthScale, length, r, g, b, 0.42f * alphaBase);
        this.drawForwardCrossSlash(0.055f * hitPopWidthScale, length, 1.0f, 1.0f, 1.0f, 0.9f * alphaBase);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void drawForwardCrossSlash(float width, float length, float r, float g, float b, float alpha) {
        this.drawForwardPlaneX(width, length, r, g, b, alpha);
        this.drawForwardPlaneY(width, length, r, g, b, alpha);
    }

    private void drawForwardPlaneX(float width, float length, float r, float g, float b, float alpha) {
        float halfW = width * 0.5f;
        int segments = 18;
        Tessellator tessellator = Tessellator.getInstance();
        Tessellator.BufferBuilder buffer = tessellator.getWorldRenderer();
        buffer.begin(7, Tessellator.VertexFormat.POSITION_COLOR);
        for (int i = 0; i < segments; ++i) {
            float t0 = (float)i / (float)segments;
            float t1 = (float)(i + 1) / (float)segments;
            float z0 = length * t0;
            float z1 = length * t1;
            float a0 = alpha * this.getBeamEdgeFade(t0);
            float a1 = alpha * this.getBeamEdgeFade(t1);
            buffer.pos((double)(-halfW), 0.0, (double)z0).color(r, g, b, a0).endVertex();
            buffer.pos((double)halfW, 0.0, (double)z0).color(r, g, b, a0).endVertex();
            buffer.pos((double)halfW, 0.0, (double)z1).color(r, g, b, a1).endVertex();
            buffer.pos((double)(-halfW), 0.0, (double)z1).color(r, g, b, a1).endVertex();
        }
        tessellator.draw();
    }

    private void drawForwardPlaneY(float width, float length, float r, float g, float b, float alpha) {
        float halfW = width * 0.5f;
        int segments = 18;
        Tessellator tessellator = Tessellator.getInstance();
        Tessellator.BufferBuilder buffer = tessellator.getWorldRenderer();
        buffer.begin(7, Tessellator.VertexFormat.POSITION_COLOR);
        for (int i = 0; i < segments; ++i) {
            float t0 = (float)i / (float)segments;
            float t1 = (float)(i + 1) / (float)segments;
            float z0 = length * t0;
            float z1 = length * t1;
            float a0 = alpha * this.getBeamEdgeFade(t0);
            float a1 = alpha * this.getBeamEdgeFade(t1);
            buffer.pos(0.0, (double)(-halfW), (double)z0).color(r, g, b, a0).endVertex();
            buffer.pos(0.0, (double)halfW, (double)z0).color(r, g, b, a0).endVertex();
            buffer.pos(0.0, (double)halfW, (double)z1).color(r, g, b, a1).endVertex();
            buffer.pos(0.0, (double)(-halfW), (double)z1).color(r, g, b, a1).endVertex();
        }
        tessellator.draw();
    }

    private float getBeamEdgeFade(float t) {
        float edgeFade = Math.min(t, 1.0f - t) * 2.0f;
        edgeFade = edgeFade * edgeFade * (3.0f - 2.0f * edgeFade);
        return Math.max(0.0f, Math.min(edgeFade, 1.0f));
    }

    @Nullable
    protected ResourceLocation getEntityTexture(EntityProjectileKirinSlash entity) {
        return null;
    }
}

