package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelBano;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderBano
extends RenderMalleable<EntityBano> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/bano.png");
    public static final ResourceLocation TEXTUREV = ResourceLocation.parse("srparasites:textures/entity/monster/banov.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/banoh.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/sbano.png");
    public static final ResourceLocation FROZEN_TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/primitivebolsterfrozen.png");

    public RenderBano(RenderManager manager) {
        super(manager, new ModelBano(), 0.5f);
    }

    protected void preRenderCallback(EntityBano entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityBano entity) {
        switch (entity.getSkin()) {
            case 5: {
                return TEXTUREV;
            }
            case 7: {
                return TEXTUREH;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityBano entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

