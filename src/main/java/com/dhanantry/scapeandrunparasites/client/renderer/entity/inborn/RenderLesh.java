package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderLesh
extends RenderLiving<EntityLesh> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/lesh.png");

    public RenderLesh(RenderManager manager) {
        super(manager, (ModelBase)new ModelLesh(), 0.2f);
    }

    protected void preRenderCallback(EntityLesh entitylivingbaseIn, float partialTickTime) {
        float ff = entitylivingbaseIn.getSelfeFlashIntensityS();
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)(ff * f2), (float)(ff * f3), (float)(ff * f2));
    }

    protected ResourceLocation getEntityTexture(EntityLesh entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityLesh entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

