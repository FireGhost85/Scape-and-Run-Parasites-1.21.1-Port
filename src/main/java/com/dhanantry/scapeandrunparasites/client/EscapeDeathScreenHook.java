package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.network.RequestEscapePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * GuiGameOverEscape of 1.12: when the server offers the escape ({@link EscapeClientState#OFFER}) the death screen gets one more
 * button under the others; it asks the server for the escape and respawns. Added to the vanilla {@link DeathScreen} instead of
 * replacing it with a subclass.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class EscapeDeathScreenHook {
    private EscapeDeathScreenHook() {}

    @SubscribeEvent
    static void onInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof DeathScreen screen) || !EscapeClientState.OFFER) {
            return;
        }
        int min = Math.max(0, SRPConfigWorld.escapeMinDistance);
        int max = Math.max(min, SRPConfigWorld.escapeMaxDistance);
        Component label = Component.translatable("gui.srparasites.escape_button").append(min + "-" + max + " ").append(Component.translatable("gui.srparasites.escape_button_blocks"));
        int bottom = 0;
        for (GuiEventListener child : screen.children()) {
            if (child instanceof AbstractWidget widget) {
                bottom = Math.max(bottom, widget.getY() + widget.getHeight());
            }
        }
        Button button = Button.builder(label, b -> {
            PacketDistributor.sendToServer(new RequestEscapePayload());
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.respawn();
            }
            mc.setScreen(null);
        }).bounds(screen.width / 2 - 100, bottom + 6, 200, 20).tooltip(net.minecraft.client.gui.components.Tooltip.create(Component.translatable("gui.srparasites.escape_tooltip"))).build();
        event.addListener(button);
    }
}
