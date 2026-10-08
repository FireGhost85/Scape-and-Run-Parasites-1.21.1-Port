package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/** Options of every SRP particle: the three ints carry what ParticleSpawner.spawnParticle(..., r, g, b) took in 1.10.9 (colour, texture variant or particle kind). */
public record SRPParticleOptions(SRPParticleType type, int r, int g, int b) implements ParticleOptions {
    public SRPParticleOptions(SRPParticleType type) {
        this(type, 0, 0, 0);
    }

    @Override
    public ParticleType<?> getType() {
        return this.type;
    }
}
