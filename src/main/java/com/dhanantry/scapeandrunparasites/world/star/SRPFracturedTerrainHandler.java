package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldData;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;

public class SRPFracturedTerrainHandler {
    private static final int PLATE_SIZE = 96;
    private static final int PLATE_JITTER = 28;
    private static final double CRACK_WIDTH = 2.25;
    private static final double COLLISION_WIDTH = 4.75;
    private static final int MIN_RAVINE_DEPTH = 20;
    private static final int MAX_RAVINE_DEPTH = 52;
    private static final int MIN_COLLISION_RISE = 7;
    private static final int MAX_COLLISION_RISE = 19;
    private static final int MIN_PLATE_OFFSET = -12;
    private static final int MAX_PLATE_OFFSET = 16;

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onPopulatePre(PopulateChunkEvent.Pre event) {
        Level world = event.getWorld();
        if (world == null || world.isClientSide || world.dimensionType() == null || DimKeys.of(world) != 0) {
            return;
        }
        SRPStarWorldData data = SRPStarWorldData.get(world);
        if (data == null || data.getStarType() != 1 || !data.isFracturedTerrainEnabled()) {
            return;
        }
        this.fractureChunk(world, event.getChunkX(), event.getChunkZ());
    }

    private void fractureChunk(Level world, int chunkX, int chunkZ) {
        Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
        long seed = world.getSeed();
        for (int localX = 0; localX < 16; ++localX) {
            for (int localZ = 0; localZ < 16; ++localZ) {
                boolean collisionRidge;
                boolean onBoundary;
                int worldX = (chunkX << 4) + localX;
                int worldZ = (chunkZ << 4) + localZ;
                PlateSample sample = this.samplePlate(seed, worldX, worldZ);
                int topY = this.findSurfaceY(chunk, localX, localZ);
                if (topY <= 4) continue;
                BlockState originalSurface = chunk.getBlockState(localX, topY, localZ);
                int plateOffset = this.getPlateOffset(sample.primaryHash);
                int roughness = this.getSurfaceRoughness(seed, worldX, worldZ);
                int targetY = this.clamp(topY + plateOffset + roughness, 4, 238);
                boolean bl = onBoundary = sample.boundaryGap <= 4.75;
                if (!onBoundary) {
                    this.reshapePlateSurface(chunk, worldX, worldZ, topY, targetY, originalSurface);
                    continue;
                }
                long boundaryHash = this.mix64(sample.primaryHash ^ Long.rotateLeft(sample.secondaryHash, 21) ^ 0x7F4A7C159E3779B9L);
                boolean bl2 = collisionRidge = this.value(boundaryHash, 100) < 36;
                if (collisionRidge) {
                    int rise = 7 + this.value(boundaryHash >>> 11, 13);
                    double closeness = 1.0 - Math.min(1.0, sample.boundaryGap / 4.75);
                    int collisionY = this.clamp(targetY + (int)Math.round((double)rise * closeness), 4, 244);
                    this.reshapePlateSurface(chunk, worldX, worldZ, topY, collisionY, originalSurface);
                    this.addCollisionTeeth(chunk, seed, worldX, worldZ, localX, localZ, collisionY, boundaryHash);
                    continue;
                }
                this.reshapePlateSurface(chunk, worldX, worldZ, topY, targetY, originalSurface);
                this.carvePlateCrack(chunk, seed, worldX, worldZ, localX, localZ, targetY, sample, boundaryHash);
            }
        }
        chunk.generateSkylightMap();
        chunk.setModified(true);
    }

