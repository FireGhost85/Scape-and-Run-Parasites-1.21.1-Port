package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteHarlequin;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteShrouded;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.neoforged.bus.api.SubscribeEvent;

public class SRPBiomes {
    public static final BiomeParasiteShrouded biomeShrouded = new BiomeParasiteShrouded();
    public static final BiomeParasiteHarlequin biomeHarlequin = new BiomeParasiteHarlequin();

    private static void register(RegistryEvent.Register<Biome> event, Biome biome, BiomeManager.BiomeType type, String name, int weight, BiomeDictionary.Type ... biomeDictTypes) {
        biome.setRegistryName(ResourceLocation.fromNamespaceAndPath("srparasites", name));
        event.getRegistry().register((IForgeRegistryEntry)biome);
        for (BiomeDictionary.Type biomeDictType : biomeDictTypes) {
            BiomeDictionary.addTypes((Biome)biome, (BiomeDictionary.Type[])new BiomeDictionary.Type[]{biomeDictType});
        }
        BiomeManager.addBiome((BiomeManager.BiomeType)type, (BiomeManager.BiomeEntry)new BiomeManager.BiomeEntry(biome, weight));
    }

    public static void clearMobSpawnList() {
        biomeShrouded.mobListClear();
        biomeShrouded.setBlocks();
        biomeHarlequin.mobListClear();
        biomeHarlequin.setBlocks();
    }

    @Mod.EventBusSubscriber(modid="srparasites")
    public static class RegistrationHandler {
        @SubscribeEvent
        public static void onEvent(RegistryEvent.Register<Biome> event) {
            if (SRPConfigWorld.biomeRegster) {
                SRPBiomes.register((RegistryEvent.Register<Biome>)event, biomeShrouded, BiomeManager.BiomeType.COOL, "biomeparasite_shrouded", SRPConfigWorld.biomeOneWeight, new BiomeDictionary.Type[]{BiomeDictionary.Type.SPOOKY});
                SRPBiomes.register((RegistryEvent.Register<Biome>)event, biomeHarlequin, BiomeManager.BiomeType.COOL, "biomeparasite_harlequin", SRPConfigWorld.biomeThreeWeight, new BiomeDictionary.Type[]{BiomeDictionary.Type.SPOOKY});
            }
        }
    }
}

