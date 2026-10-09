package com.dhanantry.scapeandrunparasites.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Opens the SRP screens (phase report, vector map, dislodgement report, compendium).
 */
final class ClientScreens {
    private ClientScreens() {}

    static void openPhaseReport(ItemStack stack) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiPhaseReport(stack));
    }

    static void openVectorMap(ItemStack stack) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.dhanantry.scapeandrunparasites.client.gui.GuiVectorMapReport(stack));
    }

    static void openDislodgementReport(ItemStack stack) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.dhanantry.scapeandrunparasites.client.gui.GuiDislodgementReport(stack));
    }

    static void useFieldGuide(Player player) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiBestiary(net.minecraft.client.Minecraft.getInstance().player));
    }
}
