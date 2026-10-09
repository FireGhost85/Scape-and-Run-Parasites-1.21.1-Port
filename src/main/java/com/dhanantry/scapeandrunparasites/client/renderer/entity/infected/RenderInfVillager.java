package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfVillager;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfVillager
extends RenderSRP<EntityInfVillager> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/villager.png");
    public static final ResourceLocation TEXTURE1 = ResourceLocation.parse("srparasites:textures/entity/monster/villager1.png");

    public RenderInfVillager(RenderManager manager) {
        super(manager, new ModelInfVillager(), 0.5f);
    }

    protected void preRenderCallback(EntityInfVillager entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float ff = entitylivingbaseIn.getSelfeFlashIntensity2();
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)(ff * f3), (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityInfVillager entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE1;
            }
        }
        return TEXTURE;
    }

    protected void applyRotations(EntityInfVillager entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

