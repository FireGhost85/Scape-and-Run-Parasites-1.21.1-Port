package com.dhanantry.scapeandrunparasites.client.legacy;


/** 1.12 OpenGlHelper: only the lightmap call matters (full-bright glow layers); the texture combiner constants are inert. */
public final class OpenGlHelper {
    public static final int defaultTexUnit = 33984;
    public static final int lightmapTexUnit = 33985;
    public static final int GL_TEXTURE2 = 33986;
    public static final int GL_COMBINE = 34160;
    public static final int GL_COMBINE_RGB = 34161;
    public static final int GL_COMBINE_ALPHA = 34162;
    public static final int GL_SOURCE0_RGB = 34176;
    public static final int GL_SOURCE1_RGB = 34177;
    public static final int GL_SOURCE2_RGB = 34178;
    public static final int GL_SOURCE0_ALPHA = 34184;
    public static final int GL_SOURCE1_ALPHA = 34185;
    public static final int GL_OPERAND0_RGB = 34192;
    public static final int GL_OPERAND1_RGB = 34193;
    public static final int GL_OPERAND2_RGB = 34194;
    public static final int GL_OPERAND0_ALPHA = 34200;
    public static final int GL_OPERAND1_ALPHA = 34201;
    public static final int GL_PRIMARY_COLOR = 34167;
    public static final int GL_CONSTANT = 34166;
    public static final int GL_PREVIOUS = 34168;
    public static final int GL_INTERPOLATE = 34165;

    public static float lastBrightnessX = 0.0f;
    public static float lastBrightnessY = 0.0f;

    private OpenGlHelper() {
    }

    /** lightmap coordinates (block light, sky light) in 1.12 units (0..240); 61680 packs to full bright. */
    public static void setLightmapTextureCoords(int unit, float x, float y) {
        int packed = ((int) x & 0xFFFF) | (((int) y & 0xFFFF) << 16);
        if (x >= 61680.0f) {
            GlContext.light = 0xF000F0;
        } else if (x == 0.0f && y == 0.0f) {
            GlContext.light = GlContext.baseLight;
        } else {
            GlContext.light = packed;
        }
    }
}
