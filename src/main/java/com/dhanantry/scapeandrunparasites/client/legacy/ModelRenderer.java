package com.dhanantry.scapeandrunparasites.client.legacy;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import java.util.List;

/** The 1.12 ModelRenderer (box based model part with rotation point and angles), drawing through {@link GlContext}. */
public class ModelRenderer {
    public float textureWidth = 64.0f;
    public float textureHeight = 32.0f;
    private int textureOffsetX;
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    public boolean mirror;
    public boolean showModel = true;
    public boolean isHidden;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public List<ModelRenderer> childModels;
    public final String boxName;
    private final List<Quad> quads = new ArrayList<>();

    public ModelRenderer(ModelBase model, String name) {
        this.textureWidth = model.textureWidth;
        this.textureHeight = model.textureHeight;
        this.boxName = name;
        model.boxList.add(this);
    }

    public ModelRenderer(ModelBase model) {
        this(model, null);
    }

    public ModelRenderer(ModelBase model, int texU, int texV) {
        this(model);
        this.setTextureOffset(texU, texV);
    }

    public void addChild(ModelRenderer renderer) {
        if (this.childModels == null) {
            this.childModels = new ArrayList<>();
        }
        this.childModels.add(renderer);
    }

    public ModelRenderer setTextureOffset(int x, int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public ModelRenderer setTextureSize(int w, int h) {
        this.textureWidth = (float) w;
        this.textureHeight = (float) h;
        return this;
    }

    public ModelRenderer addBox(String name, float x, float y, float z, int w, int h, int d) {
        return this.addBox(x, y, z, w, h, d, 0.0f);
    }

    public ModelRenderer addBox(float x, float y, float z, int w, int h, int d) {
        return this.addBox(x, y, z, w, h, d, 0.0f);
    }

    public ModelRenderer addBox(float x, float y, float z, int w, int h, int d, boolean mirrored) {
        boolean old = this.mirror;
        this.mirror = mirrored;
        this.addBox(x, y, z, w, h, d, 0.0f);
        this.mirror = old;
        return this;
    }

    public ModelRenderer addBox(float x, float y, float z, int w, int h, int d, float delta, boolean mirrored) {
        boolean old = this.mirror;
        this.mirror = mirrored;
        this.addBox(x, y, z, w, h, d, delta);
        this.mirror = old;
        return this;
    }

    public ModelRenderer addBox(float x, float y, float z, int dx, int dy, int dz, float delta) {
        int u = this.textureOffsetX;
        int v = this.textureOffsetY;
        float f = x + (float) dx;
        float f1 = y + (float) dy;
        float f2 = z + (float) dz;
        x -= delta;
        y -= delta;
        z -= delta;
        f += delta;
        f1 += delta;
        f2 += delta;
        if (this.mirror) {
            float t = f;
            f = x;
            x = t;
        }
        float[] v7 = {x, y, z};
        float[] v0 = {f, y, z};
        float[] v1 = {f, f1, z};
        float[] v2 = {x, f1, z};
        float[] v3 = {x, y, f2};
        float[] v4 = {f, y, f2};
        float[] v5 = {f, f1, f2};
        float[] v6 = {x, f1, f2};
        this.quad(new float[][]{v4, v0, v1, v5}, u + dz + dx, v + dz, u + dz + dx + dz, v + dz + dy);
        this.quad(new float[][]{v7, v3, v6, v2}, u, v + dz, u + dz, v + dz + dy);
        this.quad(new float[][]{v4, v3, v7, v0}, u + dz, v, u + dz + dx, v + dz);
        this.quad(new float[][]{v1, v2, v6, v5}, u + dz + dx, v + dz, u + dz + dx + dx, v);
        this.quad(new float[][]{v0, v7, v2, v1}, u + dz, v + dz, u + dz + dx, v + dz + dy);
        this.quad(new float[][]{v3, v4, v5, v6}, u + dz + dx + dz, v + dz, u + dz + dx + dz + dx, v + dz + dy);
        return this;
    }

    private void quad(float[][] verts, float u1, float v1, float u2, float v2) {
        float[][] p = new float[4][];
        float[] us = {u2 / this.textureWidth, u1 / this.textureWidth, u1 / this.textureWidth, u2 / this.textureWidth};
        float[] vs = {v1 / this.textureHeight, v1 / this.textureHeight, v2 / this.textureHeight, v2 / this.textureHeight};
        boolean flip = this.mirror;
        float[] uu = new float[4];
        float[] vv = new float[4];
        for (int i = 0; i < 4; i++) {
            int src = flip ? 3 - i : i;
            p[i] = verts[src];
            uu[i] = us[src];
            vv[i] = vs[src];
        }
        // normal from the unflipped vertex order: (v2 - v1) x (v0 - v1)
        float ax = verts[2][0] - verts[1][0];
        float ay = verts[2][1] - verts[1][1];
        float az = verts[2][2] - verts[1][2];
        float bx = verts[0][0] - verts[1][0];
        float by = verts[0][1] - verts[1][1];
        float bz = verts[0][2] - verts[1][2];
        float nx = ay * bz - az * by;
        float ny = az * bx - ax * bz;
        float nz = ax * by - ay * bx;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0.0f) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        if (flip) {
            nx = -nx;
            ny = -ny;
            nz = -nz;
        }
        this.quads.add(new Quad(p, uu, vv, nx, ny, nz));
    }

