package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenDeadheadTreeStructure;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * SRPColdStarTreeHandler of 1.10.9: in a cold star world the trees of the biomes are replaced by deadhead trees (or by deadhead
 * mushroom trees when the world was created with that option), with snowy grass under them.
 *
 * <p>1.12 denied the tree decoration of every biome and placed its own trees (the biome's tree count, halved unless the mushroom
 * option is on). 1.21 has no such event: {@code TreeFeatureMixin} cancels every tree the world generation places in a cold star
 * world and hands the position of the vanilla tree to {@link #onVanillaTree}, so the density and the distribution of the biomes
 * stay. Like the other generation extras the deadhead tree (a big structure) is placed later from the server tick, once the
 * chunks around are loaded.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPColdStarTreeHandler {
    private static final float NORMAL_TREE_DENSITY = 0.5f;
    private static final int TREE_SNOW_RADIUS = 7;
    private static final int TREE_SNOW_VERTICAL_SEARCH = 8;
    private static final int MAX_QUEUE = 4096;
    private static final int PER_TICK = 3;

    private record Pending(ServerLevel level, BlockPos column, long seed, int tries) {}

    private static final ConcurrentLinkedQueue<Pending> QUEUE = new ConcurrentLinkedQueue<>();

    private SRPColdStarTreeHandler() {
    }

    /** Called from the world generation threads for every tree the vanilla features would place in a cold star world. */
    public static void onVanillaTree(ServerLevel level, BlockPos origin, RandomSource rand) {
        if (!SRPWorldEntitySpawner.mushroomTrees && rand.nextFloat() >= NORMAL_TREE_DENSITY) {
            return;
        }
        if (QUEUE.size() < MAX_QUEUE) {
            QUEUE.add(new Pending(level, origin, rand.nextLong(), 0));
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        for (int i = 0; i < PER_TICK; ++i) {
            Pending p = QUEUE.poll();
            if (p == null) {
                return;
            }
            if (p.level() != level) {
                // another dimension / world: drop it
                continue;
            }
            BlockPos c = p.column();
            if (!level.hasChunksAt(c.getX() - 24, level.getMinBuildHeight(), c.getZ() - 24, c.getX() + 24, level.getMinBuildHeight() + 1, c.getZ() + 24)) {
                if (p.tries() < 40) {
                    QUEUE.add(new Pending(p.level(), c, p.seed(), p.tries() + 1));
                }
                continue;
            }
            place(level, c, RandomSource.create(p.seed()));
        }
    }

    private static void place(ServerLevel world, BlockPos column, RandomSource rand) {
        if (SRPWorldEntitySpawner.mushroomTrees) {
            BlockPos treePos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, column);
            BlockPos generationPos = treePos.below();
            if (new WorldGenDeadheadTreeStructure(true, true, false).generate(world, rand, generationPos)) {
                addSnowUnderTree(world, generationPos);
            }
            return;
        }
        BlockPos treePos = findNormalTreePosition(world, column);
        if (treePos == null) {
            return;
        }
        int sinkDepth = 1 + rand.nextInt(3);
        if (new WorldGenDeadheadTreeStructure(true, false, true).generate(world, rand, treePos.below(sinkDepth))) {
            addSnowUnderTree(world, treePos);
        }
    }

    // ------------------------------------------------------------------ snow under the tree

    private static void addSnowUnderTree(ServerLevel world, BlockPos treeSurfacePos) {
        int centerX = treeSurfacePos.getX();
        int centerZ = treeSurfacePos.getZ();
        int expectedGroundY = treeSurfacePos.getY() - 1;
        for (int dx = -TREE_SNOW_RADIUS; dx <= TREE_SNOW_RADIUS; ++dx) {
            for (int dz = -TREE_SNOW_RADIUS; dz <= TREE_SNOW_RADIUS; ++dz) {
                if (dx * dx + dz * dz > TREE_SNOW_RADIUS * TREE_SNOW_RADIUS) {
                    continue;
                }
                BlockPos ground = findGroundForSnow(world, centerX + dx, centerZ + dz, expectedGroundY);
                if (ground == null) {
                    continue;
                }
                BlockPos snowPos = ground.above();
                if (replaceVegetationWithSnowGrass(world, snowPos)) {
                    continue;
                }
                BlockState at = world.getBlockState(snowPos);
                if (at.is(Blocks.SNOW)) {
                    repairOrphanedDoublePlant(world, snowPos);
                    continue;
                }
                if (!world.isEmptyBlock(snowPos) && !at.canBeReplaced() || !Blocks.SNOW.defaultBlockState().canSurvive(world, snowPos)) {
                    continue;
                }
                world.setBlock(snowPos, Blocks.SNOW.defaultBlockState(), 2);
            }
        }
    }

    private static boolean isDoublePlant(BlockState state) {
        return (state.is(Blocks.TALL_GRASS) || state.is(Blocks.LARGE_FERN)) && state.getBlock() instanceof DoublePlantBlock;
    }

    private static boolean replaceVegetationWithSnowGrass(ServerLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (isDoublePlant(state)) {
            BlockPos lowerPos;
            BlockPos upperPos;
            if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                upperPos = pos;
                lowerPos = pos.below();
            } else {
                lowerPos = pos;
                upperPos = pos.above();
            }
            if (isDoublePlant(world.getBlockState(lowerPos)) || isDoublePlant(world.getBlockState(upperPos))) {
                world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 2);
                world.setBlock(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 2);
                ensureSnowCoveredGround(world, lowerPos.below());
                return true;
            }
        }
        if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN) || state.is(Blocks.DANDELION) || state.is(Blocks.POPPY)) {
            world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 2);
            ensureSnowCoveredGround(world, pos.below());
            return true;
        }
        return false;
    }

    private static void repairOrphanedDoublePlant(ServerLevel world, BlockPos snowPos) {
        BlockPos upperPos = snowPos.above();
        BlockState upperState = world.getBlockState(upperPos);
        if (!isDoublePlant(upperState) || upperState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.UPPER) {
            return;
        }
        world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 2);
        world.setBlock(snowPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 2);
        ensureSnowCoveredGround(world, snowPos.below());
    }

    private static void ensureSnowCoveredGround(ServerLevel world, BlockPos groundPos) {
        if (world.getBlockState(groundPos).is(Blocks.GRASS_BLOCK)) {
            world.setBlock(groundPos, SRPBlocks.SnowCoveredGrass.get().defaultBlockState(), 2);
        }
    }

    private static BlockPos findGroundForSnow(ServerLevel world, int x, int z, int expectedGroundY) {
        int maxY = Math.min(world.getMaxBuildHeight() - 2, expectedGroundY + TREE_SNOW_VERTICAL_SEARCH);
        int minY = Math.max(world.getMinBuildHeight() + 1, expectedGroundY - TREE_SNOW_VERTICAL_SEARCH);
        for (int y = maxY; y >= minY; --y) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = world.getBlockState(pos);
            if (world.isEmptyBlock(pos) || state.canBeReplaced() || isTreeOrLeaves(state)) {
                continue;
            }
            if (isForbiddenSurface(state)) {
                return null;
            }
            return state.blocksMotion() ? pos : null;
        }
        return null;
    }

    // ------------------------------------------------------------------ the position of a normal deadhead tree

    private static BlockPos findNormalTreePosition(ServerLevel world, BlockPos column) {
        BlockPos top = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, column);
        if (top.getY() <= world.getMinBuildHeight() + 1) {
            return null;
        }
        BlockPos cursor = top.below();
        BlockState state = world.getBlockState(cursor);
        if (isTreeOrLeaves(state) || isForbiddenSurface(state)) {
            return null;
        }
        for (int guard = 0; guard < 8 && cursor.getY() > world.getMinBuildHeight() + 1 && isSurfaceDecoration(world, cursor, state); ++guard) {
            cursor = cursor.below();
            state = world.getBlockState(cursor);
        }
        if (isTreeOrLeaves(state) || !isValidNormalGround(state)) {
            return null;
        }
        BlockPos treePos = cursor.above();
        BlockState atTreePos = world.getBlockState(treePos);
        if (!world.isEmptyBlock(treePos) && !atTreePos.canBeReplaced()) {
            return null;
        }
        return treePos;
    }

    private static boolean isSurfaceDecoration(ServerLevel world, BlockPos pos, BlockState state) {
        if (state.is(Blocks.SNOW)) {
            return true;
        }
        if (!state.getFluidState().isEmpty()) {
            return false;
        }
        return state.canBeReplaced();
    }

    private static boolean isTreeOrLeaves(BlockState state) {
        Block block = state.getBlock();
        return state.is(BlockTags.LOGS) || block == SRPBlocks.ParasiteTrunk.get() || state.is(BlockTags.LEAVES);
    }

    private static boolean isForbiddenSurface(BlockState state) {
        return state.is(Blocks.ICE) || state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE) || !state.getFluidState().isEmpty();
    }

    private static boolean isValidNormalGround(BlockState state) {
        return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) || state.getBlock() == SRPBlocks.SnowCoveredGrass.get();
    }
}
