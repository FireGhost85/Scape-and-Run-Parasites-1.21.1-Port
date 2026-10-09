package com.dhanantry.scapeandrunparasites.client.legacy.gui;

/** The few GL11 calls of the 1.12 GUI code. */
public final class GL11 {
    public static final int GL_SCISSOR_TEST = 3089;
    public static final int GL_DEPTH_TEST = 2929;
    public static final int GL_BLEND = 3042;
    public static final int GL_LIGHTING = 2896;
    public static final int GL_TEXTURE_2D = 3553;
    public static final int GL_CULL_FACE = 2884;
    public static final int GL_ALPHA_TEST = 3008;

    private GL11() {}

    public static void glEnable(int cap) {
        if (cap == GL_SCISSOR_TEST) {
            GuiContext.enableScissor();
        }
    }

    public static void glDisable(int cap) {
        if (cap == GL_SCISSOR_TEST) {
            GuiContext.disableScissor();
        }
    }

    public static void glScissor(int x, int y, int w, int h) {
        GuiContext.setScissor(x, y, w, h);
    }
}
