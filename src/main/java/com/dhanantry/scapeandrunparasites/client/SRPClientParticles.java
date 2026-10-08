package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.client.particle.ParticleKirinWarning;
import net.minecraft.world.level.Level;

/** Client-side particle entry points that common code (entities) may call. */
public final class SRPClientParticles {
    private SRPClientParticles() {
    }

    public static void spawnKirinWarning(Level world, double x, double y, double z, float sizeBlocks, float yawRad, int maxAgeTicks) {
        if (!world.isClientSide) {
            return;
        }
        ParticleKirinWarning.spawn(world, x, y, z, sizeBlocks, yawRad, maxAgeTicks);
    }
}
