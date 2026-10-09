package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.pure.ModelGanro;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderGanro
extends RenderMalleable<EntityGanro> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/ganro.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/ganroh.png");

    public RenderGanro(RenderManager manager) {
        super(manager, new ModelGanro(), 1.2f);
    }

    protected void preRenderCallback(EntityGanro entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityGanro entity) {
        switch (entity.getSkin()) {
            case 7: {
                return TEXTUREH;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityGanro entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

