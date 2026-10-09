package com.dhanantry.scapeandrunparasites.client.renderer.entity.abomination;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.abomination.ModelAboBodies;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.abomination.EntityAboBodies;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderAboFaces
extends RenderSRP<EntityAboBodies> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/abobodies.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderAboFaces(RenderManager manager) {
        super(manager, new ModelAboBodies(), 1.3f);
    }

    protected void preRenderCallback(EntityAboBodies entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityAboBodies entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }
}

