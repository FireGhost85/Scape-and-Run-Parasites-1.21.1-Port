package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/** Smoke tinted with the option colour. Uses the eight frames of the base game's generic particle. */
public class ParticleRGBSmoke extends LegacyParticle {
    private static final int FRAMES = 8;
    private final SpriteSet sprites;
    private final float oSize;

    protected ParticleRGBSmoke(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int r, int g, int b, SpriteSet sprites) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.sprites = sprites;
        this.setFrame(sprites, 0, FRAMES);
        this.xd *= (double) 0.1f;
        this.yd *= (double) 0.1f;
        this.zd *= (double) 0.1f;
        this.xd += xSpeed;
        this.yd += ySpeed;
        this.zd += zSpeed;
        this.rCol = (float) r / 255.0f;
        this.gCol = (float) g / 255.0f;
        this.bCol = (float) b / 255.0f;
        this.scale *= 0.75f;
        this.scale *= 2.5f;
        this.oSize = this.scale * 0.5f;
        this.lifetime = (int) (8.0 / (Math.random() * 0.8 + 0.3));
        this.lifetime = (int) ((float) this.lifetime * 2.5f);
    }

    @Override
    public float getQuadSize(float partialTick) {
        float f = ((float) this.age + partialTick) / (float) this.lifetime * 32.0f;
        f = Mth.clamp(f, 0.0f, 1.0f);
        return 0.1f * (this.oSize * f);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        this.setFrame(this.sprites, 7 - this.age * 8 / this.lifetime, FRAMES);
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.96f;
        this.yd *= 0.96f;
        this.zd *= 0.96f;
        Player entityplayer = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0, EntitySelector.NO_CREATIVE_OR_SPECTATOR);
        if (entityplayer != null) {
            AABB axisalignedbb = entityplayer.getBoundingBox();
            if (this.y > axisalignedbb.minY) {
                this.y += (axisalignedbb.minY - this.y) * 0.2;
                this.yd += (entityplayer.getDeltaMovement().y - this.yd) * 0.2;
                this.setPos(this.x, this.y, this.z);
            }
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
            return new ParticleRGBSmoke(level, x, y, z, xSpeed, ySpeed, zSpeed, options.r(), options.g(), options.b(), this.sprites);
        }
    }
}
