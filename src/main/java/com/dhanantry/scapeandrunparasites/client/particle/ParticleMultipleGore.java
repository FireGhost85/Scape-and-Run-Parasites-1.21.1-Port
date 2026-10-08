package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;

public class ParticleMultipleGore extends LegacyParticle {
    /** Textures of the description: assimilated 1-3 (0-2), pure 1-3 (3-5), primitive 1-3 (6-8), adapted 1-3 (9-11), vomit 1-6 (12-17). */
    private static final int SPRITES = 18;
    private static final int VOMIT = 12;

    protected ParticleMultipleGore(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int texture, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        if (texture >= 0 && texture <= 3) {
            int first = texture * 3;
            this.setFrame(sprites, first, SPRITES);
            if (Math.random() <= 0.5) {
                this.setFrame(sprites, first + 1, SPRITES);
            } else if (Math.random() <= 0.25) {
                this.setFrame(sprites, first + 2, SPRITES);
            }
        } else if (texture == 4) {
            this.setFrame(sprites, VOMIT, SPRITES);
            if (Math.random() <= 0.5) {
                this.setFrame(sprites, VOMIT + 1, SPRITES);
            } else if (Math.random() <= 0.5) {
                this.setFrame(sprites, VOMIT + 2, SPRITES);
            } else if (Math.random() <= 0.5) {
                this.setFrame(sprites, VOMIT + 3, SPRITES);
            } else if (Math.random() <= 0.5) {
                this.setFrame(sprites, VOMIT + 4, SPRITES);
            } else if (Math.random() <= 0.5) {
                this.setFrame(sprites, VOMIT + 5, SPRITES);
            }
        } else {
            this.setFrame(sprites, 0, SPRITES);
        }
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.setSize(0.01f, 0.01f);
        this.gravity = 0.04f;
        this.lifetime = 20 * (this.random.nextInt(3) + 1);
    }

    @Override
    public void tick() {
        int age = this.age++;
        float maxAge = this.lifetime;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd -= (double) this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98f;
        this.yd *= 0.98f;
        this.zd *= 0.98f;
        if (maxAge - (float) age <= 5.0f & this.alpha >= 0.0f) {
            this.alpha -= 0.05f;
        }
        if (this.lifetime-- <= 0) {
            this.remove();
        }
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
            return new ParticleMultipleGore(level, x, y, z, xSpeed, ySpeed, zSpeed, options.r(), this.sprites);
        }
    }
}
