package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * GenLayerSRPColdStar / GenLayerSRPWarmStar of 1.10.9 for 1.21: the biome of every noise cell of the overworld is replaced by its
 * cold (starType 1) or warm (starType 2) counterpart, so the terrain surface, features and structures of the new biome are used.
 * Called from {@code MultiNoiseBiomeSourceMixin}.
 */
public final class StarBiomeMapper {
    private static final Map<ResourceKey<Biome>, Holder<Biome>> HOLDERS = new HashMap<>();

    private StarBiomeMapper() {
    }

    public static synchronized void clearCache() {
        HOLDERS.clear();
    }

    private static ResourceKey<Biome> cold(ResourceKey<Biome> k) {
        if (k == Biomes.OCEAN || k == Biomes.LUKEWARM_OCEAN || k == Biomes.WARM_OCEAN || k == Biomes.COLD_OCEAN) {
            return Biomes.FROZEN_OCEAN;
        }
        if (k == Biomes.DEEP_OCEAN || k == Biomes.DEEP_LUKEWARM_OCEAN || k == Biomes.DEEP_COLD_OCEAN) {
            return Biomes.DEEP_FROZEN_OCEAN;
        }
        if (k == Biomes.RIVER) {
            return Biomes.FROZEN_RIVER;
        }
        if (k == Biomes.BEACH || k == Biomes.STONY_SHORE) {
            return Biomes.SNOWY_BEACH;
        }
        if (k == Biomes.PLAINS || k == Biomes.DESERT || k == Biomes.SAVANNA || k == Biomes.SPARSE_JUNGLE || k == Biomes.SWAMP || k == Biomes.MANGROVE_SWAMP
                || k == Biomes.SUNFLOWER_PLAINS || k == Biomes.MEADOW || k == Biomes.CHERRY_GROVE) {
            return Biomes.SNOWY_PLAINS;
        }
        if (k == Biomes.FOREST || k == Biomes.BIRCH_FOREST || k == Biomes.DARK_FOREST || k == Biomes.JUNGLE || k == Biomes.FLOWER_FOREST || k == Biomes.BAMBOO_JUNGLE
                || k == Biomes.OLD_GROWTH_BIRCH_FOREST || k == Biomes.TAIGA || k == Biomes.OLD_GROWTH_PINE_TAIGA || k == Biomes.OLD_GROWTH_SPRUCE_TAIGA) {
            return Biomes.SNOWY_TAIGA;
        }
        if (k == Biomes.WINDSWEPT_HILLS || k == Biomes.WINDSWEPT_FOREST || k == Biomes.WINDSWEPT_GRAVELLY_HILLS || k == Biomes.WINDSWEPT_SAVANNA
                || k == Biomes.SAVANNA_PLATEAU || k == Biomes.BADLANDS || k == Biomes.ERODED_BADLANDS || k == Biomes.WOODED_BADLANDS || k == Biomes.MUSHROOM_FIELDS) {
            return Biomes.SNOWY_SLOPES;
        }
        return k;
    }

    private static ResourceKey<Biome> warm(ResourceKey<Biome> k) {
        if (k == Biomes.RIVER || k == Biomes.FROZEN_RIVER) {
            return Biomes.DESERT;
        }
        if (k == Biomes.OCEAN || k == Biomes.DEEP_OCEAN || k == Biomes.FROZEN_OCEAN || k == Biomes.DEEP_FROZEN_OCEAN || k == Biomes.COLD_OCEAN || k == Biomes.DEEP_COLD_OCEAN
                || k == Biomes.LUKEWARM_OCEAN || k == Biomes.DEEP_LUKEWARM_OCEAN || k == Biomes.WARM_OCEAN || k == Biomes.MUSHROOM_FIELDS) {
            return Biomes.BADLANDS;
        }
        if (k == Biomes.BEACH || k == Biomes.SNOWY_BEACH) {
            return Biomes.STONY_SHORE;
        }
        if (k == Biomes.PLAINS || k == Biomes.SUNFLOWER_PLAINS || k == Biomes.FOREST || k == Biomes.BIRCH_FOREST || k == Biomes.DARK_FOREST || k == Biomes.FLOWER_FOREST
                || k == Biomes.OLD_GROWTH_BIRCH_FOREST || k == Biomes.TAIGA || k == Biomes.OLD_GROWTH_PINE_TAIGA || k == Biomes.OLD_GROWTH_SPRUCE_TAIGA || k == Biomes.SNOWY_TAIGA
                || k == Biomes.SNOWY_PLAINS || k == Biomes.ICE_SPIKES || k == Biomes.JUNGLE || k == Biomes.BAMBOO_JUNGLE || k == Biomes.SPARSE_JUNGLE || k == Biomes.SWAMP
                || k == Biomes.MANGROVE_SWAMP || k == Biomes.MEADOW || k == Biomes.CHERRY_GROVE || k == Biomes.GROVE) {
            return Biomes.SAVANNA;
        }
        if (k == Biomes.WINDSWEPT_HILLS || k == Biomes.WINDSWEPT_FOREST || k == Biomes.WINDSWEPT_GRAVELLY_HILLS || k == Biomes.WINDSWEPT_SAVANNA
                || k == Biomes.SNOWY_SLOPES || k == Biomes.FROZEN_PEAKS || k == Biomes.JAGGED_PEAKS) {
            return Biomes.WOODED_BADLANDS;
        }
        return k;
    }

    private static synchronized Holder<Biome> holderOf(ResourceKey<Biome> key, Holder<Biome> fallback) {
        Holder<Biome> h = HOLDERS.get(key);
        if (h != null) {
            return h;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return fallback;
        }
        h = server.registryAccess().registryOrThrow(Registries.BIOME).getHolder(key).<Holder<Biome>>map(r -> r).orElse(fallback);
        HOLDERS.put(key, h);
        return h;
    }

    public static Holder<Biome> map(Holder<Biome> in) {
        int starType = SRPWorldEntitySpawner.starType;
        if (starType != 1 && starType != 2) {
            return in;
        }
        ResourceKey<Biome> key = in.unwrapKey().orElse(null);
        if (key == null || !"minecraft".equals(key.location().getNamespace())) {
            return in;
        }
        ResourceKey<Biome> target = starType == 1 ? cold(key) : warm(key);
        return target == key ? in : holderOf(target, in);
    }
}
