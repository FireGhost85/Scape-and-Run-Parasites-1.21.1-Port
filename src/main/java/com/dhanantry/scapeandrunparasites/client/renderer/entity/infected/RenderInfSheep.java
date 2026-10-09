package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfSheep;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfSheep
extends RenderSRP<EntityInfSheep> {
    private static final ResourceLocation TEX_WHITE = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/entity/monster/sheep.png");
    private static final ResourceLocation TEX_GREY = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/entity/monster/sheep_grey.png");
    private static final ResourceLocation TEX_BLACK = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/entity/monster/sheep_black.png");

    public RenderInfSheep(RenderManager manager) {
        super(manager, new ModelInfSheep(), 0.5f);
    }

    protected void preRenderCallback(EntityInfSheep entitylivingbaseIn, float partialTickTime) {
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

    protected ResourceLocation getEntityTexture(EntityInfSheep entity) {
        switch (entity.getTextureVariant()) {
            case 1: {
                return TEX_GREY;
            }
            case 2: {
                return TEX_BLACK;
            }
        }
        return TEX_WHITE;
    }

    protected void applyRotations(EntityInfSheep entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

