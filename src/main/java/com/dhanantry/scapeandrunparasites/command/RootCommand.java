package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.Config;
import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/** {@code /srparasites}: generation, meteor, config reload and some switches. */
public class RootCommand extends ArgCommand {
    public RootCommand() {
        super("srparasites");
    }

    @Override
    protected List<String> words() {
        return List.of("setgeneration", "getgeneration", "spawnmeteor", "readconfigurationfile", "toggle_dotiledrops", "toggle_domobevolution", "resetdatafile", "parasites");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
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
                if (option > 5 || option < 0) {
                    msg(src, "Number too low or too high, please enter a number between 0 - 5");
                    return;
                }
                data.setGeneration((byte)option, id);
                data.setGenerationTime((int)world.getDayTime(), id);
                msg(src, "Parasite Generation set to " + option);
            }
            case "getgeneration" -> msg(src, "Current Parasite Generation " + data.getGeneration(id));
            case "spawnmeteor" -> {
                int x;
                int y;
                int z;
                int rad;
                try {
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                    rad = Integer.parseInt(args[4]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
                    msg(src, "Invalid/Missing argument");
                    return;
                }
                ParasiteSummon.spawnMeteor(x, y, z, rad, rad, world);
            }
            case "readconfigurationfile" -> {
                try {
                    Config.rebakeAll();
                    msg(src, "Configutarion files were read successfully \n NOTE: Does not work for all options, such as registry or client-side options");
                } catch (Exception e) {
                    ScapeAndRunParasites.LOGGER.error("Problem while reading configuration file", e);
                    msg(src, "There was a problem while reading configuration file, check inputs \n NOTE: Does not work for all options, such as registry or client-side options");
                }
            }
            case "toggle_dotiledrops" -> {
                SRPConfig.doTileDrops = !SRPConfig.doTileDrops;
                msg(src, "Current doTileDrop value is " + SRPConfig.doTileDrops);
            }
            case "toggle_domobevolution" -> {
                ParasiteEventEntity.canSpawnNext = !ParasiteEventEntity.canSpawnNext;
                msg(src, "Current doMobEvolution value is " + ParasiteEventEntity.canSpawnNext);
            }
            case "resetdatafile" -> {
                SRPWorldData.get(world).resetInstance(world);
                msg(src, "Data file of this dimension has been reset");
            }
            case "parasites" -> {
                int count = 0;
                for (Entity entity : world.getAllEntities()) {
                    if (entity instanceof EntityParasiteBase) {
                        ++count;
                    }
                }
                int players = world.players().size() * SRPConfig.worldMobCapPlusPlayer;
                String[] here = data.getCurrentCodeU(id).split(";");
                StringBuilder atm = new StringBuilder();
                for (int k = 0; k < here.length; ++k) {
                    atm.append(here[k]);
                    if (k != here.length - 1) {
                        atm.append(" ");
                    }
                }
                msg(src, " ====== \n -> Dislodgment code: \n" + atm + " \n -> Current Parasite Mob Cap: " + (SRPConfig.worldMobCap + players) + " \n -> Number of current parasites: " + count);
            }
            default -> {
            }
        }
    }
}
