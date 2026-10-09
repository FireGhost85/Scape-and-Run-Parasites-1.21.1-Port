package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.entity.ai.SoundEaterSoundHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SoundEaterArrowImpactHandler {
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (event.getRayTraceResult() == null) {
            return;
        }
        if (!(event.getProjectile() instanceof Projectile)) {
            return;
        }
        Level world = event.getProjectile().level();
        if (world.isClientSide) {
            return;
        }
        BlockPos hitPos = event.getRayTraceResult() instanceof BlockHitResult bhr ? bhr.getBlockPos() : event.getProjectile().blockPosition();
        SoundEaterSoundHelper.broadcastSound(world, hitPos, 18.0, 120);
    }
}

