package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.entity.ai.SoundEaterSoundHelper;
import net.minecraft.world.level.Level;
import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SoundEaterBlockSoundHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level world) || world.isClientSide) {
            return;
        }
        SoundEaterSoundHelper.broadcastSound(world, event.getPos(), 16.0, 100);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level world) || world.isClientSide) {
            return;
        }
        SoundEaterSoundHelper.broadcastSound(world, event.getPos(), 12.0, 80);
    }
}

