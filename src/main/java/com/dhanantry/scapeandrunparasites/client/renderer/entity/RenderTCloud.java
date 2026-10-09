package com.dhanantry.scapeandrunparasites.client.renderer.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import net.minecraft.resources.ResourceLocation;

public class RenderTCloud
extends Render<EntityToxicCloud> {
    private static final ResourceLocation EVOKER_ILLAGER_FANGS = ResourceLocation.parse("textures/entity/illager/fangs.png");

    public RenderTCloud(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    protected ResourceLocation getEntityTexture(EntityToxicCloud entity) {
        return EVOKER_ILLAGER_FANGS;
    }
}

