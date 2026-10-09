package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

/** {@code /srpnodes}: the Infestation Nodes (hearts). */
public class NodeCommand extends ArgCommand {
    public NodeCommand() {
        super("srpnodes");
    }

    @Override
    protected List<String> words() {
        return List.of("viewall", "clearworld", "setnode", "removenode");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (!SRPConfigWorld.nodesActivated) {
            msg(src, "Nodes are not activated");
            return;
        }
        if (!SRPConfigWorld.biomeRegster) {
            msg(src, "Biome is not activated");
            return;
        }
        if (args.length == 0) {
            msg(src, "Invalid argument");
            return;
        }
        if (args[0].equals("viewall")) {
            ParasiteEventWorld.checkNodeStatus(world);
            SRPWorldData data = SRPWorldData.get(world);
            ArrayList<Integer> xs = data.getNodes("x");
            ArrayList<Integer> ys = data.getNodes("y");
            ArrayList<Integer> zs = data.getNodes("z");
            ArrayList<Integer> as = data.getNodes("a");
            ArrayList<Integer> ts = data.getNodes("t");
            StringBuilder out = new StringBuilder("Current nodes in the world (x, y, z, age, type): \n");
            for (int i = 0; i < xs.size(); ++i) {
                out.append("-> [").append(xs.get(i)).append(", ").append(ys.get(i)).append(", ").append(zs.get(i)).append(", ").append(as.get(i)).append(", ").append(ts.get(i)).append("] \n");
            }
            msg(src, out.toString());
            return;
        }
        if (args[0].equals("clearworld")) {
            SRPWorldData.get(world).clearNodeList();
            msg(src, "There are no longer nodes in this world");
            return;
        }
        if (args.length != 5) {
            msg(src, "Invalid argument");
            return;
        }
        int x;
        int y;
        int z;
        int t;
        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            t = Integer.parseInt(args[4]);
        } catch (NumberFormatException nfe) {
            msg(src, "Invalid argument");
            return;
        }
        if (args[0].equals("setnode")) {
            BlockPos pos = new BlockPos(x, y, z);
            Biome biome = world.getBiome(pos).value();
            // 1.12: snow biome (warm -> 1, else 3), cold -> 4, warm without rain -> 2, otherwise 1
            int typeB = 1;
            boolean snowy = biome.coldEnoughToSnow(pos);
            boolean warm = biome.getBaseTemperature() >= 1.0f;
            if (biome.getPrecipitationAt(pos) == Biome.Precipitation.SNOW) {
                typeB = warm ? 1 : 3;
            } else if (biome.getBaseTemperature() < 0.2f) {
                typeB = 4;
            } else if (warm && biome.getPrecipitationAt(pos) == Biome.Precipitation.NONE) {
                typeB = 2;
            }
            int key = ParasiteEventWorld.placeHeartInWorld(world, pos, t == -1 || t > 4 ? typeB : t);
            switch (key) {
                case 1 -> msg(src, "Node placed at " + x + " " + y + " " + z);
                case 2 -> msg(src, "Nodes cannot be placed in this dimension");
                case 3 -> msg(src, "Nodes are not activated");
                case 4 -> msg(src, "Biome is not activated");
                case 5 -> msg(src, "Node is too close to spawn point");
                case 6 -> msg(src, "Evolution Phase or UD level is not high enough to place a Node ");
                case 7 -> msg(src, "Unable to find a place for the Node");
                case 8 -> msg(src, "Node is too close to another Node");
                case 9 -> msg(src, "Maximum number of Nodes reached");
                default -> msg(src, "Unknown - Nodes " + key);
            }
            return;
        }
        if (args[0].equals("removenode")) {
            if (ParasiteEventWorld.removeHeartInWorld(world, new BlockPos(x, y, z))) {
                msg(src, "Node removed at " + x + " " + y + " " + z);
            } else {
                msg(src, "Node cannot be removed at " + x + " " + y + " " + z);
            }
        }
    }
}
