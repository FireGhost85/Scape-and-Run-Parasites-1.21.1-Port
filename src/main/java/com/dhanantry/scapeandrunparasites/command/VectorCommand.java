package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** {@code /srpvectors}: the Emerging Infestation Vectors. */
public class VectorCommand extends ArgCommand {
    public VectorCommand() {
        super("srpvectors");
    }

    @Override
    protected List<String> words() {
        return List.of("viewall", "clearworld", "setvector", "removevector");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (!SRPConfigWorld.originActivated) {
            msg(src, "Emerging Infestation Vector are not activated");
            return;
        }
        if (args.length == 0) {
            msg(src, "Invalid argument");
            return;
        }
        if (args[0].equals("viewall")) {
            SRPWorldData data = SRPWorldData.get(world);
            ArrayList<Integer> xs = data.getorigins("x");
            ArrayList<Integer> ys = data.getorigins("y");
            ArrayList<Integer> zs = data.getorigins("z");
            ArrayList<Integer> as = data.getorigins("a");
            ArrayList<Integer> hs = data.getorigins("h");
            StringBuilder out = new StringBuilder("Current Emerging Infestation Vectors in the world (x, y, z, health, radius): \n");
            for (int i = 0; i < xs.size(); ++i) {
                out.append("-> [").append(xs.get(i)).append(", ").append(ys.get(i)).append(", ").append(zs.get(i)).append(", ").append(hs.get(i)).append(", ").append(as.get(i)).append("] \n");
            }
            msg(src, out.toString());
            return;
        }
        if (args[0].equals("clearworld")) {
            SRPWorldData.get(world).clearOriginList();
            msg(src, "There are no longer Emerging Infestation Vectors in this world");
            return;
        }
        // the original also demanded the health and radius words for removevector; accept the short form too
        boolean shortRemove = args.length == 4 && args[0].equals("removevector");
        if (args.length != 6 && !shortRemove) {
            msg(src, "Invalid argument.");
            msg(src, "To set an Emerging Infestation Vector type [x y z health radius].");
            return;
        }
        int x;
        int y;
        int z;
        int h;
        int a;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            h = shortRemove ? 0 : Integer.parseInt(args[4]);
            a = shortRemove ? 0 : Integer.parseInt(args[5]);
        } catch (NumberFormatException nfe) {
            msg(src, "Invalid arguments");
            return;
        }
        if (args[0].equals("setvector")) {
            switch (ParasiteEventWorld.placeOriginInWorld(world, new BlockPos(x, y, z), h, a)) {
                case 1 -> msg(src, "Emerging Infestation Vector placed at " + x + " " + y + " " + z + " with " + h + " health and " + a + " radius");
                case 2 -> msg(src, "Emerging Infestation Vector type Outbreak placed at " + x + " " + y + " " + z + " with " + h + " health and " + a + " radius");
                case 3 -> msg(src, "Emerging Infestation Vectors are not activated");
                case 6 -> msg(src, "Emerging Infestation Vector too close to another EIV ");
                case 7 -> msg(src, "Maximum number of Emerging Infestation Vector reached ");
                default -> msg(src, "Unknown - EIV ");
            }
            return;
        }
        if (args[0].equals("removevector")) {
            if (ParasiteEventWorld.removeOriginInWorld(world, new BlockPos(x, y, z))) {
                msg(src, "Emerging Infestation Vector removed at " + x + " " + y + " " + z);
            } else {
                msg(src, "Emerging Infestation Vector cannot be removed at " + x + " " + y + " " + z);
            }
        }
    }
}
