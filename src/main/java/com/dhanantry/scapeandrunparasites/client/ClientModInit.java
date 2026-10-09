package com.dhanantry.scapeandrunparasites.client;

import net.neoforged.fml.ModContainer;
import com.dhanantry.scapeandrunparasites.client.gui.screen.SRPConfigSectionScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client only: the config button of the NeoForge mods screen opens the in-game editor of the four SRP config files. */
public final class ClientModInit {
    private ClientModInit() {
    }

    public static void registerConfigScreen(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, SRPConfigSectionScreen::create);
    }
}
