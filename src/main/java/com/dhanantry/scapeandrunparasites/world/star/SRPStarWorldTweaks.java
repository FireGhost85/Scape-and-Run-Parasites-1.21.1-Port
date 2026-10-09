package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * SRPStarWorldTweaks of 1.10.9, applied to every newly generated overworld chunk: warm star worlds lose the surface water
 * between y 49 and 63, cold star worlds get snowy grass in place of the short and tall grass.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SRPStarWorldTweaks {
    private static final int WARM_STAR_SEA_LEVEL = 48;
    private static final int VANILLA_SEA_LEVEL = 63;

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        int starType = SRPStarWorldData.get(level.getServer()).getStarType();
        ChunkAccess chunk = event.getChunk();
        if (starType == 2) {
            dryWarmStarChunk(level, chunk.getPos());
        } else if (starType == 1) {
            snowColdStarGrass(level, chunk.getPos());
        }
    }

    private static void dryWarmStarChunk(ServerLevel world, ChunkPos cp) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = VANILLA_SEA_LEVEL; y > WARM_STAR_SEA_LEVEL; --y) {
                    pos.set(cp.getMinBlockX() + x, y, cp.getMinBlockZ() + z);
                    BlockState state = world.getBlockState(pos);
                    if (state.getFluidState().is(Fluids.WATER) || state.getFluidState().is(Fluids.FLOWING_WATER) || state.is(Blocks.ICE)) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void snowColdStarGrass(ServerLevel world, ChunkPos cp) {
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                findAndConvertExposedGrass(world, cp.getMinBlockX() + x, cp.getMinBlockZ() + z);
            }
        }
    }

    private static void findAndConvertExposedGrass(ServerLevel world, int x, int z) {
        for (int y = world.getMaxBuildHeight() - 1; y > world.getMinBuildHeight(); --y) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = world.getBlockState(pos);
            if (state.isAir()) {
                continue;
            }
            if (isShortVegetation(state)) {
                convertShortGrass(world, pos);
            } else if (isTall(state, DoubleBlockHalf.UPPER)) {
                BlockPos lower = pos.below();
                if (isTall(world.getBlockState(lower), DoubleBlockHalf.LOWER)) {
                    convertTallGrass(world, lower, pos);
                }
            } else if (isTall(state, DoubleBlockHalf.LOWER)) {
                BlockPos upper = pos.above();
                if (isTall(world.getBlockState(upper), DoubleBlockHalf.UPPER)) {
                    convertTallGrass(world, pos, upper);
                }
            }
            return;
        }
    }

    private static boolean groundOk(BlockState ground) {
        Block b = ground.getBlock();
        return b == Blocks.GRASS_BLOCK || b == Blocks.DIRT || b == SRPBlocks.SnowCoveredGrass.get();
    }

    private static void convertShortGrass(ServerLevel world, BlockPos pos) {
        if (!canBecomeSnowyGrass(world, pos, false) || !groundOk(world.getBlockState(pos.below()))) {
            return;
        }
        world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 2);
        ensureSnowCoveredGround(world, pos.below());
    }

    private static void convertTallGrass(ServerLevel world, BlockPos lowerPos, BlockPos upperPos) {
        if (!canBecomeSnowyGrass(world, lowerPos, true) || !groundOk(world.getBlockState(lowerPos.below()))) {
            return;
        }
        world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 2);
        world.setBlock(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 2);
        ensureSnowCoveredGround(world, lowerPos.below());
    }

    private static void ensureSnowCoveredGround(ServerLevel world, BlockPos groundPos) {
        if (world.getBlockState(groundPos).getBlock() == Blocks.GRASS_BLOCK) {
            world.setBlock(groundPos, SRPBlocks.SnowCoveredGrass.get().defaultBlockState(), 2);
        }
    }

    private static boolean canBecomeSnowyGrass(ServerLevel world, BlockPos pos, boolean tall) {
        BlockPos skyPos = tall ? pos.above(2) : pos.above();
        if (!world.canSeeSky(skyPos)) {
            return false;
        }
        return world.getBiome(pos).value().getBaseTemperature() <= 0.15f;
    }

    private static boolean isShortVegetation(BlockState state) {
        Block b = state.getBlock();
        return b == Blocks.DANDELION || b == Blocks.POPPY || b == Blocks.SHORT_GRASS || b == Blocks.FERN;
    }

    private static boolean isTall(BlockState state, DoubleBlockHalf half) {
        return (state.getBlock() == Blocks.TALL_GRASS || state.getBlock() == Blocks.LARGE_FERN) && state.getBlock() instanceof DoublePlantBlock && state.getValue(DoublePlantBlock.HALF) == half;
    }
}
