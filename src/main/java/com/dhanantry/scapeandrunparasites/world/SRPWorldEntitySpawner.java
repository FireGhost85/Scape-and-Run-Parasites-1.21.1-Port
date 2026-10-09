package com.dhanantry.scapeandrunparasites.world;


/**
 * Settings holder of the original's second (unused) spawner class: the spawning itself is done by
 * {@link SRPWorldParasiteSpawner}; only these shared fields are read by the rest of the mod.
 */
public class SRPWorldEntitySpawner {
    public static boolean triggerSPAWNING = false;
    public static int choiceNUMBER = 1;
    public static final int STAR_NORMAL = 0;
    public static final int STAR_COLD = 1;
    public static final int STAR_WARM = 2;
    public static int starType = 0;
    /** Live copies of the star world options (read by the world generation threads, set when the overworld loads). */
    public static volatile boolean mushroomTrees = false;
    public static volatile boolean fracturedTerrain = false;
}
