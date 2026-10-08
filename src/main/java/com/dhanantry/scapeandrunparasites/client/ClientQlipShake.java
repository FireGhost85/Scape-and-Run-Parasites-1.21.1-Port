package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

/**
 * Camera shake / screen darkening ("Qlip" effect): started by {@code QlipShakePayload} (Rupter-type mobs, Bough item).
 * 10 ticks ramp up, 10 hold, then fade out over 20 ticks; with a duration it keeps holding until the duration ran out.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientQlipShake {
    public static final ClientQlipShake INSTANCE = new ClientQlipShake();
    private static final int RAMP_UP_TICKS = 10;
    private static final int HOLD_TICKS = 10;
    private static final int FADE_TICKS = 20;
    private static final int TOTAL_TICKS = 40;

    private final Random rng = new Random();
    private int delayLeft = 0;
    private int elapsed = TOTAL_TICKS;
    private boolean darkScreen;
    private boolean shakeScreen;
    private float shakeScreenValue;
    private int durationShake;

    private ClientQlipShake() {
    }

    public void triggerDelayed(int durationTicks, int delayTicks, boolean dark, boolean shake, float value) {
        this.shakeScreen = shake;
        this.darkScreen = dark;
        this.durationShake = durationTicks;
        this.shakeScreenValue = value;
        this.delayLeft = Math.max(this.delayLeft, Math.max(0, delayTicks));
        if (this.elapsed == TOTAL_TICKS) {
            this.elapsed = 0;
        }
        if (this.elapsed >= RAMP_UP_TICKS) {
            this.elapsed = RAMP_UP_TICKS;
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        INSTANCE.tick();
    }

    private void tick() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        if (this.delayLeft > 0) {
            --this.delayLeft;
        } else if (this.elapsed < TOTAL_TICKS) {
            ++this.elapsed;
        }
    }

    private float intensity() {
        --this.durationShake;
        if (this.delayLeft > 0 || this.elapsed <= 0 || this.elapsed > TOTAL_TICKS) {
            return 0.0f;
        }
        if (this.elapsed <= RAMP_UP_TICKS) {
            return (float) this.elapsed / (float) RAMP_UP_TICKS;
        }
        int afterRamp = this.elapsed - RAMP_UP_TICKS;
        if (afterRamp <= HOLD_TICKS) {
            return 1.0f;
        }
        if (this.durationShake > 0) {
            this.elapsed = 30;
            return 1.0f;
        }
        int afterHold = afterRamp - HOLD_TICKS;
        return Math.max(0.0f, 1.0f - (float) afterHold / (float) FADE_TICKS);
    }

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles e) {
        INSTANCE.camera(e);
    }

    private void camera(ViewportEvent.ComputeCameraAngles e) {
        float k = this.intensity();
        if (k <= 0.0f || !this.shakeScreen) {
            return;
        }
        float strengthDeg = this.shakeScreenValue * k;
        e.setYaw(e.getYaw() + (this.rng.nextFloat() - 0.5f) * 2.0f * strengthDeg);
        e.setPitch(e.getPitch() + (this.rng.nextFloat() - 0.5f) * 2.0f * strengthDeg);
        e.setRoll(e.getRoll() + (this.rng.nextFloat() - 0.5f) * 2.0f * (strengthDeg * 0.7f));
    }

    @SubscribeEvent
    public static void onOverlayPost(RenderGuiEvent.Post e) {
        INSTANCE.overlay(e);
    }

    private void overlay(RenderGuiEvent.Post e) {
        if (!this.darkScreen) {
            return;
        }
        float k = this.intensity();
        if (k <= 0.0f) {
            return;
        }
        int w = e.getGuiGraphics().guiWidth();
        int h = e.getGuiGraphics().guiHeight();
        float maxAlpha = 0.6f;
        int a = (int) (maxAlpha * k * 255.0f) << 24;
        e.getGuiGraphics().fill(0, 0, w, h, a);
    }
}
