package com.dhanantry.scapeandrunparasites.client.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/** The 1.12 Render base: {@link #doRender} draws at the entity position of the current matrix, with {@link GlContext} set up. */
public abstract class Render<T extends Entity> extends EntityRenderer<T> {
    public final RenderManager renderManager;
    protected boolean renderOutlines;
    protected float shadowSize;

    protected Render(RenderManager manager) {
        super(manager.context);
        this.renderManager = manager;
    }

    protected abstract ResourceLocation getEntityTexture(T entity);

    protected int getTeamColor(T entity) {
        return 0;
    }

    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
    }

    public void bindTexture(ResourceLocation location) {
        GlContext.texture = location;
    }

    public boolean bindEntityTexture(T entity) {
        ResourceLocation rl = this.getEntityTexture(entity);
        if (rl == null) {
            return false;
        }
        GlContext.texture = rl;
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.getEntityTexture(entity);
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        GlContext.begin(poseStack, buffer, packedLight);
        poseStack.pushPose();
        try {
            this.doRender(entity, 0.0, 0.0, 0.0, entityYaw, partialTicks);
        } finally {
            poseStack.popPose();
            GlContext.end();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
