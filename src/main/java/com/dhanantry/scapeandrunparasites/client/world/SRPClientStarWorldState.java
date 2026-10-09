package com.dhanantry.scapeandrunparasites.client.world;

/** Star type of the world as the server told it (-1 = unknown). */
public final class SRPClientStarWorldState {
    private static volatile int starType = -1;

    private SRPClientStarWorldState() {
    }

    public static void setStarType(int type) {
        if (type < 0 || type > 2) {
            starType = -1;
            return;
        }
        starType = type;
    }

    public static int getStarType() {
        return starType;
    }

    public static boolean isKnown() {
        return starType >= 0;
    }

    public static boolean isCold() {
        return starType == 1;
    }

    public static boolean isWarm() {
        return starType == 2;
    }

    public static void reset() {
        starType = -1;
    }
}
