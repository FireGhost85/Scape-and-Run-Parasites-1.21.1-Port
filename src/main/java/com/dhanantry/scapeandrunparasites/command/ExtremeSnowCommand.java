package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.network.ExtremeSnowNetwork;
import com.dhanantry.scapeandrunparasites.world.ExtremeSnowData;
import java.util.List;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

/** {@code /srp_extremesnow <on|off|toggle> [intensity 0..1] [anywhere 0|1] [windDeg 0..359] [windSpeed 0..1]} (CommandExtremeSnow of 1.10.9). */
public class ExtremeSnowCommand extends ArgCommand {
    private static final String USAGE = "/srp_extremesnow <on|off|toggle> [intensity 0..1] [anywhere 0|1] [windDeg 0..359] [windSpeed 0..1]";

    public ExtremeSnowCommand() {
        super("srp_extremesnow");
    }

    @Override
    protected List<String> words() {
        return List.of("on", "off", "toggle");
    }

    private static double num(String s, double min, double max) throws NumberFormatException {
        return Math.max(min, Math.min(max, Double.parseDouble(s)));
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        if (args.length < 1) {
            msg(src, USAGE);
            return;
        }
        ExtremeSnowData data = ExtremeSnowData.get(world);
        boolean enabled;
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "on" -> enabled = true;
            case "off" -> enabled = false;
            case "toggle" -> enabled = !data.isEnabled();
            default -> {
                msg(src, USAGE);
                return;
            }
        }
        float intensity;
        boolean anywhere;
        float windDeg;
        float windSpeed;
        try {
            intensity = args.length >= 2 ? (float)num(args[1], 0.0, 1.0) : data.getIntensity();
            anywhere = args.length >= 3 ? num(args[2], 0, 1) >= 1 : data.isForceAnywhere();
            windDeg = args.length >= 4 ? (float)num(args[3], 0.0, 359.0) : data.getWindDeg();
            windSpeed = args.length >= 5 ? (float)num(args[4], 0.0, 1.0) : data.getWindSpeed();
        } catch (NumberFormatException e) {
            msg(src, USAGE);
            return;
        }
        data.setEnabled(enabled);
        data.setIntensity(intensity);
        data.setForceAnywhere(anywhere);
        data.setWindDeg(windDeg);
        data.setWindSpeed(windSpeed);
        ExtremeSnowNetwork.broadcast(world, enabled, intensity, anywhere, windDeg, windSpeed);
        msg(src, String.format(Locale.ROOT, "Extreme Snow: %s (intensity=%.2f, anywhere=%s, windDeg=%.0f, windSpeed=%.2f)", enabled ? "ON" : "OFF", intensity, anywhere, windDeg, windSpeed));
    }
}
