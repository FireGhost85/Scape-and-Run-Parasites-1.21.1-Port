package com.dhanantry.scapeandrunparasites.client.renderer.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.ModelProjectileHomming;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class RenderProjectileHomming
extends Render<EntityProjectileHomming> {
    private static final ResourceLocation EVOKER_ILLAGER_FANGS = ResourceLocation.parse("srparasites:textures/entity/projectile/projectileh.png");
    private final ModelProjectileHomming model = new ModelProjectileHomming();

    public RenderProjectileHomming(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    public void doRender(EntityProjectileHomming entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        float f = this.rotLerp(entity.yRotO, entity.getYRot(), partialTicks);
        float f1 = entity.xRotO + (entity.getXRot() - entity.xRotO) * partialTicks;
        float f2 = (float)entity.tickCount + partialTicks;
        GlStateManager.translate((float)((float)x), (float)((float)y + 0.15f), (float)((float)z));
        GlStateManager.rotate((float)(Mth.sin((float)(f2 * 0.1f)) * 180.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.rotate((float)(Mth.cos((float)(f2 * 0.1f)) * 180.0f), (float)1.0f, (float)0.0f, (float)0.0f);
        GlStateManager.rotate((float)(Mth.sin((float)(f2 * 0.15f)) * 360.0f), (float)0.0f, (float)0.0f, (float)1.0f);
        float f3 = 0.03125f;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale((float)-1.0f, (float)-1.0f, (float)1.0f);
        this.bindEntityTexture(entity);
        this.model.render((Entity)entity, 0.0f, 0.0f, 0.0f, f, f1, 0.03125f);
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)0.5f);
        GlStateManager.scale((float)1.5f, (float)1.5f, (float)1.5f);
        this.model.render((Entity)entity, 0.0f, 0.0f, 0.0f, f, f1, 0.03125f);
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected ResourceLocation getEntityTexture(EntityProjectileHomming entity) {
        return EVOKER_ILLAGER_FANGS;
    }

    private float rotLerp(float p_188347_1_, float p_188347_2_, float p_188347_3_) {
        float f;
        for (f = p_188347_2_ - p_188347_1_; f < -180.0f; f += 360.0f) {
        }
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return p_188347_1_ + p_188347_3_ * f;
    }
}

