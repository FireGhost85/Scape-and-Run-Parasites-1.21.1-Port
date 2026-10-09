package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.block.SRPBlockLinks;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteHarlequin;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteShrouded;

/**
 * The two parasite biomes of the original. In 1.21 the biomes themselves are data ({@code data/srparasites/worldgen/biome});
 * these are their block palette / feature helpers, looked up by the biome key.
 */
public final class SRPBiomes {
    public static final BiomeParasiteShrouded biomeShrouded = (BiomeParasiteShrouded) BiomeParasiteBase.get(SRPBlockLinks.BIOME_SHROUDED);
    public static final BiomeParasiteHarlequin biomeHarlequin = (BiomeParasiteHarlequin) BiomeParasiteBase.get(SRPBlockLinks.BIOME_HARLEQUIN);

    private SRPBiomes() {
    }
}
