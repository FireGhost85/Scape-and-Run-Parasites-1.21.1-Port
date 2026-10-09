package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The vanilla style spawn eggs of the 1.12 mod ({@code EntityEntryBuilder.egg(primary, secondary)}, colours in
 * {@link SRPEntities#EGG_COLORS}). The original only gave the entities an egg when the config switch {@code vanillaEggs} was
 * on, so the eggs are registered always but only listed in the creative tab with that switch (see {@code SRPCreativeTabs}).
 */
public final class SRPSpawnEggs {
    public static final DeferredRegister.Items EGGS = DeferredRegister.createItems(ScapeAndRunParasites.MODID);

    private SRPSpawnEggs() {}

    @SuppressWarnings("unchecked")
    public static void register(IEventBus bus) {
        SRPEntities.EGG_COLORS.forEach((name, colors) -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, name);
            EGGS.register(name + "_spawn_egg", () -> new DeferredSpawnEggItem(() -> (EntityType<? extends Mob>) BuiltInRegistries.ENTITY_TYPE.get(id), colors[0], colors[1], new Item.Properties()));
        });
        EGGS.register(bus);
    }
}
