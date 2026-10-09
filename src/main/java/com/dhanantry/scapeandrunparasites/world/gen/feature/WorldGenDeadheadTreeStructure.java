package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenerator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class WorldGenDeadheadTreeStructure
extends WorldGenerator {
    private static final int SOURCE_SEARCH_RADIUS = 6;
    private static final int LOWER_TRUNK_SEARCH_RADIUS = 5;
    private static final int LOWER_TRUNK_SEARCH_DEPTH = 96;
    private static final int ROOT_MAX_DEPTH = 48;
    private static final int ROOT_LEAF_START_DEPTH = 5;
    private static final float ROOT_WANDER_CHANCE = 0.22f;
    private static final float ROOT_LEAF_CHANCE = 0.28f;
    private static final Direction[] ROOT_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final boolean mushroomMode;
    private final boolean rootMode;
    private static final TreeTemplate[] TREES = new TreeTemplate[]{new TreeTemplate("deadhead_tree_large_1", BlockPos.containing(4, 0, 5)), new TreeTemplate("deadhead_tree_large_2", BlockPos.containing(5, 0, 5)), new TreeTemplate("deadhead_tree_large_3", BlockPos.containing(4, 0, 5)), new TreeTemplate("deadhead_tree_large_4", BlockPos.containing(6, 0, 6))};

    public WorldGenDeadheadTreeStructure(boolean notify) {
        this(notify, false, false);
    }

    public WorldGenDeadheadTreeStructure(boolean notify, boolean mushroomMode) {
        this(notify, mushroomMode, false);
    }

    public WorldGenDeadheadTreeStructure(boolean notify, boolean mushroomMode, boolean rootMode) {
        super(notify);
        this.mushroomMode = mushroomMode;
        this.rootMode = rootMode;
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        if (!(worldIn instanceof ServerLevel)) {
            return false;
        }
        ServerLevel worldServer = (ServerLevel)worldIn;
        boolean stackingOnDeadheadLeaves = this.mushroomMode && this.isDeadheadLeaves(worldIn.getBlockState(position));
        TreeTemplate selected = TREES[rand.nextInt(TREES.length)];
        Rotation rotation = Rotation.values()[rand.nextInt(Rotation.values().length)];
        StructureTemplate template = worldServer.getServer().getStructureManager().get(ResourceLocation.fromNamespaceAndPath("srparasites", selected.name)).orElse(null);
        if (template == null) {
            return false;
        }
        StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(Mirror.NONE).setRotation(rotation).setIgnoreEntities(true).addProcessor(net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor.STRUCTURE_BLOCK);
        BlockPos transformedAnchor = StructureTemplate.calculateRelativePosition(settings, (BlockPos)selected.saplingAnchor);
        BlockPos origin = position.subtract(transformedAnchor);
        BlockPos[] bounds = this.getTransformedBounds(template, settings, origin);
        BlockPos min = bounds[0];
        BlockPos max = bounds[1];
        if (min.getY() < 0 || max.getY() >= worldIn.getHeight()) {
            return false;
        }
        if (!worldIn.hasChunksAt(min, max)) {
            return false;
        }
        List<PreservedBlock> preservedIce = this.capturePreservedIce(worldIn, min, max);
        template.placeInWorld(worldServer, origin, origin, settings, rand, 3);
        this.restorePreservedIce(worldIn, preservedIce);
        if (this.rootMode && !this.mushroomMode) {
            this.growFloatingRoots(worldIn, rand, min, max);
        }
        if (stackingOnDeadheadLeaves) {
            this.connectStackedTree(worldIn, position, min, max);
        }
        return true;
    }

    private List<PreservedBlock> capturePreservedIce(Level world, BlockPos min, BlockPos max) {
        ArrayList<PreservedBlock> preserved = new ArrayList<PreservedBlock>();
        for (int x = min.getX(); x <= max.getX(); ++x) {
            for (int y = min.getY(); y <= max.getY(); ++y) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    BlockPos pos = BlockPos.containing(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (!this.isPreservedIce(state)) continue;
                    preserved.add(new PreservedBlock(pos, state));
                }
            }
        }
        return preserved;
    }

    private void restorePreservedIce(Level world, List<PreservedBlock> preserved) {
        for (PreservedBlock entry : preserved) {
            world.setBlock(entry.pos, entry.state, 2);
        }
    }

    private boolean isPreservedIce(BlockState state) {
        return state.getBlock() == Blocks.ICE || state.getBlock() == Blocks.PACKED_ICE || state.getBlock() == Blocks.FROSTED_ICE;
    }

    private void growFloatingRoots(Level world, RandomSource rand, BlockPos min, BlockPos max) {
        List<BlockPos> starts = this.findLowestTrunkLayer(world, min, max);
        for (BlockPos start : starts) {
            this.growSingleRoot(world, rand, start);
        }
    }

    private List<BlockPos> findLowestTrunkLayer(Level world, BlockPos min, BlockPos max) {
        ArrayList<BlockPos> result = new ArrayList<BlockPos>();
        for (int y = min.getY(); y <= max.getY(); ++y) {
            result.clear();
            for (int x = min.getX(); x <= max.getX(); ++x) {
                for (int z = min.getZ(); z <= max.getZ(); ++z) {
                    BlockPos pos = BlockPos.containing(x, y, z);
                    if (!this.isDeadheadTrunk(world.getBlockState(pos))) continue;
                    result.add(pos);
                }
            }
            if (result.isEmpty()) continue;
            return new ArrayList<BlockPos>(result);
        }
        return result;
    }

    private void growSingleRoot(Level world, RandomSource rand, BlockPos start) {
        BlockPos below;
        BlockPos cursor = start;
        ArrayList<BlockPos> rootBlocks = new ArrayList<BlockPos>();
        int verticalDepth = 0;
        for (int i = 0; i < 48 && !this.isRootGround(world, below = cursor.below()) && this.canRootReplace(world, below); ++i) {
            Direction direction;
            BlockPos side;
            this.placeRootTrunk(world, below);
            rootBlocks.add(below);
            cursor = below;
            ++verticalDepth;
            if (!(rand.nextFloat() < 0.22f) || !this.canRootReplace(world, side = cursor.relative(direction = ROOT_DIRECTIONS[rand.nextInt(ROOT_DIRECTIONS.length)])) || this.isRootGround(world, side.below())) continue;
            this.placeRootTrunk(world, side);
            rootBlocks.add(side);
            cursor = side;
        }
        if (verticalDepth > 5) {
            this.addRootLeaves(world, rand, rootBlocks);
        }
    }

    private void addRootLeaves(Level world, RandomSource rand, List<BlockPos> rootBlocks) {
        if (rootBlocks.size() <= 5) {
            return;
        }
        BlockState leaves = SRPBlocks.DeadheadLeaves.get().defaultBlockState();
        for (int i = 5; i < rootBlocks.size(); ++i) {
            if (rand.nextFloat() >= 0.28f) continue;
            BlockPos rootPos = rootBlocks.get(i);
            int attempts = 1 + rand.nextInt(2);
            for (int j = 0; j < attempts; ++j) {
                Direction direction = ROOT_DIRECTIONS[rand.nextInt(ROOT_DIRECTIONS.length)];
                BlockPos leafPos = rootPos.relative(direction);
                if (!this.canPlaceRootLeaf(world, leafPos)) continue;
                world.setBlock(leafPos, leaves, 2);
            }
        }
    }

    private boolean canPlaceRootLeaf(Level world, BlockPos pos) {
        if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        if (this.isDeadheadTrunk(state)) {
            return false;
        }
        if (state.is(BlockTags.LEAVES)) {
            return false;
        }
        return world.isEmptyBlock(pos) || state.canBeReplaced();
    }

    private boolean isRootGround(Level world, BlockPos pos) {
        if (pos.getY() <= 0) {
            return true;
        }
        BlockState state = world.getBlockState(pos);
        if (this.isDeadheadTrunk(state)) {
            return true;
        }
        if (state.is(BlockTags.LEAVES)) {
            return false;
        }
        if (world.isEmptyBlock(pos) || state.canBeReplaced()) {
            return false;
        }
        return LegacyMaterial.of(state).isSolid() || LegacyMaterial.of(state).isLiquid();
    }

    private boolean canRootReplace(Level world, BlockPos pos) {
        if (pos.getY() <= 0 || pos.getY() >= world.getHeight()) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        if (world.isEmptyBlock(pos)) {
            return true;
        }
        if (state.is(BlockTags.LEAVES)) {
            return true;
        }
        return state.canBeReplaced();
    }

    private void placeRootTrunk(Level world, BlockPos pos) {
        world.setBlock(pos, this.getDeadheadTrunkState(), 2);
    }

    private void connectStackedTree(Level world, BlockPos anchor, BlockPos min, BlockPos max) {
        BlockPos upperTrunk = this.findLowestUpperTrunk(world, anchor, min, max);
        if (upperTrunk == null) {
            return;
        }
        BlockPos lowerTrunk = this.findHighestLowerTrunk(world, upperTrunk);
        if (lowerTrunk == null) {
            return;
        }
        List<BlockPos> path = this.buildDownwardPath(upperTrunk, lowerTrunk);
        if (!this.canCarveConnection(world, path, lowerTrunk)) {
            return;
        }
        BlockState trunkState = this.getDeadheadTrunkState();
        for (BlockPos pos : path) {
            if (this.isDeadheadTrunk(world.getBlockState(pos))) continue;
            world.setBlock(pos, trunkState, 2);
        }
    }

    private BlockPos findLowestUpperTrunk(Level world, BlockPos anchor, BlockPos min, BlockPos max) {
        int minX = Math.max(min.getX(), anchor.getX() - 6);
        int maxX = Math.min(max.getX(), anchor.getX() + 6);
        int minZ = Math.max(min.getZ(), anchor.getZ() - 6);
        int maxZ = Math.min(max.getZ(), anchor.getZ() + 6);
        int minY = Math.max(min.getY(), anchor.getY());
        int maxY = max.getY();
        BlockPos best = null;
        long bestHorizontalDistance = Long.MAX_VALUE;
        for (int y = minY; y <= maxY; ++y) {
            BlockPos bestAtY = null;
            long bestAtYDistance = Long.MAX_VALUE;
            for (int x = minX; x <= maxX; ++x) {
                for (int z = minZ; z <= maxZ; ++z) {
                    long dz;
                    long dx;
                    long distance;
                    BlockPos pos = BlockPos.containing(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (!this.isDeadheadTrunk(state) || (distance = (dx = (long)(x - anchor.getX())) * dx + (dz = (long)(z - anchor.getZ())) * dz) >= bestAtYDistance) continue;
                    bestAtYDistance = distance;
                    bestAtY = pos;
                }
            }
            if (bestAtY == null) continue;
            best = bestAtY;
            bestHorizontalDistance = bestAtYDistance;
            break;
        }
        return best;
    }

    private BlockPos findHighestLowerTrunk(Level world, BlockPos upperTrunk) {
        int minY = Math.max(1, upperTrunk.getY() - 96);
        for (int y = upperTrunk.getY() - 1; y >= minY; --y) {
            BlockPos bestAtY = null;
            long bestDistance = Long.MAX_VALUE;
            for (int x = upperTrunk.getX() - 5; x <= upperTrunk.getX() + 5; ++x) {
                for (int z = upperTrunk.getZ() - 5; z <= upperTrunk.getZ() + 5; ++z) {
                    long dz;
                    long dx;
                    long distance;
                    BlockPos pos = BlockPos.containing(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (!this.isDeadheadTrunk(state) || (distance = (dx = (long)(x - upperTrunk.getX())) * dx + (dz = (long)(z - upperTrunk.getZ())) * dz) >= bestDistance) continue;
                    bestDistance = distance;
                    bestAtY = pos;
                }
            }
            if (bestAtY == null) continue;
            return bestAtY;
        }
        return null;
    }

    private List<BlockPos> buildDownwardPath(BlockPos upper, BlockPos lower) {
        ArrayList<BlockPos> path = new ArrayList<BlockPos>();
        BlockPos cursor = upper;
        int totalDrop = Math.max(1, upper.getY() - lower.getY());
        int step = 1;
        while (cursor.getY() > lower.getY()) {
            cursor = cursor.below();
            this.addUnique(path, cursor);
            double progress = Math.min(1.0, (double)step / (double)totalDrop);
            int desiredX = (int)Math.round((double)upper.getX() + (double)(lower.getX() - upper.getX()) * progress);
            int desiredZ = (int)Math.round((double)upper.getZ() + (double)(lower.getZ() - upper.getZ()) * progress);
            while (cursor.getX() != desiredX) {
                cursor = cursor.offset(Integer.compare(desiredX, cursor.getX()), 0, 0);
                this.addUnique(path, cursor);
            }
            while (cursor.getZ() != desiredZ) {
                cursor = cursor.offset(0, 0, Integer.compare(desiredZ, cursor.getZ()));
                this.addUnique(path, cursor);
            }
            ++step;
        }
        while (cursor.getX() != lower.getX()) {
            cursor = cursor.offset(Integer.compare(lower.getX(), cursor.getX()), 0, 0);
            this.addUnique(path, cursor);
        }
        while (cursor.getZ() != lower.getZ()) {
            cursor = cursor.offset(0, 0, Integer.compare(lower.getZ(), cursor.getZ()));
            this.addUnique(path, cursor);
        }
        return path;
    }

    private boolean canCarveConnection(Level world, List<BlockPos> path, BlockPos targetTrunk) {
        for (BlockPos pos : path) {
            BlockState state = world.getBlockState(pos);
            if (pos.equals(targetTrunk) || this.isDeadheadTrunk(state) || world.isEmptyBlock(pos) || state.is(BlockTags.LEAVES) || state.canBeReplaced()) continue;
            return false;
        }
        return true;
    }

    private void addUnique(List<BlockPos> path, BlockPos pos) {
        if (path.isEmpty() || !path.get(path.size() - 1).equals(pos)) {
            path.add(pos);
        }
    }

    private boolean isDeadheadLeaves(BlockState state) {
        return state.getBlock() == SRPBlocks.DeadheadLeaves.get();
    }

    private boolean isDeadheadTrunk(BlockState state) {
        return state.getBlock() == SRPBlocks.ParasiteTrunk.get() && state.getValue(BlockParasiteTrunk.VARIANT) == BlockParasiteTrunk.EnumType.DEADHEAD;
    }

    private BlockState getDeadheadTrunkState() {
        return SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.DEADHEAD)).setValue((Property)BlockParasiteTrunk.AXIS, Direction.Axis.Y);
    }

    private BlockPos[] getTransformedBounds(StructureTemplate template, StructurePlaceSettings settings, BlockPos origin) {
        net.minecraft.core.Vec3i size = template.getSize();
        int x1 = Math.max(0, size.getX() - 1);
        int y1 = Math.max(0, size.getY() - 1);
        int z1 = Math.max(0, size.getZ() - 1);
        BlockPos[] corners = new BlockPos[]{BlockPos.containing(0, 0, 0), BlockPos.containing(x1, 0, 0), BlockPos.containing(0, y1, 0), BlockPos.containing(0, 0, z1), BlockPos.containing(x1, y1, 0), BlockPos.containing(x1, 0, z1), BlockPos.containing(0, y1, z1), BlockPos.containing(x1, y1, z1)};
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        for (BlockPos corner : corners) {
            BlockPos transformed = StructureTemplate.calculateRelativePosition(settings, (BlockPos)corner).offset(origin);
            minX = Math.min(minX, transformed.getX());
            minY = Math.min(minY, transformed.getY());
            minZ = Math.min(minZ, transformed.getZ());
            maxX = Math.max(maxX, transformed.getX());
            maxY = Math.max(maxY, transformed.getY());
            maxZ = Math.max(maxZ, transformed.getZ());
        }
        return new BlockPos[]{BlockPos.containing(minX, minY, minZ), BlockPos.containing(maxX, maxY, maxZ)};
    }

    private static class TreeTemplate {
        private final String name;
        private final BlockPos saplingAnchor;

        private TreeTemplate(String name, BlockPos saplingAnchor) {
            this.name = name;
            this.saplingAnchor = saplingAnchor;
        }
    }

    private static class PreservedBlock {
        private final BlockPos pos;
        private final BlockState state;

        private PreservedBlock(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }
    }
}

