package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.GlContext;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.IllegalFormatException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

/** Static state of the 1.12 style GUI layer: the GuiGraphics of the frame and the "bound" texture of the 1.12 texture manager. */
public final class GuiContext {
    public static GuiGraphics g;
    public static ResourceLocation texture;
    public static boolean scissor;
    private static boolean scissorActive;
    private static int scx;
    private static int scy;
    private static int scw;
    private static int sch;

    private GuiContext() {}

    public static void bind(ResourceLocation location) {
        texture = location;
    }

    public static String fmt(String key, Object... args) {
        net.minecraft.locale.Language lang = net.minecraft.locale.Language.getInstance();
        if (!lang.has(key) && key.endsWith(".name") && lang.has(key.substring(0, key.length() - 5))) {
            key = key.substring(0, key.length() - 5);
        }
        String s = lang.getOrDefault(key);
        if (args != null && args.length > 0) {
            try {
                return String.format(s, args);
            } catch (IllegalFormatException e) {
                return s;
            }
        }
        return s;
    }

    public static boolean hasKey(String key) {
        return I18n.exists(key);
    }

    public static long systemTime() {
        return System.currentTimeMillis();
    }

    public static final FontRenderer FONT = new FontRenderer();

    public static float partial() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
    }

    /** The 1.12 GL setup of the GUI entity previews: the entity at (cx, cy) of the screen, 1.12 style scale and pitch. */
    public static void renderEntity(net.minecraft.world.entity.LivingEntity entity, float cx, float cy, float scale, float pitchDeg) {
        Minecraft mc = Minecraft.getInstance();
        com.mojang.blaze3d.vertex.PoseStack pose = g.pose();
        pose.pushPose();
        pose.translate(cx, cy, 150.0);
        pose.scale(scale, scale, -scale);
        pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(180.0f));
        pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitchDeg));
        pose.translate(0.0f, -entity.getBbHeight() / 2.0f, 0.0f);
        com.mojang.blaze3d.platform.Lighting.setupForEntityInInventory();
        net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        dispatcher.setRenderShadow(false);
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0f, 1.0f, pose, g.bufferSource(), 15728880));
        g.flush();
        dispatcher.setRenderShadow(true);
        pose.popPose();
        com.mojang.blaze3d.platform.Lighting.setupFor3DItems();
        resetColor();
    }

    public static void click() {
        Minecraft.getInstance().getSoundManager().play(net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public static void applyColor() {
        RenderSystem.setShaderColor(GlContext.r, GlContext.g, GlContext.b, GlContext.a);
    }

    public static void resetColor() {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    /** glScissor in framebuffer pixels (origin bottom left). */
    public static void setScissor(int x, int y, int w, int h) {
        scx = x;
        scy = y;
        scw = w;
        sch = h;
        if (scissor) {
            apply();
        }
    }

    public static void enableScissor() {
        scissor = true;
        apply();
    }

    public static void disableScissor() {
        if (scissorActive && g != null) {
            g.disableScissor();
        }
        scissorActive = false;
        scissor = false;
    }

    private static void apply() {
        if (g == null) {
            return;
        }
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int winH = Minecraft.getInstance().getWindow().getHeight();
        int left = (int)Math.floor(scx / scale);
        int right = (int)Math.ceil((scx + scw) / scale);
        int top = (int)Math.floor((winH - scy - sch) / scale);
        int bottom = (int)Math.ceil((winH - scy) / scale);
        if (scissorActive) {
            g.disableScissor();
        }
        g.enableScissor(left, top, right, bottom);
        scissorActive = true;
    }
}
