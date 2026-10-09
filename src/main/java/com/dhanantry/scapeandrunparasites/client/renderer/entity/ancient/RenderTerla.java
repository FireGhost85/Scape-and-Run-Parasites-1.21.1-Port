package com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.ancient.ModelTerla;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import net.minecraft.resources.ResourceLocation;

public class RenderTerla
extends RenderMalleable<EntityTerla> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/terla.png");

    public RenderTerla(RenderManager manager) {
        super(manager, new ModelTerla(), 2.4f);
    }

    protected ResourceLocation getEntityTexture(EntityTerla entity) {
        return TEXTURES;
    }
}

