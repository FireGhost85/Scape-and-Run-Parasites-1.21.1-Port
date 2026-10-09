package com.dhanantry.scapeandrunparasites.client.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * State of the 1.12 style immediate-mode rendering that the ported entity models and renderers expect: the current matrix
 * stack, texture, colour and blend state live here; {@link ModelRenderer} reads them when it draws.
 */
public final class GlContext {
    public static PoseStack pose;
    public static MultiBufferSource buffers;
    public static int baseLight = 0xF000F0;
    public static int light = 0xF000F0;
    public static int overlay = OverlayTexture.NO_OVERLAY;
    public static ResourceLocation texture;
    public static float r = 1.0f;
    public static float g = 1.0f;
    public static float b = 1.0f;
    public static float a = 1.0f;
    /** Colour mixed into the vertex colour (hit flash of the malleable parasites): result = c * (1 - tintA) + tint * tintA. */
    public static float tintR, tintG, tintB, tintA;
    public static boolean blend;
    public static boolean additive;
    public static boolean cull = true;
    private static int depth;

    private GlContext() {
    }

    public static void begin(PoseStack stack, MultiBufferSource source, int packedLight) {
        if (depth++ == 0) {
            pose = stack;
            buffers = source;
            baseLight = packedLight;
            light = packedLight;
            overlay = OverlayTexture.NO_OVERLAY;
            r = g = b = a = 1.0f;
            tintA = 0.0f;
            blend = false;
            additive = false;
            cull = true;
        }
    }

    public static void end() {
        if (--depth <= 0) {
            depth = 0;
            pose = null;
            buffers = null;
        }
    }

    public static boolean active() {
        return pose != null && buffers != null && texture != null;
    }

    public static RenderType renderType() {
        if (additive) {
            return RenderType.eyes(texture);
        }
        if (blend || a < 1.0f) {
            return RenderType.entityTranslucent(texture);
        }
        return cull ? RenderType.entityCutout(texture) : RenderType.entityCutoutNoCull(texture);
    }

    public static VertexConsumer consumer() {
        return buffers.getBuffer(renderType());
    }
}