    public void setRotationPoint(float x, float y, float z) {
        this.rotationPointX = x;
        this.rotationPointY = y;
        this.rotationPointZ = z;
    }

    public void render(float scale) {
        if (this.isHidden || !this.showModel || GlContext.pose == null) {
            return;
        }
        PoseStack stack = GlContext.pose;
        stack.pushPose();
        stack.translate(this.offsetX, this.offsetY, this.offsetZ);
        stack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
        this.rotate(stack);
        this.draw(scale);
        if (this.childModels != null) {
            for (ModelRenderer child : this.childModels) {
                child.render(scale);
            }
        }
        stack.popPose();
    }

    public void renderWithRotation(float scale) {
        this.render(scale);
    }

    public void postRender(float scale) {
        if (this.isHidden || !this.showModel || GlContext.pose == null) {
            return;
        }
        PoseStack stack = GlContext.pose;
        stack.translate(this.offsetX, this.offsetY, this.offsetZ);
        stack.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
        this.rotate(stack);
    }

    private void rotate(PoseStack stack) {
        if (this.rotateAngleZ != 0.0f) {
            stack.mulPose(com.mojang.math.Axis.ZP.rotation(this.rotateAngleZ));
        }
        if (this.rotateAngleY != 0.0f) {
            stack.mulPose(com.mojang.math.Axis.YP.rotation(this.rotateAngleY));
        }
        if (this.rotateAngleX != 0.0f) {
            stack.mulPose(com.mojang.math.Axis.XP.rotation(this.rotateAngleX));
        }
    }

    private void draw(float scale) {
        if (this.quads.isEmpty() || !GlContext.active()) {
            return;
        }
        VertexConsumer consumer = GlContext.consumer();
        PoseStack.Pose pose = GlContext.pose.last();
        float mix = GlContext.tintA;
        float cr = GlContext.r * (1.0f - mix) + GlContext.tintR * mix;
        float cg = GlContext.g * (1.0f - mix) + GlContext.tintG * mix;
        float cb = GlContext.b * (1.0f - mix) + GlContext.tintB * mix;
        for (Quad q : this.quads) {
            for (int i = 0; i < 4; i++) {
                float[] p = q.pos[i];
                consumer.addVertex(pose, p[0] * scale, p[1] * scale, p[2] * scale)
                        .setColor(cr, cg, cb, GlContext.a)
                        .setUv(q.u[i], q.v[i])
                        .setOverlay(GlContext.overlay)
                        .setLight(GlContext.light)
                        .setNormal(pose, q.nx, q.ny, q.nz);
            }
        }
    }

    private record Quad(float[][] pos, float[] u, float[] v, float nx, float ny, float nz) {
    }
}
