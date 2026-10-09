package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class SRPColdVillageWallGenerator {
    private SRPColdVillageWallGenerator() {
    }

    public static void generate(Level world, RandomSource rand, BlockPos center, int buildingCount) {
        SRPColdVillageWallGenerator.generate(world, rand, center, buildingCount, Collections.emptyList());
    }

    public static void generate(Level world, RandomSource rand, BlockPos center, int buildingCount, List<WallExclusion> exclusions) {
        if (world == null || world.isClientSide || center == null) {
            return;
        }
        if (exclusions == null) {
            exclusions = Collections.emptyList();
        }
        Bounds villageBounds = SRPColdVillageWallGenerator.computeVillageBounds(center, exclusions, buildingCount);
        int centerX = (villageBounds.minX + villageBounds.maxX) / 2;
        int centerZ = (villageBounds.minZ + villageBounds.maxZ) / 2;
        int halfX = SRPColdVillageWallGenerator.clamp((villageBounds.maxX - villageBounds.minX) / 2 + 10 + rand.nextInt(4), 22, 54);
        int halfZ = SRPColdVillageWallGenerator.clamp((villageBounds.maxZ - villageBounds.minZ) / 2 + 10 + rand.nextInt(4), 22, 52);
        int baseHeight = buildingCount >= 6 ? 3 : 2;
        int wallThickness = buildingCount >= 8 ? 2 : 1;
        int sampleCount = 176;
        double squareness = 2.45 + rand.nextDouble() * 0.55;
        double wobblePhaseA = rand.nextDouble() * Math.PI * 2.0;
        double wobblePhaseB = rand.nextDouble() * Math.PI * 2.0;
        double heightPhaseA = rand.nextDouble() * Math.PI * 2.0;
        Gate[] gates = SRPColdVillageWallGenerator.createGates(rand, SRPColdVillageWallGenerator.pickEntranceCount(rand), sampleCount);
        BlockPos layoutCenter = BlockPos.containing(centerX, center.getY(), centerZ);
        BlockPos firstPlaced = null;
        BlockPos previousPlaced = null;
        for (int i = 0; i < sampleCount; ++i) {
            double t = Math.PI * 2 * (double)i / (double)sampleCount;
            double cos = Math.cos(t);
            double sin = Math.sin(t);
            double shapeX = SRPColdVillageWallGenerator.copySign(Math.pow(Math.abs(cos), 2.0 / squareness), cos);
            double shapeZ = SRPColdVillageWallGenerator.copySign(Math.pow(Math.abs(sin), 2.0 / squareness), sin);
            double radialScale = 1.0 + Math.sin(t * 2.0 + wobblePhaseA) * 0.045 + Math.sin(t * 5.0 + wobblePhaseB) * 0.018;
            int x = centerX + (int)Math.round(shapeX * (double)halfX * radialScale);
            int z = centerZ + (int)Math.round(shapeZ * (double)halfZ * radialScale);
            BlockPos rawPoint = BlockPos.containing(x, 0, z);
            if (SRPColdVillageWallGenerator.isGate(gates, i, sampleCount)) {
                if (i % 3 == 0) {
                    SRPColdVillageWallGenerator.placeGateShoulders(world, rand, rawPoint, layoutCenter, baseHeight, wallThickness, exclusions);
                }
                previousPlaced = null;
                continue;
            }
            int localHeight = SRPColdVillageWallGenerator.computeSmoothWallHeight(baseHeight, t, heightPhaseA);
            BlockPos placed = SRPColdVillageWallGenerator.placeWallColumn(world, rawPoint, localHeight, wallThickness, exclusions);
            if (placed == null) {
                previousPlaced = null;
                continue;
            }
            if (firstPlaced == null) {
                firstPlaced = placed;
            }
            if (previousPlaced != null) {
                SRPColdVillageWallGenerator.drawWallLine(world, previousPlaced, placed, localHeight, wallThickness, exclusions);
            }
            previousPlaced = placed;
        }
        if (previousPlaced != null && firstPlaced != null) {
            SRPColdVillageWallGenerator.drawWallLine(world, previousPlaced, firstPlaced, baseHeight, wallThickness, exclusions);
        }
    }

    private static Bounds computeVillageBounds(BlockPos center, List<WallExclusion> exclusions, int buildingCount) {
        if (exclusions == null || exclusions.isEmpty()) {
            int size = 20 + buildingCount * 3;
            return new Bounds(center.getX() - size, center.getX() + size, center.getZ() - size, center.getZ() + size);
        }
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (WallExclusion exclusion : exclusions) {
            if (exclusion == null) continue;
            minX = Math.min(minX, exclusion.minX);
            maxX = Math.max(maxX, exclusion.maxX);
            minZ = Math.min(minZ, exclusion.minZ);
            maxZ = Math.max(maxZ, exclusion.maxZ);
        }
        if (minX == Integer.MAX_VALUE) {
            int size = 20 + buildingCount * 3;
            return new Bounds(center.getX() - size, center.getX() + size, center.getZ() - size, center.getZ() + size);
        }
        return new Bounds(minX, maxX, minZ, maxZ);
    }

    private static int pickEntranceCount(RandomSource rand) {
        int roll = rand.nextInt(10);
        if (roll == 0) {
            return 1;
        }
        if (roll >= 8) {
            return 3;
        }
        return 2;
    }

    private static Gate[] createGates(RandomSource rand, int entranceCount, int sampleCount) {
        Gate[] gates = new Gate[entranceCount];
        boolean[] usedQuadrants = new boolean[4];
        int quadrantSize = sampleCount / 4;
        for (int i = 0; i < entranceCount; ++i) {
            int centerIndex;
            int quadrant = rand.nextInt(4);
            for (int tries = 0; tries < 8 && usedQuadrants[quadrant]; ++tries) {
                quadrant = rand.nextInt(4);
            }
            usedQuadrants[quadrant] = true;
            for (centerIndex = quadrant * quadrantSize + quadrantSize / 2 + rand.nextInt(11) - 5; centerIndex < 0; centerIndex += sampleCount) {
            }
            while (centerIndex >= sampleCount) {
                centerIndex -= sampleCount;
            }
            gates[i] = new Gate(centerIndex, 5 + rand.nextInt(2));
        }
        return gates;
    }

    private static boolean isGate(Gate[] gates, int index, int sampleCount) {
        for (Gate gate : gates) {
            if (gate == null || !gate.contains(index, sampleCount)) continue;
            return true;
        }
        return false;
    }

    private static int computeSmoothWallHeight(int baseHeight, double t, double phaseA) {
        double wave = Math.sin(t * 3.0 + phaseA) * 0.35;
        return SRPColdVillageWallGenerator.clamp((int)Math.round((double)baseHeight + wave), 2, 3);
    }

    private static void drawWallLine(Level world, BlockPos a, BlockPos b, int wallHeight, int wallThickness, List<WallExclusion> exclusions) {
        int dx = b.getX() - a.getX();
        int dz = b.getZ() - a.getZ();
        int steps = Math.max(Math.abs(dx), Math.abs(dz));
        if (steps <= 1 || steps > 8) {
            return;
        }
        for (int i = 1; i < steps; ++i) {
            double lerp = (double)i / (double)steps;
            int x = a.getX() + (int)Math.round((double)dx * lerp);
            int z = a.getZ() + (int)Math.round((double)dz * lerp);
            SRPColdVillageWallGenerator.placeWallColumn(world, BlockPos.containing(x, 0, z), wallHeight, wallThickness, exclusions);
        }
    }

    private static void placeGateShoulders(Level world, RandomSource rand, BlockPos gatePos, BlockPos layoutCenter, int baseHeight, int wallThickness, List<WallExclusion> exclusions) {
        boolean eastWest = Math.abs(gatePos.getX() - layoutCenter.getX()) > Math.abs(gatePos.getZ() - layoutCenter.getZ());
        int shoulderHeight = Math.min(4, baseHeight + 1);
        if (eastWest) {
            SRPColdVillageWallGenerator.placeWallColumn(world, gatePos.offset(0, 0, -3 - wallThickness), shoulderHeight, wallThickness, exclusions);
            SRPColdVillageWallGenerator.placeWallColumn(world, gatePos.offset(0, 0, 3 + wallThickness), shoulderHeight, wallThickness, exclusions);
        } else {
            SRPColdVillageWallGenerator.placeWallColumn(world, gatePos.offset(-3 - wallThickness, 0, 0), shoulderHeight, wallThickness, exclusions);
            SRPColdVillageWallGenerator.placeWallColumn(world, gatePos.offset(3 + wallThickness, 0, 0), shoulderHeight, wallThickness, exclusions);
        }
    }

    private static BlockPos placeWallColumn(Level world, BlockPos columnPos, int wallHeight, int wallThickness, List<WallExclusion> exclusions) {
        BlockPos surface = SRPColdVillageWallGenerator.findTerrainSurface(world, columnPos);
        if (surface == null || surface.getY() <= 1) {
            return null;
        }
        if (SRPColdVillageWallGenerator.isExcluded(surface.getX(), surface.getZ(), exclusions) || SRPColdVillageWallGenerator.isBadWallArea(world, surface)) {
            return null;
        }
        int localThickness = SRPColdVillageWallGenerator.clamp(wallThickness, 1, 2);
        boolean placedAny = false;
        for (int ox = -localThickness / 2; ox <= localThickness / 2; ++ox) {
            for (int oz = -localThickness / 2; oz <= localThickness / 2; ++oz) {
                BlockPos base = SRPColdVillageWallGenerator.findTerrainSurface(world, surface.offset(ox, 0, oz));
                if (base == null || SRPColdVillageWallGenerator.isExcluded(base.getX(), base.getZ(), exclusions) || SRPColdVillageWallGenerator.isBadWallArea(world, base)) continue;
                SRPColdVillageWallGenerator.placeFoundation(world, base.below(), 3 + wallHeight);
                for (int y = 0; y < wallHeight; ++y) {
                    BlockPos p = base.above(y);
                    if (!SRPColdVillageWallGenerator.canReplaceWallBlock(world, p)) continue;
                    BlockState state = y == wallHeight - 1 ? BlockIds.legacyState(SRPBlocks.ParasiteRubble.get(), 9) : BlockIds.legacyState(SRPBlocks.ParasiteRubble.get(), 13);
                    world.setBlock(p, state, 2);
                    placedAny = true;
                }
            }
        }
        return placedAny ? surface : null;
    }

    private static boolean isExcluded(int x, int z, List<WallExclusion> exclusions) {
        for (WallExclusion exclusion : exclusions) {
            if (exclusion == null || !exclusion.contains(x, z)) continue;
            return true;
        }
        return false;
    }

    private static BlockPos findTerrainSurface(Level world, BlockPos pos) {
        BlockPos p = world.getHeight(BlockPos.containing(pos.getX(), 0, pos.getZ())).below();
        while (p.getY() > 1) {
            BlockState state = world.getBlockState(p);
            if (!SRPColdVillageWallGenerator.isSurfaceJunk(state)) {
                if (state.isAir()) {
                    p = p.below();
                    continue;
                }
                if (state.isSideSolid((BlockGetter)world, p, Direction.UP)) {
                    return p.above();
                }
            }
            p = p.below();
        }
        return null;
    }

    private static boolean isBadWallArea(Level world, BlockPos surface) {
        if (surface == null || SRPColdVillageWallGenerator.isBadWallSurface(world, surface.below())) {
            return true;
        }
        int baseY = surface.getY();
        int bad = 0;
        int checked = 0;
        for (int ox = -2; ox <= 2; ox += 2) {
            for (int oz = -2; oz <= 2; oz += 2) {
                BlockPos nearby = SRPColdVillageWallGenerator.findTerrainSurface(world, surface.offset(ox, 0, oz));
                ++checked;
                if (nearby != null && Math.abs(nearby.getY() - baseY) <= 3 && !SRPColdVillageWallGenerator.isBadWallSurface(world, nearby.below())) continue;
                ++bad;
            }
        }
        return checked > 0 && bad > 0;
    }

    private static boolean isSurfaceJunk(BlockState state) {
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || block == SRPBlocks.ParasiteRubble.get() || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow;
    }

    private static void placeFoundation(Level world, BlockPos start, int maxDepth) {
        BlockState support = BlockIds.legacyState(SRPBlocks.ParasiteRubble.get(), 9);
        BlockPos p = start;
        for (int depth = 0; p.getY() > 1 && depth < maxDepth && SRPColdVillageWallGenerator.shouldFillSupport(world, p); ++depth) {
            world.setBlock(p, support, 2);
            p = p.below();
        }
    }

    private static boolean isBadWallSurface(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.FROSTED_ICE || block == Blocks.WATER || block == Blocks.WATER || material == LegacyMaterial.water;
    }

    private static boolean canReplaceWallBlock(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow || state.canBeReplaced();
    }

    private static boolean shouldFillSupport(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow;
    }

    private static double copySign(double value, double sign) {
        return sign >= 0.0 ? value : -value;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class Bounds {
        int minX;
        int maxX;
        int minZ;
        int maxZ;

        Bounds(int minX, int maxX, int minZ, int maxZ) {
            this.minX = minX;
            this.maxX = maxX;
            this.minZ = minZ;
            this.maxZ = maxZ;
        }
    }

    private static final class Gate {
        final int centerIndex;
        final int halfWidth;

        Gate(int centerIndex, int halfWidth) {
            this.centerIndex = centerIndex;
            this.halfWidth = halfWidth;
        }

        boolean contains(int index, int sampleCount) {
            int diff = Math.abs(index - this.centerIndex);
            return (diff = Math.min(diff, sampleCount - diff)) <= this.halfWidth;
        }
    }

    public static final class WallExclusion {
        public final int minX;
        public final int maxX;
        public final int minZ;
        public final int maxZ;

        public WallExclusion(int minX, int maxX, int minZ, int maxZ, int padding) {
            this.minX = Math.min(minX, maxX) - padding;
            this.maxX = Math.max(minX, maxX) + padding;
            this.minZ = Math.min(minZ, maxZ) - padding;
            this.maxZ = Math.max(minZ, maxZ) + padding;
        }

        public boolean contains(int x, int z) {
            return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
        }
    }
}

