package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.world.level.Level;

/**
 * Client-side spawner of the SRP particles. Safe to reference from common code: client classes are only touched on the logical client.
 * As in 1.10.9 nothing is spawned unless the particle setting is "All".
 */
public final class ParticleSpawner {
    private ParticleSpawner() {
    }

    /** Spawns in the client level of the running game. */
    public static void spawnParticle(SRPEnumParticle type, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r, int g, int b) {
        Level level = ClientParticleHooks.level();
        if (level != null) {
            spawnParticle(level, type, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, r, g, b);
        }
    }

    public static void spawnParticle(Level level, SRPEnumParticle type, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int r, int g, int b) {
        if (!level.isClientSide || !ClientParticleHooks.allowed()) {
            return;
        }
        level.addParticle(type.options(r, g, b), xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
    }
}
