package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

/** {@code /srpgeneration}: the parasite generation of the current dimension. */
public class GenerationCommand extends ArgCommand {
    public GenerationCommand() {
        super("srpgeneration");
    }

    @Override
    protected List<String> words() {
        return List.of("setgeneration", "getgeneration", "addticks");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (!SRPConfigSystems.generationUse) {
            msg(src, "Generation is not active");
            return;
        }
        if (args.length == 0) {
            msg(src, "Invalid argument");
            return;
        }
        String id = DimKeys.of(world);
        SRPSaveData data = SRPSaveData.get(world);
        switch (args[0]) {
            case "setgeneration" -> {
                int option;
                try {
                    option = Integer.parseInt(args[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
                    msg(src, "Invalid arg");
                    return;
                }
                if (option >= 6) {
                    msg(src, "Invalid argument: generation too high");
                    return;
                }
                if (option <= -1) {
                    msg(src, "Invalid argument: generation too low");
                    return;
                }
                data.setGeneration((byte)option, id);
                data.setGenerationTime(0, id);
                msg(src, "Changed Generation of Parasites to " + args[1]);
            }
            case "getgeneration" -> msg(src, "Current Generation of Parasites: " + data.getGeneration(id));
            case "addticks" -> {
                int option;
                try {
                    option = Integer.parseInt(args[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
                    msg(src, "Invalid arg");
                    return;
                }
                data.setGenerationTime(data.getGenerationTime(id) + option, id);
                msg(src, "Added: " + option + " to the current Time");
            }
            default -> msg(src, "Invalid command");
        }
    }
}
