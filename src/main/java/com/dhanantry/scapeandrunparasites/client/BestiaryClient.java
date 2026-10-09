package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Client side of PacketBestiarySync: stores the progress, plays the unlock sounds when a player with the field guide discovers something. */
public final class BestiaryClient {
    private static boolean initialSyncDone;
    private static long joinTime = -1L;

    private BestiaryClient() {}

    private static Set<String> set(CompoundTag tag, String key) {
        Set<String> out = new HashSet<>();
        ListTag list = tag.getList(key, 8);
        for (int i = 0; i < list.size(); ++i) {
            out.add(list.getString(i));
        }
        return out;
    }

    private static boolean hasFieldGuide(Player p) {
        for (ItemStack st : p.getInventory().items) {
            if (!st.isEmpty() && st.getItem() == SRPItems.SRP_FIELD_GUIDE.get()) {
                return true;
            }
        }
        return p.getOffhandItem().getItem() == SRPItems.SRP_FIELD_GUIDE.get();
    }

    public static void reset() {
        initialSyncDone = false;
        joinTime = -1L;
    }

    public static void applySync(CompoundTag tag) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            reset();
            return;
        }
        if (joinTime < 0L) {
            joinTime = System.currentTimeMillis();
        }
        IBestiaryProgress prog = BestiaryCapability.get(mc.player);
        CompoundTag before = prog.serializeNBT();
        prog.deserializeNBT(tag);
        if (!initialSyncDone) {
            initialSyncDone = true;
        } else if (System.currentTimeMillis() - joinTime >= 2500L && hasFieldGuide(mc.player)) {
            Set<String> newCel = set(tag, "seenCelestials");
            newCel.removeAll(set(before, "seenCelestials"));
            Set<String> newMobs = set(tag, "seenMobs");
            newMobs.removeAll(set(before, "seenMobs"));
            Set<String> newBlocks = set(tag, "seenBlocks");
            newBlocks.removeAll(set(before, "seenBlocks"));
            if (!newCel.isEmpty()) {
                mc.player.playSound(com.dhanantry.scapeandrunparasites.init.SRPSounds.BOOK_UNLOCK_CELESTIAL.get(), 1.0f, 1.0f);
            } else if (!newMobs.isEmpty()) {
                mc.player.playSound(com.dhanantry.scapeandrunparasites.init.SRPSounds.BOOK_UNLOCK_ENTITY.get(), 1.0f, 1.0f);
            } else if (!newBlocks.isEmpty()) {
                mc.player.playSound(com.dhanantry.scapeandrunparasites.init.SRPSounds.BOOK_UNLOCK_BLOCK.get(), 1.0f, 1.0f);
            }
        }
        if (mc.screen != null && mc.screen.getClass().getName().toLowerCase().contains("bestiary")) {
            mc.screen.resize(mc, mc.screen.width, mc.screen.height);
        }
    }
}
