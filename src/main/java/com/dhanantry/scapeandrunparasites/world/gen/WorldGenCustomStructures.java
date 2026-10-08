package com.dhanantry.scapeandrunparasites.world.gen;

import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class WorldGenCustomStructures
implements IWorldGenerator {
    public void generate(RandomSource random, int chunkX, int chunkZ, Level world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    }

    private void generateStructure(WorldGenerator generator, Level world, RandomSource random, int chunkX, int chunkZ, int chance, Block topBlock, Class<?> ... classes) {
        ArrayList classList = new ArrayList(Arrays.asList(classes));
        int x = chunkX * 16 + random.nextInt(15);
        int z = chunkZ * 16 + random.nextInt(15);
        int y = WorldGenCustomStructures.calculateHeight(world, x, z, topBlock);
        BlockPos pos = BlockPos.containing(x, y, z);
        Class<?> biome = world.dimensionType().getBiomeForCoords(pos).getClass();
        boolean debug = true;
        if ((world.getWorldType() != WorldType.FLAT || debug) && (classList.contains(biome) || debug) && random.nextInt(chance) == 0) {
            generator.generate(world, random, pos);
        }
    }

    private static int calculateHeight(Level world, int x, int z, Block topBlock) {
        int y = world.getHeight();
        boolean flag = false;
        while (!flag && y-- >= 0) {
            Block block = world.getBlockState(BlockPos.containing(x, y, z)).getBlock();
            flag = block == topBlock;
        }
        return y;
    }

    public static void generateInPosition(WorldGenStructure generator, RandomSource random, Level world, BlockPos pos, int offsetX, int offsetY, int offsetZ) {
        generator.generate(world, random, BlockPos.containing(pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ));
    }
}

