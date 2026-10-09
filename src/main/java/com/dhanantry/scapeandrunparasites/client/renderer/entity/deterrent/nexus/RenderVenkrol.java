package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrol;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerGlowing;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import net.minecraft.resources.ResourceLocation;

public class RenderVenkrol
extends RenderMalleable<EntityVenkrol> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/venkrol.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderVenkrol(RenderManager manager) {
        super(manager, new ModelVenkrol(), 0.4f);
        this.addLayer(new LayerGlowing<EntityVenkrol>(this));
    }

    protected ResourceLocation getEntityTexture(EntityVenkrol entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityVenkrol entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

