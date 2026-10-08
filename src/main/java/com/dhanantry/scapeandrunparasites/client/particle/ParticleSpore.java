package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleSpore extends LegacyParticle {
    private static final int SPRITES = 4;

    public ParticleSpore(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x + 0.0, y, z + 0.0, xSpeed, ySpeed, zSpeed);
        this.setFrame(sprites, 0, SPRITES);
        if (Math.random() <= 0.5) {
            this.setFrame(sprites, 1, SPRITES);
        } else if (Math.random() <= 0.5) {
            this.setFrame(sprites, 2, SPRITES);
        } else if (Math.random() <= 0.5) {
            this.setFrame(sprites, 3, SPRITES);
        }
        this.xd = xSpeed * (double) 0.2f + (Math.random() * 2.0 - 1.0) * (double) 0.02f;
        this.yd = ySpeed * (double) 0.2f + (Math.random() * 2.0 - 1.0) * (double) 0.02f;
        this.zd = zSpeed * (double) 0.2f + (Math.random() * 2.0 - 1.0) * (double) 0.02f;
        this.scale = 0.0f;
        this.lifetime = 200;
        this.gravity = 0.0f;
        float color = (float) (1.0 - Math.random() * 0.5);
        this.alpha = 0.6f;
        this.rCol = color;
        this.gCol = color;
        this.bCol = color;
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
        if ((double) this.scale < 0.7 && this.age < 175) {
            this.scale += 0.05f;
        }
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        if (this.age >= 175) {
            this.scale -= 0.0278f;
            this.alpha -= 0.01f;
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
            return new ParticleSpore(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
