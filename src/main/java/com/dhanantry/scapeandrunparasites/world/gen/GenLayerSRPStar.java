package com.dhanantry.scapeandrunparasites.world.gen;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class GenLayerSRPStar
extends GenLayer {
    private static final int[] COLD_BIOMES = new int[]{Biome.getIdForBiome((Biome)Biomes.icePlains), Biome.getIdForBiome((Biome)Biomes.iceMountains), Biome.getIdForBiome((Biome)Biomes.coldTaiga), Biome.getIdForBiome((Biome)Biomes.coldTaigaHills), Biome.getIdForBiome((Biome)Biomes.taiga), Biome.getIdForBiome((Biome)Biomes.taigaHills)};
    private static final int[] WARM_BIOMES = new int[]{Biome.getIdForBiome((Biome)Biomes.desert), Biome.getIdForBiome((Biome)Biomes.desertHills), Biome.getIdForBiome((Biome)Biomes.savanna), Biome.getIdForBiome((Biome)Biomes.savannaPlateau), Biome.getIdForBiome((Biome)Biomes.mesa), Biome.getIdForBiome((Biome)Biomes.mesaPlateau_F), Biome.getIdForBiome((Biome)Biomes.mesaPlateau), Biome.getIdForBiome((Biome)Biomes.extremeHills), Biome.getIdForBiome((Biome)Biomes.extremeHillsPlus), Biome.getIdForBiome((Biome)Biomes.stoneBeach)};

    public GenLayerSRPStar(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] input = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] output = IntCache.getIntCache((int)(areaWidth * areaHeight));
        int starType = SRPWorldEntitySpawner.starType;
        for (int z = 0; z < areaHeight; ++z) {
            for (int x = 0; x < areaWidth; ++x) {
                int index = x + z * areaWidth;
                int biomeId = input[index];
                this.initChunkSeed(x + areaX, z + areaY);
                output[index] = starType == 1 ? this.getColdBiome(biomeId) : (starType == 2 ? this.getWarmBiome(biomeId) : biomeId);
            }
        }
        return output;
    }

    private int getColdBiome(int biomeId) {
        if (biomeId == Biome.getIdForBiome((Biome)Biomes.river)) {
            return Biome.getIdForBiome((Biome)Biomes.frozenRiver);
        }
        if (biomeId == Biome.getIdForBiome((Biome)Biomes.ocean) || biomeId == Biome.getIdForBiome((Biome)Biomes.deepOcean)) {
            return Biome.getIdForBiome((Biome)Biomes.frozenOcean);
        }
        if (biomeId == Biome.getIdForBiome((Biome)Biomes.beach) || biomeId == Biome.getIdForBiome((Biome)Biomes.stoneBeach)) {
            return Biome.getIdForBiome((Biome)Biomes.coldBeach);
        }
        return COLD_BIOMES[this.nextInt(COLD_BIOMES.length)];
    }

    private int getWarmBiome(int biomeId) {
        if (biomeId == Biome.getIdForBiome((Biome)Biomes.river)) {
            if (this.nextInt(18) == 0) {
                return Biome.getIdForBiome((Biome)Biomes.river);
            }
            return WARM_BIOMES[this.nextInt(WARM_BIOMES.length)];
        }
        if (biomeId == Biome.getIdForBiome((Biome)Biomes.ocean) || biomeId == Biome.getIdForBiome((Biome)Biomes.deepOcean) || biomeId == Biome.getIdForBiome((Biome)Biomes.frozenOcean)) {
            return this.nextInt(3) == 0 ? Biome.getIdForBiome((Biome)Biomes.stoneBeach) : WARM_BIOMES[this.nextInt(WARM_BIOMES.length)];
        }
        if (biomeId == Biome.getIdForBiome((Biome)Biomes.beach) || biomeId == Biome.getIdForBiome((Biome)Biomes.coldBeach)) {
            return Biome.getIdForBiome((Biome)Biomes.stoneBeach);
        }
        return WARM_BIOMES[this.nextInt(WARM_BIOMES.length)];
    }
}

