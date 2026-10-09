package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfBear;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfBear
extends RenderSRP<EntityInfBear> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/infbear.png");

    public RenderInfBear(RenderManager manager) {
        super(manager, new ModelInfBear(), 0.7f);
    }

    protected void preRenderCallback(EntityInfBear entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float ff = entitylivingbaseIn.getSelfeFlashIntensity2();
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)(1.2f * f2), (float)(1.2f * ff * f3), (float)(1.2f * f2));
    }

    protected ResourceLocation getEntityTexture(EntityInfBear entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfBear entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

