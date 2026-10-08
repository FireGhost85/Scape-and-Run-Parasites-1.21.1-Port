package com.dhanantry.scapeandrunparasites.client.particle;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPParticles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

/** Registers the sprite-set providers of the particle types (the 1.10.9 ParticleSpawner switch). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class SRPParticleProviders {
    private SRPParticleProviders() {
    }

    @SubscribeEvent
    static void onRegisterProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SRPParticles.FOG.get(), ParticleFog.Provider::new);
        event.registerSpriteSet(SRPParticles.SPORE.get(), ParticleSpore.Provider::new);
        event.registerSpriteSet(SRPParticles.GCLOUD.get(), ParticleRGBSmoke.Provider::new);
        event.registerSpriteSet(SRPParticles.GSPLASH.get(), ParticleMultipleGore.Provider::new);
        event.registerSpriteSet(SRPParticles.RHAPPY.get(), ParticleRHappy.Provider::new);
        event.registerSpriteSet(SRPParticles.BIOMASS.get(), ParticleBiomass.Provider::new);
        event.registerSpriteSet(SRPParticles.EEN.get(), ParticleEen.Provider::new);
        event.registerSpriteSet(SRPParticles.FLASH.get(), ParticleFlash.Provider::new);
        event.registerSpriteSet(SRPParticles.DOT.get(), ParticleDot.Provider::new);
        event.registerSpriteSet(SRPParticles.WIND.get(), ParticleWind.Provider::new);
        event.registerSpriteSet(SRPParticles.COOLER_FOG.get(), ParticleCoolerFog.Provider::new);
        event.registerSpriteSet(SRPParticles.RAGE.get(), ParticleRage.Provider::new);
        event.registerSpriteSet(SRPParticles.BLOOD.get(), ParticleBlood.Provider::new);
    }
}
