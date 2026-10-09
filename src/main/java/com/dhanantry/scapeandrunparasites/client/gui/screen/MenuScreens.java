package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Screens of the menus (the 1.12 GuiContainer classes registered by SRPGuiHandler). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class MenuScreens {
    private MenuScreens() {
    }

    @SubscribeEvent
    public static void register(RegisterMenuScreensEvent event) {
        event.register(SRPMenus.INFUSER_FURNACE.get(), ScreenInfuserFurnace::new);
        event.register(SRPMenus.PARASITE_LOOT.get(), ScreenParasiteLoot::new);
        event.register(SRPMenus.SCANNER.get(), ScreenScanner::new);
    }
}
