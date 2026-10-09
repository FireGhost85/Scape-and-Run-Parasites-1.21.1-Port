package com.dhanantry.scapeandrunparasites.world.star;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** Star type of the world (0 normal, 1 cold, 2 warm) and its options, stored with the overworld (SRPStarWorldData of 1.10.9). */
public class SRPStarWorldData extends SavedData {
    public static final String DATA_NAME = "srp_star_world_data";
    private int starType = 0;
    private boolean mushroomTreesEnabled = false;
    private boolean fracturedTerrainEnabled = false;
    /** True while the data was just created (a new world), so the creation settings can be applied once. */
    private boolean fresh = true;

    public static SRPStarWorldData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SRPStarWorldData::new, SRPStarWorldData::load, null), DATA_NAME);
    }

    public boolean isFresh() {
        return this.fresh;
    }

    public void markUsed() {
        this.fresh = false;
        this.setDirty();
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
            this.setDirty();
        }
    }

    public boolean isMushroomTreesEnabled() {
        return this.mushroomTreesEnabled;
    }

    public void setMushroomTreesEnabled(boolean enabled) {
        if (this.mushroomTreesEnabled != enabled) {
            this.mushroomTreesEnabled = enabled;
            this.setDirty();
        }
    }

    public boolean isFracturedTerrainEnabled() {
        return this.starType == 1 && this.fracturedTerrainEnabled;
    }

    public void setFracturedTerrainEnabled(boolean enabled) {
        boolean allowed = this.starType == 1 && enabled;
        if (this.fracturedTerrainEnabled != allowed) {
            this.fracturedTerrainEnabled = allowed;
            this.setDirty();
        }
    }

    public static SRPStarWorldData load(CompoundTag nbt, HolderLookup.Provider registries) {
        SRPStarWorldData d = new SRPStarWorldData();
        d.fresh = false;
        d.starType = nbt.getInt("StarType");
        if (d.starType < 0 || d.starType > 2) {
            d.starType = 0;
        }
        d.mushroomTreesEnabled = nbt.contains("MushroomTrees") && nbt.getBoolean("MushroomTrees");
        d.fracturedTerrainEnabled = d.starType == 1 && nbt.contains("FracturedTerrain") && nbt.getBoolean("FracturedTerrain");
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag compound, HolderLookup.Provider registries) {
        compound.putInt("StarType", this.starType);
        compound.putBoolean("MushroomTrees", this.mushroomTreesEnabled);
        compound.putBoolean("FracturedTerrain", this.fracturedTerrainEnabled);
        return compound;
    }
}
