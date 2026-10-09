package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

/** {@code /srpudevelopment}: the Ubiquitous Development level and the progress of every dimension. */
public class UDevelopmentCommand extends ArgCommand {
    public UDevelopmentCommand() {
        super("srpudevelopment");
    }

    @Override
    protected List<String> words() {
        return List.of("getlevel", "setlevel", "viewalldims", "setdimevolution");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (!SRPConfigSystems.useEvolution) {
            msg(src, "Evolution levels are not active");
            return;
        }
        if (args.length == 0) {
            msg(src, "Invalid argument");
            return;
        }
        SRPSaveData data = SRPSaveData.get(world);
        switch (args[0]) {
            case "getlevel" -> msg(src, " ======> \n -> Current Ubiquitous Development Level: " + data.getDeveLevel());
            case "setlevel" -> {
                int option;
                try {
                    option = Integer.parseInt(args[1]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
                    msg(src, "Invalid arg");
                    return;
                }
                if (option >= 5) {
                    msg(src, "Invalid argument: level too high");
                    return;
                }
                if (option <= -1) {
                    msg(src, "Invalid argument: level too low");
                    return;
                }
                SRPSaveData.falseLevel = option;
                msg(src, "Changed Ubiquitous Development Level to " + args[1]);
                msg(src, "Set this value to 0 to remove fake level");
            }
            case "viewalldims" -> {
                List<String> dims = data.getDimensionKeys();
                List<Integer> phases = data.getPhasesList();
                List<Integer> kills = data.getKillsList();
                List<Integer> gen = data.getGenerationsList();
                StringBuilder out = new StringBuilder("Current Parasite progress in dimensions \n (id, phase, points, generation): \n");
                for (int i = 0; i < dims.size(); ++i) {
                    out.append("-> [").append(dims.get(i)).append(", ").append(phases.get(i)).append(", ").append(kills.get(i)).append(", ").append(gen.get(i)).append("] \n");
                }
                msg(src, out.toString());
            }
            case "setdimevolution" -> this.setDimEvolution(src, world, data, args);
            default -> msg(src, "Invalid command");
        }
    }

    private void setDimEvolution(CommandSourceStack src, ServerLevel world, SRPSaveData data, String[] args) {
        int phase;
        String id;
        try {
            id = DimKeys.normalize(args[1]);
            phase = Integer.parseInt(args[2]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
            tr(src, "command.srpevolution.error.invalid_arg");
            return;
        }
        if (phase >= 11) {
            tr(src, "command.srpevolution.setphase.error.phase_too_high");
            return;
        }
        if (phase <= -3) {
            tr(src, "command.srpevolution.setphase.error.phase_too_low");
            return;
        }
        if (phase == -1) {
            data.setEvolutionPhase(id, (byte)phase, true, world);
            data.setTotalKills(id, 100 * phase, false, world, true, 50);
        } else if (phase == -2) {
            data.setTotalKills(id, 100 * phase, false, world, true, 51);
            data.setEvolutionPhase(id, (byte)phase, true, world);
        } else {
            data.setEvolutionPhase(id, (byte)phase, true, world);
        }
        if (args.length == 3) {
            tr(src, "command.srpevolution.getphase.dimension", args[1]);
            tr(src, "command.srpevolution.setphase.changed_phase", args[2]);
            return;
        }
        int gen;
        try {
            gen = Integer.parseInt(args[3]);
        } catch (NumberFormatException nfe) {
            tr(src, "command.srpevolution.setphase.error.invalid_arg_evo");
            return;
        }
        if (gen >= 6) {
            tr(src, "command.srpevolution.setphase.changed_phase", args[3]);
            tr(src, "command.srpevolution.setphase.error.generation_too_high");
            return;
        }
        if (gen <= -1) {
            tr(src, "command.srpevolution.setphase.changed_phase", args[3]);
            tr(src, "command.srpevolution.setphase.error.generation_too_low");
            return;
        }
        data.setGeneration((byte)gen, id);
        data.setGenerationTime(0, id);
        tr(src, "command.srpevolution.getphase.dimension", args[1]);
        tr(src, "command.srpevolution.setphase.changed_phase", args[2]);
        tr(src, "command.srpevolution.setphase.changed_generation", args[3]);
    }
}
