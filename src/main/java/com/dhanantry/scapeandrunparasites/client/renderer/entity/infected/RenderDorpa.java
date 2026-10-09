package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelDorpa;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderDorpa
extends RenderSRP<EntityDorpa> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/dorpa.png");
    public static final ResourceLocation TEXTURE2 = ResourceLocation.parse("srparasites:textures/entity/monster/dorpa2.png");

    public RenderDorpa(RenderManager manager) {
        super(manager, new ModelDorpa(), 1.2f);
    }

    protected void preRenderCallback(EntityDorpa entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)(0.78f * f2), (float)(0.78f * f3), (float)(0.78f * f2));
    }

    protected ResourceLocation getEntityTexture(EntityDorpa entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE2;
            }
        }
        return TEXTURE;
    }

    protected void applyRotations(EntityDorpa entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

