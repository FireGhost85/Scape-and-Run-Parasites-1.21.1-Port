package com.dhanantry.scapeandrunparasites.client.weather;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Direction of the blizzard: it brakes, turns black and runs backwards while a Heblu / Kirin is near (SRPBlizzardDirectionClient of 1.10.9). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class SRPBlizzardDirectionClient {
    private static boolean reverseRequested = false;
    private static int committedDirection = 1;
    private static float motion = 1.0f;
    private static float previousMotion = 1.0f;
    private static boolean braking = false;
    private static int holdTicks = 0;
    private static double motionPhase = 0.0;
    private static double previousMotionPhase = 0.0;
    private static float blackBlend = 0.0f;
    private static float previousBlackBlend = 0.0f;
    private static boolean fadingBackToWhite = false;

    private SRPBlizzardDirectionClient() {
    }

    public static void setReverseRequested(boolean reverse) {
        if (reverseRequested == reverse) {
            return;
        }
        reverseRequested = reverse;
        playSwitchSound();
        int desiredDirection = reverse ? -1 : 1;
        if (desiredDirection != committedDirection) {
            braking = true;
            holdTicks = 0;
            if (reverse) {
                fadingBackToWhite = false;
            }
        } else if (braking) {
            braking = false;
            holdTicks = 0;
        }
    }

    public static float getMotion(float partialTicks) {
        return previousMotion + (motion - previousMotion) * partialTicks;
    }

    public static double getMotionPhase(float partialTicks) {
        return previousMotionPhase + (motionPhase - previousMotionPhase) * (double)partialTicks;
    }

    public static float getBlackBlend(float partialTicks) {
        return previousBlackBlend + (blackBlend - previousBlackBlend) * partialTicks;
    }

    public static boolean isReversed() {
        return committedDirection < 0;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            reset();
            return;
        }
        previousMotion = motion;
        previousMotionPhase = motionPhase;
        previousBlackBlend = blackBlend;
        int desiredDirection = reverseRequested ? -1 : 1;
        if (desiredDirection != committedDirection) {
            braking = true;
        }
        if (braking) {
            float magnitude = Math.abs(motion);
            magnitude = Math.max(0.0f, magnitude - 0.065f);
            motion = magnitude * (float)committedDirection;
            if (reverseRequested && committedDirection > 0) {
                blackBlend = Math.min(1.0f, blackBlend + 0.075f);
            }
            if (magnitude <= 0.001f) {
                motion = 0.0f;
                if (!reverseRequested && committedDirection < 0) {
                    fadingBackToWhite = true;
                }
                if (fadingBackToWhite) {
                    blackBlend = Math.max(0.0f, blackBlend - 0.125f);
                }
                boolean colorReady = reverseRequested ? blackBlend >= 0.999f : blackBlend <= 0.001f;
                if (desiredDirection == committedDirection) {
                    braking = false;
                    holdTicks = 0;
                } else if (!colorReady) {
                    holdTicks = 0;
                } else if (holdTicks < 8) {
                    ++holdTicks;
                } else {
                    committedDirection = desiredDirection;
                    braking = false;
                    holdTicks = 0;
                    fadingBackToWhite = false;
                }
            }
        } else {
            float target = committedDirection;
            if (motion < target) {
                motion = Math.min(target, motion + 0.045f);
            } else if (motion > target) {
                motion = Math.max(target, motion - 0.045f);
            }
            blackBlend = committedDirection < 0 ? Math.min(1.0f, blackBlend + 0.08f) : Math.max(0.0f, blackBlend - 0.08f);
        }
        motionPhase += (double)motion;
    }

    private static void playSwitchSound() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        mc.getSoundManager().play(new SoundBlizzardReverse(SRPSounds.BLIZZARD_REVERSE.get(), mc.player));
    }

    public static void reset() {
        reverseRequested = false;
        committedDirection = 1;
        motion = 1.0f;
        previousMotion = 1.0f;
        braking = false;
        holdTicks = 0;
        motionPhase = 0.0;
        previousMotionPhase = 0.0;
        blackBlend = 0.0f;
        previousBlackBlend = 0.0f;
        fadingBackToWhite = false;
    }
}
