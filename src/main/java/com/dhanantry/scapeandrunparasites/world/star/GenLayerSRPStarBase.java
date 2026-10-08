package com.dhanantry.scapeandrunparasites.world.star;


public abstract class GenLayerSRPStarBase
extends GenLayer {
    public GenLayerSRPStarBase(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }
}

