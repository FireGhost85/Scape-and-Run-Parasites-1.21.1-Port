package com.dhanantry.scapeandrunparasites.world.gen;

import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * The 1.12 class was an IWorldGenerator (random structures per chunk, disabled in the original: {@code generate} was empty);
 * only the placement helper of the features is kept.
 */
public class WorldGenCustomStructures {
    public static void generateInPosition(WorldGenStructure generator, RandomSource random, Level world, BlockPos pos, int offsetX, int offsetY, int offsetZ) {
        generator.generate(world, random, BlockPos.containing(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ));
    }
}
