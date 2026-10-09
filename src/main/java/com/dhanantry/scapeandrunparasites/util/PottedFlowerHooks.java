package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockPottedSRPFlower;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class PottedFlowerHooks {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getFace() == Direction.UP) {
            return;
        }
        if (e.getLevel().getBlockState(e.getPos()).getBlock() != Blocks.FLOWER_POT) {
            return;
        }
        ItemStack held = e.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        Item item = held.getItem();
        BlockPottedSRPFlower potted = null;
        if (item == SRPBlocks.ASSIMILATED_BLOSSOM.get().asItem()) {
            potted = SRPBlocks.POTTED_ASSIMILATED_BLOSSOM.get();
        }
        if (potted == null) {
            return;
        }
        if (!e.getLevel().isClientSide()) {
            e.getLevel().setBlock(e.getPos(), potted.defaultBlockState(), 3);
            if (!e.getEntity().getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        e.setCanceled(true);
        e.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
    }
}

