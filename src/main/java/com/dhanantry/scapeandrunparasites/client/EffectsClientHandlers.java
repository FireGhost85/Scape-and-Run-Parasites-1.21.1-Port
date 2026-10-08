package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.client.fx.ClientExtremeSnow;
import com.dhanantry.scapeandrunparasites.client.fx.ParticleVengeance;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.client.particle.SRPParticleRegistry;
import com.dhanantry.scapeandrunparasites.network.ExtremeSnowPayload;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.network.PureParticlesPayload;
import com.dhanantry.scapeandrunparasites.network.VengeanceFxPayload;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client handlers of the effect and particle payloads. */
public final class EffectsClientHandlers {
    private EffectsClientHandlers() {
    }

    public static void particle(ParticlePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> handleParticle(msg));
    }

    public static void pureParticles(PureParticlesPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            SRPParticleRegistry.spawnPureBurst(ctx.player().level(), msg.x(), msg.y(), msg.z(), msg.count(), msg.kind());
        });
    }

    public static void vengeanceFx(VengeanceFxPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Vec3 pos = new Vec3(msg.x(), msg.y(), msg.z());
            switch (msg.fxType()) {
                case VengeanceFxPayload.SPARKS -> ParticleVengeance.electricSparksAround(pos, msg.a(), msg.count());
                case VengeanceFxPayload.IMPACT_DUST -> ParticleVengeance.impactDust(pos, msg.count());
                case VengeanceFxPayload.HEAVY_BLEED -> ParticleVengeance.heavyBleedAround(pos, msg.count());
                case VengeanceFxPayload.LIGHTNING_EXPL -> ParticleVengeance.lightningExplosion(pos);
                default -> {
                }
            }
        });
    }

    public static void extremeSnow(ExtremeSnowPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientExtremeSnow.setState(msg.enabled(), msg.intensity(), msg.anywhere(), msg.windDeg(), msg.windSpeed()));
    }

    private static void handleParticle(ParticlePayload message) {
        RandomSource rand = RandomSource.create();
        switch (message.particleType()) {
            case 1: {
                spawnParticles(SRPEnumParticle.GSPLASH, rand, message, 0, 0, 0);
                break;
            }
            case 2: {
                for (int i = 0; i <= 3; ++i) {
                    spawnParticles(SRPEnumParticle.GCLOUD, rand, message, 0, 0, 0);
                }
                break;
            }
            case 3: {
                for (int i = 0; i <= 5; ++i) {
                    spawnParticles(SRPEnumParticle.RHAPPY, rand, message, 0, 0, 0);
                }
                break;
            }
            case 4: {
                for (int i = 0; i <= 23; ++i) {
                    spawnParticles(SRPEnumParticle.GCLOUD, rand, message, 125, 227, 118);
                }
                break;
            }
            case 5: {
                break;
            }
            case 10: {
                int i;
                spawnParticles(SRPEnumParticle.GSPLASH, rand, message, 0, 0, 0);
                spawnParticles(SRPEnumParticle.GSPLASH, rand, message, 0, 0, 0);
                spawnParticles(SRPEnumParticle.GSPLASH, rand, message, 0, 0, 0);
                for (i = 0; i <= 20; ++i) {
                    if (i % 5 != 0) continue;
                    spawnParticlesGore(message, SRPEnumParticle.GSPLASH, 0, -1, -1);
                }
                for (i = 0; i <= 20; ++i) {
                    if (i % 5 != 0) continue;
                    spawnParticles(SRPEnumParticle.GCLOUD, rand, message, 127, 0, 0);
                }
                break;
            }
            case 11: {
                int i;
                for (i = 0; i <= 160; ++i) {
                    if (i % 5 != 0) continue;
                    spawnParticlesGore(message, SRPEnumParticle.GSPLASH, 0, -1, -1);
                }
                for (i = 0; i <= 60; ++i) {
                    if (i % 5 != 0) continue;
                    spawnParticles(SRPEnumParticle.GCLOUD, rand, message, 127, 0, 0);
                }
                break;
            }
            case 12: {
                spawnParticles(SRPEnumParticle.RAGE, rand, message, 0, 0, 0);
                break;
            }
            default:
                break;
        }
    }

    private static void spawnParticles(SRPEnumParticle particleType, RandomSource rand, ParticlePayload message, int r, int g, int b) {
        double d0 = rand.nextGaussian() * 0.02;
        double d1 = rand.nextGaussian() * 0.02;
        double d2 = rand.nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, message.x() + (double) (rand.nextFloat() * message.width() * 2.0f) - (double) message.width(), message.y() + 0.5 + (double) (rand.nextFloat() * message.height()), message.z() + (double) (rand.nextFloat() * message.width() * 2.0f) - (double) message.width(), d0, d1, d2, r, g, b);
    }

    private static void spawnParticlesGore(ParticlePayload message, SRPEnumParticle particleType, int r, int g, int b) {
        double xF = 1.0;
        double yF = 4.0;
        RandomSource rand = RandomSource.create();
        double d0 = (float) message.x() + rand.nextFloat();
        double d1 = (float) message.y() + rand.nextFloat();
        double d2 = (float) message.z() + rand.nextFloat();
        double d3 = d0 - message.x();
        double d4 = d1 - message.y();
        double d5 = d2 - message.z();
        double d6 = (float) Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
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
        ParticleSpawner.spawnParticle(particleType, message.x(), message.y() + 0.2 + 1.0, message.z(), d3, d4, d5, r, g, b);
    }
}
