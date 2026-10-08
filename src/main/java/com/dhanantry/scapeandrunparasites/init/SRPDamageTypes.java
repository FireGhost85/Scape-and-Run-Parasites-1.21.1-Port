package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import javax.annotation.Nullable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

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

    /** {@code EntityProjectileHebluLight.DamageSourceHebluLight} (magic damage, projectile and owner). */
    public static final ResourceKey<DamageType> HEBLU_LIGHT = key("heblu_light");
    /** {@code EntityProjectileHebluLight.DamageSourceHebluLightNeutral} (magic damage, no entity). */
    public static final ResourceKey<DamageType> HEBLU_LIGHT_NEUTRAL = key("heblu_light_neutral");
    /** {@code EntityProjectileKirinSlash.DamageSourceKirinSlash} (magic damage, slash and owner). */
    public static final ResourceKey<DamageType> KIRIN_SLASH = key("kirin_slash");

    private SRPDamageTypes() {
    }

    /** {@code new DamageSource(name)} of 1.12 with the optional direct and causing entity. */
    public static DamageSource source(Level level, ResourceKey<DamageType> key, @Nullable Entity direct, @Nullable Entity causing) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key), direct, causing);
    }

    private static ResourceKey<DamageType> key(String path) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path));
    }
}
