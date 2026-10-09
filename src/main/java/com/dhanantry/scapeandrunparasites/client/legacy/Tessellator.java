package com.dhanantry.scapeandrunparasites.client.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;

/** The 1.12 Tessellator/BufferBuilder: collects the vertices of one draw call and emits them through {@link GlContext}. */
public final class Tessellator {
    private static final Tessellator INSTANCE = new Tessellator();
    private final BufferBuilder buffer = new BufferBuilder();

    public static Tessellator getInstance() {
        return INSTANCE;
    }

    public BufferBuilder getBuffer() {
        return this.buffer;
    }

    public BufferBuilder getWorldRenderer() {
        return this.buffer;
    }

    public void draw() {
        this.buffer.draw();
    }

    public enum VertexFormat { POSITION, POSITION_COLOR, POSITION_TEX, POSITION_TEX_COLOR, POSITION_TEX_NORMAL, POSITION_TEX_LMAP_COLOR }

    public static final class BufferBuilder {
        private int mode;
        private VertexFormat format = VertexFormat.POSITION;
        private final List<float[]> vertices = new ArrayList<>();
        private float[] cur = new float[9];

        public void begin(int glMode, VertexFormat vertexFormat) {
            this.mode = glMode;
            this.format = vertexFormat;
            this.vertices.clear();
            this.cur = newVertex();
        }

        private static float[] newVertex() {
            return new float[]{0, 0, 0, 0, 0, 1, 1, 1, 1};
        }

        public BufferBuilder pos(double x, double y, double z) {
            this.cur[0] = (float) x;
            this.cur[1] = (float) y;
            this.cur[2] = (float) z;
            return this;
        }

        public BufferBuilder tex(double u, double v) {
            this.cur[3] = (float) u;
            this.cur[4] = (float) v;
            return this;
        }

        public BufferBuilder color(float r, float g, float b, float a) {
            this.cur[5] = r;
            this.cur[6] = g;
            this.cur[7] = b;
            this.cur[8] = a;
            return this;
        }

        public BufferBuilder color(int r, int g, int b, int a) {
            return this.color(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
        }

        public BufferBuilder normal(float x, float y, float z) {
            return this;
        }

        public BufferBuilder lightmap(int a, int b) {
            return this;
        }

        public void endVertex() {
            this.vertices.add(this.cur);
            this.cur = newVertex();
        }

        /** 2D screen shapes (GUI circles / rings): the vertex colours are drawn untextured with the gui render type. */
        private boolean drawGuiColor() {
            if (!com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.rendering || GlContext.pose == null
                    || this.format != VertexFormat.POSITION_COLOR || !(this.mode == 4 || this.mode == 5 || this.mode == 6 || this.mode == 7)) {
                return false;
            }
            if (GlStateManager.isTexture2DEnabled()) {
                return false;
            }
            VertexConsumer consumer = com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.g.bufferSource().getBuffer(net.minecraft.client.renderer.RenderType.gui());
            org.joml.Matrix4f m = GlContext.pose.last().pose();
            int n = this.vertices.size();
            java.util.List<int[]> tris = new ArrayList<>();
            switch (this.mode) {
                case 4 -> {
                    for (int i = 0; i + 2 < n; i += 3) tris.add(new int[]{i, i + 1, i + 2});
                }
                case 5 -> {
                    for (int i = 0; i + 2 < n; ++i) tris.add(new int[]{i, i + 1, i + 2});
                }
                case 6 -> {
                    for (int i = 1; i + 1 < n; ++i) tris.add(new int[]{0, i, i + 1});
                }
                default -> {
                    for (int i = 0; i + 3 < n; i += 4) {
                        tris.add(new int[]{i, i + 1, i + 2});
                        tris.add(new int[]{i, i + 2, i + 3});
                    }
                }
            }
            for (int[] t : tris) {
                for (int pass = 0; pass < 2; ++pass) {
                    int[] o = pass == 0 ? new int[]{t[0], t[1], t[2], t[2]} : new int[]{t[0], t[2], t[1], t[1]};
                    for (int idx : o) {
                        float[] v = this.vertices.get(idx);
                        consumer.addVertex(m, v[0], v[1], v[2]).setColor(v[5] * GlContext.r, v[6] * GlContext.g, v[7] * GlContext.b, v[8] * GlContext.a);
                    }
                }
            }
            this.vertices.clear();
            return true;
        }

        /** 1.12 screens draw textured quads with the "bound" texture of the GUI layer (rotated / scaled by the matrix stack, animated sprites, ...). */
        private boolean drawGuiTextured() {
            if (!com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.rendering || com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.g == null
                    || GlContext.pose == null || com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.texture == null || this.mode != 7
                    || this.format != VertexFormat.POSITION_TEX && this.format != VertexFormat.POSITION_TEX_COLOR) {
                return false;
            }
            boolean colored = this.format == VertexFormat.POSITION_TEX_COLOR;
            com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.g.flush();
            com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexColorShader);
            com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, com.dhanantry.scapeandrunparasites.client.legacy.gui.GuiContext.texture);
            com.mojang.blaze3d.systems.RenderSystem.enableBlend();
            com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            com.mojang.blaze3d.vertex.BufferBuilder bb = com.mojang.blaze3d.vertex.Tesselator.getInstance().begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX_COLOR);
            org.joml.Matrix4f m = GlContext.pose.last().pose();
            for (float[] v : this.vertices) {
                float cr = colored ? v[5] * GlContext.r : GlContext.r;
                float cg = colored ? v[6] * GlContext.g : GlContext.g;
                float cb = colored ? v[7] * GlContext.b : GlContext.b;
                float ca = colored ? v[8] * GlContext.a : GlContext.a;
                bb.addVertex(m, v[0], v[1], v[2]).setUv(v[3], v[4]).setColor(cr, cg, cb, ca);
            }
            this.vertices.clear();
            com.mojang.blaze3d.vertex.BufferUploader.drawWithShader(bb.buildOrThrow());
            return true;
        }

        public void draw() {
            if (this.drawGuiColor() || this.drawGuiTextured()) {
                return;
            }
            if (GlContext.active() && !this.vertices.isEmpty() && (this.mode == 7 || this.mode == 4)) {
                VertexConsumer consumer = GlContext.consumer();
                PoseStack.Pose pose = GlContext.pose.last();
                boolean colored = this.format == VertexFormat.POSITION_TEX_COLOR || this.format == VertexFormat.POSITION_COLOR || this.format == VertexFormat.POSITION_TEX_LMAP_COLOR;
                for (float[] v : this.vertices) {
                    float cr = colored ? v[5] * GlContext.r : GlContext.r;
                    float cg = colored ? v[6] * GlContext.g : GlContext.g;
                    float cb = colored ? v[7] * GlContext.b : GlContext.b;
                    float ca = colored ? v[8] * GlContext.a : GlContext.a;
                    consumer.addVertex(pose, v[0], v[1], v[2]).setColor(cr, cg, cb, ca).setUv(v[3], v[4])
                            .setOverlay(GlContext.overlay).setLight(GlContext.light).setNormal(pose, 0.0f, 1.0f, 0.0f);
                }
            }
            this.vertices.clear();
        }
    }
}
