package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.entity.ai.SoundEaterSoundHelper;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class SoundEaterBlockSoundHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Level world = event.getLevel();
        if (world.isClientSide) {
            return;
        }
        SoundEaterSoundHelper.broadcastSound(world, event.getPos(), 16.0, 100);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.PlaceEvent event) {
        Level world = event.getLevel();
        if (world.isClientSide) {
            return;
        }
        SoundEaterSoundHelper.broadcastSound(world, event.getPos(), 12.0, 80);
    }
}

