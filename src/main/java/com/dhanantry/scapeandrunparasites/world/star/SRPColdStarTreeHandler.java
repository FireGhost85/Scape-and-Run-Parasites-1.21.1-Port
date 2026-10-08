package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenDeadheadTreeStructure;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;

public class SRPColdStarTreeHandler {
    private static final float NORMAL_TREE_DENSITY = 0.5f;
    private static final int TREE_SNOW_RADIUS = 7;
    private static final int TREE_SNOW_VERTICAL_SEARCH = 8;
    private final WorldGenDeadheadTreeStructure normalTree = new WorldGenDeadheadTreeStructure(true, false, true);
    private final WorldGenDeadheadTreeStructure mushroomTree = new WorldGenDeadheadTreeStructure(true, true, false);

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onDecorateTree(DecorateBiomeEvent.Decorate event) {
        boolean mushroomTrees;
        if (event.getType() != DecorateBiomeEvent.Decorate.EventType.TREE) {
            return;
        }
        Level world = event.getWorld();
        if (world == null || world.isClientSide || world.dimensionType() == null || DimKeys.of(world) != 0) {
            return;
        }
        SRPStarWorldData data = SRPStarWorldData.get(world);
        if (data == null || data.getStarType() != 1) {
            return;
        }
        event.setResult(MobDespawnEvent.Result.DENY);
        RandomSource rand = event.getRand();
        ChunkPos chunk = event.getChunkPos();
        BlockPos chunkOrigin = BlockPos.containing(chunk.chunkXPos * 16, 0, chunk.chunkZPos * 16);
        Biome biome = world.getBiome(chunkOrigin.offset(8, 0, 8)).value();
        int treeCount = biome.theBiomeDecorator.treesPerChunk;
        if (rand.nextFloat() < biome.theBiomeDecorator.extraTreeChance) {
            ++treeCount;
        }
        if (!(mushroomTrees = data.isMushroomTreesEnabled())) {
            treeCount = this.reduceNormalTreeDensity(treeCount, rand);
        }
        for (int i = 0; i < treeCount; ++i) {
            int sinkDepth;
            BlockPos generationPos;
            BlockPos treePos;
            int x = rand.nextInt(16) + 8;
            int z = rand.nextInt(16) + 8;
            BlockPos column = chunkOrigin.offset(x, 0, z);
            if (mushroomTrees) {
                treePos = world.getHeight(column);
                BlockPos generationPos2 = treePos.below();
                if (!this.mushroomTree.generate(world, rand, generationPos2)) continue;
                this.addSnowUnderTree(world, generationPos2);
                continue;
            }
            treePos = this.findNormalTreePosition(world, column);
            if (treePos == null || !this.normalTree.generate(world, rand, generationPos = treePos.below(sinkDepth = 1 + rand.nextInt(3)))) continue;
            this.addSnowUnderTree(world, treePos);
        }
    }

    private int reduceNormalTreeDensity(int originalCount, RandomSource rand) {
        if (originalCount <= 0) {
            return 0;
        }
        float scaled = (float)originalCount * 0.5f;
        int whole = (int)scaled;
        float remainder = scaled - (float)whole;
        if (rand.nextFloat() < remainder) {
            ++whole;
        }
        return whole;
    }

    private void addSnowUnderTree(Level world, BlockPos treeSurfacePos) {
        int centerX = treeSurfacePos.getX();
        int centerZ = treeSurfacePos.getZ();
        int expectedGroundY = treeSurfacePos.getY() - 1;
        for (int dx = -7; dx <= 7; ++dx) {
            for (int dz = -7; dz <= 7; ++dz) {
                BlockPos snowPos;
                BlockPos ground;
                if (dx * dx + dz * dz > 49 || (ground = this.findGroundForSnow(world, centerX + dx, centerZ + dz, expectedGroundY)) == null || this.replaceVegetationWithSnowGrass(world, snowPos = ground.above())) continue;
                BlockState at = world.getBlockState(snowPos);
                if (at.getBlock() == Blocks.SNOW) {
                    this.repairOrphanedDoublePlant(world, snowPos);
                    continue;
                }
                if (!world.isEmptyBlock(snowPos) && !at.getBlock().isReplaceable((BlockGetter)world, snowPos) || !Blocks.SNOW.canPlaceBlockAt(world, snowPos)) continue;
                world.setBlock(snowPos, Blocks.SNOW.defaultBlockState(), 2);
            }
        }
    }

