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

        public void draw() {
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
