package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.special;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.special.ModelSpeEnderman;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeEnderman;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderSpeEnderman
extends RenderSRP<EntitySpeEnderman> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/speenderman.png");

    public RenderSpeEnderman(RenderManager manager) {
        super(manager, new ModelSpeEnderman(), 0.5f);
    }

    protected void preRenderCallback(EntitySpeEnderman entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)(f2 * 1.1f), (float)(f3 * 1.1f), (float)(f2 * 1.1f));
    }

    protected ResourceLocation getEntityTexture(EntitySpeEnderman entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntitySpeEnderman entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

