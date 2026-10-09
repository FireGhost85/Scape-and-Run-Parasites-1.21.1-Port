package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/** The billboard renderer of the parasite projectiles (SRPProjectile of 1.12): one camera facing textured quad. */
public class SRPProjectile extends Render<Entity> {
    private final float scale;
    private ResourceLocation texture;

    public SRPProjectile(RenderManager manager, float scale) {
        super(manager);
        this.scale = scale;
    }

    public SRPProjectile(RenderManager manager, float scale, ResourceLocation sprite) {
        super(manager);
        this.scale = scale;
        this.texture = sprite;
    }

    public SRPProjectile setTexture(ResourceLocation sprite) {
        this.texture = sprite;
        return this;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        GlStateManager.pushMatrix();
        this.bindEntityTexture(entity);
        GlStateManager.translate(x, y, z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(this.scale, this.scale, this.scale);
        Tessellator tessellator = Tessellator.getInstance();
        Tessellator.BufferBuilder bufferbuilder = tessellator.getWorldRenderer();
        GlStateManager.rotate(180.0f - camera.getYRot(), 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-camera.getXRot(), 1.0f, 0.0f, 0.0f);
        bufferbuilder.begin(7, Tessellator.VertexFormat.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5, -0.25, 0.0).tex(0.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        bufferbuilder.pos(0.5, -0.25, 0.0).tex(1.0, 1.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        bufferbuilder.pos(0.5, 0.75, 0.0).tex(1.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        bufferbuilder.pos(-0.5, 0.75, 0.0).tex(0.0, 0.0).normal(0.0f, 1.0f, 0.0f).endVertex();
        tessellator.draw();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.texture;
    }
}
