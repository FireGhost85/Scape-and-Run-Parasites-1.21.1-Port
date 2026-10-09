package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.network.BestiarySyncPayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** {@code /srpbestiarystats clear [player]}: resets the damage and death counters of the stats page. */
public class BestiaryStatsCommand extends ArgCommand {
    public BestiaryStatsCommand() {
        super("srpbestiarystats");
    }

    @Override
    protected List<String> words() {
        return List.of("clear");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel level, String[] args) {
        if (args.length < 1 || !"clear".equalsIgnoreCase(args[0])) {
            tr(src, "commands.srparasites.bestiary_stats.usage", "/srpbestiarystats clear [player]");
            return;
        }
        ServerPlayer target;
        if (args.length >= 2) {
            target = src.getServer().getPlayerList().getPlayerByName(args[1]);
            if (target == null) {
                tr(src, "commands.srparasites.bestiary_stats.player_not_found", args[1]);
                return;
            }
        } else if (src.getEntity() instanceof ServerPlayer p) {
            target = p;
        } else {
            tr(src, "commands.srparasites.bestiary_stats.console_requires_player");
            return;
        }
        IBestiaryProgress prog = BestiaryCapability.get(target);
        prog.clearStatsPageData();
        SRPSend.sendToPlayer(target, new BestiarySyncPayload(prog.serializeNBT()));
        tr(src, "commands.srparasites.bestiary_stats.cleared_other", target.getGameProfile().getName());
        if (src.getEntity() != target) {
            target.sendSystemMessage(net.minecraft.network.chat.Component.translatable("commands.srparasites.bestiary_stats.cleared_self"));
        }
    }
}
