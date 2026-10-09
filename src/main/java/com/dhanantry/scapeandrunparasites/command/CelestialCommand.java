package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialEventManager;
import com.dhanantry.scapeandrunparasites.world.celestial.CelestialNightData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

/** {@code /srp_celestial} (CommandForceCelestial of 1.10.9). The client side "debug" branch of the original is the server branch here. */
public class CelestialCommand extends ArgCommand {
    public CelestialCommand() {
        super("srp_celestial");
    }

    @Override
    protected List<String> words() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("list");
        options.add("debug");
        options.add("clear");
        options.add("none");
        options.add("off");
        options.add("all");
        options.add("dark_days");
        options.add("dark_days_end");
        options.addAll(CelestialObjectRegistry.getAllIds());
        return options;
    }

    private static long getNightIndex(ServerLevel world) {
        return world.getDayTime() / 24000L;
    }

    private static long getNextTimeOfDay(ServerLevel world, long targetDayTime) {
        long current = world.getDayTime();
        long dayStart = current - current % 24000L;
        long target = dayStart + targetDayTime;
        if (target <= current) {
            target += 24000L;
        }
        return target;
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
        boolean nowForced;
        if (!SRPConfigWorld.enableCelestialObjects) {
            msg(src, "SRP celestial objects are disabled in the config (enableCelestialObjects = false).");
            return;
        }
        if (args.length < 1) {
            msg(src, "Usage: /srp_celestial <list|debug|clear|all|dark_days|dark_days_end|id>");
            return;
        }
        String sub = args[0];
        String dim = DimKeys.of(world);
        if ("list".equalsIgnoreCase(sub)) {
            List<String> ids = CelestialObjectRegistry.getAllIds();
            if (ids.isEmpty()) {
                msg(src, "No celestial events are registered.");
            } else {
                msg(src, "Celestial events: " + String.join(", ", ids));
            }
            msg(src, "Special commands: dark_days, dark_days_end");
            return;
        }
        SRPSaveData save = SRPSaveData.get(world);
        byte phase = save == null ? -1 : save.getEvolutionPhase(dim);
        CelestialNightData nightData = CelestialNightData.get(world);
        CelestialNightData.DimState state = nightData.getOrCreate(dim);
        if ("debug".equalsIgnoreCase(sub)) {
            msg(src, "[SERVER] dim=" + dim + " phase=" + phase + " day=" + getNightIndex(world) + " dayTime=" + world.getDayTime() % 24000L + " active=" + state.active.size() + " forced=" + state.forced.size() + " darkDaysEndTime=" + state.darkDaysEndTime + " darkDaysEndingSoundPlayed=" + state.darkDaysEndingSoundPlayed);
            msg(src, "[SERVER] active=" + String.join(", ", state.active));
            msg(src, "[SERVER] forced=" + String.join(", ", state.forced));
            return;
        }
        if (save == null) {
            msg(src, "SRPSaveData is null for dim " + dim);
            return;
        }
        if ("clear".equalsIgnoreCase(sub) || "none".equalsIgnoreCase(sub) || "off".equalsIgnoreCase(sub)) {
            state.forced.clear();
            state.active.remove("dark_days");
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
            nightData.markDirty();
            CelestialEventManager.clearForced(world);
            CelestialEventManager.syncDim(world, dim);
            msg(src, "Cleared all forced celestial events in dimension " + dim);
            return;
        }
        if ("dark_days".equalsIgnoreCase(sub)) {
            if (!SRPConfigWorld.darkDaysEnabled) {
                msg(src, "Dark Days is disabled in the config.");
                return;
            }
            world.setDayTime(getNextTimeOfDay(world, 1000L));
            state.active.clear();
            state.forced.clear();
            state.darkDaysStartTime = world.getDayTime() + 160L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
            nightData.markDirty();
            CelestialEventManager.startDarkDays(world);
            msg(src, "Queued Celestial Displacement. CD DD will activate in 8 seconds in dimension " + dim);
            return;
        }
        if ("dark_days_end".equalsIgnoreCase(sub)) {
            boolean wasActive = state.forced.contains("dark_days") || state.active.contains("dark_days");
            if (!wasActive) {
                msg(src, "Celestial Displacement is not active in dimension " + dim);
                return;
            }
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = world.getDayTime() + 200L;
            state.darkDaysEndingSoundPlayed = true;
            nightData.markDirty();
            CelestialEventManager.stopDarkDays(world);
            msg(src, "Queued Celestial Realignment. Displacement will end in 10 seconds in dimension " + dim);
            return;
        }
        if ("all".equalsIgnoreCase(sub)) {
            List<String> ids = CelestialObjectRegistry.getAllIds();
            if (ids.isEmpty()) {
                msg(src, "No celestial events are registered.");
                return;
            }
            ArrayList<String> toForce = new ArrayList<String>();
            for (String eventId : ids) {
                if ("dark_days".equals(eventId) || SRPConfigWorld.isCelestialEventBlacklisted(eventId)) continue;
                toForce.add(eventId);
            }
            state.active.remove("dark_days");
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
            CelestialEventManager.clearForced(world);
            state.forced.clear();
            for (String eventId : toForce) {
                state.forced.add(eventId);
                CelestialEventManager.force(world, eventId, true);
            }
            nightData.markDirty();
            CelestialEventManager.syncDim(world, dim);
            msg(src, "Forced " + toForce.size() + " compatible celestial events in dimension " + dim + ". Celestial Displacement was skipped.");
            return;
        }
        String id = sub;
        if (!CelestialObjectRegistry.getAllIds().contains(id)) {
            msg(src, "Unknown celestial id: " + id);
            return;
        }
        if (SRPConfigWorld.isCelestialEventBlacklisted(id) || "dark_days".equals(id) && !SRPConfigWorld.darkDaysEnabled) {
            msg(src, "Celestial event '" + id + "' is disabled in the config.");
            return;
        }
        if (state.forced.contains(id)) {
            state.forced.remove(id);
            state.active.remove(id);
            nowForced = false;
            CelestialEventManager.force(world, id, false);
        } else {
            state.forced.add(id);
            nowForced = true;
            if ("dark_days".equals(id)) {
                state.active.clear();
                state.forced.clear();
                state.forced.add("dark_days");
                state.darkDaysEndTime = world.getDayTime() + 6000L;
                state.darkDaysEndingSoundPlayed = false;
                CelestialEventManager.startDarkDays(world);
            } else {
                CelestialEventManager.force(world, id, true);
            }
        }
        if (!SRPConfigWorld.darkDaysEnabled) {
            state.active.remove("dark_days");
            state.forced.remove("dark_days");
            state.darkDaysStartTime = -1L;
            state.darkDaysEndTime = -1L;
            state.darkDaysEndingSoundPlayed = false;
        }
        nightData.markDirty();
        CelestialEventManager.syncDim(world, dim);
        msg(src, (nowForced ? "Forced" : "Unforced") + " celestial event '" + id + "' in dimension " + dim);
    }
}
