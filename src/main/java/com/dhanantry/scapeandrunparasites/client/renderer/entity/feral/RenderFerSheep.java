package com.dhanantry.scapeandrunparasites.client.renderer.entity.feral;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.feral.ModelFerSheep;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderFerSheep
extends RenderSRP<EntityFerSheep> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/fersheep.png");

    public RenderFerSheep(RenderManager manager) {
        super(manager, new ModelFerSheep(), 0.5f);
    }

    protected void preRenderCallback(EntityFerSheep entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityFerSheep entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityFerSheep entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

