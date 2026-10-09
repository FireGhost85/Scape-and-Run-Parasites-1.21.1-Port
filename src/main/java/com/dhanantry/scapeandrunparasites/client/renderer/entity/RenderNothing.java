package com.dhanantry.scapeandrunparasites.client.renderer.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Renders nothing. 1.12 silently drew nothing for an entity without a renderer; 1.21 crashes the client, so every entity type
 * of the mod that has no renderer of its own gets this one (see ClientRenderers).
 */
public class RenderNothing extends Render<Entity> {
    public RenderNothing(RenderManager manager) {
        super(manager);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
