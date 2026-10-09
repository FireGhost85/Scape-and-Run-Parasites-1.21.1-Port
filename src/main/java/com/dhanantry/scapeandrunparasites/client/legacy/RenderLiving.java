package com.dhanantry.scapeandrunparasites.client.legacy;

import net.minecraft.world.entity.LivingEntity;

/** 1.12 RenderLiving (the leash and name tag handling is the vanilla one of the 1.21 base class). */
public abstract class RenderLiving<T extends LivingEntity> extends RenderLivingBase<T> {
    protected RenderLiving(RenderManager manager, ModelBase model, float shadowSize) {
        super(manager, model, shadowSize);
    }
}
