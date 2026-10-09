package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.entity.ai.SoundEaterSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class SoundEaterArrowImpactHandler {
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() == null) {
            return;
        }
        if (!(event.getEntity() instanceof IProjectile)) {
            return;
        }
        Level world = event.getEntity().level();
        if (world.isClientSide) {
            return;
        }
        BlockPos hitPos = event.getRayTraceResult().getBlockPos();
        if (hitPos == null) {
            hitPos = BlockPos.containing(event.getEntity());
        }
        SoundEaterSoundHelper.broadcastSound(world, hitPos, 18.0, 120);
    }
}

