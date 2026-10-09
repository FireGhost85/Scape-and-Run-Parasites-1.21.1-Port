package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The creative tab of the original (one tab, icon {@code itembase}); it lists every item and block item of the mod. */
public final class SRPCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ScapeAndRunParasites.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("srparasites", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.srparasites"))
            .icon(() -> new ItemStack(SRPItems.itembase.get()))
            .displayItems((parameters, output) -> {
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof net.neoforged.neoforge.common.DeferredSpawnEggItem && !com.dhanantry.scapeandrunparasites.config.SRPConfig.vanillaEggs) {
                        continue;
                    }
                    if (ScapeAndRunParasites.MODID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace())) {
                        output.accept(item);
                    }
                }
            })
            .build());

    private SRPCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
