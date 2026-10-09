package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

/**
 * Infested bonemeal only grows the consumed, deadhead and infested parasite saplings. Result.DENY of 1.12 (nothing grows, the
 * item stays) is a cancelled event, Result.ALLOW (grown, item used) a cancelled event with {@code setSuccessful(true)}.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class InfestedBonemealHandler {
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent e) {
        if (e.getStack().isEmpty() || e.getStack().getItem() != SRPItems.infestedbonemeal.get()) {
            return;
        }
        Level world = e.getLevel();
        BlockPos pos = e.getPos();
        RandomSource rand = world.random;
        BlockState state = e.getState();
        Block block = state.getBlock();
        if (block != SRPBlocks.ParasiteSapling.get() || !(block instanceof BlockParasiteSapling sap)) {
            return;
        }
        BlockParasiteSapling.EnumType type = state.getValue(BlockParasiteSapling.VARIANT);
        boolean growable = type == BlockParasiteSapling.EnumType.CONSUMED || type == BlockParasiteSapling.EnumType.DEADHEAD || type == BlockParasiteSapling.EnumType.INFESTED;
        if (!growable) {
            e.setCanceled(true);
            e.setSuccessful(false);
            return;
        }
        boolean grew;
        if (state.getValue(BlockParasiteSapling.STAGE) == 0) {
            grew = world.setBlock(pos, state.setValue(BlockParasiteSapling.STAGE, 1), 4);
        } else {
            sap.generateTree(world, pos, state, rand);
            grew = true;
        }
        e.setCanceled(true);
        e.setSuccessful(grew);
    }
}
