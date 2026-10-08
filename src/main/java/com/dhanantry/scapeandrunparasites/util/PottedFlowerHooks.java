package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.block.BlockPottedSRPFlower;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class PottedFlowerHooks {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock e) {
        if (e.getFace() == Direction.UP) {
            return;
        }
        if (e.getWorld().getBlockState(e.getPos()).getBlock() != Blocks.FLOWER_POT) {
            return;
        }
        BlockEntity te = e.getWorld().getBlockEntity(e.getPos());
        if (!(te instanceof TileEntityFlowerPot)) {
            return;
        }
        if (!((TileEntityFlowerPot)te).getFlowerItemStack().isEmpty()) {
            return;
        }
        ItemStack held = e.getItemStack();
        if (held.isEmpty()) {
            return;
        }
        Item item = held.getItem();
        BlockPottedSRPFlower potted = null;
        if (item == Item.getItemFromBlock((Block)SRPBlocks.ASSIMILATED_BLOSSOM.get())) {
            potted = SRPBlocks.POTTED_ASSIMILATED_BLOSSOM.get();
        }
        if (potted == null) {
            return;
        }
        if (!e.getWorld().isClientSide) {
            e.getWorld().setBlock(e.getPos(), potted.defaultBlockState(), 3);
            if (!e.getEntityPlayer().getAbilities().instabuild) {
                held.shrink(1);
            }
        }
        e.setCanceled(true);
        e.setCancellationResult(EnumActionResult.SUCCESS);
    }
}