    private PlateSample samplePlate(long seed, int worldX, int worldZ) {
        int cellX = Math.floorDiv(worldX, 96);
        int cellZ = Math.floorDiv(worldZ, 96);
        double nearest = Double.MAX_VALUE;
        double second = Double.MAX_VALUE;
        long primaryHash = 0L;
        long secondaryHash = 0L;
        for (int gx = cellX - 1; gx <= cellX + 1; ++gx) {
            for (int gz = cellZ - 1; gz <= cellZ + 1; ++gz) {
                int jitterZ;
                double centerZ;
                double dz;
                long plateHash = this.hashCell(seed, gx, gz);
                int jitterX = this.value(plateHash, 57) - 28;
                double centerX = (double)gx * 96.0 + 48.0 + (double)jitterX;
                double dx = (double)worldX - centerX;
                double distance = dx * dx + (dz = (double)worldZ - (centerZ = (double)gz * 96.0 + 48.0 + (double)(jitterZ = this.value(plateHash >>> 17, 57) - 28))) * dz;
                if (distance < nearest) {
                    second = nearest;
                    secondaryHash = primaryHash;
                    nearest = distance;
                    primaryHash = plateHash;
                    continue;
                }
                if (!(distance < second)) continue;
                second = distance;
                secondaryHash = plateHash;
            }
        }
        double nearestDistance = Math.sqrt(nearest);
        double secondDistance = Math.sqrt(second);
        return new PlateSample(primaryHash, secondaryHash, Math.max(0.0, secondDistance - nearestDistance));
    }

    private int getPlateOffset(long plateHash) {
        int offset = -12 + this.value(plateHash >>> 7, 29);
        if (this.value(plateHash >>> 31, 100) < 16) {
            int extra = 6 + this.value(plateHash >>> 39, 9);
            offset += (plateHash & 1L) == 0L ? extra : -extra;
        }
        return this.clamp(offset, -20, 24);
    }

    private int getSurfaceRoughness(long seed, int worldX, int worldZ) {
        double broad = this.coherentNoise(seed, worldX, worldZ, 32, -3335678366873096957L);
        double detail = this.coherentNoise(seed, worldX, worldZ, 14, -7723592293110705685L);
        return (int)Math.round(broad * 1.25 + detail * 0.45);
    }

    private void reshapePlateSurface(Chunk chunk, int worldX, int worldZ, int originalTopY, int targetY, BlockState originalSurface) {
        block6: {
            BlockState below;
            BlockState existing;
            SurfaceProfile profile;
            block5: {
                if (!this.isTerrainSurface(originalSurface)) {
                    return;
                }
                if (targetY == originalTopY) {
                    return;
                }
                profile = this.getProfile(originalSurface);
                if (targetY <= originalTopY) break block5;
                for (int y = originalTopY + 1; y <= targetY; ++y) {
                    BlockState state = y == targetY ? profile.surface : (y >= targetY - 3 ? profile.filler : profile.core);
                    this.setBlock(chunk, worldX, y, worldZ, state);
                }
                break block6;
            }
            for (int y = originalTopY; y > targetY && this.canCarveTerrain(existing = this.getBlock(chunk, worldX, y, worldZ)); --y) {
                this.setBlock(chunk, worldX, y, worldZ, Blocks.AIR.defaultBlockState());
            }
            BlockState target = this.getBlock(chunk, worldX, targetY, worldZ);
            if (!this.canReplaceTerrainTop(target)) break block6;
            this.setBlock(chunk, worldX, targetY, worldZ, profile.surface);
            for (int depth = 1; depth <= 3 && targetY - depth > 1 && this.canReplaceTerrainTop(below = this.getBlock(chunk, worldX, targetY - depth, worldZ)); ++depth) {
                this.setBlock(chunk, worldX, targetY - depth, worldZ, profile.filler);
            }
        }
    }

    private void carvePlateCrack(Chunk chunk, long seed, int worldX, int worldZ, int localX, int localZ, int surfaceY, PlateSample sample, long boundaryHash) {
        double width = 2.25 + (double)this.value(boundaryHash >>> 8, 100) / 100.0 * 1.75;
        if (sample.boundaryGap > width) {
            return;
        }
        double edgeWarp = this.coherentNoise(seed, worldX, worldZ, 13, -7723592293110705685L) * 1.15;
        if (sample.boundaryGap + edgeWarp > width) {
            return;
        }
        int depth = 20 + this.value(boundaryHash >>> 19, 33);
        int bottomY = Math.max(4, surfaceY - depth);
        for (int y = surfaceY; y >= bottomY; --y) {
            BlockState state = chunk.getBlockState(localX, y, localZ);
            if (!this.canCarveTerrain(state)) {
                if (!state.getMaterial().isLiquid()) continue;
                break;
            }
            this.setBlock(chunk, worldX, y, worldZ, Blocks.AIR.defaultBlockState());
        }
        if (this.value(boundaryHash >>> 43, 100) < 38) {
            this.addCrackShelf(chunk, worldX, worldZ, surfaceY, bottomY, boundaryHash);
        }
    }

