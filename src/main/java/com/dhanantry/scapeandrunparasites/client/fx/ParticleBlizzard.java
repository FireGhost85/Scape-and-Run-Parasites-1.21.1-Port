package com.dhanantry.scapeandrunparasites.client.fx;

import com.dhanantry.scapeandrunparasites.client.particle.LegacyParticle;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSprites;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

/**
 * Snowflake of the extreme snow storm. 1.10.9 extended the base game's snow shovel particle, which no longer exists, so its
 * behaviour (frames of the generic particle, grow-in size, damping) is part of this class.
 */
public class ParticleBlizzard extends LegacyParticle {
    private static final String[] GENERIC = {"generic_0", "generic_1", "generic_2", "generic_3", "generic_4", "generic_5", "generic_6", "generic_7"};
    @Nullable
    private final LocalPlayer focus;
    private final float snowDigSize;
    private int frame = 0;

    public ParticleBlizzard(ClientLevel w, double x, double y, double z, double vx, double vy, double vz, @Nullable LocalPlayer focus) {
        super(w, x, y, z, vx, vy, vz);
        this.setSprite(ParticleSprites.vanilla(GENERIC[0]));
        this.xd *= (double) 0.1f;
        this.yd *= (double) 0.1f;
        this.zd *= (double) 0.1f;
        this.xd += vx;
        this.yd += vy;
        this.zd += vz;
        float f = 1.0f - (float) (Math.random() * (double) 0.3f);
        this.rCol = f;
        this.gCol = f;
        this.bCol = f;
        this.scale *= 0.75f;
        this.snowDigSize = this.scale;
        this.focus = focus;
        this.gravity = 0.06f;
        this.alpha = 0.95f;
        this.hasPhysics = true;
        this.lifetime = 200;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float f = ((float) this.age + partialTick) / (float) this.lifetime * 32.0f;
        f = Mth.clamp(f, 0.0f, 1.0f);
        return 0.1f * (this.snowDigSize * f);
    }

    @Override
    public void tick() {
        this.yd -= 0.02;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
        int next = Mth.clamp(7 - this.age * 8 / this.lifetime, 0, 7);
        if (next != this.frame) {
            this.frame = next;
            this.setSprite(ParticleSprites.vanilla(GENERIC[next]));
        }
        this.yd -= 0.03;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= (double) 0.99f;
        this.yd *= (double) 0.99f;
        this.zd *= (double) 0.99f;
        if (this.onGround) {
            this.remove();
            return;
        }
        if (this.focus != null && this.focus.distanceToSqr(this.x, this.y, this.z) > 400.0) {
            this.remove();
        }
    }
}
