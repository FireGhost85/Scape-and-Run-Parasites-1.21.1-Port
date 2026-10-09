package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.BlockBestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.SRPBlockCompendiumRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.effects.SRPStatusEffectRegistry;
import com.dhanantry.scapeandrunparasites.network.BestiarySyncPayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/** {@code /srpguide}: unlock and clear the discoveries of the field guide / bestiary of the player that runs it. */
public class GuideCommand extends ArgCommand {
    private static final Map<UUID, CompoundTag> SNAPSHOTS = new ConcurrentHashMap<>();

    public GuideCommand() {
        super("srpguide");
    }

    @Override
    protected List<String> words() {
        return List.of("unlockall", "unlockblocks", "unlockcelestial", "unlockeffects", "clear", "clearall", "reset", "clearblocks", "clearcelestial", "cleareffects");
    }

    private static void sync(ServerPlayer p, IBestiaryProgress prog) {
        SRPSend.sendToPlayer(p, new BestiarySyncPayload(prog.serializeNBT()));
    }

    @Override
    protected void execute(CommandSourceStack src, ServerLevel level, String[] args) {
        if (!(src.getEntity() instanceof ServerPlayer p)) {
            tr(src, "command.srpguide.players_only");
            return;
        }
        if (args.length == 0) {
            tr(src, "command.srpguide.usage");
            return;
        }
        IBestiaryProgress prog = BestiaryCapability.get(p);
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "unlockall" -> {
                SNAPSHOTS.put(p.getUUID(), prog.serializeNBT());
                int mobCount = 0;
                int blockCount = 0;
                int effectCount = 0;
                for (ParasiteTier t : ParasiteTier.values()) {
                    prog.markTierSeen(t);
                }
                for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
                    prog.markMobSeen(e.mobId);
                    int have = prog.getKills(e.mobId);
                    if (have < 999) {
                        prog.addKill(e.mobId, 999 - have);
                    }
                    if (e.tier != null) {
                        prog.markTierSeen(e.tier);
                    }
                    ++mobCount;
                }
                for (BlockBestiaryEntry entry : SRPBlockCompendiumRegistry.all()) {
                    if (!prog.hasSeenBlock(entry.id)) {
                        prog.markBlockSeen(entry.id);
                        ++blockCount;
                    }
                }
                for (SRPStatusEffectRegistry.Entry entry : SRPStatusEffectRegistry.all()) {
                    if (!prog.hasSeenEffect(entry.id)) {
                        prog.markEffectSeen(entry.id);
                        ++effectCount;
                    }
                }
                tr(src, "command.srpguide.unlockall_ok", mobCount);
                tr(src, "command.srpguide.unlockblocks_ok", blockCount);
                tr(src, "command.srpguide.unlockcelestial_ok", 0);
                tr(src, "command.srpguide.unlockeffects_ok", effectCount);
                sync(p, prog);
            }
            case "unlockblocks" -> {
                int count = 0;
                for (BlockBestiaryEntry entry : SRPBlockCompendiumRegistry.all()) {
                    if (!prog.hasSeenBlock(entry.id)) {
                        prog.markBlockSeen(entry.id);
                        ++count;
                    }
                }
                tr(src, "command.srpguide.unlockblocks_ok", count);
                sync(p, prog);
            }
            case "unlockcelestial" -> {
                // the celestial objects come with the celestial events (deferred)
                tr(src, "command.srpguide.unlockcelestial_ok", 0);
                sync(p, prog);
            }
            case "unlockeffects" -> {
                int count = 0;
                for (SRPStatusEffectRegistry.Entry e : SRPStatusEffectRegistry.all()) {
                    if (!prog.hasSeenEffect(e.id)) {
                        prog.markEffectSeen(e.id);
                        ++count;
                    }
                }
                tr(src, "command.srpguide.unlockeffects_ok", count);
                sync(p, prog);
            }
            case "clear" -> {
                CompoundTag snap = SNAPSHOTS.get(p.getUUID());
                if (snap != null) {
                    prog.deserializeNBT(snap.copy());
                    tr(src, "command.srpguide.clear_restore_ok");
                } else {
                    prog.deserializeNBT(new CompoundTag());
                    tr(src, "command.srpguide.clear_no_snapshot");
                }
                sync(p, prog);
            }
            case "clearall", "reset" -> {
                prog.deserializeNBT(new CompoundTag());
                tr(src, "command.srpguide.clearall_ok");
                sync(p, prog);
            }
            case "clearblocks" -> {
                prog.getSeenBlocks().clear();
                tr(src, "command.srpguide.clearblocks_ok");
                sync(p, prog);
            }
            case "clearcelestial" -> {
                prog.getSeenCelestials().clear();
                tr(src, "command.srpguide.clearcelestial_ok");
                sync(p, prog);
            }
            case "cleareffects" -> {
                prog.getSeenEffects().clear();
                tr(src, "command.srpguide.cleareffects_ok");
                sync(p, prog);
            }
            default -> tr(src, "command.srpguide.usage");
        }
    }
}
