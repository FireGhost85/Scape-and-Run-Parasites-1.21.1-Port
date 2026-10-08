package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleEen extends LegacyParticle {
    private final double coordX;
    private final double coordY;
    private final double coordZ;

    protected ParticleEen(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setFrame(sprites, 0, 1);
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.coordX = x;
        this.coordY = y;
        this.coordZ = z;
        this.xo = x + xSpeed;
        this.yo = y + ySpeed;
        this.zo = z + zSpeed;
        this.x = this.xo;
        this.y = this.yo;
        this.z = this.zo;
        this.lifetime = (int) (Math.random() * 10.0) + 30;
    }

    @Override
    public void move(double dx, double dy, double dz) {
        this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float partialTick) {
        int i = super.getLightColor(partialTick);
        float f = (float) this.age / (float) this.lifetime;
        f *= f;
        f *= f;
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((k += (int) (f * 15.0f * 16.0f)) > 240) {
            k = 240;
        }
        return j | k << 16;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = (float) this.age / (float) this.lifetime;
        f = 1.0f - f;
        float f1 = 1.0f - f;
        f1 *= f1;
        f1 *= f1;
        this.x = this.coordX + this.xd * (double) f;
        this.y = this.coordY + this.yd * (double) f - (double) (f1 * 1.2f);
        this.z = this.coordZ + this.zd * (double) f;
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
            return new ParticleEen(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
