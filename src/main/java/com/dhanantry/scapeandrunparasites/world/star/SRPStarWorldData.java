package com.dhanantry.scapeandrunparasites.world.star;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class SRPStarWorldData
extends WorldSavedData {
    private static final String DATA_NAME = "srp_star_world_data";
    private int starType = 0;
    private boolean mushroomTreesEnabled = false;
    private boolean fracturedTerrainEnabled = false;

    public SRPStarWorldData() {
        super(DATA_NAME);
    }

    public SRPStarWorldData(String name) {
        super(name);
    }

    public static SRPStarWorldData get(Level world) {
        MapStorage storage = world.getPerWorldStorage();
        SRPStarWorldData data = (SRPStarWorldData)storage.loadData(SRPStarWorldData.class, DATA_NAME);
        if (data == null) {
            data = new SRPStarWorldData();
            storage.setData(DATA_NAME, (WorldSavedData)data);
            data.markDirty();
        }
        return data;
    }

    public int getStarType() {
        return this.starType;
    }

    public void setStarType(int starType) {
        if (starType < 0 || starType > 2) {
            starType = 0;
        }
        boolean changed = this.starType != starType;
        this.starType = starType;
        if (this.starType != 1 && this.fracturedTerrainEnabled) {
            this.fracturedTerrainEnabled = false;
            changed = true;
        }
        if (changed) {
            this.markDirty();
        }
    }

    public boolean isMushroomTreesEnabled() {
        return this.mushroomTreesEnabled;
    }

    public void setMushroomTreesEnabled(boolean enabled) {
        if (this.mushroomTreesEnabled != enabled) {
            this.mushroomTreesEnabled = enabled;
            this.markDirty();
        }
    }

    public boolean isFracturedTerrainEnabled() {
        return this.starType == 1 && this.fracturedTerrainEnabled;
    }

    public void setFracturedTerrainEnabled(boolean enabled) {
        boolean allowed;
        boolean bl = allowed = this.starType == 1 && enabled;
        if (this.fracturedTerrainEnabled != allowed) {
            this.fracturedTerrainEnabled = allowed;
            this.markDirty();
        }
    }

    public void readFromNBT(CompoundTag nbt) {
        this.starType = nbt.getInt("StarType");
        if (this.starType < 0 || this.starType > 2) {
            this.starType = 0;
        }
        this.mushroomTreesEnabled = nbt.contains("MushroomTrees") && nbt.getBoolean("MushroomTrees");
        this.fracturedTerrainEnabled = this.starType == 1 && nbt.contains("FracturedTerrain") && nbt.getBoolean("FracturedTerrain");
    }

    public CompoundTag writeToNBT(CompoundTag compound) {
        compound.putInt("StarType", this.starType);
        compound.putBoolean("MushroomTrees", this.mushroomTreesEnabled);
        compound.putBoolean("FracturedTerrain", this.fracturedTerrainEnabled);
        return compound;
    }
}

