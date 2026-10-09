package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

/** {@code /srpdislodgment}: the dislodgment codes of the current dimension. */
public class DislodgmentCommand extends ArgCommand {
    public DislodgmentCommand() {
        super("srpdislodgment");
    }

    @Override
    protected List<String> words() {
        return List.of("codes_reset", "random_code", "set_code");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (args.length == 0) {
            msg(src, "Invalid argument");
            return;
        }
        String id = DimKeys.of(world);
        SRPSaveData data = SRPSaveData.get(world);
        if (args[0].equals("codes_reset")) {
            data.reduceCodesCooldown(id, 1000000000, world);
            msg(src, "Dislodgment codes back to 0");
            return;
        }
        if (args[0].equals("random_code")) {
            int x;
            try {
                x = Integer.parseInt(args[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException nfe) {
                msg(src, "Invalid argument, please enter: duration");
                return;
            }
            for (int tries = 10; tries > 0; --tries) {
                int code = world.random.nextInt(30);
                int val = world.random.nextInt(6) + 1;
                if (data.setCurrentCode(id, code, val, x, world, true, 0)) {
                    msg(src, "Dislodgment Code: " + code + " Value: " + val + " Duration: " + x);
                    return;
                }
            }
            msg(src, "Dislo Error");
            return;
        }
        if (args[0].equals("set_code")) {
            if (args.length != 4) {
                msg(src, "Invalid argument, please enter: duration - code - value");
                return;
            }
            int x;
            int code;
            int val;
            try {
                x = Integer.parseInt(args[1]);
                code = Integer.parseInt(args[2]);
                val = Integer.parseInt(args[3]);
            } catch (NumberFormatException nfe) {
                msg(src, "Invalid argument, please enter: duration - code - value");
                return;
            }
            if (data.setCurrentCode(id, code, val, x, world, true, 0)) {
                msg(src, "Dislodgment Code: " + code + " Value: " + val + " Duration: " + x);
            } else {
                msg(src, "Dislodgment Code " + code + " in use, in cooldown or invalid args, please enter: duration - code - value");
            }
            return;
        }
        msg(src, "Invalid command");
    }
}
