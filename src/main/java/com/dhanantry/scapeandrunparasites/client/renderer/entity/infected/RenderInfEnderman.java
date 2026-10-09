package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfEnderman;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfEnderman
extends RenderSRP<EntityInfEnderman> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/infenderman.png");
    public static final ResourceLocation TEXTURE_1 = ResourceLocation.parse("srparasites:textures/entity/monster/infenderman1.png");
    public static final ResourceLocation TEXTURE_ARIRAL = ResourceLocation.parse("srparasites:textures/entity/monster/infenderman_ariral.png");

    public RenderInfEnderman(RenderManager manager) {
        super(manager, new ModelInfEnderman(), 0.5f);
    }

    protected void preRenderCallback(EntityInfEnderman entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityInfEnderman entity) {
        if (entity.isAriral()) {
            return TEXTURE_ARIRAL;
        }
        return entity.getTextureVariant() == 1 ? TEXTURE_1 : TEXTURE;
    }

    protected void applyRotations(EntityInfEnderman entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

