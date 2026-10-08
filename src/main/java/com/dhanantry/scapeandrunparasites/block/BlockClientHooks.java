package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.fx.ClientSRPParticles;
import com.dhanantry.scapeandrunparasites.client.fx.ParticleInfestedLeaf;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.client.particle.SRPParticleRegistry;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Client-only particle code of the block {@code randomDisplayTick} methods (1.12 {@code ParticleSpawner} calls).
 * Only called from {@code animateTick}, which runs on the logical client.
 */
public final class BlockClientHooks {
    private BlockClientHooks() {
    }

    /** True when the block above is air or a parasite bush (the early-return test shared by all spore emitters). */
    public static boolean openAbove(Level level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return above.is(Blocks.AIR) || above.is(SRPBlocks.ParasiteBush.get());
    }

    /** Spore cloud above the block ({@code rsBlockParticleS} chance), only if the space above is open. */
    public static void spore(Level level, BlockPos pos, RandomSource rand) {
        if (!openAbove(level, pos)) {
            return;
        }
        if (rand.nextDouble() <= (double) SRPConfigSystems.rsBlockParticleS) {
            double d0 = pos.getX() + rand.nextDouble();
            double d1 = pos.getY() + 2.5;
            double d2 = pos.getZ() + rand.nextDouble();
            ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
        }
    }

    /** Infestation stage 2 spores / stage 3 fog + spores, as in the infested stain, rubble and sand blocks. */
    public static void stageParticles(Level level, BlockPos pos, int stage, RandomSource rand) {
        if (!openAbove(level, pos)) {
            return;
        }
        if (stage == 2) {
            if (rand.nextDouble() <= (double) SRPConfigSystems.rsBlockParticleS) {
                double d0 = pos.getX() + rand.nextDouble();
                double d1 = pos.getY() + 2.5;
                double d2 = pos.getZ() + rand.nextDouble();
                ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
            }
        } else if (stage == 3) {
            if (rand.nextDouble() <= (double) SRPConfigSystems.rsBlockParticleF) {
                double d0 = pos.getX() + rand.nextDouble();
                double d1 = pos.getY() + 1.5;
                double d2 = pos.getZ() + rand.nextDouble();
                int g = 200;
                if (rand.nextInt(3) == 0) {
                    g += 50;
                }
                ParticleSpawner.spawnParticle(SRPEnumParticle.FOG, d0, d1, d2, 0.0, 1.0E-4, 0.0, 0, g, 0);
            }
            if (rand.nextDouble() <= (double) SRPConfigSystems.rsBlockParticleS) {
                double d0 = pos.getX() + rand.nextDouble();
                double d1 = pos.getY() + 2.5;
                double d2 = pos.getZ() + rand.nextDouble();
                ParticleSpawner.spawnParticle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
            }
        }
    }

