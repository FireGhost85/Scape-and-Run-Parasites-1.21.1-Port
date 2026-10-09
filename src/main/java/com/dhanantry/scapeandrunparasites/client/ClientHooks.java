package com.dhanantry.scapeandrunparasites.client;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Client-only entry points that common code (items, entities) calls on the logical client. The screens and shaders behind
 * them belong to the client milestone (M8); the state is kept here so the renderers / screens can read it.
 */
public final class ClientHooks {
    /** Ticks left of the "breathe" screen effect (alveolar fluid / dead blood). */
    public static int breatheTicks = 0;

    private ClientHooks() {}

    public static void enableBreathe(int ticks) {
        breatheTicks = Math.max(breatheTicks, ticks);
        com.dhanantry.scapeandrunparasites.client.shader.BreatheShaderManager.enableFor(ticks);
    }

    /** The local player (client only), or null. */
    public static Player localPlayer() {
        return net.minecraft.client.Minecraft.getInstance().player;
    }

    public static void openPhaseReport(ItemStack stack) {
        ClientScreens.openPhaseReport(stack);
    }

    public static void openVectorMap(ItemStack stack) {
        ClientScreens.openVectorMap(stack);
    }

    public static void openDislodgementReport(ItemStack stack) {
        ClientScreens.openDislodgementReport(stack);
    }

    public static void useFieldGuide(Player player) {
        ClientScreens.useFieldGuide(player);
    }
}
