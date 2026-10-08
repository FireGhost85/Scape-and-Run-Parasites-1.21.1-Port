package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleBiomass extends LegacyParticle {
    private static final int SPRITES = 4;
    private final SpriteSet sprites;

    protected ParticleBiomass(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        ParticleSpawner.spawnParticle(level, SRPEnumParticle.DOT, x, y, z, 0.0, 0.0, 0.0, 165, 255, 0);
        ParticleSpawner.spawnParticle(level, SRPEnumParticle.DOT, x, y, z, 0.0, 0.0, 0.0, 60, 229, 27);
        this.setFrame(sprites, 0, SPRITES);
        if (Math.random() <= 0.5) {
            this.setFrame(sprites, 1, SPRITES);
        } else if (Math.random() <= 0.5) {
            this.setFrame(sprites, 2, SPRITES);
        } else if (Math.random() <= 0.5) {
            this.setFrame(sprites, 3, SPRITES);
        }
        this.scale = 3.0f;
        this.lifetime = 45;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
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
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        if (this.age >= 25) {
            this.scale -= 0.1f;
            this.rCol -= 0.03f;
            this.gCol -= 0.02f;
            this.bCol -= 0.025f;
        }
        this.yd += 0.005;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98f;
        this.yd *= 0.98f;
        this.zd *= 0.98f;
        if (this.onGround) {
            this.xd *= 0.7f;
            this.zd *= 0.7f;
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
            return new ParticleBiomass(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
