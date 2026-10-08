package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleCoolerFog extends LegacyParticle {
    /** Textures of the description: fog_intro1..5 (0-4) then fog1..4 (5-8). */
    private static final int SPRITES = 9;
    /** Sprite shown from every eighth tick of the (doubled) age on: intro 1-5, fog 1-4 twice over, intro 5-1 back. */
    private static final int[] FRAMES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 5, 6, 7, 8, 4, 3, 2, 1, 0};
    private final SpriteSet sprites;
    private final float speedHor;
    private final float speedVer;

    protected ParticleCoolerFog(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.setFrame(sprites, 0, SPRITES);
        this.scale = (float) (10.0 + Math.random() * 0.5);
        this.lifetime = 144;
        this.alpha = 0.9f;
        float sXZ = 0.01f;
        this.speedHor = (float) (Math.random() * (double) sXZ - Math.random() * (double) sXZ);
        float sY = 0.005f;
        this.speedVer = (float) (Math.random() * (double) sY - Math.random() * (double) sY);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int age = this.age++;
        this.y -= (double) this.speedVer;
        this.x += (double) this.speedHor;
        this.z += (double) this.speedHor;
        this.setFrame(this.sprites, FRAMES[Math.min(age / 8, FRAMES.length - 1)], SPRITES);
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
            return new ParticleCoolerFog(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
