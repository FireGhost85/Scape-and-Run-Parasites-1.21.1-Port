package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleRHappy extends LegacyParticle {
    protected ParticleRHappy(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.setFrame(sprites, 0, 1);
        this.scale = 2.0f;
        this.lifetime = 40;
        if (ySpeed == 0.0 && (xSpeed != 0.0 || zSpeed != 0.0)) {
            this.xd = xSpeed;
            this.yd = ySpeed + 0.1;
            this.zd = zSpeed;
        }
    }

    public static class Provider implements ParticleProvider<SRPParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SRPParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleRHappy(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
