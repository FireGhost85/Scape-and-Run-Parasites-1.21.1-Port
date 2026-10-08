package com.dhanantry.scapeandrunparasites.world;

/**
 * Pace choice picked on the world-creation "SRP world settings" screen (SRPWorldEntitySpawner.choiceNUMBER in 1.10.9):
 * 0 = x0.5, 1 = x1 (default), 2 = x3, 3 = x10. Copied into the world's {@link SRPSaveData} when it is first created.
 */
public final class SRPPace {
    public static int choiceNUMBER = 1;

    private SRPPace() {}
}
