package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.world.level.Level;

/** The only place of the particle spawner that touches Minecraft. */
final class ClientParticleHooks {
    private ClientParticleHooks() {
    }

    @Nullable
    static Level level() {
        return Minecraft.getInstance().level;
    }

    static boolean allowed() {
        Minecraft mc = Minecraft.getInstance();
        return mc.getCameraEntity() != null && mc.particleEngine != null && mc.options.particles().get() == ParticleStatus.ALL;
    }
}
