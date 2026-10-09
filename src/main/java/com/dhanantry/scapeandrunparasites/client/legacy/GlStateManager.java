package com.dhanantry.scapeandrunparasites.client.legacy;

import com.mojang.math.Axis;

/** The subset of the 1.12 GlStateManager that the parasite renderers use, mapped onto {@link GlContext}. */
public final class GlStateManager {
    public enum SourceFactor { ZERO, ONE, SRC_ALPHA, ONE_MINUS_SRC_ALPHA, DST_COLOR, DST_ALPHA, ONE_MINUS_DST_ALPHA, CONSTANT_ALPHA, SRC_COLOR, ONE_MINUS_SRC_COLOR, ONE_MINUS_DST_COLOR, SRC_ALPHA_SATURATE, CONSTANT_COLOR, ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA }

    public enum DestFactor { ZERO, ONE, SRC_ALPHA, ONE_MINUS_SRC_ALPHA, DST_COLOR, DST_ALPHA, ONE_MINUS_DST_ALPHA, CONSTANT_ALPHA, SRC_COLOR, ONE_MINUS_SRC_COLOR, ONE_MINUS_DST_COLOR, CONSTANT_COLOR, ONE_MINUS_CONSTANT_COLOR, ONE_MINUS_CONSTANT_ALPHA }

    private GlStateManager() {
    }

    public static void pushMatrix() {
        if (GlContext.pose != null) GlContext.pose.pushPose();
    }

    public static void popMatrix() {
        if (GlContext.pose != null) GlContext.pose.popPose();
    }

    public static void translate(float x, float y, float z) {
        if (GlContext.pose != null) GlContext.pose.translate(x, y, z);
    }

    public static void translate(double x, double y, double z) {
        translate((float) x, (float) y, (float) z);
    }

    public static void scale(float x, float y, float z) {
        if (GlContext.pose != null) GlContext.pose.scale(x, y, z);
    }

    public static void scale(double x, double y, double z) {
        scale((float) x, (float) y, (float) z);
    }

    public static void rotate(float angle, float x, float y, float z) {
        if (GlContext.pose == null || angle == 0.0f) return;
        float len = (float) Math.sqrt(x * x + y * y + z * z);
        if (len == 0.0f) return;
        GlContext.pose.mulPose(Axis.of(new org.joml.Vector3f(x / len, y / len, z / len)).rotationDegrees(angle));
    }

    public static void color(float r, float g, float b, float a) {
        GlContext.r = Math.max(0.0f, Math.min(1.0f, r));
        GlContext.g = Math.max(0.0f, Math.min(1.0f, g));
        GlContext.b = Math.max(0.0f, Math.min(1.0f, b));
        GlContext.a = Math.max(0.0f, Math.min(1.0f, a));
    }

    public static void color(float r, float g, float b) {
        color(r, g, b, 1.0f);
    }

    public static void enableBlend() {
        GlContext.blend = true;
    }

    public static void disableBlend() {
        GlContext.blend = false;
        GlContext.additive = false;
    }

    public static void blendFunc(SourceFactor src, DestFactor dst) {
        GlContext.additive = src == SourceFactor.ONE && dst == DestFactor.ONE;
    }

    public static void blendFunc(int src, int dst) {
        GlContext.additive = src == 1 && dst == 1;
    }

    public static void tryBlendFuncSeparate(SourceFactor a, DestFactor b, SourceFactor c, DestFactor d) {
        blendFunc(a, b);
    }

    public static void tryBlendFuncSeparate(int a, int b, int c, int d) {
        blendFunc(a, b);
    }

    public static void enableCull() {
        GlContext.cull = true;
    }

    public static void disableCull() {
        GlContext.cull = false;
    }

    public static void enableAlpha() {
    }

    public static void disableAlpha() {
    }

    public static void alphaFunc(int func, float ref) {
    }

    public static void depthMask(boolean flag) {
    }

    public static void enableDepth() {
    }

    public static void disableDepth() {
    }

    public static void depthFunc(int func) {
    }

    public static void enableLighting() {
    }

    public static void disableLighting() {
    }

    public static void enableRescaleNormal() {
    }

    public static void disableRescaleNormal() {
    }

    private static boolean texture2D = true;

    public static boolean isTexture2DEnabled() {
        return texture2D;
    }

    public static void enableTexture2D() {
        texture2D = true;
    }

    public static void disableTexture2D() {
        texture2D = false;
    }

    public static void enableColorMaterial() {
    }

    public static void disableColorMaterial() {
    }

    public static void enableOutlineMode(int color) {
    }

    public static void disableOutlineMode() {
    }

    public static void enableBlendProfile(Object profile) {
    }

    public static void disableBlendProfile(Object profile) {
    }

    public static void matrixMode(int mode) {
    }

    public static void loadIdentity() {
    }

    public static void setActiveTexture(int unit) {
    }

    public static void glTexEnvi(int a, int b, int c) {
    }

    public static void glTexEnv(int a, int b, java.nio.FloatBuffer buffer) {
    }

    public static void glTexParameteri(int a, int b, int c) {
    }

    public static void glNormal3f(float x, float y, float z) {
    }

    public static void bindTexture(int id) {
    }
}
