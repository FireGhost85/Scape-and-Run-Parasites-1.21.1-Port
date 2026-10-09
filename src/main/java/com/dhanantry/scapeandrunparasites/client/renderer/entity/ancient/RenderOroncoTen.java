package com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.ancient.ModelOroncoTen;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOroncoTen;
import net.minecraft.resources.ResourceLocation;

public class RenderOroncoTen
extends RenderSRP<EntityOroncoTen> {
    public static final ResourceLocation TEXTURE00 = ResourceLocation.parse("srparasites:textures/entity/monster/oroncoten0.png");
    public static final ResourceLocation TEXTURE01 = ResourceLocation.parse("srparasites:textures/entity/monster/oroncoten1.png");

    public RenderOroncoTen(RenderManager manager) {
        super(manager, new ModelOroncoTen(), 0.8f);
    }

    protected ResourceLocation getEntityTexture(EntityOroncoTen entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE01;
            }
        }
        return TEXTURE00;
    }
}

