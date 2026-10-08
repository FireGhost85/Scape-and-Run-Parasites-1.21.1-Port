package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleDot extends LegacyParticle {
    protected ParticleDot(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float r, float g, float b, SpriteSet sprites) {
        super(level, x, y, z);
        this.setFrame(sprites, 0, 1);
        this.scale = 2.0f;
        this.lifetime = 40;
        this.alpha = 0.0f;
        this.rCol = r / 255.0f;
        this.gCol = g / 255.0f;
        this.bCol = b / 255.0f;
        if (ySpeed != 0.0 || xSpeed != 0.0 || zSpeed != 0.0) {
            this.xd = xSpeed;
            this.yd = ySpeed;
            this.zd = zSpeed;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return this.glowingLightColor(partialTick);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int age = this.age++;
        float maxAge = this.lifetime;
        this.y = this.yo + (double) age * 0.01;
        this.alpha = age > 20 ? (maxAge - (float) age) / maxAge : (age <= 5 & age != 1 ? ((float) age - maxAge) / maxAge : 1.0f);
        if (this.age++ >= this.lifetime) {
            this.remove();
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
            return new ParticleDot(level, x, y, z, xSpeed, ySpeed, zSpeed, options.r(), options.g(), options.b(), this.sprites);
        }
    }
}
