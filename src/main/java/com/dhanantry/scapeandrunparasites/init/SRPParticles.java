package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.particle.SRPParticleType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Particle types: the SRPEnumParticle values of 1.10.9. The other particles of the mod are created directly by their spawners. */
public final class SRPParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(Registries.PARTICLE_TYPE, ScapeAndRunParasites.MODID);

    private SRPParticles() {
    }

    public static void register(IEventBus bus) {
        PARTICLES.register(bus);
    }

    private static DeferredHolder<ParticleType<?>, SRPParticleType> create(String name) {
        return PARTICLES.register(name, SRPParticleType::new);
    }

    public static final DeferredHolder<ParticleType<?>, SRPParticleType> FOG = create("fog");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> SPORE = create("spore");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> GCLOUD = create("gcloud");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> GSPLASH = create("gsplash");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> RHAPPY = create("rhappy");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> BIOMASS = create("biomass");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> EEN = create("een");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> FLASH = create("flash");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> DOT = create("dot");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> WIND = create("wind");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> COOLER_FOG = create("coolerfog");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> RAGE = create("rage");
    public static final DeferredHolder<ParticleType<?>, SRPParticleType> BLOOD = create("blood");
}
