package com.dhanantry.scapeandrunparasites.client.legacy;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/** Stand-in for the 1.12 RenderManager that the renderer constructors receive: wraps the 1.21 renderer provider context. */
public final class RenderManager {
    public final EntityRendererProvider.Context context;

    public RenderManager(EntityRendererProvider.Context context) {
        this.context = context;
    }
}
