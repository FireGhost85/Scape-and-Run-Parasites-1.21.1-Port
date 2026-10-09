package com.dhanantry.scapeandrunparasites.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** {@code /srpguideclear}: the same as {@code /srpguide clear}. */
public class GuideClearCommand extends ArgCommand {
    public GuideClearCommand() {
        super("srpguideclear");
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel level, String[] args) {
        if (!(src.getEntity() instanceof ServerPlayer)) {
            msg(src, "Players only.");
            return;
        }
        src.getServer().getCommands().performPrefixedCommand(src, "srpguide clear");
    }
}