    private void addCrackShelf(Chunk chunk, int worldX, int worldZ, int surfaceY, int bottomY, long hash) {
        int shelfY = bottomY + Math.max(3, (surfaceY - bottomY) / 2);
        if (shelfY >= surfaceY - 2) {
            return;
        }
        BlockState state = this.getBlock(chunk, worldX, shelfY, worldZ);
        if (state.getBlock() == Blocks.AIR) {
            this.setBlock(chunk, worldX, shelfY, worldZ, this.value(hash >>> 51, 2) == 0 ? Blocks.STONE.defaultBlockState() : Blocks.GRAVEL.defaultBlockState());
        }
    }

    private void addCollisionTeeth(Chunk chunk, long seed, int worldX, int worldZ, int localX, int localZ, int surfaceY, long boundaryHash) {
        double ridgeNoise = this.coherentNoise(seed, worldX, worldZ, 11, boundaryHash ^ 0xDB4F0B9175AE2165L);
        if (ridgeNoise < 0.28) {
            return;
        }
        int extra = 2 + (int)Math.round(Math.min(1.0, (ridgeNoise - 0.28) / 0.72) * 6.0);
        for (int i = 1; i <= extra && surfaceY + i < 250; ++i) {
            this.setBlock(chunk, worldX, surfaceY + i, worldZ, Blocks.STONE.defaultBlockState());
        }
    }

    private int findSurfaceY(Chunk chunk, int localX, int localZ) {
        int height;
        for (int y = height = Math.min(255, Math.max(1, chunk.getHeightValue(localX, localZ) - 1)); y > 1; --y) {
            BlockState state = chunk.getBlockState(localX, y, localZ);
            if (state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.SNOW) continue;
            return y;
        }
        return 1;
    }

    private SurfaceProfile getProfile(BlockState surface) {
        Block block = surface.getBlock();
        if (block == Blocks.GRASS_BLOCK) {
            return new SurfaceProfile(Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.STONE.defaultBlockState());
        }
        if (block == Blocks.DIRT) {
            return new SurfaceProfile(surface, Blocks.DIRT.defaultBlockState(), Blocks.STONE.defaultBlockState());
        }
        if (block == Blocks.SAND) {
            return new SurfaceProfile(surface, surface, Blocks.SANDSTONE.defaultBlockState());
        }
        if (block == Blocks.GRAVEL) {
            return new SurfaceProfile(surface, surface, Blocks.STONE.defaultBlockState());
        }
        if (block == Blocks.ICE || block == Blocks.PACKED_ICE) {
            return new SurfaceProfile(surface, Blocks.PACKED_ICE.defaultBlockState(), Blocks.STONE.defaultBlockState());
        }
        if (block == Blocks.SNOW_BLOCK) {
            return new SurfaceProfile(surface, Blocks.SNOW_BLOCK.defaultBlockState(), Blocks.STONE.defaultBlockState());
        }
        return new SurfaceProfile(surface, Blocks.STONE.defaultBlockState(), Blocks.STONE.defaultBlockState());
    }

