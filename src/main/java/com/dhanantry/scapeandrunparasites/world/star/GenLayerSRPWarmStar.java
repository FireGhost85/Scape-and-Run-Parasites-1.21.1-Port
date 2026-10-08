package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.star.GenLayerSRPStarBase;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class GenLayerSRPWarmStar
extends GenLayerSRPStarBase {
    private static final int OCEAN = Biome.getIdForBiome((Biome)Biomes.ocean);
    private static final int DEEP_OCEAN = Biome.getIdForBiome((Biome)Biomes.deepOcean);
    private static final int FROZEN_OCEAN = Biome.getIdForBiome((Biome)Biomes.frozenOcean);
    private static final int RIVER = Biome.getIdForBiome((Biome)Biomes.river);
    private static final int BEACH = Biome.getIdForBiome((Biome)Biomes.beach);
    private static final int COLD_BEACH = Biome.getIdForBiome((Biome)Biomes.coldBeach);
    private static final int STONE_BEACH = Biome.getIdForBiome((Biome)Biomes.stoneBeach);
    private static final int PLAINS = Biome.getIdForBiome((Biome)Biomes.plains);
    private static final int DESERT = Biome.getIdForBiome((Biome)Biomes.desert);
    private static final int DESERT_HILLS = Biome.getIdForBiome((Biome)Biomes.desertHills);
    private static final int SAVANNA = Biome.getIdForBiome((Biome)Biomes.savanna);
    private static final int SAVANNA_PLATEAU = Biome.getIdForBiome((Biome)Biomes.savannaPlateau);
    private static final int MESA = Biome.getIdForBiome((Biome)Biomes.mesa);
    private static final int MESA_ROCK = Biome.getIdForBiome((Biome)Biomes.mesaPlateau_F);
    private static final int MESA_CLEAR_ROCK = Biome.getIdForBiome((Biome)Biomes.mesaPlateau);
    private static final int FOREST = Biome.getIdForBiome((Biome)Biomes.forest);
    private static final int FOREST_HILLS = Biome.getIdForBiome((Biome)Biomes.forestHills);
    private static final int BIRCH_FOREST = Biome.getIdForBiome((Biome)Biomes.birchForest);
    private static final int BIRCH_FOREST_HILLS = Biome.getIdForBiome((Biome)Biomes.birchForestHills);
    private static final int ROOFED_FOREST = Biome.getIdForBiome((Biome)Biomes.roofedForest);
    private static final int TAIGA = Biome.getIdForBiome((Biome)Biomes.taiga);
    private static final int TAIGA_HILLS = Biome.getIdForBiome((Biome)Biomes.taigaHills);
    private static final int COLD_TAIGA = Biome.getIdForBiome((Biome)Biomes.coldTaiga);
    private static final int COLD_TAIGA_HILLS = Biome.getIdForBiome((Biome)Biomes.coldTaigaHills);
    private static final int EXTREME_HILLS = Biome.getIdForBiome((Biome)Biomes.extremeHills);
    private static final int EXTREME_HILLS_EDGE = Biome.getIdForBiome((Biome)Biomes.extremeHillsEdge);
    private static final int EXTREME_HILLS_WITH_TREES = Biome.getIdForBiome((Biome)Biomes.extremeHillsPlus);
    private static final int SWAMPLAND = Biome.getIdForBiome((Biome)Biomes.swampland);
    private static final int JUNGLE = Biome.getIdForBiome((Biome)Biomes.jungle);
    private static final int JUNGLE_HILLS = Biome.getIdForBiome((Biome)Biomes.jungleHills);
    private static final int JUNGLE_EDGE = Biome.getIdForBiome((Biome)Biomes.jungleEdge);
    private static final int MUSHROOM_ISLAND = Biome.getIdForBiome((Biome)Biomes.mushroomIsland);
    private static final int MUSHROOM_ISLAND_SHORE = Biome.getIdForBiome((Biome)Biomes.mushroomIslandShore);

    public GenLayerSRPWarmStar(long seed, GenLayer parent) {
        super(seed, parent);
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] input = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] output = IntCache.getIntCache((int)(areaWidth * areaHeight));
        for (int z = 0; z < areaHeight; ++z) {
            for (int x = 0; x < areaWidth; ++x) {
                int index = x + z * areaWidth;
                output[index] = this.convertBiome(input[index]);
            }
        }
        return output;
    }

    private int convertBiome(int biomeId) {
        if (biomeId == RIVER) {
            return DESERT;
        }
        if (biomeId == OCEAN || biomeId == DEEP_OCEAN || biomeId == FROZEN_OCEAN) {
            return MESA;
        }
        if (biomeId == BEACH || biomeId == COLD_BEACH || biomeId == MUSHROOM_ISLAND_SHORE) {
            return STONE_BEACH;
        }
        if (biomeId == PLAINS) {
            return SAVANNA;
        }
        if (biomeId == FOREST || biomeId == BIRCH_FOREST || biomeId == ROOFED_FOREST || biomeId == TAIGA || biomeId == COLD_TAIGA || biomeId == JUNGLE || biomeId == JUNGLE_EDGE || biomeId == SWAMPLAND) {
            return SAVANNA;
        }
        if (biomeId == FOREST_HILLS || biomeId == BIRCH_FOREST_HILLS || biomeId == TAIGA_HILLS || biomeId == COLD_TAIGA_HILLS || biomeId == JUNGLE_HILLS) {
            return SAVANNA_PLATEAU;
        }
        if (biomeId == EXTREME_HILLS || biomeId == EXTREME_HILLS_EDGE || biomeId == EXTREME_HILLS_WITH_TREES) {
            return MESA_ROCK;
        }
        if (biomeId == DESERT) {
            return DESERT;
        }
        if (biomeId == DESERT_HILLS) {
            return DESERT_HILLS;
        }
        if (biomeId == SAVANNA) {
            return SAVANNA;
        }
        if (biomeId == SAVANNA_PLATEAU) {
            return SAVANNA_PLATEAU;
        }
        if (biomeId == MESA || biomeId == MUSHROOM_ISLAND) {
            return MESA;
        }
        if (biomeId == MESA_ROCK) {
            return MESA_ROCK;
        }
        if (biomeId == MESA_CLEAR_ROCK) {
            return MESA_CLEAR_ROCK;
        }
        return SAVANNA;
    }
}

