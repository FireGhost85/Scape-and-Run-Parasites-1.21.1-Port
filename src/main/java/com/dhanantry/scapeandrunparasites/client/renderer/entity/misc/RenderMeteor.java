package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelMeteor;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityMeteor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RenderMeteor
extends Render<EntityMeteor> {
    protected ModelSRP mainModel2 = new ModelMeteor();
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/projectile/meteor.png");

    public RenderMeteor(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    public boolean shouldRender(EntityMeteor livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    protected ResourceLocation getEntityTexture(EntityMeteor entity) {
        return TEXTURES;
    }

    public void doRender(EntityMeteor entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        this.doRenderCosmical(entity, x, y, z, entityYaw, partialTicks);
    }

    public void doRenderCosmical(EntityMeteor entity, double x, double y, double z, float entityYaw, float partialTicks) {
        boolean shouldSit;
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel2.isRiding = shouldSit = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
        try {
            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
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
            float f7 = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            float f8 = this.handleRotationFloat(entity, partialTicks);
            this.applyRotations(entity, f8, f, partialTicks);
            float f4 = this.prepareScaleCosmical(entity, partialTicks);
            float f5 = 0.0f;
            float f6 = 0.0f;
            if (!entity.isPassenger()) {
                f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                f6 = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks);
                if (f5 > 1.0f) {
                    f5 = 1.0f;
                }
                f2 = f1 - f;
            }
            GlStateManager.enableAlpha();
            this.mainModel2.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel2.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
            this.mainModel2.setRotationAnglesCosmical(f6, f5, f8, f2, f7, f4, entity);
            if (this.renderOutlines) {
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode((int)this.getTeamColor(entity));
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
            } else {
                this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4);
                GlStateManager.depthMask((boolean)true);
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

    protected void renderModelCosmical(EntityMeteor entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag1;
        boolean flag = false;
        boolean bl = flag1 = !flag && !entitylivingbaseIn.isInvisibleTo((Player)Minecraft.getInstance().player);
        if (flag || flag1) {
            if (!this.bindEntityTextureCosmical(entitylivingbaseIn)) {
                return;
            }
            this.mainModel2.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        }
    }

    protected boolean bindEntityTextureCosmical(EntityMeteor entity) {
        ResourceLocation resourcelocation = this.getEntityTexture(entity);
        if (resourcelocation == null) {
            return false;
        }
        this.bindTexture(resourcelocation);
        return true;
    }

    protected float prepareScaleCosmical(EntityMeteor entitylivingbaseIn, float partialTicks) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale((float)-1.0f, (float)-1.0f, (float)1.0f);
        this.preRenderCallbackCosmical(entitylivingbaseIn, partialTicks);
        float f = 0.0625f;
        GlStateManager.translate((float)0.0f, (float)-1.501f, (float)0.0f);
        return 0.0625f;
    }

    protected void preRenderCallbackCosmical(EntityMeteor entitylivingbaseIn, float partialTickTime) {
        float f = 1.0f;
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.1f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        float plusX = 0.5f;
        float plusY = 0.5f;
        if (!entitylivingbaseIn.getRoot()) {
            plusX = -0.8f;
            plusY = -0.8f;
        }
        GlStateManager.scale((float)(plusX + f2), (float)(plusY + f3), (float)(plusX + f2));
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

    protected void renderLivingAt(EntityMeteor entityLivingBaseIn, double x, double y, double z) {
        GlStateManager.translate((float)((float)x), (float)((float)y), (float)((float)z));
    }

    protected float handleRotationFloat(EntityMeteor livingBase, float partialTicks) {
        return (float)livingBase.tickCount + partialTicks;
    }

    protected void applyRotations(EntityMeteor entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        GlStateManager.rotate((float)(180.0f - rotationYaw), (float)0.0f, (float)1.0f, (float)0.0f);
        if (entityLiving.deathTime > 0) {
            float f = ((float)entityLiving.deathTime + partialTicks - 1.0f) / 20.0f * 1.6f;
            if ((f = Mth.sqrt((float)f)) > 1.0f) {
                f = 1.0f;
            }
            GlStateManager.rotate((float)(f * 90.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        } else {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s))) {
                GlStateManager.translate((float)0.0f, (float)(entityLiving.getBbHeight() + 0.1f), (float)0.0f);
                GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
        }
    }
}

