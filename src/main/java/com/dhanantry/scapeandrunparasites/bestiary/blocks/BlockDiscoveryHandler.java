package com.dhanantry.scapeandrunparasites.bestiary.blocks;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.network.BestiarySyncPayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Having a block of the compendium in the inventory discovers it in the bestiary. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class BlockDiscoveryHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post e) {
        if (!(e.getEntity() instanceof ServerPlayer p) || p.tickCount % 20 != 0) {
            return;
        }
        IBestiaryProgress prog = BestiaryCapability.get(p);
        if (scanInventory(p.getInventory(), prog)) {
            SRPSend.sendToPlayer(p, new BestiarySyncPayload(prog.serializeNBT()));
        }
    }

    private static boolean scanInventory(Inventory inv, IBestiaryProgress prog) {
        boolean changed = false;
        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack st = inv.getItem(i);
            if (st.isEmpty() || !(st.getItem() instanceof BlockItem itemBlock)) {
                continue;
            }
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(itemBlock.getBlock());
            if (SRPBlockCompendiumRegistry.get(id) == null || prog.hasSeenBlock(id)) {
                continue;
            }
            prog.markBlockSeen(id);
            changed = true;
        }
        return changed;
    }
}
