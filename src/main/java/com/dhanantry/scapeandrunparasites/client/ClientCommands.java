package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiBestiary;
import com.mojang.brigadier.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/** [ADD] /srpcompendium: opens the compendium without the field guide (dev convenience). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientCommands {
    private ClientCommands() {}

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("srpcompendium").executes(ctx -> {
            Minecraft mc = Minecraft.getInstance();
            mc.tell(() -> mc.setScreen(new GuiBestiary(mc.player)));
            return Command.SINGLE_SUCCESS;
        }));
    }
}
