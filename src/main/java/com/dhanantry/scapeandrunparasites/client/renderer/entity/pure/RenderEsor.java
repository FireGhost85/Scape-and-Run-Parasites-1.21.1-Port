package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.pure.ModelEsor;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderEsor
extends RenderMalleable<EntityEsor> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/esor.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/esorh.png");

    public RenderEsor(RenderManager manager) {
        super(manager, new ModelEsor(), 1.2f);
    }

    protected void preRenderCallback(EntityEsor entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityEsor entity) {
        switch (entity.getSkin()) {
            case 7: {
                return TEXTUREH;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityEsor entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

