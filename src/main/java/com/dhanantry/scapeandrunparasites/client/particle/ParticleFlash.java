package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleFlash extends LegacyParticle {
    private final int R;
    private final int G;
    private final int B;

    protected ParticleFlash(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int r, int g, int b, SpriteSet sprites) {
        super(level, x, y, z, (double) r, (double) g, (double) b);
        this.R = r;
        this.G = g;
        this.B = b;
        this.setFrame(sprites, 0, 1);
        this.scale = 6.0f;
        this.lifetime = 8;
        if (ySpeed != 0.0 || xSpeed != 0.0 || zSpeed != 0.0) {
            this.xd = xSpeed;
            this.yd = ySpeed;
            this.zd = zSpeed;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        return 0xF0 | i << 16;
    }

    @Override
    public void tick() {
        int age = this.age++;
        float rot = 20.0f;
        if (age >= this.lifetime) {
            this.remove();
            return;
        }
        this.scale = 8.0f / ((float) age * 0.5f);
        this.alpha = (float) age / 0.1f;
        this.setExtrapolatedRoll((float) (Math.sin(rot * (float) age) * Math.cos(rot * (float) age)));
        this.rCol = age * this.R;
        this.bCol = age * this.B;
        this.gCol = age * this.G;
    }

    public static class Provider implements ParticleProvider<SRPParticleOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Nullable
        @Override
        public Particle createParticle(SRPParticleOptions options, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleFlash(level, x, y, z, xSpeed, ySpeed, zSpeed, options.r(), options.g(), options.b(), this.sprites);
        }
    }
}
