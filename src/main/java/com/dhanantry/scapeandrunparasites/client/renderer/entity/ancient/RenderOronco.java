package com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.ancient.ModelOronco;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import net.minecraft.resources.ResourceLocation;

public class RenderOronco
extends RenderMalleable<EntityOronco> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/oronco.png");

    public RenderOronco(RenderManager manager) {
        super(manager, new ModelOronco(), 4.0f);
    }

    protected ResourceLocation getEntityTexture(EntityOronco entity) {
        return TEXTURES;
    }
}

