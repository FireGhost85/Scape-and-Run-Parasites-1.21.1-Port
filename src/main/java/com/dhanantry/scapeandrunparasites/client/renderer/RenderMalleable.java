package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.legacy.GlContext;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * The malleable parasites flash in a colour of their hit status (1.12: texture combiner with a constant colour) instead of the
 * vanilla red flash: the colour is mixed into the vertex colour with the same strength.
 */
public abstract class RenderMalleable<T extends EntityPMalleable>
extends RenderSRP<T> {
    public RenderMalleable(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }

    @Override
    protected int getOverlay(T entity, float partialTicks) {
        if (entity.hurtTime > 0 || entity.deathTime > 0) {
            switch (entity.getHitStatus()) {
                case 0 -> this.tint(1.0f, 0.0f, 0.0f, 0.3f);
                case 1 -> this.tint(0.0f, 1.0f, 0.0f, 0.3f);
                case 2 -> this.tint(1.0f, 0.0f, 1.0f, 0.3f);
                case 3 -> this.tint(0.1f, 0.2f, 0.2f, 1.0f);
                default -> { }
            }
        }
        return OverlayTexture.NO_OVERLAY;
    }

    private void tint(float r, float g, float b, float a) {
        GlContext.tintR = r;
        GlContext.tintG = g;
        GlContext.tintB = b;
        GlContext.tintA = a;
    }

    public ResourceLocation getGlowTexture(ResourceLocation base) {
        String path = base.getPath();
        int ind = path.lastIndexOf(46);
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), path.substring(0, ind) + "_glow" + path.substring(ind));
    }

    public ResourceLocation getBaseTexture(T entitylivingbaseIn) {
        return this.getEntityTexture(entitylivingbaseIn);
    }
}
