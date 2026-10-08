package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

/**
 * Uses the eight frames of the base game's generic particle (the first row of the 1.12 particle sheet).
 * The colour channels are the raw option values, as in 1.10.9 (they are not divided by 255).
 */
public class ParticleFog extends LegacyParticle {
    private static final int FRAMES = 8;
    private final SpriteSet sprites;

    public ParticleFog(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int r, int g, int b, SpriteSet sprites) {
        super(level, x + 0.0, y, z + 0.0, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.setFrame(sprites, 0, FRAMES);
        this.xd = xSpeed * (double) 0.2f + (Math.random() * 2.0 - 1.0) * (double) 0.02f;
        this.yd = ySpeed * (double) 0.2f + (Math.random() * 2.0 - 1.0) * (double) 0.02f;
        this.zd = zSpeed * (double) 0.2f + (Math.random() * 2.0 - 1.0) * (double) 0.02f;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.scale = 0.0f;
        this.alpha = 0.8f;
        this.lifetime = 200;
        this.gravity = 0.0f;
    }

    @Override
    public Particle setPower(float multiplier) {
        this.xd *= (double) multiplier;
        this.yd *= (double) multiplier;
        this.zd *= (double) multiplier;
        return this;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if ((double) this.scale < 8.0 && this.age < 175) {
            this.scale = (float) ((double) this.scale + 0.3);
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.setFrame(this.sprites, 7 - this.age * 8 / this.lifetime, FRAMES);
        if (this.age >= 175) {
            this.scale = (float) ((double) this.scale - 0.3);
        }
        this.yd -= 0.04 * (double) this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98f;
        this.yd += 1.0E-16;
        this.zd *= 0.98f;
        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
            this.yd += 1.0E-5;
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
            return new ParticleFog(level, x, y, z, xSpeed, ySpeed, zSpeed, options.r(), options.g(), options.b(), this.sprites);
        }
    }
}
