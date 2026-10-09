package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.pure.preeminent.ModelPheon;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderPheon
extends RenderMalleable<EntityPheon> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/pheon.png");
    public static final ResourceLocation TEXTURESP = ResourceLocation.parse("srparasites:textures/entity/monster/pheonsp1.png");

    public RenderPheon(RenderManager manager) {
        super(manager, new ModelPheon(), 1.3f);
    }

    protected void preRenderCallback(EntityPheon entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityPheon entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURESP;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityPheon entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

