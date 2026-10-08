package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

/**
 * Common base keeping the 1.12 particleScale semantic (quad half size = 0.1 * scale), the fixed sprite frames of the
 * 1.10.9 particles and their block-light glow.
 */
public abstract class LegacyParticle extends TextureSheetParticle {
    /** 1.12 particleScale. */
    protected float scale;

    protected LegacyParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.scale = this.quadSize / 0.1f;
    }

    protected LegacyParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.scale = this.quadSize / 0.1f;
    }

    @Override
    public float getQuadSize(float partialTick) {
        return 0.1f * this.scale;
    }

    /** Selects the sprite of the given frame index of a particle description with {@code count} textures. */
    protected void setFrame(SpriteSet sprites, int index, int count) {
        this.setSprite(sprites.get(Mth.clamp(index, 0, count - 1), Math.max(count - 1, 1)));
    }

    /**
     * 1.12 rendered the angle as {@code angle + (angle - prevAngle) * partialTicks} and these particles never stored the previous
     * angle, so the angle seen on screen is {@code angle * (1 + partialTicks)}, which is what Mth.lerp(partialTicks, oRoll, roll) gives for these values.
     */
    protected void setExtrapolatedRoll(float angle) {
        this.oRoll = angle;
        this.roll = angle * 2.0f;
    }

    /** Raises the block light of the particle towards 15 while it ages (the getBrightnessForRender override shared by several particles). */
    protected int glowingLightColor(float partialTick) {
        float f = Mth.clamp(((float) this.age + partialTick) / (float) this.lifetime, 0.0f, 1.0f);
        int i = super.getLightColor(partialTick);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        if ((j += (int) (f * 15.0f * 16.0f)) > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
