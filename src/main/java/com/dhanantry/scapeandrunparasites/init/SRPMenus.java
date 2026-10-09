package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.container.ContainerInfuserFurnace;
import com.dhanantry.scapeandrunparasites.container.ContainerParasiteLoot;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Menu types (the 1.12 GUI ids of {@code SRPGuiHandler}). Screens are registered by the client. */
public final class SRPMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ScapeAndRunParasites.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerInfuserFurnace>> INFUSER_FURNACE = MENUS.register("infuser_furnace",
            () -> new MenuType<>(ContainerInfuserFurnace::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<ContainerParasiteLoot>> PARASITE_LOOT = MENUS.register("parasite_loot",
            () -> new MenuType<>(ContainerParasiteLoot::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<com.dhanantry.scapeandrunparasites.container.ScannerContainer>> SCANNER = MENUS.register("scanner",
            () -> net.neoforged.neoforge.common.extensions.IMenuTypeExtension.create(com.dhanantry.scapeandrunparasites.container.ScannerContainer::new));

    private SRPMenus() {
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
