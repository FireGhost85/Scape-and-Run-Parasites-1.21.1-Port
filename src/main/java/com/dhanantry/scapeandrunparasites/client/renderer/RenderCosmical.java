package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.legacy.LayerRenderer;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.renderer.LayerCosmicalHacking;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public abstract class RenderCosmical<T extends EntityPCosmical>
extends RenderMalleable<T> {
    protected ModelSRP mainModel2;
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = ResourceLocation.parse("srparasites:textures/entity/layer/cosmichasking.png");

    public RenderCosmical(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
        this.mainModel2 = (ModelSRP)modelbaseIn;
        this.addLayer((LayerRenderer)new LayerCosmicalHacking(this, this.mainModel2));
    }

    public RenderCosmical(RenderManager rendermanagerIn, ModelBase modelbaseIn, ModelSRP modelbaseIn2, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
        this.mainModel2 = modelbaseIn2;
    }

    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        this.doRenderCosmical(entity, x, y, z, entityYaw, partialTicks);
        this.doRenderLaser(entity, x, y, z, entityYaw, partialTicks);
    }

    public void doRenderLaser(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        for (LivingEntity entitylivingbase : ((EntityPCosmical)entity).getTargetedEntityVictims()) {
            if (entitylivingbase == null) continue;
            float f = 10.0f;
            Tessellator tessellator = Tessellator.getInstance();
            Tessellator.BufferBuilder bufferbuilder = tessellator.getWorldRenderer();
            this.bindTexture(GUARDIAN_BEAM_TEXTURE);
            GlStateManager.glTexParameteri((int)3553, (int)10242, (int)10497);
            GlStateManager.glTexParameteri((int)3553, (int)10243, (int)10497);
            GlStateManager.disableCull();
            GlStateManager.disableBlend();
            GlStateManager.depthMask((boolean)true);
            float f1 = 240.0f;
            OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
            GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
            float f2 = (float)((EntityPCosmical)entity).level().getGameTime() + partialTicks;
            float f3 = f2 * 0.5f % 1.0f;
            float f4 = entity.getEyeHeight();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)((float)x), (float)((float)y + f4), (float)((float)z));
            Vec3 vec3d = this.getPosition(entitylivingbase, (double)entitylivingbase.getBbHeight() * 0.5, partialTicks);
            Vec3 vec3d1 = this.getPosition((LivingEntity)entity, f4, partialTicks);
            Vec3 vec3d2 = vec3d.subtract(vec3d1);
            double d0 = vec3d2.length() + 1.0;
            vec3d2 = vec3d2.normalize();
            float f5 = (float)Math.acos(vec3d2.y);
            float f6 = (float)Math.atan2(vec3d2.z, vec3d2.x);
            GlStateManager.rotate((float)((1.5707964f + -f6) * 57.295776f), (float)0.0f, (float)1.0f, (float)0.0f);
            GlStateManager.rotate((float)(f5 * 57.295776f), (float)1.0f, (float)0.0f, (float)0.0f);
            boolean i = true;
            double d1 = (double)f2 * 0.05 * -1.5;
            bufferbuilder.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
            float f7 = f * f;
            int j = 178 + (int)(f7 * 191.0f);
            int k = 0 + (int)(f7 * 191.0f);
            int l = 250 - (int)(f7 * 64.0f);
            double d2 = 0.2;
            double d3 = 0.282;
            double d4 = 0.0 + Math.cos(d1 + 2.356194490192345) * 0.282;
            double d5 = 0.0 + Math.sin(d1 + 2.356194490192345) * 0.282;
            double d6 = 0.0 + Math.cos(d1 + 0.7853981633974483) * 0.282;
            double d7 = 0.0 + Math.sin(d1 + 0.7853981633974483) * 0.282;
            double d8 = 0.0 + Math.cos(d1 + 3.9269908169872414) * 0.282;
            double d9 = 0.0 + Math.sin(d1 + 3.9269908169872414) * 0.282;
            double d10 = 0.0 + Math.cos(d1 + 5.497787143782138) * 0.282;
            double d11 = 0.0 + Math.sin(d1 + 5.497787143782138) * 0.282;
            double d12 = 0.0 + Math.cos(d1 + Math.PI) * 0.2;
            double d13 = 0.0 + Math.sin(d1 + Math.PI) * 0.2;
            double d14 = 0.0 + Math.cos(d1 + 0.0) * 0.2;
            double d15 = 0.0 + Math.sin(d1 + 0.0) * 0.2;
            double d16 = 0.0 + Math.cos(d1 + 1.5707963267948966) * 0.2;
            double d17 = 0.0 + Math.sin(d1 + 1.5707963267948966) * 0.2;
            double d18 = 0.0 + Math.cos(d1 + 4.71238898038469) * 0.2;
            double d19 = 0.0 + Math.sin(d1 + 4.71238898038469) * 0.2;
            double d20 = 0.0;
            double d21 = 0.4999;
            double d22 = -1.0f + f3;
            double d23 = d0 * 2.5 + d22;
            bufferbuilder.pos(d12, d0, d13).tex(0.4999, d23).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d12, 0.0, d13).tex(0.4999, d22).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d14, 0.0, d15).tex(0.0, d22).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d14, d0, d15).tex(0.0, d23).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d16, d0, d17).tex(0.4999, d23).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d16, 0.0, d17).tex(0.4999, d22).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d18, 0.0, d19).tex(0.0, d22).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d18, d0, d19).tex(0.0, d23).color(j, k, l, 255).endVertex();
            double d24 = 0.0;
            if (((EntityPCosmical)entity).tickCount % 2 == 0) {
                d24 = 0.5;
            }
            bufferbuilder.pos(d4, d0, d5).tex(0.5, d24 + 0.5).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d6, d0, d7).tex(1.0, d24 + 0.5).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d10, d0, d11).tex(1.0, d24).color(j, k, l, 255).endVertex();
            bufferbuilder.pos(d8, d0, d9).tex(0.5, d24).color(j, k, l, 255).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    private Vec3 getPosition(LivingEntity entityLivingBaseIn, double p_177110_2_, float p_177110_4_) {
        double d0 = entityLivingBaseIn.xOld + (entityLivingBaseIn.getX() - entityLivingBaseIn.xOld) * (double)p_177110_4_;
        double d1 = p_177110_2_ + entityLivingBaseIn.yOld + (entityLivingBaseIn.getY() - entityLivingBaseIn.yOld) * (double)p_177110_4_;
        double d2 = entityLivingBaseIn.zOld + (entityLivingBaseIn.getZ() - entityLivingBaseIn.zOld) * (double)p_177110_4_;
        return new Vec3(d0, d1, d2);
    }

    public void doRenderCosmical(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        boolean shouldSit;
        if (((EntityPCosmical)entity).getCloneC()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel2.swingProgress = this.getSwingProgress(entity, partialTicks);
        this.mainModel2.isRiding = shouldSit = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
        this.mainModel2.isChild = entity.isBaby();
        try {
            float f = this.interpolateRotation(((EntityPCosmical)entity).yBodyRotO, ((EntityPCosmical)entity).yBodyRot, partialTicks);
            float f1 = this.interpolateRotation(((EntityPCosmical)entity).yHeadRotO, ((EntityPCosmical)entity).yHeadRot, partialTicks);
            float f2 = f1 - f;
            if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
                LivingEntity entitylivingbase = (LivingEntity)entity.getVehicle();
                f = this.interpolateRotation(entitylivingbase.yBodyRotO, entitylivingbase.yBodyRot, partialTicks);
                f2 = f1 - f;
                float f3 = Mth.wrapDegrees((float)f2);
                if (f3 < -85.0f) {
                    f3 = -85.0f;
                }
                if (f3 >= 85.0f) {
                    f3 = 85.0f;
                }
                f = f1 - f3;
                if (f3 * f3 > 2500.0f) {
                    f += f3 * 0.2f;
                }
                f2 = f1 - f;
            }
            float f7 = ((EntityPCosmical)entity).xRotO + (((EntityPCosmical)entity).getXRot() - ((EntityPCosmical)entity).xRotO) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f8 = this.handleRotationFloat(entity, partialTicks);
            this.rotateCorpse(entity, f8, f, partialTicks);
            float f4 = this.prepareScaleCosmical(entity, partialTicks);
            float f5 = 0.0f;
            float f6 = 0.0f;
            if (!entity.isPassenger()) {
                f5 = entity.walkAnimation.speed(partialTicks);
                f6 = entity.walkAnimation.position(partialTicks);
                if (entity.isBaby()) {
                    f6 *= 3.0f;
                }
                if (f5 > 1.0f) {
                    f5 = 1.0f;
                }
                f2 = f1 - f;
            }
            GlStateManager.enableAlpha();
            this.mainModel2.setLivingAnimations((LivingEntity)entity, f6, f5, partialTicks);
            this.mainModel2.setRotationAnglesCosmical(f6, f5, f8, f2, f7, f4, (Entity)entity);
            if (this.renderOutlines) {
                boolean flag1 = false;
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode((int)0);
                if (!this.renderMarker) {
                    this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4);
                }
                this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
                if (flag1) {
                    
                }
            } else {
                this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4);
                GlStateManager.depthMask((boolean)true);
                this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }
            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception) {
            // empty catch block
        }
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag1;
        if (((EntityPCosmical)entitylivingbaseIn).getCloneC()) {
            boolean flag12;
            boolean flag = this.isVisible(entitylivingbaseIn);
            boolean bl = flag12 = !flag && !entitylivingbaseIn.isInvisibleTo((Player)Minecraft.getInstance().player);
            if (flag || flag12) {
                if (!this.bindEntityTextureCosmical(entitylivingbaseIn)) {
                    return;
                }
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
                GlStateManager.depthMask((boolean)true);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc((int)516, (float)0.003921569f);
                if (((EntityPCosmical)entitylivingbaseIn).getShadowStatus()) {
                    this.mainModel2.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                }
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc((int)516, (float)0.1f);
                GlStateManager.depthMask((boolean)true);
            }
            return;
        }
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean bl = flag1 = !flag && !entitylivingbaseIn.isInvisibleTo((Player)Minecraft.getInstance().player);
        if (flag || flag1) {
            if (!this.bindEntityTexture(entitylivingbaseIn)) {
                return;
            }
            if (flag1) {
            }
            this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            if (flag1) {
            }
        }
    }

    protected void renderModelCosmical(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag1;
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean bl = flag1 = !flag && !entitylivingbaseIn.isInvisibleTo((Player)Minecraft.getInstance().player);
        if (flag || flag1) {
            if (!this.bindEntityTextureCosmical(entitylivingbaseIn)) {
                return;
            }
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)((EntityPCosmical)entitylivingbaseIn).shadowDamageR);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc((int)516, (float)0.003921569f);
            if (((EntityPCosmical)entitylivingbaseIn).getShadowStatus()) {
                this.mainModel2.renderC((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            }
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc((int)516, (float)0.1f);
            GlStateManager.depthMask((boolean)true);
        }
    }

    protected abstract ResourceLocation getEntityTextureCosmical(T var1);

    protected boolean bindEntityTextureCosmical(T entity) {
        ResourceLocation resourcelocation = this.getEntityTextureCosmical(entity);
        if (resourcelocation == null) {
            return false;
        }
        this.bindTexture(resourcelocation);
        return true;
    }

    protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
        if (((EntityPCosmical)entitylivingbaseIn).getCloneC()) {
            GlStateManager.scale((double)1.2, (double)1.2, (double)1.2);
        }
    }

    protected float prepareScaleCosmical(T entitylivingbaseIn, float partialTicks) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale((float)-1.0f, (float)-1.0f, (float)1.0f);
        this.preRenderCallbackCosmical(entitylivingbaseIn, partialTicks);
        float f = 0.0625f;
        GlStateManager.translate((float)0.0f, (float)-1.85f, (float)0.0f);
        return 0.0625f;
    }

    protected void preRenderCallbackCosmical(T entitylivingbaseIn, float partialTickTime) {
        GlStateManager.scale((double)1.2, (double)1.2, (double)1.2);
    }
}

