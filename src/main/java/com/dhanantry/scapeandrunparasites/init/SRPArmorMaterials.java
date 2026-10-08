package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Armor materials (EnumHelper.addArmorMaterial of 1.12): the layer names are the 1.12 texture names
 * ({@code textures/models/armor/<name>_layer_1.png}). The durability multipliers are exposed separately because 1.21 takes
 * the durability from the item properties ({@link #durability(ArmorItem.Type, int)}).
 */
public final class SRPArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, ScapeAndRunParasites.MODID);

    private SRPArmorMaterials() {}

    public static void register(IEventBus bus) {
        MATERIALS.register(bus);
    }

    private static Map<ArmorItem.Type, Integer> defense(int boots, int legs, int chest, int helm) {
        Map<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, boots);
        map.put(ArmorItem.Type.LEGGINGS, legs);
        map.put(ArmorItem.Type.CHESTPLATE, chest);
        map.put(ArmorItem.Type.HELMET, helm);
        map.put(ArmorItem.Type.BODY, chest);
        return map;
    }

    private static Holder<ArmorMaterial> reg(String name, Map<ArmorItem.Type, Integer> defense, int enchantability, Holder<net.minecraft.sounds.SoundEvent> sound, float toughness) {
        return MATERIALS.register(name, () -> new ArmorMaterial(defense, enchantability, sound, () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, name))), toughness, 0.0f));
    }

    /** Living armor (config driven, layer "livings"). */
    public static final Holder<ArmorMaterial> LIVING = MATERIALS.register("livings", () -> new ArmorMaterial(
            defense(SRPConfig.livingBoots, SRPConfig.livingLegs, SRPConfig.livingChest, SRPConfig.livingHelm), 1, SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "livings"))),
            SRPConfig.livingToughness, 0.0f));
    /** Sentient armor (config driven, layer "sentients"). */
    public static final Holder<ArmorMaterial> SENTIENT = MATERIALS.register("sentients", () -> new ArmorMaterial(
            defense(SRPConfig.sentientBoots, SRPConfig.sentientLegs, SRPConfig.sentientChest, SRPConfig.sentientHelm), 1, SoundEvents.ARMOR_EQUIP_DIAMOND,
            () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "sentients"))),
            SRPConfig.sentientToughness, 0.0f));
    public static final Holder<ArmorMaterial> MOBILITY = reg("mobility_armor", defense(3, 6, 7, 3), 18, SoundEvents.ARMOR_EQUIP_LEATHER, 1.0f);
    public static final Holder<ArmorMaterial> VENKROL_BOOT = reg("venkrol_boot", defense(4, 7, 9, 4), 20, SoundEvents.ARMOR_EQUIP_DIAMOND, 3.0f);
    public static final Holder<ArmorMaterial> HIJACKED = MATERIALS.register("hijacked_iron", () -> new ArmorMaterial(defense(4, 7, 9, 4), 15,
            SRPSounds.FLESH_GROW, () -> Ingredient.EMPTY, List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "hijacked_iron"))), 3.0f, 0.0f));

    /** The durability of an armor piece: the per-slot base of the vanilla armors times the 1.12 multiplier. */
    public static int durability(ArmorItem.Type type, int multiplier) {
        return type.getDurability(multiplier);
    }

    public static int livingMultiplier() {
        return SRPConfig.livingDura;
    }

    public static int sentientMultiplier() {
        return SRPConfig.sentienDura;
    }
}
