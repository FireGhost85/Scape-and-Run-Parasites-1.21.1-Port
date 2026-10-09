package com.dhanantry.scapeandrunparasites.client.renderer.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class HitboxNoRender
extends Render<EntityHitbox> {
    public HitboxNoRender(RenderManager renderManager) {
        super(renderManager);
    }

    @Nullable
    protected ResourceLocation getEntityTexture(EntityHitbox entity) {
        return null;
    }
}

