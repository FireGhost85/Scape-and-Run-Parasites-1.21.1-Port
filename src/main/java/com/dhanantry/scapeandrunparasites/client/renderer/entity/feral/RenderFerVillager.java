package com.dhanantry.scapeandrunparasites.client.renderer.entity.feral;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.feral.ModelFerVillager;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerVillager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderFerVillager
extends RenderSRP<EntityFerVillager> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/fervillager.png");

    public RenderFerVillager(RenderManager manager) {
        super(manager, new ModelFerVillager(), 0.5f);
    }

    protected void preRenderCallback(EntityFerVillager entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityFerVillager entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityFerVillager entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

