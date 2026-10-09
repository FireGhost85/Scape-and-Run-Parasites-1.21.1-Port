package com.dhanantry.scapeandrunparasites.client.renderer.entity.projectile;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHebluLight;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class RenderHebluLight
extends Render<EntityProjectileHebluLight> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/particle/arc_flash.png");
    private static final ResourceLocation TAIL_TEXTURE = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/particle/arc_flash_tail.png");

    public RenderHebluLight(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0.0f;
    }

    public void doRender(EntityProjectileHebluLight entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float b;
        float g;
        float r;
        double mx = entity.getDeltaMovement().x;
        double my = entity.getDeltaMovement().y;
        double mz = entity.getDeltaMovement().z;
        double speed = Math.sqrt(mx * mx + my * my + mz * mz);
        if (speed < 0.001) {
            speed = 0.001;
            mz = 0.001;
        }
        float yaw = (float)(Math.atan2(mx, mz) * 180.0 / Math.PI);
        float horizontal = (float)Math.sqrt(mx * mx + mz * mz);
        float pitch = (float)(-Math.atan2(my, horizontal) * 180.0 / Math.PI);
        float width = 0.62f - (float)Math.min(speed * 0.045, 0.24);
        float coreWidth = 0.82f - (float)Math.min(speed * 0.05, 0.28);
        float backLength = 3.4f + (float)Math.min(speed * 3.4, 8.5);
        float frontLength = 0.45f + (float)Math.min(speed * 0.28, 0.85);
        if (entity.isParriedLight()) {
            r = 0.0f;
            g = 0.45f;
            b = 1.0f;
        } else {
            float danger = entity.getPlayerDangerColorAmount();
            r = 1.0f;
            g = 0.86f - danger * 0.86f;
            b = 0.42f - danger * 0.42f;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x), (float)((float)y), (float)((float)z));
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        GlStateManager.rotate((float)yaw, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)pitch, (float)1.0f, (float)0.0f, (float)0.0f);
        this.bindTexture(TAIL_TEXTURE);
        this.drawCrossRibbon(width * 1.25f, frontLength, backLength, r, g, b, 0.3f);
        this.drawCrossRibbon(width * 0.72f, frontLength * 0.65f, backLength * 1.25f, r, g, b, 0.48f);
        this.drawCrossRibbon(width * 0.38f, frontLength * 0.45f, backLength * 1.55f, r, g, b, 0.78f);
        this.bindTexture(TEXTURE);
        this.drawCrossCore(coreWidth, 0.85f, r, g, b, 0.9f);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void drawCrossRibbon(float width, float frontLength, float backLength, float r, float g, float b, float alpha) {
        this.drawRibbonPlaneX(width, frontLength, backLength, r, g, b, alpha);
        this.drawRibbonPlaneY(width, frontLength, backLength, r, g, b, alpha);
    }

    private void drawCrossCore(float width, float length, float r, float g, float b, float alpha) {
        this.drawRibbonPlaneX(width, length, length, r, g, b, alpha);
        this.drawRibbonPlaneY(width, length, length, r, g, b, alpha);
    }

    private void drawRibbonPlaneX(float width, float frontLength, float backLength, float r, float g, float b, float alpha) {
        float halfW = width * 0.5f;
        Tessellator tessellator = Tessellator.getInstance();
        Tessellator.BufferBuilder buffer = tessellator.getWorldRenderer();
        buffer.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        buffer.pos((double)(-halfW), 0.0, (double)(-backLength)).tex(0.0, 1.0).color(r, g, b, 0.0f).endVertex();
        buffer.pos((double)halfW, 0.0, (double)(-backLength)).tex(1.0, 1.0).color(r, g, b, 0.0f).endVertex();
        buffer.pos((double)halfW, 0.0, (double)frontLength).tex(1.0, 0.0).color(r, g, b, alpha).endVertex();
        buffer.pos((double)(-halfW), 0.0, (double)frontLength).tex(0.0, 0.0).color(r, g, b, alpha).endVertex();
        tessellator.draw();
    }

    private void drawRibbonPlaneY(float width, float frontLength, float backLength, float r, float g, float b, float alpha) {
        float halfW = width * 0.5f;
        Tessellator tessellator = Tessellator.getInstance();
        Tessellator.BufferBuilder buffer = tessellator.getWorldRenderer();
        buffer.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        buffer.pos(0.0, (double)(-halfW), (double)(-backLength)).tex(0.0, 1.0).color(r, g, b, 0.0f).endVertex();
        buffer.pos(0.0, (double)halfW, (double)(-backLength)).tex(1.0, 1.0).color(r, g, b, 0.0f).endVertex();
        buffer.pos(0.0, (double)halfW, (double)frontLength).tex(1.0, 0.0).color(r, g, b, alpha).endVertex();
        buffer.pos(0.0, (double)(-halfW), (double)frontLength).tex(0.0, 0.0).color(r, g, b, alpha).endVertex();
        tessellator.draw();
    }

    @Nullable
    protected ResourceLocation getEntityTexture(EntityProjectileHebluLight entity) {
        return TEXTURE;
    }
}

