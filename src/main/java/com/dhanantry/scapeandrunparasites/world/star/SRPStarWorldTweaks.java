package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;

public class SRPStarWorldTweaks {
    private static final int VANILLA_SEA_LEVEL = 63;
    private static final int WARM_STAR_SEA_LEVEL = 48;

    @SubscribeEvent
    public void onPopulatePost(PopulateChunkEvent.Post event) {
        Level world = event.getLevel();
        if (world == null || world.dimensionType() == null || !DimKeys.of(world).equals(DimKeys.normalize("0"))) {
            return;
        }
        int starType = SRPStarWorldData.get(world).getStarType();
        if (starType == 2) {
            this.dryWarmStarChunk(world, event.getChunkX(), event.getChunkZ());
        }
    }

    @SubscribeEvent
    public void onDecoratePost(DecorateBiomeEvent.Post event) {
        Level world = event.getLevel();
        if (world == null || world.dimensionType() == null || !DimKeys.of(world).equals(DimKeys.normalize("0"))) {
            return;
        }
        int starType = SRPStarWorldData.get(world).getStarType();
        if (starType != 1) {
            return;
        }
        int chunkX = event.getChunkPos().chunkXPos;
        int chunkZ = event.getChunkPos().chunkZPos;
        this.snowColdStarGrass(world, chunkX, chunkZ);
    }

    private void dryWarmStarChunk(Level world, int chunkX, int chunkZ) {
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                for (int y = 63; y > 48; --y) {
                    pos.set(startX + x, y, startZ + z);
                    BlockState state = world.getBlockState((BlockPos)pos);
                    if (LegacyMaterial.of(state) != LegacyMaterial.water && state.getBlock() != Blocks.ICE) continue;
                    world.setBlock((BlockPos)pos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    private void snowColdStarGrass(Level world, int chunkX, int chunkZ) {
        int startX = (chunkX << 4) + 8;
        int startZ = (chunkZ << 4) + 8;
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                int worldX = startX + x;
                int worldZ = startZ + z;
                this.findAndConvertExposedGrass(world, worldX, worldZ);
            }
        }
    }

    private void findAndConvertExposedGrass(Level world, int x, int z) {
        for (int y = world.getMaxBuildHeight() - 1; y > 0; --y) {
            BlockPos pos = BlockPos.containing(x, y, z);
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() == Blocks.AIR) continue;
            if (this.isShortVegetation(state)) {
                this.convertShortGrass(world, pos);
                return;
            }
            if (this.isTallGrassUpper(state)) {
                this.convertTallGrassFromUpper(world, pos);
                return;
            }
            if (this.isTallGrassLower(state)) {
                this.convertTallGrassFromLower(world, pos);
                return;
            }
            return;
        }
    }

    private void convertShortGrass(Level world, BlockPos pos) {
        if (!this.canBecomeSnowyGrass(world, pos, false)) {
            return;
        }
        BlockState below = world.getBlockState(pos.below());
        if (below.getBlock() != Blocks.GRASS_BLOCK && below.getBlock() != Blocks.DIRT && below.getBlock() != SRPBlocks.SnowCoveredGrass.get()) {
            return;
        }
        world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 2);
        this.ensureSnowCoveredGround(world, pos.below());
    }

    private void convertTallGrassFromUpper(Level world, BlockPos upperPos) {
        BlockPos lowerPos = upperPos.below();
        BlockState lowerState = world.getBlockState(lowerPos);
        if (!this.isTallGrassLower(lowerState)) {
            return;
        }
        this.convertTallGrass(world, lowerPos, upperPos);
    }

    private void convertTallGrassFromLower(Level world, BlockPos lowerPos) {
        BlockPos upperPos = lowerPos.above();
        BlockState upperState = world.getBlockState(upperPos);
        if (upperState.getBlock() != Blocks.TALL_GRASS) {
            return;
        }
        if (upperState.getValue((Property)BlockDoublePlant.HALF) != BlockDoublePlant.EnumBlockHalf.UPPER) {
            return;
        }
        this.convertTallGrass(world, lowerPos, upperPos);
    }

    private void convertTallGrass(Level world, BlockPos lowerPos, BlockPos upperPos) {
        if (!this.canBecomeSnowyGrass(world, lowerPos, true)) {
            return;
        }
        BlockState ground = world.getBlockState(lowerPos.below());
        if (ground.getBlock() != Blocks.GRASS_BLOCK && ground.getBlock() != Blocks.DIRT && ground.getBlock() != SRPBlocks.SnowCoveredGrass.get()) {
            return;
        }
        world.setBlock(upperPos, Blocks.AIR.defaultBlockState(), 2);
        world.setBlock(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 2);
        this.ensureSnowCoveredGround(world, lowerPos.below());
    }

    private void ensureSnowCoveredGround(Level world, BlockPos groundPos) {
        BlockState ground = world.getBlockState(groundPos);
        if (ground.getBlock() == Blocks.GRASS_BLOCK) {
            world.setBlock(groundPos, SRPBlocks.SnowCoveredGrass.get().defaultBlockState(), 2);
        }
    }

    private boolean canBecomeSnowyGrass(Level world, BlockPos pos, boolean tall) {
        BlockPos skyPos;
        BlockPos blockPos = skyPos = tall ? pos.above(2) : pos.above();
        if (!world.canSeeSky(skyPos)) {
            return false;
        }
        return world.getBiome(pos).value().getFloatTemperature(pos) <= 0.15f;
    }

    private boolean isShortVegetation(BlockState state) {
        if (state.getBlock() == Blocks.DANDELION || state.getBlock() == Blocks.POPPY) {
            return true;
        }
        if (state.getBlock() != Blocks.SHORT_GRASS) {
            return false;
        }
        BlockTallGrass.EnumType type = (BlockTallGrass.EnumType)state.getValue((Property)BlockTallGrass.TYPE);
        return type == BlockTallGrass.EnumType.GRASS || type == BlockTallGrass.EnumType.FERN;
    }

    private boolean isTallGrassUpper(BlockState state) {
        return state.getBlock() == Blocks.TALL_GRASS && state.getValue((Property)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER;
    }

    private boolean isTallGrassLower(BlockState state) {
        if (state.getBlock() != Blocks.TALL_GRASS) {
            return false;
        }
        return state.getValue((Property)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.LOWER;
    }
}

