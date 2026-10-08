package com.dhanantry.scapeandrunparasites.dislodgment;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Start / per-second / end effects of the dislodgments (SRPSaveData.startDislo, midDislo, endDislo of 1.10.9). */
public final class Dislodgments {
    private Dislodgments() {}

    public static void start(Level level, int position, int time) {
        if (!(level instanceof ServerLevel server)) return;
        int ticks = time * 20 + 50;
        for (Entity entity : server.getAllEntities()) {
            if (!(entity instanceof IDislodgmentTarget target)) continue;
            switch (position) {
                case 2, 3, 4, 6, 7, 8, 9, 18, 20, 21, 22 -> target.setDislodgment(position, 1);
                case 5 -> entity.hurt(server.damageSources().fellOutOfWorld(), 10000.0f);
                case 11, 15, 16, 17, 19 -> target.setDislodgment(position, ticks);
                default -> { return; }
            }
        }
    }

    public static void mid(Level level, int position, int code) {
        if (!(level instanceof ServerLevel server)) return;
        switch (position) {
            case 12 -> {
                for (Entity entity : server.getAllEntities()) {
                    if (entity instanceof LivingEntity && !(entity instanceof IDislodgmentTarget)) {
                        entity.hurt(server.damageSources().wither(), (float) code * (float) SRPConfigSystems.disloSeconds);
                    }
                }
            }
            case 13 -> {
                for (Player player : server.players()) {
                    player.causeFoodExhaustion((float) code * (float) SRPConfigSystems.disloSeconds);
                }
            }
            default -> { }
        }
    }

    public static void end(Level level, int position) {
        if (!(level instanceof ServerLevel server)) return;
        switch (position) {
            case 2 -> ParasiteEventEntity.disloNumber2(server);
            case 5 -> ParasiteEventEntity.disloNumber5(null, server);
            case 3, 4, 6, 7, 8, 9, 11, 15, 16, 17, 18, 19, 20, 21, 22 -> {
                for (Entity entity : server.getAllEntities()) {
                    if (entity instanceof IDislodgmentTarget target) target.setDislodgment(position, 0);
                }
            }
            default -> { }
        }
        if (position == 2) {
            for (Entity entity : server.getAllEntities()) {
                if (entity instanceof IDislodgmentTarget target) target.setDislodgment(2, 0);
            }
        }
    }
}
