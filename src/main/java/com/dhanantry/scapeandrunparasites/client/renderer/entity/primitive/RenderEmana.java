package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelEmana;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerGlowing;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderEmana
extends RenderMalleable<EntityEmana> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/emana.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/emanah.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/semana.png");
    public static final ResourceLocation FROZEN_TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/primitiveyelloweyefrozen.png");

    public RenderEmana(RenderManager manager) {
        super(manager, new ModelEmana(), 0.2f);
        this.addLayer(new LayerGlowing<EntityEmana>(this));
    }

    protected void preRenderCallback(EntityEmana entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityEmana entity) {
        switch (entity.getSkin()) {
            case 7: {
                return TEXTUREH;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityEmana entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

