package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.network.GuiDistortionStatePayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/** {@code /srpguidistortion <on|off|toggle|status> [player|all]} with the aliases {@code srpguidist} and {@code srpdistortion}. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class GuiDistortionCommand extends ArgCommand {
    private static final String DISABLED_TAG = "SRPGuiDistortionDisabled";
    private static final String OVERRIDE_TAG = "SRPGuiDistortionCreativeOverride";

    public GuiDistortionCommand(String name) {
        super(name);
    }

    @Override
    protected List<String> words() {
        return List.of("on", "off", "toggle", "status");
    }

    private static boolean creativeOrSpectator(ServerPlayer p) {
        GameType t = p.gameMode.getGameModeForPlayer();
        return t == GameType.CREATIVE || t == GameType.SPECTATOR;
    }

    private static void sync(ServerPlayer p) {
        CompoundTag tag = p.getPersistentData();
        SRPSend.sendToPlayer(p, new GuiDistortionStatePayload(tag.getBoolean(DISABLED_TAG), tag.getBoolean(OVERRIDE_TAG)));
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) {
            sync(p);
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        CompoundTag old = e.getOriginal().getPersistentData();
        CompoundTag now = e.getEntity().getPersistentData();
        now.putBoolean(DISABLED_TAG, old.getBoolean(DISABLED_TAG));
        now.putBoolean(OVERRIDE_TAG, old.getBoolean(OVERRIDE_TAG));
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel level, String[] args) {
        if (args.length < 1) {
            tr(src, "commands.srparasites.guidistortion.usage.text", "/" + this.name() + " <on|off|toggle|status> [player|all]");
            return;
        }
        String mode = args[0].toLowerCase(Locale.ROOT);
        if (!(mode.equals("on") || mode.equals("off") || mode.equals("toggle") || mode.equals("status"))) {
            tr(src, "commands.srparasites.guidistortion.invalid_argument");
            return;
        }
        List<ServerPlayer> targets = new ArrayList<>();
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("all") || args[1].equals("@a")) {
                targets.addAll(src.getServer().getPlayerList().getPlayers());
            } else {
                ServerPlayer t = src.getServer().getPlayerList().getPlayerByName(args[1]);
                if (t == null) {
                    tr(src, "commands.srparasites.guidistortion.player_not_found", args[1]);
                    return;
                }
                targets.add(t);
            }
        } else if (src.getEntity() instanceof ServerPlayer p) {
            targets.add(p);
        } else {
            tr(src, "commands.srparasites.guidistortion.console_requires_player");
            return;
        }
        for (ServerPlayer target : targets) {
            this.handleTarget(src, target, mode, targets.size() > 1);
        }
        if (targets.size() > 1 && !mode.equals("status")) {
            tr(src, "commands.srparasites.guidistortion.set_all", targets.size());
        }
    }

    private static Component state(boolean disabled) {
        return Component.translatable(disabled ? "commands.srparasites.guidistortion.state.disabled" : "commands.srparasites.guidistortion.state.enabled");
    }

    private void handleTarget(CommandSourceStack src, ServerPlayer target, String mode, boolean multi) {
        CompoundTag persisted = target.getPersistentData();
        boolean currentlyDisabled = persisted.getBoolean(DISABLED_TAG);
        boolean creativeOverride = persisted.getBoolean(OVERRIDE_TAG);
        boolean cs = creativeOrSpectator(target);
        boolean effectivelyDisabled = currentlyDisabled || cs && !creativeOverride;
        if (mode.equals("status")) {
            src.sendSuccess(() -> Component.translatable("commands.srparasites.guidistortion.status.detailed", target.getName(), state(currentlyDisabled),
                    Component.translatable(creativeOverride ? "commands.srparasites.guidistortion.creative_override.enabled" : "commands.srparasites.guidistortion.creative_override.disabled"),
                    Component.translatable(cs ? "commands.srparasites.guidistortion.gamemode.creative_or_spectator" : "commands.srparasites.guidistortion.gamemode.normal")), false);
            return;
        }
        boolean newDisabled;
        boolean newOverride = creativeOverride;
        if (mode.equals("on")) {
            newDisabled = false;
            if (cs) {
                newOverride = true;
            }
        } else if (mode.equals("off")) {
            newDisabled = true;
            newOverride = false;
        } else if (effectivelyDisabled) {
            newDisabled = false;
            if (cs) {
                newOverride = true;
            }
        } else {
            newDisabled = true;
            newOverride = false;
        }
        persisted.putBoolean(DISABLED_TAG, newDisabled);
        persisted.putBoolean(OVERRIDE_TAG, newOverride);
        SRPSend.sendToPlayer(target, new GuiDistortionStatePayload(newDisabled, newOverride));
        final boolean nd = newDisabled;
        if (!multi) {
            src.sendSuccess(() -> Component.translatable("commands.srparasites.guidistortion.set_other", target.getName(), state(nd)), false);
        }
        if (src.getEntity() != target) {
            target.sendSystemMessage(Component.translatable("commands.srparasites.guidistortion.set_self", state(nd)));
        }
    }
}
