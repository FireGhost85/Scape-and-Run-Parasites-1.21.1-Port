package com.dhanantry.scapeandrunparasites.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/** Client effects of the Vengeance grapple and its lightning, built from base game particles. */
public final class ParticleVengeance {
    private static final RandomSource RAND = RandomSource.create();

    private ParticleVengeance() {
    }

    private static ClientLevel world() {
        return Minecraft.getInstance().level;
    }

    public static void electricSparksAround(Vec3 center, float radius, int count) {
        ClientLevel w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            double ang = RAND.nextDouble() * Math.PI * 2.0;
            double r = (double) radius * (0.2 + RAND.nextDouble() * 0.8);
            double px = center.x + Math.cos(ang) * r + (RAND.nextDouble() - 0.5) * 0.2;
            double py = center.y + 0.2 + RAND.nextDouble() * 1.4;
            double pz = center.z + Math.sin(ang) * r + (RAND.nextDouble() - 0.5) * 0.2;
            double vx = (RAND.nextDouble() - 0.5) * 0.8;
            double vy = (RAND.nextDouble() - 0.5) * 0.3;
            double vz = (RAND.nextDouble() - 0.5) * 0.8;
            w.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.65f, 0.1f, 0.9f), px, py, pz, 0.0, 0.0, 0.0);
            if (RAND.nextInt(3) == 0) {
                w.addParticle(ParticleTypes.ENCHANTED_HIT, px, py, pz, vx, vy, vz);
            }
            if (RAND.nextInt(4) != 0) continue;
            w.addParticle(ParticleTypes.END_ROD, px, py, pz, vx * 0.2, vy * 0.2, vz * 0.2);
        }
    }

    public static void impactDust(Vec3 hitPos, int count) {
        ClientLevel w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        BlockParticleOption dust = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());
        for (int i = 0; i < count; ++i) {
            double px = hitPos.x + (RAND.nextDouble() - 0.5) * 0.4;
            double py = hitPos.y + RAND.nextDouble() * 0.2;
            double pz = hitPos.z + (RAND.nextDouble() - 0.5) * 0.4;
            double vx = (RAND.nextDouble() - 0.5) * 0.35;
            double vy = 0.1 + RAND.nextDouble() * 0.25;
            double vz = (RAND.nextDouble() - 0.5) * 0.35;
            w.addParticle(dust, px, py, pz, vx, vy, vz);
            if (!RAND.nextBoolean()) continue;
            w.addParticle(ParticleTypes.DAMAGE_INDICATOR, px, py + 0.1, pz, 0.0, 0.05, 0.0);
        }
    }

    public static void heavyBleedAround(Vec3 center, int count) {
        ClientLevel w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            double px = center.x + (RAND.nextDouble() - 0.5) * 0.8;
            double py = center.y + 0.3 + RAND.nextDouble() * 1.2;
            double pz = center.z + (RAND.nextDouble() - 0.5) * 0.8;
            w.addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.55f, 0.02f, 0.02f), px, py, pz, 0.0, 0.0, 0.0);
            if (RAND.nextInt(4) != 0) continue;
            w.addParticle(ParticleTypes.DRIPPING_LAVA, px, py, pz, 0.0, 0.0, 0.0);
        }
    }

    public static void lightningExplosion(Vec3 pos) {
        double vz;
        double vy;
        double vx;
        int i;
        ClientLevel w = ParticleVengeance.world();
        if (w == null) {
            return;
        }
        w.addParticle(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y + 0.5, pos.z, 0.0, 0.0, 0.0);
        for (i = 0; i < 12; ++i) {
            vx = (RAND.nextDouble() - 0.5) * 0.6;
            vy = RAND.nextDouble() * 0.4;
            vz = (RAND.nextDouble() - 0.5) * 0.6;
            w.addParticle(ParticleTypes.LARGE_SMOKE, pos.x, pos.y + 0.3, pos.z, vx, vy, vz);
        }
        for (i = 0; i < 18; ++i) {
            vx = (RAND.nextDouble() - 0.5) * 1.1;
            vy = RAND.nextDouble() * 0.7;
            vz = (RAND.nextDouble() - 0.5) * 1.1;
            w.addParticle(ParticleTypes.ENCHANTED_HIT, pos.x, pos.y + 0.6, pos.z, vx, vy, vz);
        }
    }
}
