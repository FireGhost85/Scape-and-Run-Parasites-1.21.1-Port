package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockThornshade;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.BonemealEvent;

/** Bone meal (dye 15 in 1.12) does nothing on thornshade. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class ThornshadeBonemealBlocker {
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (!(event.getState().getBlock() instanceof BlockThornshade)) {
            return;
        }
        ItemStack stack = event.getStack();
        if (stack.isEmpty()) {
            return;
        }
        if (stack.is(Items.BONE_MEAL)) {
            event.setCanceled(true);
            event.setSuccessful(false);
        }
    }
}
