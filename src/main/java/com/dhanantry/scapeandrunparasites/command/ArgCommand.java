package com.dhanantry.scapeandrunparasites.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * The 1.12 commands took one free text line ({@code String[] args}, split at the spaces) and checked the words themselves; the
 * Brigadier registration keeps that shape so that every command and message of the original is unchanged. Permission level 2
 * as in 1.12 ({@code canCommandSenderUseCommand(2, name)}).
 */
public abstract class ArgCommand {
    private final String name;

    protected ArgCommand(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    /** The words of the original {@code getTabCompletions}. */
    protected List<String> words() {
        return List.of();
    }

    protected abstract void execute(CommandSourceStack source, ServerLevel level, String[] args);

    protected static void msg(CommandSourceStack source, String text) {
        source.sendSuccess(() -> Component.literal(text), false);
    }

    protected static void tr(CommandSourceStack source, String key, Object... args) {
        source.sendSuccess(() -> Component.translatable(key, args), false);
    }

    private int run(CommandContext<CommandSourceStack> ctx, String line) {
        CommandSourceStack source = ctx.getSource();
        String[] args = line.isBlank() ? new String[0] : line.trim().split("\\s+");
        this.execute(source, source.getLevel(), args);
        return 1;
    }

    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(this.name).requires(s -> s.hasPermission(2));
        root.executes(ctx -> this.run(ctx, ""));
        root.then(Commands.argument("args", StringArgumentType.greedyString())
                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(this.words(), builder))
                .executes(ctx -> this.run(ctx, StringArgumentType.getString(ctx, "args"))));
        dispatcher.register(root);
    }
}
