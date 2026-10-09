package com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelWymoAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityWymoAdapted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderWymoAdapted
extends RenderMalleable<EntityWymoAdapted> {
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/wymoa.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderWymoAdapted(RenderManager manager) {
        super(manager, new ModelWymoAdapted(), 0.0f);
    }

    protected ResourceLocation getEntityTexture(EntityWymoAdapted entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTUREH;
    }

    protected void preRenderCallback(EntityWymoAdapted entitylivingbaseIn, float partialTickTime) {
        float xx = 1.1f;
        float yy = 1.1f;
        switch (entitylivingbaseIn.getBodyNumber()) {
            case 1: {
                xx = 1.23f;
                yy = 1.23f;
                break;
            }
            case 2: {
                xx = 1.47f;
                yy = 1.47f;
                break;
            }
            case 3: {
                xx = 1.23f;
                yy = 1.27f;
                break;
            }
            case 4: {
                xx = 1.05f;
                yy = 1.17f;
            }
        }
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)(xx * f2), (float)(yy * f3), (float)f2);
    }
}

