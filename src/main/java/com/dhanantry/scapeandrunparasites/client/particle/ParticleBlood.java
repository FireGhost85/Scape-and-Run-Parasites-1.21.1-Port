package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleBlood extends LegacyParticle {
    private static final int SPRITES = 3;
    private static final int BLOOD1 = 0;
    private static final int BLOOD2 = 1;
    private static final int BLOOD_LAND = 2;
    private final SpriteSet sprites;
    private final int type;
    private final float mroew = (float) ((Math.random() - Math.random()) * 0.05);

    protected ParticleBlood(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int r, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.type = r;
        this.setFrame(sprites, BLOOD1, SPRITES);
        this.scale = 2.0f;
        this.lifetime = 40;
        this.alpha = 0.9f;
        if (this.type == 1) {
            this.xd = this.mroew;
            this.yd = ySpeed;
            this.zd = this.mroew;
        }
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        int age = this.age++;
        this.move(this.xd, this.yd, this.zd);
        if (this.type == 0) {
            this.yd -= 0.5;
        }
        if (this.type == 1) {
            this.lifetime = 80;
            this.xo = this.x;
            this.yo = this.y;
            this.zo = this.z;
            this.yd = 0.31;
            this.xd += (double) this.mroew * 0.5;
            this.zd += (double) this.mroew * 0.5;
            this.gravity = 0.04f;
        }
        if (age >= 4) {
            this.setFrame(this.sprites, BLOOD2, SPRITES);
        }
        if (this.onGround) {
            this.setFrame(this.sprites, BLOOD_LAND, SPRITES);
            this.xd /= 10.0;
            this.zd /= 10.0;
        }
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
            return new ParticleBlood(level, x, y, z, xSpeed, ySpeed, zSpeed, options.r(), this.sprites);
        }
    }
}
