package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSIVH;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSVBase;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSVTop;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import java.nio.FloatBuffer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class RenderEntityBodyModel
extends Render<EntityBodyModel> {
    public static final ResourceLocation TDODSIVH = ResourceLocation.parse("srparasites:textures/entity/monster/dodsivh.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/sdodivhead.png");
    public static final ResourceLocation TVENKROLSVTOP = ResourceLocation.parse("srparasites:textures/entity/monster/venkrolsvtop.png");
    public static final ResourceLocation STEXTUREVENKROLSVTOP = ResourceLocation.parse("srparasites:textures/entity/monster/svenkrolsvtop.png");
    public static final ResourceLocation TVENKROLSVBASE = ResourceLocation.parse("srparasites:textures/entity/monster/venkrolsvbase.png");
    public static final ResourceLocation STEXTUREVENKROLSVBASE = ResourceLocation.parse("srparasites:textures/entity/monster/svenkrolsvbase.png");
    protected ModelSRP MDODSIVH = new ModelDodSIVH();
    protected ModelSRP MVENKROLSVBASE = new ModelVenkrolSVBase();
    protected ModelSRP MVENKROLSVTOP = new ModelVenkrolSVTop();
    private static final DynamicTexture TEXTURE_BRIGHTNESS = null;
    protected FloatBuffer brightnessBuffer = java.nio.FloatBuffer.allocate(4);

    public RenderEntityBodyModel(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    protected ResourceLocation getEntityTexture(EntityBodyModel entity) {
        switch (entity.getSkin()) {
            case 0: {
                return TDODSIVH;
            }
            case 2: {
                return TVENKROLSVBASE;
            }
            case 3: {
                return TVENKROLSVTOP;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TDODSIVH;
    }

    public void doRender(EntityBodyModel entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        byte typePart = entity.getSkin();
        float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
        float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
        float f2 = f1 - f;
        float f7 = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
        this.renderLivingAt(entity, x, y, z);
        float f8 = this.handleRotationFloat(entity, partialTicks);
        this.applyRotations(entity, f8, f, partialTicks);
        float f4 = this.prepareScale(entity, partialTicks);
        float f5 = 0.0f;
        float f6 = 0.0f;
        f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
        f6 = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks);
        GlStateManager.enableAlpha();
        switch (typePart) {
            case 0: {
                this.MDODSIVH.setLivingAnimations(entity, f6, f5, partialTicks);
                this.MDODSIVH.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
                break;
            }
            case 2: {
                this.MVENKROLSVBASE.setLivingAnimations(entity, f6, f5, partialTicks);
                this.MVENKROLSVBASE.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
                break;
            }
            case 3: {
                this.MVENKROLSVTOP.setLivingAnimations(entity, f6, f5, partialTicks);
                this.MVENKROLSVTOP.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
                break;
            }
            case 120: {
                this.MDODSIVH.setLivingAnimations(entity, f6, f5, partialTicks);
                this.MDODSIVH.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
            }
        }
        boolean flag = this.setDoRenderBrightness(entity, partialTicks);
        this.renderModel(entity, f6, f5, f8, f2, f7, f4);
        if (flag) {
            this.unsetBrightness();
        }
        GlStateManager.depthMask((boolean)true);
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f;
        for (f = yawOffset - prevYawOffset; f < -180.0f; f += 360.0f) {
        }
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return prevYawOffset + partialTicks * f;
    }

    protected void renderLivingAt(EntityBodyModel entityLivingBaseIn, double x, double y, double z) {
        GlStateManager.translate((float)((float)x), (float)((float)y), (float)((float)z));
    }

    protected float handleRotationFloat(EntityBodyModel livingBase, float partialTicks) {
        return (float)livingBase.tickCount + partialTicks;
    }

    protected void applyRotations(EntityBodyModel entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        GlStateManager.rotate((float)(180.0f - rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
        if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s))) {
            GlStateManager.translate((float)0.0f, (float)(entityLiving.getBbHeight() + 0.1f), (float)0.0f);
            GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        }
    }

    public float prepareScale(EntityBodyModel entitylivingbaseIn, float partialTicks) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale((float)-1.0f, (float)-1.0f, (float)1.0f);
        this.preRenderCallback(entitylivingbaseIn, partialTicks);
        float f = 0.0625f;
        GlStateManager.translate((float)0.0f, (float)-1.501f, (float)0.0f);
        return 0.0625f;
    }

    protected void preRenderCallback(EntityBodyModel entitylivingbaseIn, float partialTickTime) {
    }

    protected void renderModel(EntityBodyModel entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag1;
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean bl = flag1 = !flag && !entitylivingbaseIn.isInvisibleTo((Player)Minecraft.getInstance().player);
        if (flag || flag1) {
            if (!this.bindEntityTexture(entitylivingbaseIn)) {
                return;
            }
            if (flag1) {
            }
            switch (entitylivingbaseIn.getSkin()) {
                case 0: {
                    this.MDODSIVH.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 2: {
                    this.MVENKROLSVBASE.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 3: {
                    this.MVENKROLSVTOP.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 120: {
                    this.MDODSIVH.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                }
            }
            if (flag1) {
            }
        }
    }

    protected boolean setDoRenderBrightness(EntityBodyModel entityLivingBaseIn, float partialTicks) {
        return this.setBrightness(entityLivingBaseIn, partialTicks, true);
    }

    protected boolean setBrightness(EntityBodyModel entitylivingbaseIn, float partialTicks, boolean combineTextures) {
        boolean flag1;
        float f = entitylivingbaseIn.getLightLevelDependentMagicValue();
        int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
        boolean flag = (i >> 24 & 0xFF) > 0;
        boolean bl = flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
        if (!flag && !flag1) {
            return false;
        }
        if (!flag && !combineTextures) {
            return false;
        }
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.glTexEnvi((int)8960, (int)8704, (int)OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_RGB, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_RGB, (int)OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_RGB, (int)OpenGlHelper.GL_PRIMARY_COLOR);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_ALPHA, (int)7681);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_ALPHA, (int)OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_ALPHA, (int)770);
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.glTexEnvi((int)8960, (int)8704, (int)OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_RGB, (int)OpenGlHelper.GL_INTERPOLATE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_RGB, (int)OpenGlHelper.GL_CONSTANT);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_RGB, (int)OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE2_RGB, (int)OpenGlHelper.GL_CONSTANT);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND2_RGB, (int)770);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_ALPHA, (int)7681);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_ALPHA, (int)OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_ALPHA, (int)770);
        this.brightnessBuffer.position(0);
        if (flag1) {
            this.brightnessBuffer.put(1.0f);
            this.brightnessBuffer.put(0.0f);
            this.brightnessBuffer.put(0.0f);
            this.brightnessBuffer.put(0.3f);
        } else {
            float f1 = (float)(i >> 24 & 0xFF) / 255.0f;
            float f2 = (float)(i >> 16 & 0xFF) / 255.0f;
            float f3 = (float)(i >> 8 & 0xFF) / 255.0f;
            float f4 = (float)(i & 0xFF) / 255.0f;
            this.brightnessBuffer.put(f2);
            this.brightnessBuffer.put(f3);
            this.brightnessBuffer.put(f4);
            this.brightnessBuffer.put(1.0f - f1);
        }
        this.brightnessBuffer.flip();
        GlStateManager.glTexEnv((int)8960, (int)8705, (FloatBuffer)this.brightnessBuffer);
        GlStateManager.setActiveTexture((int)OpenGlHelper.GL_TEXTURE2);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture((int)0);
        GlStateManager.glTexEnvi((int)8960, (int)8704, (int)OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_RGB, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_RGB, (int)OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_RGB, (int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_ALPHA, (int)7681);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_ALPHA, (int)OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_ALPHA, (int)770);
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        return true;
    }

    protected int getColorMultiplier(EntityBodyModel entitylivingbaseIn, float lightBrightness, float partialTickTime) {
        return 0;
    }

    protected boolean isVisible(EntityBodyModel p_193115_1_) {
        return !p_193115_1_.isInvisible() || this.renderOutlines;
    }

    protected void unsetBrightness() {
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.glTexEnvi((int)8960, (int)8704, (int)OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_RGB, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_RGB, (int)OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_RGB, (int)OpenGlHelper.GL_PRIMARY_COLOR);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_ALPHA, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_ALPHA, (int)OpenGlHelper.defaultTexUnit);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_ALPHA, (int)OpenGlHelper.GL_PRIMARY_COLOR);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_ALPHA, (int)770);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_ALPHA, (int)770);
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.glTexEnvi((int)8960, (int)8704, (int)OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_RGB, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_RGB, (int)5890);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_RGB, (int)OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_ALPHA, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_ALPHA, (int)770);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_ALPHA, (int)5890);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.setActiveTexture((int)OpenGlHelper.GL_TEXTURE2);
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture((int)0);
        GlStateManager.glTexEnvi((int)8960, (int)8704, (int)OpenGlHelper.GL_COMBINE);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_RGB, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND1_RGB, (int)768);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_RGB, (int)5890);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE1_RGB, (int)OpenGlHelper.GL_PREVIOUS);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_COMBINE_ALPHA, (int)8448);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_OPERAND0_ALPHA, (int)770);
        GlStateManager.glTexEnvi((int)8960, (int)OpenGlHelper.GL_SOURCE0_ALPHA, (int)5890);
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
    }
}

