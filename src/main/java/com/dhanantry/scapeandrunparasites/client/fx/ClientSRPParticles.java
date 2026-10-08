package com.dhanantry.scapeandrunparasites.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.RandomSource;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSprites;

/** Per-tick budget of the directly created fx particles and their shared sprites. Only used on the logical client. */
public final class ClientSRPParticles {
    private static final int MAX_PER_TICK = 68;
    private static final String[] INFESTED_LEAF_NAMES = {"infested_leaves", "infested_leaves2", "infested_leaves3", "infested_leaves4"};
    private static int spawnedThisTick = 0;

    private ClientSRPParticles() {
    }

    public static boolean canSpawn() {
        return spawnedThisTick < MAX_PER_TICK;
    }

    public static void onSpawn() {
        ++spawnedThisTick;
    }

    /** Called at the end of every client tick (see EffectsClientEvents). */
    public static void resetTick() {
        spawnedThisTick = 0;
    }

    public static ParticleEngine fx() {
        return Minecraft.getInstance().particleEngine;
    }

    public static int infestedLeafCount() {
        return INFESTED_LEAF_NAMES.length;
    }

    public static TextureAtlasSprite infestedLeaf(int index) {
        return ParticleSprites.mod(INFESTED_LEAF_NAMES[index]);
    }

    public static TextureAtlasSprite randomInfestedLeaf(RandomSource rand) {
        return infestedLeaf(rand.nextInt(INFESTED_LEAF_NAMES.length));
    }
}
