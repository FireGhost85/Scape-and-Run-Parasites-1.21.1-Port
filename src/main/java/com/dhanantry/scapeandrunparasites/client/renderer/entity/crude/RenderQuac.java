package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelQuac;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityQuac;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderQuac
extends RenderSRP<EntityQuac> {
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/quac.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderQuac(RenderManager manager) {
        super(manager, new ModelQuac(), 0.0f);
    }

    protected ResourceLocation getEntityTexture(EntityQuac entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTUREH;
    }

    protected void preRenderCallback(EntityQuac entitylivingbaseIn, float partialTickTime) {
        float xx = 1.0f;
        float yy = 1.0f;
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

