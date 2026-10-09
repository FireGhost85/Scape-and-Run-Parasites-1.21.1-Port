package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfPig;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfPig
extends RenderSRP<EntityInfPig> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/pig.png");

    public RenderInfPig(RenderManager manager) {
        super(manager, new ModelInfPig(), 0.5f);
    }

    protected void preRenderCallback(EntityInfPig entitylivingbaseIn, float partialTickTime) {
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

    protected ResourceLocation getEntityTexture(EntityInfPig entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfPig entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

