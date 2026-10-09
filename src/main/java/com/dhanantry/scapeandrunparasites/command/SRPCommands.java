package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import java.util.List;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Registers the commands of the mod (the 18 of {@code CommonProxy.serverInit} of 1.12). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPCommands {
    private SRPCommands() {}

    @SubscribeEvent
    static void onRegister(RegisterCommandsEvent event) {
        List<ArgCommand> commands = List.of(
                new EvolutionCommand(),
                new VectorCommand(),
                new NodeCommand(),
                new ColonyCommand(),
                new RootCommand(),
                new DislodgmentCommand(),
                new GenerationCommand(),
                new UDevelopmentCommand(),
                new SummonNidusCommand(),
                new HelpCommand());
        for (ArgCommand command : commands) {
            command.register(event.getDispatcher());
        }
    }
}