    private boolean replaceVegetationWithSnowGrass(Level world, BlockPos pos) {
        BlockTallGrass.EnumType type;
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.TALL_GRASS) {
            BlockPos lowerPos;
            BlockPos upperPos;
            if (state.getValue((Property)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
                upperPos = pos;
                lowerPos = pos.below();
            } else {
                lowerPos = pos;
                upperPos = pos.above();
            }
            BlockState lowerState = world.getBlockState(lowerPos);
            BlockState upperState = world.getBlockState(upperPos);
            if (lowerState.getBlock() == Blocks.TALL_GRASS || upperState.getBlock() == Blocks.TALL_GRASS) {
                world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 2);
                world.setBlock(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 2);
                this.ensureSnowCoveredGround(world, lowerPos.below());
                return true;
            }
        }
        if (block == Blocks.SHORT_GRASS && ((type = (BlockTallGrass.EnumType)state.getValue((Property)BlockTallGrass.TYPE)) == BlockTallGrass.EnumType.GRASS || type == BlockTallGrass.EnumType.FERN)) {
            world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 2);
            this.ensureSnowCoveredGround(world, pos.below());
            return true;
        }
        if (block == Blocks.DANDELION || block == Blocks.POPPY) {
            world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 2);
            this.ensureSnowCoveredGround(world, pos.below());
            return true;
        }
        return false;
    }

    private void repairOrphanedDoublePlant(Level world, BlockPos snowPos) {
        BlockPos upperPos = snowPos.above();
        BlockState upperState = world.getBlockState(upperPos);
        if (upperState.getBlock() != Blocks.TALL_GRASS) {
            return;
        }
        if (upperState.getValue((Property)BlockDoublePlant.HALF) != BlockDoublePlant.EnumBlockHalf.UPPER) {
            return;
        }
        world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 2);
        world.setBlock(snowPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 2);
        this.ensureSnowCoveredGround(world, snowPos.below());
    }

    private void ensureSnowCoveredGround(Level world, BlockPos groundPos) {
        BlockState ground = world.getBlockState(groundPos);
        if (ground.getBlock() == Blocks.GRASS_BLOCK) {
            world.setBlock(groundPos, SRPBlocks.SnowCoveredGrass.get().defaultBlockState(), 2);
        }
    }

    private BlockPos findGroundForSnow(Level world, int x, int z, int expectedGroundY) {
        int maxY = Math.min(world.getMaxBuildHeight() - 2, expectedGroundY + 8);
        int minY = Math.max(1, expectedGroundY - 8);
        for (int y = maxY; y >= minY; --y) {
            BlockPos pos = BlockPos.containing(x, y, z);
            BlockState state = world.getBlockState(pos);
            if (world.isEmptyBlock(pos) || state.getBlock().isReplaceable((BlockGetter)world, pos) || this.isTreeOrLeaves(state, world, pos)) continue;
            if (this.isForbiddenSurface(state)) {
                return null;
            }
            if (state.getMaterial().isSolid()) {
                return pos;
            }
            return null;
        }
        return null;
    }

    private BlockPos findNormalTreePosition(Level world, BlockPos column) {
        BlockPos top = world.getHeight(column);
        if (top.getY() <= 1) {
            return null;
        }
        BlockPos cursor = top.below();
        BlockState state = world.getBlockState(cursor);
        if (this.isTreeOrLeaves(state, world, cursor)) {
            return null;
        }
        if (this.isForbiddenSurface(state)) {
            return null;
        }
        for (int guard = 0; guard < 8 && cursor.getY() > 1 && this.isSurfaceDecoration(world, cursor, state); ++guard) {
            cursor = cursor.below();
            state = world.getBlockState(cursor);
        }
        if (this.isTreeOrLeaves(state, world, cursor)) {
            return null;
        }
        if (!this.isValidNormalGround(state)) {
            return null;
        }
        BlockPos treePos = cursor.above();
        BlockState atTreePos = world.getBlockState(treePos);
        if (!world.isEmptyBlock(treePos) && !atTreePos.getBlock().isReplaceable((BlockGetter)world, treePos)) {
            return null;
        }
        return treePos;
    }

    private boolean isSurfaceDecoration(Level world, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.SNOW) {
            return true;
        }
        if (state.getMaterial().isLiquid()) {
            return false;
        }
        return block.isReplaceable((BlockGetter)world, pos);
    }

    private boolean isTreeOrLeaves(BlockState state, Level world, BlockPos pos) {
        Block block = state.getBlock();
        return block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || block == SRPBlocks.ParasiteTrunk.get() || block.isLeaves(state, (BlockGetter)world, pos);
    }

    private boolean isForbiddenSurface(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.FROSTED_ICE || state.getMaterial().isLiquid();
    }

    private boolean isValidNormalGround(BlockState state) {
        Block block = state.getBlock();
        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.FARMLAND || block == SRPBlocks.SnowCoveredGrass.get();
    }
}

