package com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.pure.preeminent.ModelVesta;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerSnow;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderVesta
extends RenderMalleable<EntityVesta> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/vesta.png");
    public static final ResourceLocation TEXTURE1 = ResourceLocation.parse("srparasites:textures/entity/monster/vestare.png");

    public RenderVesta(RenderManager manager) {
        super(manager, new ModelVesta(), 1.3f);
        this.addLayer(new LayerSnow(this, ResourceLocation.parse("srparasites:textures/entity/layer/vestasnow.png"), 1.01, 1.001, 1.01));
    }

    protected void preRenderCallback(EntityVesta entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityVesta entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE1;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityVesta entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

