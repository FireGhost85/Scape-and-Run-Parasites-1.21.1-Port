package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelGothol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityGothol;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderGothol
extends RenderLiving<EntityGothol> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/gothol.png");
    public static final ResourceLocation TEXTURES1 = ResourceLocation.parse("srparasites:textures/entity/monster/test.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/sgothol.png");

    public RenderGothol(RenderManager manager) {
        super(manager, (ModelBase)new ModelGothol(), 1.2f);
    }

    protected void preRenderCallback(EntityGothol entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityGothol entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURES;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityGothol entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

