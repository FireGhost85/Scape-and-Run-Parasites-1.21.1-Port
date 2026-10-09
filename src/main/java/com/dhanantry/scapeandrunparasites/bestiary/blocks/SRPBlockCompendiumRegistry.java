package com.dhanantry.scapeandrunparasites.bestiary.blocks;

import com.dhanantry.scapeandrunparasites.bestiary.blocks.BlockBestiaryEntry;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class SRPBlockCompendiumRegistry {
    private static final Map<ResourceLocation, BlockBestiaryEntry> ENTRIES = new LinkedHashMap<ResourceLocation, BlockBestiaryEntry>();

    private SRPBlockCompendiumRegistry() {
    }

    public static BlockBestiaryEntry register(Block block, String nameKey, String loreKey) {
        if (block == null) {
            System.out.println("[BlockCompendium] Skipping entry '" + nameKey + "' because block is null");
            return null;
        }
        ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) {
            System.out.println("[BlockCompendium] Skipping entry '" + nameKey + "' because registryName is null for " + block);
            return null;
        }
        BlockBestiaryEntry entry = new BlockBestiaryEntry(block, nameKey, loreKey);
        ENTRIES.put(id, entry);
        return entry;
    }

    public static BlockBestiaryEntry get(ResourceLocation id) {
        return ENTRIES.get(id);
    }

    public static Collection<BlockBestiaryEntry> all() {
        return ENTRIES.values();
    }

    public static void clear() {
        ENTRIES.clear();
    }

    public static void registerDefaults() {
        SRPBlockCompendiumRegistry.clear();
        SRPBlockCompendiumRegistry.register(SRPBlocks.HarleskinnBlock.get(), "block.srparasites.harleskinn_block", "bestiary.block.srparasites.harleskinn_block.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.LocsBlock.get(), "block.srparasites.locs_block", "bestiary.block.srparasites.locs_block.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.FogNullifier.get(), "block.srparasites.fog_nullifier", "bestiary.block.srparasites.fog_nullifier.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.dodN.get(), "block.srparasites.dispatchern", "bestiary.block.srparasites.dispatchern.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.AssimilatedSugarCane.get(), "block.srparasites.assimilated_reed", "bestiary.block.srparasites.assimilated_reed.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.INFESTED_FURNACE.get(), "block.srparasites.infested_furnace", "bestiary.block.srparasites.infested_furnace.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.BiomassBlock.get(), "block.srparasites.biomass_block", "bestiary.block.srparasites.biomass_block.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.ResidueBlock.get(), "block.srparasites.residue_block", "bestiary.block.srparasites.residue_block.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.Alveoli.get(), "block.srparasites.alveoli", "bestiary.block.srparasites.alveoli.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.SickAlveoli.get(), "block.srparasites.sick_alveoli", "bestiary.block.srparasites.sick_alveoli.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.HairFollicleBlock.get(), "block.srparasites.hair_follicle_block", "bestiary.block.srparasites.hair_follicle_block.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.PARASITE_BARRIER.get(), "block.srparasites.parasite_barrier", "bestiary.block.srparasites.parasite_barrier.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.AssimilatedPumpkin.get(), "block.srparasites.assimilated_pumpkin", "bestiary.block.srparasites.assimilated_pumpkin.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.EscaBulb.get(), "block.srparasites.esca_bulb", "bestiary.block.srparasites.esca_bulb.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.NODE_LAMP.get(), "block.srparasites.node_redstone_lamp", "bestiary.block.srparasites.node_redstone_lamp.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.ParasiteLoot.get(), "block.srparasites.parasiteloot_common", "bestiary.block.srparasites.parasiteloot_common.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.InfestPurify.get(), "block.srparasites.infestation_purifier", "bestiary.block.srparasites.infestation_purifier.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.ParasiteMouth.get(), "block.srparasites.parasitemouth", "bestiary.block.srparasites.parasitemouth.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.RelayBase.get(), "block.srparasites.relay_base", "bestiary.block.srparasites.relay_base.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.RelayMiddle.get(), "block.srparasites.relay_middle", "bestiary.block.srparasites.relay_middle.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.RelayRoof.get(), "block.srparasites.relay_roof", "bestiary.block.srparasites.relay_roof.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.BiomePurifier.get(), "block.srparasites.biomepurifier", "bestiary.block.srparasites.biomepurifier.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.InfestRemain.get(), "block.srparasites.infestremain", "bestiary.block.srparasites.infested_remain.desc");
        SRPBlockCompendiumRegistry.register(SRPBlocks.diseasedSponge.get(), "block.srparasites.diseased_sponge", "bestiary.block.srparasites.diseased_sponge.desc");
    }
}

