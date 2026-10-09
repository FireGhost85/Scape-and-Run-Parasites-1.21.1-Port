package com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelShycoAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderShycoAdapted
extends RenderMalleable<EntityShycoAdapted> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/shycoa.png");
    public static final ResourceLocation TEXTURE2 = ResourceLocation.parse("srparasites:textures/entity/monster/shycoatyrant.png");
    public static final ResourceLocation TEXTURE3 = ResourceLocation.parse("srparasites:textures/entity/monster/shycoalovecraft.png");
    public static final ResourceLocation TEXTURE4 = ResourceLocation.parse("srparasites:textures/entity/monster/shycoaabyss.png");
    public static final ResourceLocation TEXTUREV = ResourceLocation.parse("srparasites:textures/entity/monster/shycoav.png");
    public static final ResourceLocation TEXTUREB = ResourceLocation.parse("srparasites:textures/entity/monster/shycoab.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/shycoah.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderShycoAdapted(RenderManager manager) {
        super(manager, new ModelShycoAdapted(), 1.0f);
    }

    protected void preRenderCallback(EntityShycoAdapted entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityShycoAdapted entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE2;
            }
            case 5: {
                return TEXTUREV;
            }
            case 6: {
                return TEXTUREB;
            }
            case 7: {
                return TEXTUREH;
            }
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURE;
    }
}

