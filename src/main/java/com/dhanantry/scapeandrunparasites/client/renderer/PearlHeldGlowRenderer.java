package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.item.ItemBeholderPearl;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;

/** PearlHeldGlowRenderer of 1.10.9: the pearl in the first person hand is drawn again as a jittering glow, with glitch frames near an infected enderman. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class PearlHeldGlowRenderer {
    private static final float GLOW_BASE_ALPHA = 0.4f;
    private static final int GLOW_LAYERS = 2;
    private static final int GLOW_SAMPLES = 16;
    private static final float GLOW_BASE_RADIUS = 0.0075f;
    private static final float GLOW_RADIUS_STEP = 0.0035f;
    private static final float GLOW_TIME_SPEED = 0.44f;
    private static final float GLOW_ANGLE_WOBBLE = 0.24f;
    private static final float GLOW_RADIUS_JITTER = 0.0032f;
    private static final float VIBE_BASE_AMPLITUDE = 0.0012f;
    private static final float VIBE_FREQ_A = 19.0f;
    private static final float VIBE_FREQ_B = 31.0f;
    private static final float GLITCH_CHANCE_PER_TICK = 0.09f;
    private static final int GLITCH_PASSES = 7;
    private static final float GLITCH_ALPHA = 0.6f;
    private static final float GLITCH_OFFSET_MAX = 0.016f;
    private static final float GLITCH_ROT_MAX_DEG = 14.0f;
    private static final float GLITCH_SCALE_MAX = 1.09f;
    private static final float GLITCH_SHAKE_FREQ = 27.0f;
    private static final float GLITCH_SHAKE_BASE = 0.0045f;
    private static int glitchFrames = 0;
    private static int lastTickSeen = -1;

    private PearlHeldGlowRenderer() {
    }

    private static float ampForState(int s) {
        switch (s) {
            case 1:
                return 1.0f;
            case 2:
                return 1.65f;
            case 3:
                return 2.4f;
            default:
                return 0.35f;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent e) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null || mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }
        ItemStack stack = e.getItemStack();
        if (stack.isEmpty() || stack.getItem() != SRPItems.pearl.get()) {
            return;
        }
        int state = Mth.clamp((int)ItemBeholderPearl.pearlState(stack, mc.level, player), 0, 3);
        float amp = ampForState(state);
        int tick = player.tickCount;
        if (tick != lastTickSeen) {
            lastTickSeen = tick;
            float chance = GLITCH_CHANCE_PER_TICK * (0.75f + 0.5f * amp);
            if (glitchFrames <= 0 && mc.level.random.nextFloat() < chance) {
                glitchFrames = 2 + mc.level.random.nextInt(4);
            }
        }
        float pt = e.getPartialTick();
        float pitch = e.getInterpolatedPitch();
        PoseStack ps = e.getPoseStack();
        var hand = e.getHand();
        float swing = e.getSwingProgress();
        float equip = e.getEquipProgress();
        int light = e.getPackedLight();
        var buffers = e.getMultiBufferSource();
        e.setCanceled(true);
        mc.gameRenderer.itemInHandRenderer.renderArmWithItem(player, pt, pitch, hand, swing, stack, equip, ps, buffers, light);
        float t = ((float)player.tickCount + pt) * GLOW_TIME_SPEED;
        for (int layer = 0; layer < GLOW_LAYERS; ++layer) {
            float baseR = GLOW_BASE_RADIUS + (float)layer * GLOW_RADIUS_STEP;
            float layerAlpha = GLOW_BASE_ALPHA * (1.0f - (float)layer * 0.2f);
            TintedBufferSource tinted = new TintedBufferSource(buffers, true, 0.85f, 0.95f, 1.0f, layerAlpha);
            for (int i = 0; i < GLOW_SAMPLES; ++i) {
                float a = (float)i / (float)GLOW_SAMPLES * ((float)Math.PI * 2);
                float aW = Mth.sin(t * 0.9f + (float)i * 0.7f) * GLOW_ANGLE_WOBBLE;
                float ang = a + aW;
                float rJ = Mth.sin(t * 1.7f + (float)i * 1.3f + (float)layer * 0.8f) * GLOW_RADIUS_JITTER;
                float r = baseR + rJ;
                float dx = Mth.cos(ang) * r;
                float dy = Mth.sin(ang) * r;
                float jx = (Mth.sin(t * VIBE_FREQ_A + (float)i * 0.73f) + Mth.cos(t * VIBE_FREQ_B + (float)i * 0.41f)) * 0.5f * (VIBE_BASE_AMPLITUDE * amp);
                float jy = (Mth.cos(t * 16.53f + (float)i * 0.31f) + Mth.sin(t * 34.72f + (float)i * 0.27f)) * 0.5f * (VIBE_BASE_AMPLITUDE * amp);
                ps.pushPose();
                ps.translate(dx + jx, dy + jy, 0.0f);
                mc.gameRenderer.itemInHandRenderer.renderArmWithItem(player, pt, pitch, hand, swing, stack, equip, ps, tinted, light);
                ps.popPose();
            }
        }
        if (glitchFrames > 0) {
            float shakeAmp = GLITCH_SHAKE_BASE * (0.75f + 0.6f * amp);
            float shake = Mth.sin(((float)player.tickCount + pt) * (GLITCH_SHAKE_FREQ * (0.9f + 0.2f * amp))) * shakeAmp;
            // additive like the glow: 1.10.9 set the blend function (SRC_ALPHA, ONE) once for the glow and the glitch copies; a normal alpha blend drew solid ghost copies of the pearl that looked like it teleported
            TintedBufferSource glitch = new TintedBufferSource(buffers, true, 1.0f, 1.0f, 1.0f, GLITCH_ALPHA);
            for (int p = 0; p < GLITCH_PASSES; ++p) {
                float offX = (mc.level.random.nextFloat() * 2.0f - 1.0f) * (GLITCH_OFFSET_MAX * (0.6f + 0.6f * amp)) + shake;
                float offY = (mc.level.random.nextFloat() * 2.0f - 1.0f) * (GLITCH_OFFSET_MAX * (0.6f + 0.6f * amp)) + shake;
                float rot = (mc.level.random.nextFloat() * 2.0f - 1.0f) * (GLITCH_ROT_MAX_DEG * (0.75f + 0.4f * amp));
                float scl = 1.0f + mc.level.random.nextFloat() * (GLITCH_SCALE_MAX - 1.0f);
                ps.pushPose();
                ps.translate(offX, offY, 0.0f);
                ps.mulPose(Axis.ZP.rotationDegrees(rot));
                ps.scale(scl, scl, scl);
                mc.gameRenderer.itemInHandRenderer.renderArmWithItem(player, pt, pitch, hand, swing, stack, equip, ps, glitch, light);
                ps.popPose();
            }
            --glitchFrames;
        }
    }
}
