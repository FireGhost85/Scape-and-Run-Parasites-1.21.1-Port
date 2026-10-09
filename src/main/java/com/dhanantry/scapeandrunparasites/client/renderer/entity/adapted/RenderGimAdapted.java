package com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelGimAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityGimAdapted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderGimAdapted
extends RenderMalleable<EntityGimAdapted> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/gima.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderGimAdapted(RenderManager manager) {
        super(manager, new ModelGimAdapted(), 1.3f);
    }

    protected void preRenderCallback(EntityGimAdapted entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityGimAdapted entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURE;
    }
}