    private boolean isTerrainSurface(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.BEDROCK || state.getMaterial().isLiquid()) {
            return false;
        }
        Material material = state.getMaterial();
        return material == Material.rock || material == Material.ground || material == Material.grass || material == Material.sand || material == Material.snow || material == Material.craftedSnow || material == Material.ice || material == Material.packedIce;
    }

    private boolean canCarveTerrain(BlockState state) {
        Block block = state.getBlock();
        if (block == Blocks.BEDROCK || state.getMaterial().isLiquid() || block.hasTileEntity(state)) {
            return false;
        }
        Material material = state.getMaterial();
        return material == Material.rock || material == Material.ground || material == Material.grass || material == Material.sand || material == Material.snow || material == Material.craftedSnow || material == Material.ice || material == Material.packedIce;
    }

    private boolean canReplaceTerrainTop(BlockState state) {
        if (state.getBlock().hasTileEntity(state)) {
            return false;
        }
        return this.canCarveTerrain(state) || state.getBlock() == Blocks.AIR;
    }

    private BlockState getBlock(Chunk chunk, int worldX, int y, int worldZ) {
        return chunk.getBlockState(worldX & 0xF, y, worldZ & 0xF);
    }

    private void setBlock(Chunk chunk, int worldX, int y, int worldZ, BlockState state) {
        chunk.setBlockState(BlockPos.containing(worldX, y, worldZ), state);
    }

    private double coherentNoise(long seed, int worldX, int worldZ, int scale, long salt) {
        int cellX = Math.floorDiv(worldX, scale);
        int cellZ = Math.floorDiv(worldZ, scale);
        int localX = Math.floorMod(worldX, scale);
        int localZ = Math.floorMod(worldZ, scale);
        double tx = (double)localX / (double)scale;
        double tz = (double)localZ / (double)scale;
        tx = this.smoothStep(tx);
        tz = this.smoothStep(tz);
        double v00 = this.latticeNoise(seed, cellX, cellZ, salt);
        double v10 = this.latticeNoise(seed, cellX + 1, cellZ, salt);
        double v01 = this.latticeNoise(seed, cellX, cellZ + 1, salt);
        double v11 = this.latticeNoise(seed, cellX + 1, cellZ + 1, salt);
        double top = this.lerp(v00, v10, tx);
        double bottom = this.lerp(v01, v11, tx);
        return this.lerp(top, bottom, tz);
    }

    private double latticeNoise(long seed, int cellX, int cellZ, long salt) {
        long hash = this.mix64(seed ^ salt ^ (long)cellX * 341873128712L ^ (long)cellZ * 132897987541L);
        long positive = hash & Long.MAX_VALUE;
        double unit = (double)(positive % 1000000L) / 999999.0;
        return unit * 2.0 - 1.0;
    }

    private double smoothStep(double value) {
        return value * value * (3.0 - 2.0 * value);
    }

    private double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private long hashCell(long seed, int cellX, int cellZ) {
        return this.mix64(seed ^ (long)cellX * 341873128712L ^ (long)cellZ * 132897987541L ^ 0x9E3779B97F4A7C15L);
    }

    private long hashPosition(long seed, int x, int z, long salt) {
        return this.mix64(seed ^ salt ^ (long)x * 341873128712L ^ (long)z * 132897987541L);
    }

    private long mix64(long value) {
        value ^= value >>> 30;
        value *= -4658895280553007687L;
        value ^= value >>> 27;
        value *= -7723592293110705685L;
        value ^= value >>> 31;
        return value;
    }

    private int value(long hash, int bound) {
        if (bound <= 1) {
            return 0;
        }
        int mixed = (int)(hash ^ hash >>> 32);
        return Math.floorMod(mixed, bound);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class SurfaceProfile {
        private final BlockState surface;
        private final BlockState filler;
        private final BlockState core;

        private SurfaceProfile(BlockState surface, BlockState filler, BlockState core) {
            this.surface = surface;
            this.filler = filler;
            this.core = core;
        }
    }

    private static class PlateSample {
        private final long primaryHash;
        private final long secondaryHash;
        private final double boundaryGap;

        private PlateSample(long primaryHash, long secondaryHash, double boundaryGap) {
            this.primaryHash = primaryHash;
            this.secondaryHash = secondaryHash;
            this.boundaryGap = boundaryGap;
        }
    }
}

