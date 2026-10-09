package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

public class InfestedBonemealHandler {
    @SubscribeEvent
    public void onBonemeal(BonemealEvent e) {
        boolean growable;
        if (e.getStack() == null || e.getStack().getItem() != SRPItems.infestedbonemeal.get()) {
            return;
        }
        Level world = e.getLevel();
        BlockPos pos = e.getPos();
        RandomSource rand = world.random;
        BlockState state = e.getBlock();
        Block block = state.getBlock();
        if (block != SRPBlocks.ParasiteSapling.get()) {
            return;
        }
        if (!(block instanceof BlockParasiteSapling)) {
            return;
        }
        BlockParasiteSapling sap = (BlockParasiteSapling)block;
        BlockParasiteSapling.EnumType type = (BlockParasiteSapling.EnumType)(state.getValue(BlockParasiteSapling.VARIANT));
        boolean bl = growable = type == BlockParasiteSapling.EnumType.CONSUMED || type == BlockParasiteSapling.EnumType.DEADHEAD || type == BlockParasiteSapling.EnumType.INFESTED;
        if (!growable) {
            e.setResult(MobDespawnEvent.Result.DENY);
            return;
        }
        boolean grew = false;
        if ((Integer)state.getValue((Property)BlockParasiteSapling.STAGE) == 0) {
            grew = world.setBlock(pos, state.setValue((Property)BlockParasiteSapling.STAGE, Integer.valueOf(1)), 4);
        } else {
            sap.generateTree(world, pos, state, rand);
            grew = true;
        }
        e.setResult(grew ? Event.Result.ALLOW : MobDespawnEvent.Result.DENY);
    }
}

