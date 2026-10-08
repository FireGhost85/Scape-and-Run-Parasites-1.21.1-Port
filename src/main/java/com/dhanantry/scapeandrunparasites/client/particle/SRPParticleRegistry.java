package com.dhanantry.scapeandrunparasites.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/** Client side spawner of the purification particles (client only). */
public final class SRPParticleRegistry {
    private SRPParticleRegistry() {
    }

    @Nullable
    public static TextureAtlasSprite randPureSprite(RandomSource r) {
        return r.nextBoolean() ? ParticleSprites.mod("pure1") : ParticleSprites.mod("pure2");
    }

    public static void spawnPureBurst(Level world, double x, double y, double z, int count, int kind) {
        if (!(world instanceof ClientLevel level)) {
            return;
        }
        TextureAtlasSprite pure1 = ParticleSprites.mod("pure1");
        TextureAtlasSprite pure2 = ParticleSprites.mod("pure2");
        for (int i = 0; i < count; ++i) {
            TextureAtlasSprite sprite = (i & 1) == 0 ? pure1 : pure2;
            ParticlePure p = new ParticlePure(level, x, y, z, kind, sprite);
            Minecraft.getInstance().particleEngine.add(p);
        }
    }
}
