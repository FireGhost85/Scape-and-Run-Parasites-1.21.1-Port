package com.dhanantry.scapeandrunparasites.client.renderer.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import net.minecraft.resources.ResourceLocation;

public class RenderDamage
extends Render<EntityDamage> {
    private static final ResourceLocation EVOKER_ILLAGER_FANGS = ResourceLocation.parse("textures/entity/illager/fangs.png");

    public RenderDamage(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
    }

    protected ResourceLocation getEntityTexture(EntityDamage entity) {
        return EVOKER_ILLAGER_FANGS;
    }
}

