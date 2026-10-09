package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockThornshade;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class ThornshadeBonemealBlocker {
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        BlockPos pos;
        Level world = event.getLevel();
        BlockState state = world.getBlockState(pos = event.getPos());
        if (!(state.getBlock() instanceof BlockThornshade)) {
            return;
        }
        ItemStack stack = event.getStack();
        if (stack.isEmpty()) {
            return;
        }
        if (stack.getItem() == Items.DYE && stack.getMetadata() == 15) {
            event.setCanceled(true);
            event.setResult(MobDespawnEvent.Result.DENY);
        }
    }
}

