package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** {@code /srpcolonies}: the parasite colonies and the global adaptation. */
public class ColonyCommand extends ArgCommand {
    public ColonyCommand() {
        super("srpcolonies");
    }

    @Override
    protected List<String> words() {
        return List.of("viewall", "resetglobaladaptation", "viewallglobaladaptation", "clearworld", "setcolony", "removecolony");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (!SRPConfigWorld.coloniesActivated) {
            msg(src, "Colonies are not activated");
            return;
        }
        if (args.length == 0) {
            msg(src, "Invalid argument");
            return;
        }
        SRPWorldData data = SRPWorldData.get(world);
        switch (args[0]) {
            case "viewall" -> {
                ParasiteEventWorld.checkColonyStatus(world);
                ArrayList<Integer> xs = data.getColonies("x");
                ArrayList<Integer> ys = data.getColonies("y");
                ArrayList<Integer> zs = data.getColonies("z");
                ArrayList<Integer> as = data.getColonies("a");
                StringBuilder out = new StringBuilder("Current colonies in the world (x, y, z, points): \n");
                for (int i = 0; i < xs.size(); ++i) {
                    out.append("-> [").append(xs.get(i)).append(", ").append(ys.get(i)).append(", ").append(zs.get(i)).append(", ").append(as.get(i)).append("] \n");
                }
                msg(src, out.toString());
                return;
            }
            case "resetglobaladaptation" -> {
                data.resetGlobalAdaptation();
                msg(src, "Global adaptation has been reset");
                return;
            }
            case "viewallglobaladaptation" -> {
                ArrayList<Integer> a = data.getAdaptationI();
                ArrayList<String> b = data.getAdaptationS();
                StringBuilder out = new StringBuilder("Current global adaptation (Damage type, points): \n ");
                for (int i = 0; i < a.size(); ++i) {
                    out.append("-> [").append(b.get(i)).append(", ").append(a.get(i)).append("] \n");
                }
                msg(src, out.toString());
                return;
            }
            case "clearworld" -> {
                data.clearColonyList();
                msg(src, "There are no longer colonies in this world");
                return;
            }
            default -> {
            }
        }
        if (args.length != 4) {
            msg(src, "Invalid argument");
            return;
        }
        int x;
        int y;
        int z;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException nfe) {
            msg(src, "Invalid argument");
            return;
        }
        if (args[0].equals("setcolony")) {
            switch (ParasiteEventWorld.placeColonyInWorld(world, new BlockPos(x, y, z))) {
                case 1 -> msg(src, "Colony placed at " + x + " " + y + " " + z);
                case 2 -> msg(src, "Colonies cannot be placed in this dimension");
                case 3 -> msg(src, "Colonies are not activated");
                case 4 -> msg(src, "Evolution Phase or UD level is not high enough to place a Colony");
                case 5 -> msg(src, "Unable to find a place for the Colony ");
                case 6 -> msg(src, "Colony too close to another Colony ");
                case 7 -> msg(src, "Maximum number of Colonies reached ");
                default -> msg(src, "Unknown - Colonies ");
            }
            return;
        }
        if (args[0].equals("removecolony")) {
            if (ParasiteEventWorld.removeColonyInWorld(world, new BlockPos(x, y, z))) {
                msg(src, "Colony removed at " + x + " " + y + " " + z);
            } else {
                msg(src, "Colony cannot be removed at " + x + " " + y + " " + z);
            }
        }
    }
}
