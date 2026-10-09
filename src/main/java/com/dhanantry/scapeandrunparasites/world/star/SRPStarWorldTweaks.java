package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluids;

/**
 * SRPStarWorldTweaks of 1.10.9, applied to every newly generated overworld chunk: warm star worlds lose the surface water
 * between y 49 and 63, cold star worlds get snowy grass in place of the short and tall grass.
 *
 * <p>It runs on the chunk that is being generated, at the end of its decoration ({@code ChunkGeneratorMixin}). It was a handler of
 * the chunk load event first, which set the blocks of the finished chunk: the neighbour updates and the block placement hooks of
 * those blocks read the neighbouring chunks and stopped the world generation (the chunk task waited for chunks that wait for it).
 */
public final class SRPStarWorldTweaks {
    private static final int WARM_STAR_SEA_LEVEL = 48;
    private static final int VANILLA_SEA_LEVEL = 63;

    private SRPStarWorldTweaks() {
    }

    public static void apply(WorldGenLevel level, ChunkAccess chunk, int starType) {
        if (starType == 2) {
            dryWarmStarChunk(chunk);
        } else if (starType == 1) {
            snowColdStarGrass(level, chunk);
        }
    }

    private static void dryWarmStarChunk(ChunkAccess chunk) {
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = VANILLA_SEA_LEVEL; y > WARM_STAR_SEA_LEVEL; --y) {
                    pos.set(minX + x, y, minZ + z);
                    BlockState state = chunk.getBlockState(pos);
                    if (state.getFluidState().is(Fluids.WATER) || state.getFluidState().is(Fluids.FLOWING_WATER) || state.is(Blocks.ICE)) {
                        chunk.setBlockState(pos, Blocks.AIR.defaultBlockState(), false);
                    }
                }
            }
        }
    }

    private static void snowColdStarGrass(WorldGenLevel level, ChunkAccess chunk) {
        int minX = chunk.getPos().getMinBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                findAndConvertExposedGrass(level, chunk, minX + x, minZ + z);
            }
        }
    }

    private static void findAndConvertExposedGrass(WorldGenLevel level, ChunkAccess chunk, int x, int z) {
        int top = Math.min(level.getMaxBuildHeight() - 1, chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z));
        for (int y = top; y > level.getMinBuildHeight(); --y) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = chunk.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            if (isShortVegetation(state)) {
                convertShortGrass(level, chunk, pos);
            } else if (isTall(state, DoubleBlockHalf.UPPER)) {
                BlockPos lower = pos.below();
                if (isTall(chunk.getBlockState(lower), DoubleBlockHalf.LOWER)) {
                    convertTallGrass(level, chunk, lower, pos);
                }
            } else if (isTall(state, DoubleBlockHalf.LOWER)) {
                BlockPos upper = pos.above();
                if (isTall(chunk.getBlockState(upper), DoubleBlockHalf.UPPER)) {
                    convertTallGrass(level, chunk, pos, upper);
                }
            }
            return;
        }
    }

    private static boolean groundOk(BlockState ground) {
        Block b = ground.getBlock();
        return b == Blocks.GRASS_BLOCK || b == Blocks.DIRT || b == SRPBlocks.SnowCoveredGrass.get();
    }

    private static void convertShortGrass(WorldGenLevel level, ChunkAccess chunk, BlockPos pos) {
        if (!coldEnough(level, pos) || !groundOk(chunk.getBlockState(pos.below()))) {
            return;
        }
        chunk.setBlockState(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), false);
        ensureSnowCoveredGround(chunk, pos.below());
    }

    private static void convertTallGrass(WorldGenLevel level, ChunkAccess chunk, BlockPos lowerPos, BlockPos upperPos) {
        if (!coldEnough(level, lowerPos) || !groundOk(chunk.getBlockState(lowerPos.below()))) {
            return;
        }
        chunk.setBlockState(upperPos, Blocks.AIR.defaultBlockState(), false);
        chunk.setBlockState(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), false);
        ensureSnowCoveredGround(chunk, lowerPos.below());
    }

    private static void ensureSnowCoveredGround(ChunkAccess chunk, BlockPos groundPos) {
        if (chunk.getBlockState(groundPos).getBlock() == Blocks.GRASS_BLOCK) {
            chunk.setBlockState(groundPos, SRPBlocks.SnowCoveredGrass.get().defaultBlockState(), false);
        }
    }

    /** The plant is the top block of its column (open sky above), the biome has to be cold enough. */
    private static boolean coldEnough(WorldGenLevel level, BlockPos pos) {
        return level.getBiome(pos).value().getBaseTemperature() <= 0.15f;
    }

    private static boolean isShortVegetation(BlockState state) {
        Block b = state.getBlock();
        return b == Blocks.DANDELION || b == Blocks.POPPY || b == Blocks.SHORT_GRASS || b == Blocks.FERN;
    }

    private static boolean isTall(BlockState state, DoubleBlockHalf half) {
        return (state.getBlock() == Blocks.TALL_GRASS || state.getBlock() == Blocks.LARGE_FERN) && state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == half;
    }
}
