package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** {@code /srpevolution}: evolution phase, points, cooldown, locks and the loss/gain switches of the current dimension. */
public class EvolutionCommand extends ArgCommand {
    public EvolutionCommand() {
        super("srpevolution");
    }

    @Override
    protected List<String> words() {
        return List.of("evolutionlock_reset", "evolutionlock_unlockall", "evolutionlock_getlist", "toggle_evolutiongaining", "toggle_evolutionloss",
                "addpoints", "setcooldown", "getphase", "setphase", "addcooldown", "set_evolutionloss", "set_evolutiongaining");
    }

    private static Integer parse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (!SRPConfigSystems.useEvolution) {
            tr(src, "command.srpevolution.error.evolution_disabled");
            return;
        }
        if (args.length == 0) {
            tr(src, "command.srpevolution.error.invalid_argument");
            return;
        }
        String id = DimKeys.of(world);
        SRPSaveData data = SRPSaveData.get(world);
        switch (args[0]) {
            case "set_evolutionloss" -> {
                if (args.length < 2) {
                    tr(src, "command.srpevolution.error.invalid_arg");
                } else if (args[1].equals("true")) {
                    data.setLoss(true, id);
                    tr(src, "command.srpevolution.evolutionloss.set.true");
                } else if (args[1].equals("false")) {
                    data.setLoss(false, id);
                    tr(src, "command.srpevolution.evolutionloss.set.false");
                } else {
                    tr(src, "command.srpevolution.error.invalid_arg");
                }
            }
            case "set_evolutiongaining" -> {
                if (args.length < 2) {
                    tr(src, "command.srpevolution.error.invalid_arg");
                } else if (args[1].equals("true")) {
                    data.setGaining(true, id);
                    tr(src, "command.srpevolution.evolutiongaining.set.true");
                } else if (args[1].equals("false")) {
                    data.setGaining(false, id);
                    tr(src, "command.srpevolution.evolutiongaining.set.false");
                } else {
                    tr(src, "command.srpevolution.error.invalid_arg");
                }
            }
            case "getphase" -> this.getPhase(src, world, data, id);
            case "evolutionlock_reset" -> {
                data.resetLock();
                tr(src, "command.srpevolution.evolutionlock.reset");
            }
            case "evolutionlock_unlockall" -> {
                data.unlockAllParasite();
                tr(src, "command.srpevolution.evolutionlock.unlockall");
            }
            case "evolutionlock_getlist" -> {
                StringBuilder sb = new StringBuilder();
                for (Integer x : data.getLockedList()) {
                    sb.append(x).append(" ");
                }
                tr(src, "command.srpevolution.evolutionlock.getlist", sb.toString());
            }
            case "addpoints" -> this.addPoints(src, world, data, id, args);
            case "setcooldown", "addcooldown" -> {
                Integer option = args.length > 1 ? parse(args[1]) : null;
                if (option == null) {
                    tr(src, "command.srpevolution.error.invalid_arg");
                    return;
                }
                if (option < 0) {
                    tr(src, "command.srpevolution.cooldown.error.negative");
                    return;
                }
                tr(src, "command.srpevolution.cooldown.set", option);
                data.setCooldown(option, world, id, args[0].equals("addcooldown"));
            }
            case "setphase" -> this.setPhase(src, world, data, id, args);
            default -> tr(src, "command.srpevolution.error.invalid_command");
        }
    }

    private void addPoints(CommandSourceStack src, ServerLevel world, SRPSaveData data, String id, String[] args) {
        Integer option = args.length > 1 ? parse(args[1]) : null;
        if (option == null) {
            tr(src, "command.srpevolution.error.invalid_arg");
            return;
        }
        if (data.getEvolutionPhase(id) == -2) {
            tr(src, "command.srpevolution.addpoints.error.phase_minus2");
            return;
        }
        if (data.getCooldown(world, id) != 0) {
            tr(src, "command.srpevolution.addpoints.error.cooldown");
            return;
        }
        if (!data.getCanGain(id) && option > 0) {
            tr(src, "command.srpevolution.addpoints.error.cannot_gain");
            return;
        }
        if (!data.getCanLoss(id) && option < 0) {
            tr(src, "command.srpevolution.addpoints.error.cannot_lose");
            return;
        }
        boolean ok = data.setTotalKills(id, option, true, world, true, true, 47);
        if (ok) {
            tr(src, Math.abs(option) == 1 ? "command.srpevolution.addpoints.success.one" : "command.srpevolution.addpoints.success.many", option);
            return;
        }
        tr(src, "command.srpevolution.error.generic");
    }

    private void setPhase(CommandSourceStack src, ServerLevel world, SRPSaveData data, String id, String[] args) {
        Integer option = args.length > 1 ? parse(args[1]) : null;
        if (option == null) {
            tr(src, "command.srpevolution.error.invalid_arg");
            return;
        }
        if (option >= 11) {
            tr(src, "command.srpevolution.setphase.error.phase_too_high");
            return;
        }
        if (option <= -3) {
            tr(src, "command.srpevolution.setphase.error.phase_too_low");
            return;
        }
        if (option == -1) {
            data.setEvolutionPhase(id, (byte)(int)option, true, world);
            data.setTotalKills(id, 100 * option, false, world, true, 48);
        } else if (option == -2) {
            data.setTotalKills(id, 100 * option, false, world, true, 49);
            data.setEvolutionPhase(id, (byte)(int)option, true, world);
        } else {
            data.setEvolutionPhase(id, (byte)(int)option, true, world);
        }
        if (args.length == 2) {
            tr(src, "command.srpevolution.setphase.changed_phase", args[1]);
            return;
        }
        Integer gen = parse(args[2]);
        if (gen == null) {
            tr(src, "command.srpevolution.setphase.error.invalid_arg_evo");
            return;
        }
        if (gen >= 6) {
            tr(src, "command.srpevolution.setphase.changed_phase", args[1]);
            tr(src, "command.srpevolution.setphase.error.generation_too_high");
            return;
        }
        if (gen <= -1) {
            tr(src, "command.srpevolution.setphase.changed_phase", args[1]);
            tr(src, "command.srpevolution.setphase.error.generation_too_low");
            return;
        }
        data.setGeneration((byte)(int)gen, id);
        data.setGenerationTime(0, id);
        tr(src, "command.srpevolution.setphase.changed_phase", args[1]);
        tr(src, "command.srpevolution.setphase.changed_generation", args[2]);
    }

    private void getPhase(CommandSourceStack src, ServerLevel world, SRPSaveData data, String id) {
        int count = 0;
        int coth = 0;
        for (Entity entity : world.getAllEntities()) {
            if (entity instanceof EntityParasiteBase) {
                if (entity instanceof EntityCanHaveBodies bodies) {
                    if (bodies.getBodyNumber() == 0) {
                        ++count;
                    }
                } else {
                    ++count;
                }
            }
            if (entity instanceof LivingEntity living && living.hasEffect(SRPPotions.COTH_E)) {
                ++coth;
            }
        }
        int players = world.players().size() * SRPConfig.worldMobCapPlusPlayer;
        ArrayList<Integer> halo = SRPWorldData.get(world).getorigins("x");
        String[] here = data.getCurrentCodeU(id).split(";");
        StringBuilder atm = new StringBuilder();
        for (int k = 0; k < here.length; ++k) {
            atm.append(here[k]);
            if (k != here.length - 1) {
                atm.append(" ");
            }
        }
        int needed = getNeededPoints((byte)(data.getEvolutionPhase(id) + 1));
        tr(src, "command.srpevolution.getphase.header");
        tr(src, "command.srpevolution.getphase.dimension", id);
        tr(src, "command.srpevolution.getphase.phase", data.getEvolutionPhase(id));
        tr(src, "command.srpevolution.getphase.total_points", data.getTotalKills(id));
        tr(src, "command.srpevolution.getphase.points_next", needed);
        double progress = (double)data.getTotalKills(id) / (double)needed * 100.0;
        tr(src, "command.srpevolution.getphase.progress", String.format(Locale.ROOT, "%.1f", progress));
        tr(src, "command.srpevolution.getphase.cooldown", data.getCooldown(world, id));
        tr(src, "command.srpevolution.getphase.gaining", String.valueOf(data.getCanGain(id)));
        tr(src, "command.srpevolution.getphase.loss", String.valueOf(data.getCanLoss(id)));
        tr(src, "command.srpevolution.getphase.dislodgement");
        tr(src, "command.srpevolution.getphase.dislodgement.value", atm.toString());
        tr(src, "command.srpevolution.getphase.mobcap", SRPConfig.worldMobCap + players);
        tr(src, "command.srpevolution.getphase.generation", data.getGeneration(id));
        tr(src, "command.srpevolution.getphase.gen_ticks", data.getGenerationNeededTime(world, id));
        tr(src, "command.srpevolution.getphase.udl", data.getDeveLevel());
        tr(src, "command.srpevolution.getphase.parasite_count", count);
        tr(src, "command.srpevolution.getphase.coth_count", coth);
        tr(src, "command.srpevolution.getphase.eivs", halo == null ? 0 : halo.size());
        if (src.getEntity() instanceof Player player) {
            SRPWorldData worldData = SRPWorldData.get(world);
            BlockPos pos = worldData.nearestInfectionPosition(false, player.blockPosition());
            boolean within = pos != null;
            if (within) {
                msg(src, " -> WITHIN VECTOR: " + within);
            } else if (halo != null && halo.size() > 0) {
                pos = worldData.nearestInfectionPosition(true, player.blockPosition());
                if (pos != null) {
                    int dis = (int)Math.sqrt(player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()));
                    int area = worldData.nearestInfectionValueArea(player.blockPosition(), false);
                    msg(src, " -> WITHIN VECTOR: " + within);
                    msg(src, " -> Distance to nearest EIV: " + (dis - area));
                }
            }
        }
        tr(src, "command.srpevolution.getphase.footer");
    }

    public static int getNeededPoints(byte in) {
        return switch (in) {
            case 1 -> SRPConfigSystems.phaseKillsOne;
            case 2 -> SRPConfigSystems.phaseKillsTwo;
            case 3 -> SRPConfigSystems.phaseKillsThree;
            case 4 -> SRPConfigSystems.phaseKillsFour;
            case 5 -> SRPConfigSystems.phaseKillsFive;
            case 6 -> SRPConfigSystems.phaseKillsSix;
            case 7 -> SRPConfigSystems.phaseKillsSeven;
            case 8 -> SRPConfigSystems.phaseKillsEight;
            case 9 -> SRPConfigSystems.phaseKillsNine;
            case 10 -> SRPConfigSystems.phaseKillsTen;
            default -> 0;
        };
    }
}