    /** Rare green dot above an infested stain whose upper face is exposed. */
    public static void stainDot(Level level, BlockPos pos, RandomSource rand) {
        BlockPos above = pos.above();
        boolean openAbove = level.isEmptyBlock(above) || !level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN);
        if (openAbove & rand.nextFloat() < 0.001f) {
            double x = pos.getX() + 0.5 + rand.nextDouble() * 0.5;
            double y = pos.getY() + 1.0;
            double z = pos.getZ() + 0.5 + rand.nextDouble() * 0.5;
            ParticleSpawner.spawnParticle(SRPEnumParticle.DOT, x, y, z, 0.0, 0.0, 0.0, 17, 74, 20);
        }
    }

    /** Falling infested-leaf particle below infested leaves. */
    public static void infestedLeaf(Level level, double x, double y, double z) {
        if (!ClientSRPParticles.canSpawn()) {
            return;
        }
        ParticleInfestedLeaf p = new ParticleInfestedLeaf(level, x, y, z);
        ClientSRPParticles.fx().add(p);
        ClientSRPParticles.onSpawn();
    }

    /** Client handler of {@code SRPPacketParticle}. */
    public static void particleBurst(Level level, double x, double y, double z, float width, float height, byte type) {
        RandomSource rand = RandomSource.create();
        switch (type) {
            case 1:
                burst(SRPEnumParticle.GSPLASH, rand, x, y, z, width, height, 0, 0, 0);
                break;
            case 2:
                for (int i = 0; i <= 3; ++i) {
                    burst(SRPEnumParticle.GCLOUD, rand, x, y, z, width, height, 0, 0, 0);
                }
                break;
            case 3:
                for (int i = 0; i <= 5; ++i) {
                    burst(SRPEnumParticle.RHAPPY, rand, x, y, z, width, height, 0, 0, 0);
                }
                break;
            case 4:
                for (int i = 0; i <= 23; ++i) {
                    burst(SRPEnumParticle.GCLOUD, rand, x, y, z, width, height, 125, 227, 118);
                }
                break;
            case 5:
                break;
            case 10: {
                int i;
                burst(SRPEnumParticle.GSPLASH, rand, x, y, z, width, height, 0, 0, 0);
                burst(SRPEnumParticle.GSPLASH, rand, x, y, z, width, height, 0, 0, 0);
                burst(SRPEnumParticle.GSPLASH, rand, x, y, z, width, height, 0, 0, 0);
                for (i = 0; i <= 20; ++i) {
                    if (i % 5 != 0) {
                        continue;
                    }
                    burstGore(x, y, z, SRPEnumParticle.GSPLASH, 0, -1, -1);
                }
                for (i = 0; i <= 20; ++i) {
                    if (i % 5 != 0) {
                        continue;
                    }
                    burst(SRPEnumParticle.GCLOUD, rand, x, y, z, width, height, 127, 0, 0);
                }
                break;
            }
            case 11: {
                int i;
                for (i = 0; i <= 160; ++i) {
                    if (i % 5 != 0) {
                        continue;
                    }
                    burstGore(x, y, z, SRPEnumParticle.GSPLASH, 0, -1, -1);
                }
                for (i = 0; i <= 60; ++i) {
                    if (i % 5 != 0) {
                        continue;
                    }
                    burst(SRPEnumParticle.GCLOUD, rand, x, y, z, width, height, 127, 0, 0);
                }
                break;
            }
            case 12:
                burst(SRPEnumParticle.RAGE, rand, x, y, z, width, height, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    private static void burst(SRPEnumParticle particleType, RandomSource rand, double x, double y, double z, float width, float height, int r, int g, int b) {
        double d0 = rand.nextGaussian() * 0.02;
        double d1 = rand.nextGaussian() * 0.02;
        double d2 = rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, x + (double) (rand.nextFloat() * width * 2.0f) - (double) width, y + 0.5 + (double) (rand.nextFloat() * height),
                z + (double) (rand.nextFloat() * width * 2.0f) - (double) width, d0, d1, d2, r, g, b);
    }

    private static void burstGore(double px, double py, double pz, SRPEnumParticle particleType, int r, int g, int b) {
        double xF = 1.0;
        double yF = 4.0;
        RandomSource rand = RandomSource.create();
        double d0 = (float) px + rand.nextFloat();
        double d1 = (float) py + rand.nextFloat();
        double d2 = (float) pz + rand.nextFloat();
        double d3 = d0 - px;
        double d4 = d1 - py;
        double d5 = d2 - pz;
        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d3 = d3 * (d7 *= (double) (rand.nextFloat() * rand.nextFloat() + 0.3f)) * xF;
        d4 = d4 * d7 * yF;
        d5 = d5 * d7 * xF;
        d3 = Math.min(d3, 0.2) * (Math.random() * 2.0 - 1.0);
        d4 = Math.min(d4, 0.6) * (Math.random() * 2.0 - 1.0);
        d5 = Math.min(d5, 0.2) * (Math.random() * 2.0 - 1.0);
        ParticleSpawner.spawnParticle(particleType, px, py + 0.2 + 1.0, pz, d3, d4, d5, r, g, b);
    }

    /** Client handler of {@code MsgSpawnPureParticles}. */
    public static void pureBurst(Level level, double x, double y, double z, int count, int kind) {
        SRPParticleRegistry.spawnPureBurst(level, x, y, z, count, kind);
    }

    public static void flash(double x, double y, double z, int r, int g, int b) {
        ParticleSpawner.spawnParticle(SRPEnumParticle.FLASH, x, y, z, 0.0, 0.0, 0.0, r, g, b);
    }

    public static void particle(SRPEnumParticle type, double x, double y, double z, double vx, double vy, double vz, int r, int g, int b) {
        ParticleSpawner.spawnParticle(type, x, y, z, vx, vy, vz, r, g, b);
    }
}
