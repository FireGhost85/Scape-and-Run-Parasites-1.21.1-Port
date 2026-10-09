package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection1;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** {@code /srp_summon_nidus [x y z [stage]]}: generates the Nidus/Nexus protection structure. */
public class SummonNidusCommand extends ArgCommand {
    public SummonNidusCommand() {
        super("srp_summon_nidus");
    }

    @Override
    protected List<String> words() {
        return List.of("~ ~ ~", "~ ~ ~ 1", "~ ~ ~ 2", "~ ~ ~ 3", "~ ~ ~ 4");
    }

    private static int coord(String token, double base) {
        if (token.startsWith("~")) {
            return (int)Math.floor(base + (token.length() > 1 ? Double.parseDouble(token.substring(1)) : 0.0));
        }
        return (int)Math.floor(Double.parseDouble(token));
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        int stage = 1;
        BlockPos pos;
        try {
            if (args.length == 0) {
                pos = BlockPos.containing(src.getPosition());
            } else if (args.length == 3 || args.length == 4) {
                pos = new BlockPos(coord(args[0], src.getPosition().x), coord(args[1], src.getPosition().y), coord(args[2], src.getPosition().z));
                if (args.length == 4) {
                    stage = Integer.parseInt(args[3]);
                }
            } else {
                msg(src, "Usage: srp_summon_nidus [x y z [stage]]");
                return;
            }
        } catch (NumberFormatException e) {
            msg(src, "Invalid number");
            return;
        }
        boolean generated = new WorldGenParasiteNexusProtection1(false, stage).generate(world, world.random, pos.below());
        msg(src, generated ? "Generated Nidus/Nexus protection structure at " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " with stage " + stage : "Could not generate the Nidus/Nexus protection structure here");
    }
}
