package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import net.minecraft.resources.ResourceLocation;

public class RenderRemain
extends Render<EntityRemain> {
    private static final ResourceLocation EVOKER_ILLAGER_FANGS = ResourceLocation.parse("textures/entity/illager/fangs.png");

    public RenderRemain(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    protected ResourceLocation getEntityTexture(EntityRemain entity) {
        return EVOKER_ILLAGER_FANGS;
    }
}

