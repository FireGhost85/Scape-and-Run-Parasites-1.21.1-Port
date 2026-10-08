package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleRage extends LegacyParticle {
    private static final int SPRITES = 3;

    protected ParticleRage(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.setFrame(sprites, 0, SPRITES);
        if (Math.random() <= 0.5) {
            this.setFrame(sprites, 1, SPRITES);
        } else if (Math.random() <= 0.5) {
            this.setFrame(sprites, 2, SPRITES);
        }
        this.scale = 2.0f;
        this.lifetime = 30;
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
        float mow = (float) (Math.random() - Math.random());
        float meow = (float) ((Math.random() - Math.random()) * 0.1);
        float mreow = (float) (Math.random() * 0.2);
        this.alpha -= 0.05f;
        this.scale -= 0.08f;
        this.rCol = 1.0f - mreow;
        this.gCol = 1.0f - mreow;
        this.bCol = 1.0f - mreow;
        this.y -= (double) (0.01f + meow);
        this.x += (double) meow;
        this.z += (double) meow;
        this.setExtrapolatedRoll(0.01f * (float) age * mow);
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
            return new ParticleRage(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
