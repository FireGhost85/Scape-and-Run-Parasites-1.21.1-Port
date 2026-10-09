package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.special;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.special.ModelSpeBear;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeBear;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RenderSpeBear
extends RenderSRP<EntitySpeBear> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/spebear.png");
    private static final ResourceLocation GUARDIAN_BEAM_TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/test.png");

    public RenderSpeBear(RenderManager manager) {
        super(manager, new ModelSpeBear(), 0.5f);
    }

    protected void preRenderCallback(EntitySpeBear entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)(f2 * 1.3f), (float)(f3 * 1.3f), (float)(f2 * 1.3f));
    }

    protected ResourceLocation getEntityTexture(EntitySpeBear entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntitySpeBear entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }

    public void doRender(EntitySpeBear entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        LivingEntity entitylivingbase = entity.getTargetedEntity();
        if (entitylivingbase != null) {
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
            float f2 = (float)entity.level().getGameTime() + partialTicks;
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
            int j = 64 + (int)(f7 * 191.0f);
            int k = 32 + (int)(f7 * 191.0f);
            int l = 128 - (int)(f7 * 64.0f);
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
            if (entity.tickCount % 2 == 0) {
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
}

