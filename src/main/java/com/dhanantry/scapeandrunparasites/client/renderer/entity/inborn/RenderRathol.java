package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderRathol
extends RenderLiving<EntityRathol> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/rathol.png");
    public static final ResourceLocation TEXTURES1 = ResourceLocation.parse("srparasites:textures/entity/monster/ratholone.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/srathol.png");

    public RenderRathol(RenderManager manager) {
        super(manager, (ModelBase)new ModelRathol(), 1.2f);
    }

    protected void preRenderCallback(EntityRathol entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityRathol entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURES1;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityRathol entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

