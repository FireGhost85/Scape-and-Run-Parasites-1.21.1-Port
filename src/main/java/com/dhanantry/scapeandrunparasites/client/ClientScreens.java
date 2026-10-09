package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Opens the SRP screens. The screens themselves (phase report, vector map, dislodgement report, bestiary) are ported with
 * the rest of the GUI in the client milestone; until then the calls only log.
 */
final class ClientScreens {
    private ClientScreens() {}

    static void openPhaseReport(ItemStack stack) {
        ScapeAndRunParasites.LOGGER.debug("phase report screen: not ported yet");
    }

    static void openVectorMap(ItemStack stack) {
        ScapeAndRunParasites.LOGGER.debug("vector map screen: not ported yet");
    }

    static void openDislodgementReport(ItemStack stack) {
        ScapeAndRunParasites.LOGGER.debug("dislodgement report screen: not ported yet");
    }

    static void useFieldGuide(Player player) {
        com.dhanantry.scapeandrunparasites.client.gui.BestiaryScreen.open();
    }
}
