package com.dhanantry.scapeandrunparasites.world.spawner;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPSpawning;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 1.12 {@code SRPSpawning.init()}: when the custom (phase) spawner is off, the parasites are added to the spawn lists of every
 * biome with the spawn rates of the config. Registered through {@code data/srparasites/neoforge/biome_modifier/parasite_spawns.json}.
 */
public record SRPSpawnBiomeModifier() implements BiomeModifier {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ScapeAndRunParasites.MODID);
    public static final MapCodec<SRPSpawnBiomeModifier> CODEC = MapCodec.unit(SRPSpawnBiomeModifier::new);
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<SRPSpawnBiomeModifier>> HOLDER = SERIALIZERS.register("parasite_spawns", () -> CODEC);

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) {
            return;
        }
        for (SRPSpawning.BiomeSpawn spawn : SRPSpawning.biomeSpawns()) {
            MobCategory category = switch (spawn.category()) {
                case 1 -> MobCategory.CREATURE;
                case 2 -> MobCategory.WATER_CREATURE;
                default -> MobCategory.MONSTER;
            };
            builder.getMobSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(spawn.type().get(), spawn.weight(), spawn.min(), spawn.max()));
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}
