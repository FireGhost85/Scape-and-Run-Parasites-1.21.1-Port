package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderLivingBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSIV;
import com.dhanantry.scapeandrunparasites.client.renderer.LayerVenkrolTornado;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerGlowing;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import net.minecraft.resources.ResourceLocation;

public class RenderVenkrolSIV
extends RenderMalleable<EntityVenkrolSIV> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/venkrolsiv.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderVenkrolSIV(RenderManager manager) {
        super(manager, new ModelVenkrolSIV(), 0.4f);
        this.addLayer(new LayerGlowing<EntityVenkrolSIV>(this));
        this.addLayer(new LayerVenkrolTornado((RenderLivingBase<EntityVenkrolSIV>)this));
    }

    protected ResourceLocation getEntityTexture(EntityVenkrolSIV entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityVenkrolSIV entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

