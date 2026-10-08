package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPColdStar;
import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPWarmStar;

public class GenLayerSRPStarRouter
extends GenLayer {
    private final GenLayer normalLayer;
    private final GenLayer coldLayer;
    private final GenLayer warmLayer;

    public GenLayerSRPStarRouter(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        this.normalLayer = parent;
        this.coldLayer = new GenLayerSRPColdStar(seed + 101L, parent);
        this.warmLayer = new GenLayerSRPWarmStar(seed + 202L, parent);
    }

    public void initWorldGenSeed(long seed) {
        super.initWorldGenSeed(seed);
        if (this.normalLayer != null) {
            this.normalLayer.initWorldGenSeed(seed);
        }
        if (this.coldLayer != null) {
            this.coldLayer.initWorldGenSeed(seed);
        }
        if (this.warmLayer != null) {
            this.warmLayer.initWorldGenSeed(seed);
        }
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int starType = SRPWorldEntitySpawner.starType;
        if (starType == 1) {
            return this.coldLayer.getInts(areaX, areaY, areaWidth, areaHeight);
        }
        if (starType == 2) {
            return this.warmLayer.getInts(areaX, areaY, areaWidth, areaHeight);
        }
        return this.normalLayer.getInts(areaX, areaY, areaWidth, areaHeight);
    }
}

