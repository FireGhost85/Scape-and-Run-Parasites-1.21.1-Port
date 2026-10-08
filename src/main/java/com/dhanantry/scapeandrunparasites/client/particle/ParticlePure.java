package com.dhanantry.scapeandrunparasites.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/** Purification swirl. Created directly by {@link SRPParticleRegistry#spawnPureBurst}. */
public class ParticlePure extends LegacyParticle {
    public static final int KIND_WAVE = 0;
    public static final int KIND_PULSE = 1;
    private final double cx;
    private final double cy;
    private final double cz;
    private final int kind;
    private double angle;
    private double radius;
    private double omega;
    private double up;
    private double out;
    private float startScale;

    public ParticlePure(ClientLevel level, double x, double y, double z, int kind, TextureAtlasSprite sprite) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.cx = x;
        this.cy = y;
        this.cz = z;
        this.kind = kind;
        this.lifetime = 22 + this.random.nextInt(14);
        this.hasPhysics = false;
        this.gravity = 0.0f;
        this.alpha = 0.95f;
        this.angle = this.random.nextDouble() * Math.PI * 2.0;
        this.radius = 0.15 + this.random.nextDouble() * 0.25;
        this.omega = 0.2 + this.random.nextDouble() * 0.2;
        this.up = 0.01 + this.random.nextDouble() * 0.02;
        this.out = 0.07 + this.random.nextDouble() * 0.05;
        this.startScale = 0.9f + this.random.nextFloat() * 0.5f;
        this.scale = 0.0f;
        this.setSprite(sprite);
        this.x += (this.random.nextDouble() - 0.5) * 0.05;
        this.y += this.random.nextDouble() * 0.03;
        this.z += (this.random.nextDouble() - 0.5) * 0.05;
    }

    @Override
    public int getLightColor(float partialTick) {
        return this.glowingLightColor(partialTick);
    }

    @Override
    public void tick() {
        TextureAtlasSprite alt;
        int age;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if ((age = this.age++) >= this.lifetime) {
            this.remove();
            return;
        }
        float t = (float) age / (float) this.lifetime;
        float easeIn = (float) Math.min(1.0, (double) t * 8.0);
        float easeOut = (float) (1.0 - Math.max(0.0, (double) (t - 0.5f) * 2.0));
        this.scale = this.startScale * easeIn * easeOut;
        this.alpha = 0.95f * easeOut + 0.05f;
        if (age % 4 == 0 && this.random.nextBoolean() && (alt = SRPParticleRegistry.randPureSprite(this.random)) != null) {
            this.setSprite(alt);
        }
        if (this.kind == 0) {
            this.angle += this.omega;
            this.radius *= 0.992;
            double sx = Math.cos(this.angle) * this.radius;
            double sz = Math.sin(this.angle) * this.radius;
            double wob = Math.sin((double) age * 0.2 + this.angle) * 0.01;
            this.x = this.cx + sx + wob;
            this.z = this.cz + sz - wob;
            this.y += this.up;
        } else {
            double theta = this.angle;
            double r = (double) age * this.out;
            double curl = Math.sin((double) age * 0.25) * 0.06;
            double sx = Math.cos(theta) * r - Math.sin(theta) * curl;
            double sz = Math.sin(theta) * r + Math.cos(theta) * curl;
            this.x = this.cx + sx;
            this.z = this.cz + sz;
            this.y = this.cy + Math.sin((double) age * 0.25) * 0.03;
        }
    }
}
