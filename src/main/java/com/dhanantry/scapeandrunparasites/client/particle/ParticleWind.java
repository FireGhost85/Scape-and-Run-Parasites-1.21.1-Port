package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleWind extends LegacyParticle {
    private static final int SPRITES = 13;
    private final SpriteSet sprites;

    protected ParticleWind(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.setFrame(sprites, 0, SPRITES);
        this.scale = 2.0f;
        this.lifetime = 52;
        this.hasPhysics = true;
        this.alpha = 0.5f;
        if (ySpeed != 0.0 || xSpeed != 0.0 || zSpeed != 0.0) {
            this.xd = xSpeed;
            this.yd = ySpeed;
            this.zd = zSpeed;
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        int age = this.age++;
        this.y += (double) age * this.yd;
        this.x += this.xd + (Math.random() * ((double) age * 0.005) - Math.random() * ((double) age * 0.005));
        this.z += this.zd + (Math.random() * ((double) age * 0.005) - Math.random() * ((double) age * 0.005));
        if (age >= 4) {
            this.setFrame(this.sprites, Math.min(age / 4, SPRITES - 1), SPRITES);
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
            return new ParticleWind(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
