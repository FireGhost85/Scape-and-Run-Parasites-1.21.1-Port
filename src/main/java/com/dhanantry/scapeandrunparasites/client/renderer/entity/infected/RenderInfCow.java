package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfCow;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfCow
extends RenderSRP<EntityInfCow> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/cow.png");

    public RenderInfCow(RenderManager manager) {
        super(manager, new ModelInfCow(), 0.5f);
    }

    protected void preRenderCallback(EntityInfCow entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float ff = entitylivingbaseIn.getSelfeFlashIntensity2();
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)(ff * f3), (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityInfCow entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfCow entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

