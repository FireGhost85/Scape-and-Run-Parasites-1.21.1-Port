package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPColdStar;
import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPWarmStar;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldEvents;

public class GenLayerSRPDynamicStar
extends GenLayer {
    private final GenLayer original;
    private final GenLayer cold;
    private final GenLayer warm;

    public GenLayerSRPDynamicStar(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        this.original = parent;
        this.cold = new GenLayerSRPColdStar(seed ^ 0x5DEECE66DL, parent);
        this.warm = new GenLayerSRPWarmStar(seed ^ 0xBL, parent);
    }

    public void initWorldGenSeed(long seed) {
        super.initWorldGenSeed(seed);
        this.cold.initWorldGenSeed(seed);
        this.warm.initWorldGenSeed(seed);
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int starType = SRPStarWorldEvents.getActiveStarTypeForGeneration();
        if (starType == 1) {
            return this.cold.getInts(areaX, areaY, areaWidth, areaHeight);
        }
        if (starType == 2) {
            return this.warm.getInts(areaX, areaY, areaWidth, areaHeight);
        }
        return this.original.getInts(areaX, areaY, areaWidth, areaHeight);
    }
}

