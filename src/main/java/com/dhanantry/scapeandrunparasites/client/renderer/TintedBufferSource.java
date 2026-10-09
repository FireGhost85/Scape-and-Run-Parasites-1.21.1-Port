package com.dhanantry.scapeandrunparasites.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;

/**
 * 1.12 drew the held item again with {@code glColor} and an additive blend; in 1.21 the colour lives in the vertices. This buffer
 * source redirects every draw to one additive (or alpha) item-atlas render type and multiplies the vertex colour.
 */
public final class TintedBufferSource implements MultiBufferSource {
    /** Item atlas, additive blend, no depth write (the 1.12 glBlendFunc(SRC_ALPHA, ONE) + depthMask(false)). */
    private static final Function<ResourceLocation, RenderType> ADDITIVE = Util.memoize(tex -> RenderType.create("srp_additive_item", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                    .setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true)));
    private static final Function<ResourceLocation, RenderType> ALPHA = Util.memoize(tex -> RenderType.create("srp_alpha_item", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setLightmapState(RenderStateShard.LIGHTMAP)
                    .setOverlayState(RenderStateShard.OVERLAY)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(true)));

    private final MultiBufferSource delegate;
    private final boolean additive;
    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public TintedBufferSource(MultiBufferSource delegate, boolean additive, float r, float g, float b, float a) {
        this.delegate = delegate;
        this.additive = additive;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    @Override
    public VertexConsumer getBuffer(RenderType type) {
        RenderType target = (this.additive ? ADDITIVE : ALPHA).apply(TextureAtlas.LOCATION_BLOCKS);
        return new Tinted(this.delegate.getBuffer(target), this.r, this.g, this.b, this.a);
    }

    private static final class Tinted implements VertexConsumer {
        private final VertexConsumer out;
        private final float r;
        private final float g;
        private final float b;
        private final float a;

        Tinted(VertexConsumer out, float r, float g, float b, float a) {
            this.out = out;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            this.out.addVertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            this.out.setColor((int)(red * this.r), (int)(green * this.g), (int)(blue * this.b), (int)(alpha * this.a));
            return this;
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            this.out.setUv(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            this.out.setUv1(u, v);
            return this;
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            this.out.setUv2(u, v);
            return this;
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            this.out.setNormal(x, y, z);
            return this;
        }
    }
}
