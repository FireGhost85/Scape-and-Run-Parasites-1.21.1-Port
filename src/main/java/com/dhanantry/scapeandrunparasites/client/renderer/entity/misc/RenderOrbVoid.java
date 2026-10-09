package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelOrbScary;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbVoid;
import java.util.HashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RenderOrbVoid
extends Render<EntityOrbVoid> {
    protected ModelSRP mainModel2 = new ModelOrbScary();
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/orbvoid.png");
    private static final ResourceLocation LIGHTNING_TEX = ResourceLocation.parse("srparasites:textures/entity/monster/orbvoid_armor.png");
    private static final float SPHERE_RADIUS = 0.317f;
    private static final int SPHERE_STACKS = 18;
    private static final int SPHERE_SLICES = 18;
    private final HashMap<Integer, Float> scaleSmooth = new HashMap();

    public RenderOrbVoid(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    protected ResourceLocation getEntityTexture(EntityOrbVoid entity) {
        return TEXTURES;
    }

    public boolean shouldRender(EntityOrbVoid livingEntity, Frustum camera, double camX, double camY, double camZ) {
        return true;
    }

    public void doRender(EntityOrbVoid entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        this.doRenderCosmical(entity, x, y, z, entityYaw, partialTicks);
    }

    public void doRenderCosmical(EntityOrbVoid entity, double x, double y, double z, float entityYaw, float partialTicks) {
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
                this.renderModelCosmical(entity, f6, f5, f8, f2, f7, f4, partialTicks);
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

    protected void renderModelCosmical(EntityOrbVoid e, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, float partialTicks) {
        boolean flag1;
        boolean flag = false;
        boolean bl = flag1 = !flag && !e.isInvisibleTo((Player)Minecraft.getInstance().player);
        if (flag || flag1) {
            if (!this.bindEntityTextureCosmical(e)) {
                return;
            }
            boolean cameraInside = this.isCameraInsideOrb(e, partialTicks);
            GlStateManager.enableDepth();
            GlStateManager.depthFunc((int)515);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.alphaFunc((int)516, (float)0.003921569f);
            GlStateManager.depthMask((!cameraInside ? 1 : 0) != 0);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            this.renderTexturedSphere(0.317f, 18, 18);
            GlStateManager.depthMask((boolean)false);
            GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            this.renderChargedAura(e, partialTicks);
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc((int)516, (float)0.1f);
            GlStateManager.depthMask((boolean)true);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
    }

    private boolean isCameraInsideOrb(EntityOrbVoid e, float partialTicks) {
        Entity view = Minecraft.getInstance().getCameraEntity();
        if (view == null) {
            return false;
        }
        Vec3 eye = view.getEyePosition(partialTicks);
        double GROW = 1.25;
        AABB bb = e.getBoundingBox().inflate(1.25);
        return bb.contains(eye);
    }

    private void renderChargedAura(EntityOrbVoid e, float partialTicks) {
        this.bindTexture(LIGHTNING_TEX);
        GlStateManager.pushMatrix();
        float sca = 1.12f;
        GlStateManager.scale((float)sca, (float)sca, (float)sca);
        GlStateManager.disableLighting();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        float prevBX = OpenGlHelper.lastBrightnessX;
        float prevBY = OpenGlHelper.lastBrightnessY;
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)240.0f, (float)240.0f);
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        float t = (float)e.tickCount + partialTicks;
        float pulse = 0.25f + 0.15f * Mth.sin((float)(t * 0.25f));
        GlStateManager.color((float)0.6f, (float)0.85f, (float)1.0f, (float)pulse);
        float uScroll = t * 0.01f;
        float vScroll = t * 0.015f;
        this.renderSwirlSphere(0.317f, 18, 18, uScroll, vScroll);
        GlStateManager.setActiveTexture((int)OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)prevBX, (float)prevBY);
        GlStateManager.setActiveTexture((int)OpenGlHelper.defaultTexUnit);
        GlStateManager.disableBlend();
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableLighting();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.popMatrix();
    }

    private void renderSwirlSphere(float radius, int stacks, int slices, float uOff, float vOff) {
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder bb = tess.getWorldRenderer();
        float uScale = 2.0f;
        float vScale = 2.0f;
        for (int i = 0; i < stacks; ++i) {
            float v0 = (float)i / (float)stacks;
            float v1 = (float)(i + 1) / (float)stacks;
            double phi0 = Math.PI * (double)v0;
            double phi1 = Math.PI * (double)v1;
            bb.begin(5, Tessellator.VertexFormat.POSITION_TEX);
            for (int j = 0; j <= slices; ++j) {
                float u = (float)j / (float)slices;
                double theta = Math.PI * 2 * (double)u;
                double x0 = Math.sin(phi0) * Math.cos(theta);
                double y0 = Math.cos(phi0);
                double z0 = Math.sin(phi0) * Math.sin(theta);
                double x1 = Math.sin(phi1) * Math.cos(theta);
                double y1 = Math.cos(phi1);
                double z1 = Math.sin(phi1) * Math.sin(theta);
                float uu = u * 2.0f + uOff;
                float vv0 = (1.0f - v0) * 2.0f + vOff;
                float vv1 = (1.0f - v1) * 2.0f + vOff;
                bb.pos(x0 * (double)radius, y0 * (double)radius, z0 * (double)radius).tex((double)uu, (double)vv0).endVertex();
                bb.pos(x1 * (double)radius, y1 * (double)radius, z1 * (double)radius).tex((double)uu, (double)vv1).endVertex();
            }
            tess.draw();
        }
    }

    private void renderTexturedSphere(float radius, int stacks, int slices) {
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder bb = tess.getWorldRenderer();
        for (int i = 0; i < stacks; ++i) {
            float v0 = (float)i / (float)stacks;
            float v1 = (float)(i + 1) / (float)stacks;
            double phi0 = Math.PI * (double)v0;
            double phi1 = Math.PI * (double)v1;
            bb.begin(5, Tessellator.VertexFormat.POSITION_TEX);
            for (int j = 0; j <= slices; ++j) {
                float u = (float)j / (float)slices;
                double theta = Math.PI * 2 * (double)u;
                double x0 = Math.sin(phi0) * Math.cos(theta);
                double y0 = Math.cos(phi0);
                double z0 = Math.sin(phi0) * Math.sin(theta);
                double x1 = Math.sin(phi1) * Math.cos(theta);
                double y1 = Math.cos(phi1);
                double z1 = Math.sin(phi1) * Math.sin(theta);
                bb.pos(x0 * (double)radius, y0 * (double)radius, z0 * (double)radius).tex((double)u, (double)(1.0f - v0)).endVertex();
                bb.pos(x1 * (double)radius, y1 * (double)radius, z1 * (double)radius).tex((double)u, (double)(1.0f - v1)).endVertex();
            }
            tess.draw();
        }
    }

    protected boolean bindEntityTextureCosmical(EntityOrbVoid entity) {
        ResourceLocation resourcelocation = this.getEntityTexture(entity);
        if (resourcelocation == null) {
            return false;
        }
        this.bindTexture(resourcelocation);
        return true;
    }

    protected float prepareScaleCosmical(EntityOrbVoid entitylivingbaseIn, float partialTicks) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale((float)-1.0f, (float)-1.0f, (float)1.0f);
        this.preRenderCallbackCosmical(entitylivingbaseIn, partialTicks);
        return 0.0625f;
    }

    protected void preRenderCallbackCosmical(EntityOrbVoid e, float partialTickTime) {
        int id;
        Float prev;
        float age = (float)e.tickCount + partialTickTime;
        float base = Math.max(e.getBbWidth() * 2.0f, e.getBbHeight() * 1.9f) * 0.8f;
        float GROW_TICKS = 35.0f;
        float g = Mth.clamp((float)(age / 35.0f), (float)0.0f, (float)1.0f);
        g = g * g * (3.0f - 2.0f * g);
        float normalScale = base * (0.35f + 0.95f * g);
        float tau = Mth.clamp((float)(age / 35.0f), (float)0.0f, (float)1.0f);
        float BOUNCE_AMP = 0.28f;
        float DAMP = 5.0f;
        float FREQ = 10.0f;
        float bounce = 0.28f * (float)Math.exp(-5.0f * tau) * Mth.sin((float)(10.0f * tau * (float)Math.PI));
        float targetScale = normalScale * (1.0f + bounce);
        if (targetScale < 0.001f) {
            targetScale = 0.001f;
        }
        if ((prev = this.scaleSmooth.get(id = e.getId())) == null) {
            prev = Float.valueOf(targetScale);
        }
        float smooth = prev.floatValue() + (targetScale - prev.floatValue()) * 0.2f;
        this.scaleSmooth.put(id, Float.valueOf(smooth));
        if (e.isRemoved()) {
            this.scaleSmooth.remove(id);
        }
        GlStateManager.scale((float)smooth, (float)smooth, (float)smooth);
        float hoverAmp = 0.05f * (0.2f + 0.8f * g);
        float hover = Mth.sin((float)(age * 0.1f + (float)id * 0.3f)) * hoverAmp;
        float yaw = age * 1.4f % 360.0f;
        float pitch = 8.0f * Mth.sin((float)(age * 0.07f + (float)id));
        GlStateManager.rotate((float)yaw, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)pitch, (float)1.0f, (float)0.0f, (float)0.0f);
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

    protected void renderLivingAt(EntityOrbVoid entityLivingBaseIn, double x, double y, double z) {
        GlStateManager.translate((float)((float)x), (float)((float)y), (float)((float)z));
    }

    protected float handleRotationFloat(EntityOrbVoid livingBase, float partialTicks) {
        return (float)livingBase.tickCount + partialTicks;
    }

    protected void applyRotations(EntityOrbVoid entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
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

