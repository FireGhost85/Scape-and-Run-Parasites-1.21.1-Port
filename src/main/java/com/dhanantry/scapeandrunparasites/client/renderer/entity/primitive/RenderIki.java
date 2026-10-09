package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelIki;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderIki
extends RenderMalleable<EntityIki> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/vermin.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/vermin.png");
    public static final ResourceLocation FROZEN_TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/primitiveverminfrozen.png");

    public RenderIki(RenderManager manager) {
        super(manager, new ModelIki(), 0.2f);
    }

    protected void preRenderCallback(EntityIki entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityIki entity) {
        switch (entity.getSkin()) {
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityIki entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

