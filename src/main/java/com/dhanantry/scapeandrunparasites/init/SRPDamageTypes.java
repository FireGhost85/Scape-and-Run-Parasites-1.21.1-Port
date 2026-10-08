package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

/**
 * Keys of the damage types that 1.12 built with {@code new DamageSource(name)} (data files under
 * {@code data/srparasites/damage_type}, flags in the {@code minecraft:bypasses_*} tags).
 */
public final class SRPDamageTypes {
    /** {@code sepeku}: bypasses armour, absolute, allowed in creative ({@code ItemBough}). */
    public static final ResourceKey<DamageType> SEPEKU = key("sepeku");
    /** {@code biomass}: bypasses armour ({@code BlockBiomassBlock}). */
    public static final ResourceKey<DamageType> BIOMASS = key("biomass");
    /** {@code srp_dod_block}: bypasses armour, absolute ({@code BlockDod}). */
    public static final ResourceKey<DamageType> DOD_BLOCK = key("srp_dod_block");
    /** {@code parasite_mouth}: bypasses armour ({@code BlockParasiteMouth}). */
    public static final ResourceKey<DamageType> PARASITE_MOUTH = key("parasite_mouth");

    private SRPDamageTypes() {
    }

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path));
    }
}
