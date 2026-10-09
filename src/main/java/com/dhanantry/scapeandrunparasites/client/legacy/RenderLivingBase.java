package com.dhanantry.scapeandrunparasites.client.legacy;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/** The 1.12 RenderLivingBase: body rotation, scaling, the model call and the layers, written against {@link GlContext}. */
public abstract class RenderLivingBase<T extends LivingEntity> extends Render<T> {
    protected ModelBase mainModel;
    protected List<LayerRenderer<T>> layerRenderers = new ArrayList<>();
    protected boolean renderMarker;
    protected float shadowSize;
    protected float partialTicksNow;

    protected RenderLivingBase(RenderManager manager, ModelBase model, float shadowSize) {
        super(manager);
        this.mainModel = model;
        this.shadowRadius = shadowSize;
        this.shadowSize = shadowSize;
    }

    public ModelBase getMainModel() {
        return this.mainModel;
    }

    protected boolean addLayer(LayerRenderer<T> layer) {
        return this.layerRenderers.add(layer);
    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f = yawOffset - prevYawOffset;
        while (f < -180.0f) {
            f += 360.0f;
        }
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return prevYawOffset + partialTicks * f;
    }

    protected float handleRotationFloat(T livingBase, float partialTicks) {
        return (float) livingBase.tickCount + partialTicks;
    }

    protected float getDeathMaxRotation(T entityLivingBaseIn) {
        return 90.0f;
    }

    protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
        GlStateManager.translate((float) x, (float) y, (float) z);
    }

    protected void rotateCorpse(T entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        GlStateManager.rotate(180.0f - rotationYaw, 0.0f, 1.0f, 0.0f);
        if (entityLiving.deathTime > 0) {
            float f = ((float) entityLiving.deathTime + partialTicks - 1.0f) / 20.0f * 1.6f;
            f = Mth.sqrt(f);
            if (f > 1.0f) {
                f = 1.0f;
            }
            GlStateManager.rotate(f * this.getDeathMaxRotation(entityLiving), 0.0f, 0.0f, 1.0f);
        }
    }

    protected boolean isVisible(T livingEntity) {
        return !livingEntity.isInvisible();
    }

    protected float getSwingProgress(T livingBase, float partialTickTime) {
        return livingBase.getAttackAnim(partialTickTime);
    }

    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
    }

    protected float prepareScale(T entitylivingbaseIn, float partialTicks) {
        GlStateManager.scale(-1.0f, -1.0f, 1.0f);
        this.preRenderCallback(entitylivingbaseIn, partialTicks);
        GlStateManager.translate(0.0f, -1.501f, 0.0f);
        return 0.0625f;
    }

    /** Overlay (hurt flash) of the model; the malleable parasites override the colour. */
    protected int getOverlay(T entity, float partialTicks) {
        return LivingEntityRenderer.getOverlayCoords(entity, 0.0f);
    }

    public void setLightmap(LivingEntity entity) {
        GlContext.light = GlContext.baseLight;
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel.isRiding = entity.isPassenger() && entity.getVehicle() != null;
        this.mainModel.isChild = entity.isBaby();
        float f = this.interpolateRotation(entity.yBodyRotO, entity.yBodyRot, partialTicks);
        float f1 = this.interpolateRotation(entity.yHeadRotO, entity.yHeadRot, partialTicks);
        float f2 = f1 - f;
        float f7 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        this.renderLivingAt(entity, x, y, z);
        float f8 = this.handleRotationFloat(entity, partialTicks);
        this.rotateCorpse(entity, f8, f, partialTicks);
        float f4 = this.prepareScale(entity, partialTicks);
        float f5 = 0.0f;
        float f6 = 0.0f;
        if (!entity.isPassenger() && entity.isAlive()) {
            f5 = entity.walkAnimation.speed(partialTicks);
            f6 = entity.walkAnimation.position(partialTicks);
            if (entity.isBaby()) {
                f6 *= 3.0f;
            }
            if (f5 > 1.0f) {
                f5 = 1.0f;
            }
        }
        this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
        this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
        this.partialTicksNow = partialTicks;
        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
        this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    protected void renderModel(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        float partialTicks = this.partialTicksNow;
        boolean visible = !entity.isInvisible();
        boolean ghost = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
        if (!visible && !ghost) {
            return;
        }
        if (!this.bindEntityTexture(entity)) {
            return;
        }
        GlContext.overlay = this.getOverlay(entity, partialTicks);
        if (ghost) {
            GlContext.a = 0.15f;
            GlContext.blend = true;
        }
        this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (ghost) {
            GlContext.a = 1.0f;
            GlContext.blend = false;
        }
        GlContext.tintA = 0.0f;
    }

    protected void renderLayers(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        for (LayerRenderer<T> layer : this.layerRenderers) {
            GlContext.overlay = this.getOverlay(entity, partialTicks);
            layer.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            GlContext.r = GlContext.g = GlContext.b = GlContext.a = 1.0f;
            GlContext.additive = false;
            GlContext.blend = false;
            GlContext.tintA = 0.0f;
            GlContext.light = GlContext.baseLight;
        }
    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
