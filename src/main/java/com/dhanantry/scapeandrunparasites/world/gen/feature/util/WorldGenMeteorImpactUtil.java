package com.dhanantry.scapeandrunparasites.world.gen.feature.util;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class WorldGenMeteorImpactUtil {
    private static final int MIN_CARVE_Y = 5;
    private static final Map<String, List<PendingStructure>> PENDING = new HashMap<String, List<PendingStructure>>();
    private static final Map<String, Set<Long>> MAIN_METEOR_CENTERS = new HashMap<String, Set<Long>>();

    private WorldGenMeteorImpactUtil() {
    }

    public static void tickPendingStructures(Level world) {
        String dim = DimKeys.of(world);
        List<PendingStructure> list = PENDING.get(dim);
        if (list == null || list.isEmpty()) {
            return;
        }
        long now = world.getGameTime();
        Iterator<PendingStructure> it = list.iterator();
        while (it.hasNext()) {
            PendingStructure p = it.next();
            if (now < p.executeAt) continue;
            RandomSource r = RandomSource.create(p.seed);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure(p.name), r, world, p.origin, p.offX, p.offY, p.offZ);
            it.remove();
        }
        if (list.isEmpty()) {
            PENDING.remove(dim);
        }
    }

    public static void scheduleDelayedStructure(Level world, RandomSource rand, String name, BlockPos origin, int offX, int offY, int offZ, int delayTicks) {
        String dim = DimKeys.of(world);
        List<PendingStructure> list = PENDING.get(dim);
        if (list == null) {
            list = new ArrayList<PendingStructure>();
            PENDING.put(dim, list);
        }
        long at = world.getGameTime() + (long)Math.max(1, delayTicks);
        list.add(new PendingStructure(name, origin, offX, offY, offZ, at, rand.nextLong()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void markMainMeteor(Level world, BlockPos center) {
        String dim = DimKeys.of(world);
        Map<String, Set<Long>> map = MAIN_METEOR_CENTERS;
        synchronized (map) {
            Set<Long> set = MAIN_METEOR_CENTERS.get(dim);
            if (set == null) {
                set = new HashSet<Long>();
                MAIN_METEOR_CENTERS.put(dim, set);
            }
            set.add(center.asLong());
            if (set.size() > 512) {
                set.clear();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isNearMainMeteor(Level world, BlockPos pos, int minDist) {
        Set<Long> set;
        String dim = DimKeys.of(world);
        Map<String, Set<Long>> map = MAIN_METEOR_CENTERS;
        synchronized (map) {
            set = MAIN_METEOR_CENTERS.get(dim);
        }
        if (set == null || set.isEmpty()) {
            return false;
        }
        int minDistSq = minDist * minDist;
        for (Long l : set) {
            int dz;
            BlockPos c = BlockPos.of((long)l);
            int dx = c.getX() - pos.getX();
            if (dx * dx + (dz = c.getZ() - pos.getZ()) * dz > minDistSq) continue;
            return true;
        }
        return false;
    }

    /** The impact leaves water as it is: a column whose top block is a fluid is not carved and gets no stain, rubble or rim. */
    private static boolean isFluidTop(Level world, BlockPos top) {
        return !world.getBlockState(top).getFluidState().isEmpty();
    }

    public static void carveCraterBowl(Level world, RandomSource rand, BlockPos surface, int radius, int depth, float steepness, BlockState rim, BlockState stain, BlockState cooked) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int coreR = Math.max(2, (int)((float)radius * 0.28f));
        int coreRR = coreR * coreR;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                BlockPos p;
                int dx = x;
                int dz = z;
                int d2 = dx * dx + dz * dz;
                if (d2 > radius * radius) continue;
                BlockPos colTop = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(cx + x, surface.getY(), cz + z));
                if (world.hasChunkAt(colTop.below()) && isFluidTop(world, colTop.below())) continue;
                int topY = colTop.getY();
                double dist = Math.sqrt(d2);
                double t = dist / (double)radius;
                double bowl = 1.0 - t;
                double curve = bowl * bowl;
                int cut = topY - (int)Math.round((double)depth * curve);
                for (int y = topY; y > cut && y > 5 && world.hasChunkAt(p = BlockPos.containing(cx + x, y, cz + z)); --y) {
                    BlockState s = world.getBlockState(p);
                    LegacyMaterial m = LegacyMaterial.of(s);
                    if (m == LegacyMaterial.air || p.getY() <= 5) continue;
                    world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                }
                BlockPos top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(cx + x, surface.getY(), cz + z)).below();
                if (!world.hasChunkAt(top) || isFluidTop(world, top)) continue;
                if (d2 <= coreRR) {
                    if (rand.nextInt(3) == 0) {
                        world.setBlock(top, stain, 2);
                        continue;
                    }
                    world.setBlock(top, cooked, 2);
                    continue;
                }
                if (dist >= (double)radius - 1.5) {
                    world.setBlock(top, rim, 2);
                    continue;
                }
                if (!(dist >= (double)radius * 0.55) || rand.nextInt(4) != 0) continue;
                world.setBlock(top, stain, 2);
            }
        }
    }

    public static void scorchRings(Level world, RandomSource rand, BlockPos surface, int radius, BlockState stain) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int ring1 = (int)((float)radius * 1.15f);
        int ring2 = (int)((float)radius * 1.45f);
        for (int x = -ring2; x <= ring2; ++x) {
            for (int z = -ring2; z <= ring2; ++z) {
                BlockPos top;
                int dx = x;
                int dz = z;
                int d2 = dx * dx + dz * dz;
                if (d2 < ring1 * ring1 || d2 > ring2 * ring2 || rand.nextInt(3) != 0 || !world.hasChunkAt(top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(cx + x, surface.getY(), cz + z)).below()) || isFluidTop(world, top)) continue;
                world.setBlock(top, stain, 2);
            }
        }
    }

    public static void spawnEjecta(Level world, RandomSource rand, BlockPos surface, int radius, double dirX, double dirZ, BlockState rubble, BlockState stain) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int max = (int)((float)radius * 3.2f);
        int min = (int)((float)radius * 1.1f);
        for (int i = 0; i < radius * 30; ++i) {
            double dist = min + rand.nextInt(Math.max(1, max - min));
            double spread = (rand.nextDouble() - 0.5) * 1.25;
            double px = dirX * dist + -dirZ * dist * spread;
            double pz = dirZ * dist + dirX * dist * spread;
            int x = cx + (int)Math.round(px);
            int z = cz + (int)Math.round(pz);
            BlockPos top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(x, surface.getY(), z)).below();
            if (!world.hasChunkAt(top) || isFluidTop(world, top)) continue;
            if (rand.nextInt(5) == 0) {
                world.setBlock(top, stain, 2);
                continue;
            }
            world.setBlock(top, rubble, 2);
        }
    }

    public static void microCraters(Level world, RandomSource rand, BlockPos surface, int radius, double dirX, double dirZ, BlockState stain) {
        int cx = surface.getX();
        int cz = surface.getZ();
        int count = Math.max(3, radius / 3);
        for (int i = 0; i < count; ++i) {
            double dist = (double)radius * (1.2 + rand.nextDouble() * 2.0);
            double spread = (rand.nextDouble() - 0.5) * 1.6;
            double px = dirX * dist + -dirZ * dist * spread;
            double pz = dirZ * dist + dirX * dist * spread;
            int x0 = cx + (int)Math.round(px);
            int z0 = cz + (int)Math.round(pz);
            int r = 2 + rand.nextInt(3);
            int rr = r * r;
            for (int x = -r; x <= r; ++x) {
                for (int z = -r; z <= r; ++z) {
                    BlockPos top;
                    int d2 = x * x + z * z;
                    if (d2 > rr || !world.hasChunkAt(top = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(x0 + x, surface.getY(), z0 + z)).below()) || isFluidTop(world, top) || rand.nextInt(3) != 0) continue;
                    world.setBlock(top, stain, 2);
                }
            }
        }
    }

    public static TunnelResult carveAngledTunnel(Level world, BlockPos center, int radius, int length, double dirX, double dirY, double dirZ) {
        double mag = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        if (mag < 1.0E-6) {
            return new TunnelResult(false, 0, 0, 0, 0);
        }
        dirX /= mag;
        dirY /= mag;
        dirZ /= mag;
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        boolean any = false;
        int firstY = 0;
        int lastY = 0;
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int t = 0; t <= length; ++t) {
            int px = cx + (int)Math.round(dirX * (double)t);
            int py = cy + (int)Math.round(dirY * (double)t);
            int pz = cz + (int)Math.round(dirZ * (double)t);
            TunnelResultSlice slice = WorldGenMeteorImpactUtil.carveSphereCount(world, BlockPos.containing(px, py, pz), radius);
            if (!slice.brokeAny) continue;
            if (!any) {
                any = true;
                firstY = slice.firstBrokenY;
            }
            lastY = slice.lastBrokenY;
            if (slice.lowestY < low) {
                low = slice.lowestY;
            }
            if (slice.highestY <= high) continue;
            high = slice.highestY;
        }
        if (!any) {
            return new TunnelResult(false, 0, 0, 0, 0);
        }
        return new TunnelResult(true, firstY, lastY, low, high);
    }

    public static void clearVegetationInArea(Level world, BlockPos center, int radius, int yMin, int yMax) {
        int cx = center.getX();
        int cz = center.getZ();
        int rr = radius * radius;
        for (int x = -radius; x <= radius; ++x) {
            for (int z = -radius; z <= radius; ++z) {
                int d2 = x * x + z * z;
                if (d2 > rr) continue;
                int ax = cx + x;
                int az = cz + z;
                for (int y = yMin; y <= yMax; ++y) {
                    BlockState s;
                    BlockPos p = BlockPos.containing(ax, y, az);
                    if (!world.hasChunkAt(p) || LegacyMaterial.of(s = world.getBlockState(p)) == LegacyMaterial.air || !WorldGenMeteorImpactUtil.isVegetation(s) || p.getY() <= 5) continue;
                    world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    private static TunnelResultSlice carveSphereCount(Level world, BlockPos center, int radius) {
        int cx = center.getX();
        int cy = center.getY();
        int cz = center.getZ();
        int rr = radius * radius;
        boolean any = false;
        int firstY = 0;
        int lastY = 0;
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    BlockState s;
                    BlockPos p;
                    int d2 = x * x + y * y + z * z;
                    if (d2 > rr || !world.hasChunkAt(p = BlockPos.containing(cx + x, cy + y, cz + z)) || LegacyMaterial.of(s = world.getBlockState(p)) == LegacyMaterial.air || p.getY() <= 5) continue;
                    world.setBlock(p, Blocks.AIR.defaultBlockState(), 2);
                    if (!any) {
                        any = true;
                        firstY = p.getY();
                    }
                    lastY = p.getY();
                    if (p.getY() < low) {
                        low = p.getY();
                    }
                    if (p.getY() <= high) continue;
                    high = p.getY();
                }
            }
        }
        if (!any) {
            return new TunnelResultSlice(false, 0, 0, 0, 0);
        }
        return new TunnelResultSlice(true, firstY, lastY, low, high);
    }

    private static boolean isVegetation(BlockState s) {
        Block b = s.getBlock();
        LegacyMaterial m = LegacyMaterial.of(s);
        if (m == LegacyMaterial.leaves || m == LegacyMaterial.wood || m == LegacyMaterial.vine) {
            return true;
        }
        if (b == Blocks.OAK_LEAVES || b == Blocks.ACACIA_LEAVES || b == Blocks.OAK_LOG || b == Blocks.ACACIA_LOG || b == Blocks.VINE || b == Blocks.TORCH || b == Blocks.REDSTONE_TORCH || b == Blocks.SNOW || b == Blocks.ICE || b == Blocks.PACKED_ICE || b == Blocks.FROSTED_ICE || b == Blocks.FIRE || b == Blocks.SHORT_GRASS || b == Blocks.DEAD_BUSH || b == Blocks.DANDELION || b == Blocks.POPPY || b == Blocks.BROWN_MUSHROOM || b == Blocks.RED_MUSHROOM || b == Blocks.TALL_GRASS) {
            return true;
        }
        if (b instanceof LeavesBlock) {
            return true;
        }
        if (b.defaultBlockState().is(BlockTags.LOGS)) {
            return true;
        }
        if (b.defaultBlockState().is(BlockTags.LOGS)) {
            return true;
        }
        return b instanceof VineBlock;
    }

    public static void updateWaterAfterImpact(Level world, BlockPos surface, int radius, int depth) {
        int yMax;
        int cx = surface.getX();
        int cy = surface.getY();
        int cz = surface.getZ();
        int r = Math.max(8, (int)((float)radius * 1.8f) + 12);
        int yMin = cy - depth - 6;
        if (yMin < 1) {
            yMin = 1;
        }
        if ((yMax = cy + 24) > 255) {
            yMax = 255;
        }
        Block still = Blocks.WATER;
        Block flowing = Blocks.WATER;
        for (int x = -r; x <= r; ++x) {
            for (int z = -r; z <= r; ++z) {
                for (int y = yMin; y <= yMax; ++y) {
                    BlockPos p = BlockPos.containing(cx + x, y, cz + z);
                    if (!world.hasChunkAt(p)) continue;
                    BlockState s = world.getBlockState(p);
                    Block b = s.getBlock();
                    if (b == still || b == flowing) {
                        world.scheduleTick(p, b, 1);
                        world.updateNeighborsAt(p, b);
                        continue;
                    }
                    if (LegacyMaterial.of(s) != LegacyMaterial.air) continue;
                    BlockPos p2 = p.above();
                    BlockState s2 = world.getBlockState(p2);
                    Block b2 = s2.getBlock();
                    if (b2 == still || b2 == flowing) {
                        world.scheduleTick(p2, b2, 1);
                        world.updateNeighborsAt(p2, b2);
                    }
                    if ((b2 = (s2 = world.getBlockState(p2 = p.below())).getBlock()) == still || b2 == flowing) {
                        world.scheduleTick(p2, b2, 1);
                        world.updateNeighborsAt(p2, b2);
                    }
                    if ((b2 = (s2 = world.getBlockState(p2 = p.north())).getBlock()) == still || b2 == flowing) {
                        world.scheduleTick(p2, b2, 1);
                        world.updateNeighborsAt(p2, b2);
                    }
                    if ((b2 = (s2 = world.getBlockState(p2 = p.south())).getBlock()) == still || b2 == flowing) {
                        world.scheduleTick(p2, b2, 1);
                        world.updateNeighborsAt(p2, b2);
                    }
                    if ((b2 = (s2 = world.getBlockState(p2 = p.west())).getBlock()) == still || b2 == flowing) {
                        world.scheduleTick(p2, b2, 1);
                        world.updateNeighborsAt(p2, b2);
                    }
                    if ((b2 = (s2 = world.getBlockState(p2 = p.east())).getBlock()) != still && b2 != flowing) continue;
                    world.scheduleTick(p2, b2, 1);
                    world.updateNeighborsAt(p2, b2);
                }
            }
        }
    }

    private static final class TunnelResultSlice {
        final boolean brokeAny;
        final int firstBrokenY;
        final int lastBrokenY;
        final int lowestY;
        final int highestY;

        TunnelResultSlice(boolean brokeAny, int firstBrokenY, int lastBrokenY, int lowestY, int highestY) {
            this.brokeAny = brokeAny;
            this.firstBrokenY = firstBrokenY;
            this.lastBrokenY = lastBrokenY;
            this.lowestY = lowestY;
            this.highestY = highestY;
        }
    }

    public static final class TunnelResult {
        public final boolean anyBroken;
        public final int firstBrokenY;
        public final int lastBrokenY;
        public final int lowestY;
        public final int highestY;

        public TunnelResult(boolean anyBroken, int firstBrokenY, int lastBrokenY, int lowestY, int highestY) {
            this.anyBroken = anyBroken;
            this.firstBrokenY = firstBrokenY;
            this.lastBrokenY = lastBrokenY;
            this.lowestY = lowestY;
            this.highestY = highestY;
        }
    }

    private static final class PendingStructure {
        final String name;
        final BlockPos origin;
        final int offX;
        final int offY;
        final int offZ;
        final long executeAt;
        final long seed;

        PendingStructure(String name, BlockPos origin, int offX, int offY, int offZ, long executeAt, long seed) {
            this.name = name;
            this.origin = origin;
            this.offX = offX;
            this.offY = offY;
            this.offZ = offZ;
            this.executeAt = executeAt;
            this.seed = seed;
        }
    }
}

