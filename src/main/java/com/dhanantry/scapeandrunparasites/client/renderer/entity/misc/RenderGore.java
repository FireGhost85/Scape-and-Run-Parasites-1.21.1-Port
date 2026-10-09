package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelGore;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderGore
extends Render<EntityGore> {
    protected ModelBase model = new ModelGore();
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/gore.png");

    public RenderGore(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
        this.shadowSize = 0.1f;
    }

    protected ResourceLocation getEntityTexture(EntityGore entity) {
        return TEXTURES;
    }

    public void doRender(EntityGore entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x), (float)((float)y + 1.5f), (float)((float)z));
        this.bindTexture(TEXTURES);
        GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode((int)this.getTeamColor(entity));
        }
        this.model.render((Entity)entity, 0.0f, 0.0f, (float)entity.tickCount, entity.getYRot(), entity.getXRot(), 0.0625f);
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
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
}

