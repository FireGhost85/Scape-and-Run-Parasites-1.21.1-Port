package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import net.minecraft.util.RandomSource;

/** Client-side phase / music state (static fields of SRPEventHandlerBus in 1.10.9). */
public final class SRPClientState {
    public static byte clientCurrentEvoPhase;
    public static int clientScent = 0;
    public static int clientVector = 0;
    public static int musicTimer = 1000;
    public static float fog;
    public static float fogRed;
    public static float fogGreen;
    public static float fogBlue;

    private SRPClientState() {}

    /** SRPEventHandlerBus.resetSouncTicker. */
    public static void resetSoundTicker(int in) {
        if (in > 0) {
            if (musicTimer < in) return;
            musicTimer = in;
            return;
        }
        RandomSource rand = RandomSource.create();
        int range = SRPConfig.musicMax - SRPConfig.musicMin + 1;
        if (range <= 0 || SRPConfig.musicMax <= SRPConfig.musicMin) {
            range = 1; // original computed a throw-away value here and would have thrown on nextInt(<=0); keep it safe
        }
        int next = rand.nextInt(range) + SRPConfig.musicMin;
        if (musicTimer < next && musicTimer > 0) return;
        musicTimer = next;
    }

    /** Leaving a world (SRPEventHandlerBus.onWorldLoad / WorldEvent.Unload). */
    public static void reset() {
        clientCurrentEvoPhase = 0;
        clientScent = 0;
        musicTimer = 1000;
        fog = 0.0f;
    }
}
