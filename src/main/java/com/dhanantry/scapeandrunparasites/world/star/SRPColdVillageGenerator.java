package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPColdVillageWallGenerator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;

public final class SRPColdVillageGenerator {
    private static final int VILLAGE_DISTANCE = 20;
    private static final int VILLAGE_SEPARATION = 5;
    private static final int VILLAGE_SALT = 10387312;
    private static final ResourceLocation WELL = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_well");
    private static final ResourceLocation BLACKSMITH = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_blacksmith");
    private static final ResourceLocation CHURCH = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_church");
    private static final ResourceLocation FARM = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_farm");
    private static final ResourceLocation MEDIUM_HOUSE = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_medium_house");
    private static final ResourceLocation MEDIUM_HOUSE_1 = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_medium_house_1");
    private static final ResourceLocation SMALL_1 = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_small1");
    private static final ResourceLocation SMALL_2 = ResourceLocation.fromNamespaceAndPath("srparasites", "dh_village_small2");

    /** Whether the chunk is the chunk of a village of the grid (the village grid of 1.12, with the salt of this generator). */
    public static boolean isVillageChunk(ServerLevel level, int chunkX, int chunkZ) {
        return new SRPColdVillageGenerator().isVillageChunkOf(level, chunkX, chunkZ);
    }

    /** Builds the village of the chunk (IWorldGenerator.generate of 1.10.9); the chunks around are loaded as needed. */
    public static void generateVillage(ServerLevel level, int chunkX, int chunkZ) {
        long seed = level.getSeed() ^ ((long) chunkX * 341873128712L + (long) chunkZ * 132897987541L);
        new SRPColdVillageGenerator().generate(RandomSource.create(seed), chunkX, chunkZ, level);
    }

    private void generate(RandomSource random, int chunkX, int chunkZ, ServerLevel world) {
        if (SRPWorldEntitySpawner.starType != 1) {
            return;
        }
        if (!this.isVillageChunkOf(world, chunkX, chunkZ)) {
            return;
        }
        int centerX = (chunkX << 4) + 8;
        int centerZ = (chunkZ << 4) + 8;
        BlockPos center = this.findTerrainSurface(world, BlockPos.containing(centerX, 0, centerZ));
        if (center == null) {
            return;
        }
        if (!this.isValidColdVillageBiome(world, center)) {
            return;
        }
        if (this.isBadBuildArea(world, center, 12)) {
            return;
        }
        this.generateVillage(world, random, center);
    }

    private boolean isVillageChunkOf(ServerLevel world, int chunkX, int chunkZ) {
        int regionX = chunkX;
        int regionZ = chunkZ;
        if (regionX < 0) {
            regionX -= 19;
        }
        if (regionZ < 0) {
            regionZ -= 19;
        }
        int gridX = regionX / 20;
        int gridZ = regionZ / 20;
        java.util.Random rand = new java.util.Random((long) gridX * 341873128712L + (long) gridZ * 132897987541L + world.getSeed() + 10387312L);
        gridX *= 20;
        gridZ *= 20;
        return chunkX == (gridX += rand.nextInt(15)) && chunkZ == (gridZ += rand.nextInt(15));
    }

    private boolean isValidColdVillageBiome(ServerLevel world, BlockPos center) {
        int radius = 20;
        int checked = 0;
        int valid = 0;
        for (int x = -radius; x <= radius; x += 8) {
            for (int z = -radius; z <= radius; z += 8) {
                ++checked;
                if (!this.isColdVillageBiome(world.getBiome(center.offset(x, 0, z)))) continue;
                ++valid;
            }
        }
        return checked > 0 && valid >= Math.max(1, checked * 2 / 3);
    }

    private boolean isColdVillageBiome(Holder<Biome> biome) {
        return biome.is(Biomes.SNOWY_PLAINS) || biome.is(Biomes.SNOWY_TAIGA) || biome.is(Biomes.SNOWY_BEACH) || biome.is(Biomes.FROZEN_RIVER);
    }

    private void generateVillage(ServerLevel world, RandomSource random, BlockPos center) {
        Rotation rotation = Rotation.values()[random.nextInt(Rotation.values().length)];
        VillageStyle style = this.pickVillageStyle(random);
        ArrayList<SRPColdVillageWallGenerator.WallExclusion> wallExclusions = new ArrayList<SRPColdVillageWallGenerator.WallExclusion>();
        int buildingCount = 0;
        BlockPos wellPos = center.below(7);
        if (this.placeAnchored(world, WELL, wellPos, rotation, false)) {
            this.addWallExclusion(world, wallExclusions, WELL, wellPos, rotation, 7);
            ++buildingCount;
        }
        if ((buildingCount += this.placePatternBuildings(world, random, center, rotation, style, wallExclusions)) >= 2) {
            SRPColdVillageWallGenerator.generate(world, random, center, buildingCount, wallExclusions);
            this.scatterLogPiles(world, random, center, wallExclusions, buildingCount, style);
        }
        this.spawnVillagers(world, random, center, Math.max(2, buildingCount + style.extraVillagers));
    }

    private VillageStyle pickVillageStyle(RandomSource random) {
        int roll = random.nextInt(100);
        if (roll < 28) {
            return new VillageStyle(86, -15, 0, 0);
        }
        if (roll < 74) {
            return new VillageStyle(100, 0, 1, 1);
        }
        return new VillageStyle(116, 15, 2, 2);
    }

    private int placePatternBuildings(ServerLevel world, RandomSource random, BlockPos center, Rotation rotation, VillageStyle style, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions) {
        int count = 0;
        if (this.placeAnchoredScaled(world, wallExclusions, SMALL_1, center, this.scaleOffset(-16, style.spreadPercent), 0, rotation, false)) {
            ++count;
        }
        if (this.placeAnchoredScaled(world, wallExclusions, SMALL_2, center, this.scaleOffset(16, style.spreadPercent), 0, rotation, false)) {
            ++count;
        }
        if (this.placeAnchoredScaled(world, wallExclusions, MEDIUM_HOUSE, center, this.scaleOffset(-8, style.spreadPercent), this.scaleOffset(-18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 70 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, MEDIUM_HOUSE_1, center, this.scaleOffset(8, style.spreadPercent), this.scaleOffset(18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 58 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, BLACKSMITH, center, this.scaleOffset(22, style.spreadPercent), this.scaleOffset(-12, style.spreadPercent), rotation, true)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 48 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, CHURCH, center, this.scaleOffset(-22, style.spreadPercent), this.scaleOffset(14, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (this.shouldPlaceOptional(random, 66 + style.optionalBias) && this.placeAnchoredScaled(world, wallExclusions, FARM, center, this.scaleOffset(22, style.spreadPercent), this.scaleOffset(18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        if (style.spreadPercent >= 110 && this.shouldPlaceOptional(random, 45) && this.placeAnchoredScaled(world, wallExclusions, random.nextBoolean() ? SMALL_1 : SMALL_2, center, this.scaleOffset(-24, style.spreadPercent), this.scaleOffset(18, style.spreadPercent), rotation, false)) {
            ++count;
        }
        return count;
    }

    private boolean shouldPlaceOptional(RandomSource random, int percent) {
        percent = Math.max(0, Math.min(100, percent));
        return random.nextInt(100) < percent;
    }

    private int scaleOffset(int value, int percent) {
        return (int)Math.round((double)value * ((double)percent / 100.0));
    }

    private boolean placeAnchoredScaled(ServerLevel world, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, ResourceLocation structure, BlockPos center, int offsetX, int offsetZ, Rotation rotation, boolean blacksmith) {
        BlockPos offset = this.rotateOffset(offsetX, offsetZ, rotation);
        BlockPos doorPos = center.offset(offset.getX(), 0, offset.getZ());
        if (this.isBadBuildArea(world, doorPos, 8)) {
            return false;
        }
        if (this.isIceLakeArea(world, doorPos, 7)) {
            return false;
        }
        BlockPos surfaceDoorPos = this.findTerrainSurface(world, doorPos);
        if (surfaceDoorPos == null) {
            return false;
        }
        if (this.placeAnchored(world, structure, surfaceDoorPos, rotation, blacksmith)) {
            this.addWallExclusion(world, wallExclusions, structure, surfaceDoorPos, rotation, 6);
            return true;
        }
        return false;
    }

    private void addWallExclusion(ServerLevel world, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, ResourceLocation structure, BlockPos origin, Rotation rotation, int padding) {
        if (wallExclusions == null) {
            return;
        }
        StructureTemplate template = world.getServer().getStructureManager().get(structure).orElse(null);
        if (template == null) {
            return;
        }
        Vec3i size = template.getSize();
        int maxLocalX = Math.max(0, size.getX() - 1);
        int maxLocalZ = Math.max(0, size.getZ() - 1);
        BlockPos a = this.transformLocal(origin, 0, 0, 0, size, rotation);
        BlockPos b = this.transformLocal(origin, maxLocalX, 0, 0, size, rotation);
        BlockPos c = this.transformLocal(origin, 0, 0, maxLocalZ, size, rotation);
        BlockPos d = this.transformLocal(origin, maxLocalX, 0, maxLocalZ, size, rotation);
        int minX = Math.min(Math.min(a.getX(), b.getX()), Math.min(c.getX(), d.getX()));
        int maxX = Math.max(Math.max(a.getX(), b.getX()), Math.max(c.getX(), d.getX()));
        int minZ = Math.min(Math.min(a.getZ(), b.getZ()), Math.min(c.getZ(), d.getZ()));
        int maxZ = Math.max(Math.max(a.getZ(), b.getZ()), Math.max(c.getZ(), d.getZ()));
        wallExclusions.add(new SRPColdVillageWallGenerator.WallExclusion(minX, maxX, minZ, maxZ, padding));
    }

    private BlockPos rotateOffset(int x, int z, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90: {
                return BlockPos.containing(-z, 0, x);
            }
            case CLOCKWISE_180: {
                return BlockPos.containing(-x, 0, -z);
            }
            case COUNTERCLOCKWISE_90: {
                return BlockPos.containing(z, 0, -x);
            }
        }
        return BlockPos.containing(x, 0, z);
    }

    private boolean placeAnchored(ServerLevel world, ResourceLocation structure, BlockPos entrancePos, Rotation rotation, boolean blacksmith) {
        StructureTemplate template = world.getServer().getStructureManager().get(structure).orElse(null);
        if (template == null) {
            return false;
        }
        if (this.isBadBuildArea(world, entrancePos, 6)) {
            return false;
        }
        this.clearTreeBlocksForTemplate(world, template, entrancePos, rotation);
        StructurePlaceSettings settings = new StructurePlaceSettings().setMirror(Mirror.NONE).setRotation(rotation).setIgnoreEntities(false);
        template.placeInWorld(world, entrancePos, entrancePos, settings, world.getRandom(), 2);
        this.buildRubbleSupports(world, template, entrancePos, rotation);
        if (blacksmith) {
            this.fillBlacksmithChests(world, entrancePos, template, rotation);
        }
        return true;
    }

    private boolean isIceLakeArea(ServerLevel world, BlockPos center, int radius) {
        int bad = 0;
        int checked = 0;
        for (int x = -radius; x <= radius; x += 4) {
            for (int z = -radius; z <= radius; z += 4) {
                BlockPos surface = this.findTerrainSurface(world, center.offset(x, 0, z));
                ++checked;
                if (surface != null && !this.isBadVillageSurface(world.getBlockState(surface.below()))) continue;
                ++bad;
            }
        }
        return checked > 0 && bad > Math.max(4, checked / 2);
    }

    private boolean isBadBuildArea(ServerLevel world, BlockPos center, int radius) {
        radius = Math.max(3, Math.min(radius, 12));
        int bad = 0;
        int checked = 0;
        int minY = 255;
        int maxY = 0;
        for (int x = -radius; x <= radius; x += 4) {
            for (int z = -radius; z <= radius; z += 4) {
                BlockPos surface = this.findTerrainSurface(world, center.offset(x, 0, z));
                ++checked;
                if (surface == null || this.isBadVillageSurface(world.getBlockState(surface.below()))) {
                    ++bad;
                    continue;
                }
                minY = Math.min(minY, surface.getY());
                maxY = Math.max(maxY, surface.getY());
            }
        }
        if (checked <= 0) {
            return true;
        }
        if (bad > Math.max(2, checked / 3)) {
            return true;
        }
        return maxY - minY > 8;
    }

    private boolean isBadVillageSurface(BlockState state) {
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.WATER || block == Blocks.WATER || block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.FROSTED_ICE || material == LegacyMaterial.water;
    }

    private BlockPos findTerrainSurface(ServerLevel world, BlockPos pos) {
        BlockPos p = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, new BlockPos(pos.getX(), 0, pos.getZ())).below();
        while (p.getY() > 1) {
            BlockState state = world.getBlockState(p);
            if (!this.isSurfaceJunk(state)) {
                if (state.isAir()) {
                    p = p.below();
                    continue;
                }
                if (state.isFaceSturdy(world, p, Direction.UP)) {
                    return p.above();
                }
            }
            p = p.below();
        }
        return null;
    }

    private boolean isSurfaceJunk(BlockState state) {
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow;
    }

    private void clearTreeBlocksForTemplate(ServerLevel world, StructureTemplate template, BlockPos origin, Rotation rotation) {
        Vec3i size = template.getSize();
        int sizeX = size.getX();
        int sizeY = Math.min(size.getY() + 3, 18);
        int sizeZ = size.getZ();
        for (int localX = -1; localX <= sizeX; ++localX) {
            for (int localZ = -1; localZ <= sizeZ; ++localZ) {
                for (int localY = 0; localY <= sizeY; ++localY) {
                    BlockPos p = this.transformLocal(origin, localX, localY, localZ, size, rotation);
                    BlockState state = world.getBlockState(p);
                    if (!this.isTreeBlock(state)) continue;
                    world.removeBlock(p, false);
                }
            }
        }
    }

    private boolean isTreeBlock(BlockState state) {
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || material == LegacyMaterial.leaves;
    }

    private void buildRubbleSupports(ServerLevel world, StructureTemplate template, BlockPos origin, Rotation rotation) {
        List<StructureTemplate.StructureBlockInfo> blocks = this.getTemplateBlocks(template);
        if (blocks == null || blocks.isEmpty()) {
            return;
        }
        Vec3i size = template.getSize();
        int foundationY = this.findTemplateFoundationLayer(blocks);
        if (foundationY < 0) {
            return;
        }
        BlockState averageSupportState = this.findAverageSupportState(world, blocks, origin, size, rotation, foundationY);
        for (StructureTemplate.StructureBlockInfo info : blocks) {
            BlockPos sourcePos;
            BlockState sourceState;
            if (info == null || info.pos() == null || info.state() == null || info.pos().getY() != foundationY || !this.isValidFoundationSource(info.state()) || !this.isValidFoundationSource(sourceState = world.getBlockState(sourcePos = this.transformLocal(origin, info.pos().getX(), info.pos().getY(), info.pos().getZ(), size, rotation)))) continue;
            BlockState supportState = this.isGoodSupportCube(sourceState) ? sourceState : averageSupportState;
            this.duplicateFoundationDown(world, sourcePos, supportState);
        }
    }

    private BlockState findAverageSupportState(ServerLevel world, List<StructureTemplate.StructureBlockInfo> blocks, BlockPos origin, Vec3i size, Rotation rotation, int foundationY) {
        HashMap<BlockState, Integer> counts = new HashMap<BlockState, Integer>();
        BlockState bestState = Blocks.DIRT.defaultBlockState();
        int bestCount = 0;
        for (StructureTemplate.StructureBlockInfo info : blocks) {
            BlockPos worldPos;
            BlockState worldState;
            if (info == null || info.pos() == null || info.state() == null || info.pos().getY() != foundationY || !this.isValidFoundationSource(info.state()) || !this.isValidFoundationSource(worldState = world.getBlockState(worldPos = this.transformLocal(origin, info.pos().getX(), info.pos().getY(), info.pos().getZ(), size, rotation))) || !this.isGoodSupportCube(worldState)) continue;
            Integer count = (Integer)counts.get(worldState);
            int newCount = count == null ? 1 : count + 1;
            counts.put(worldState, newCount);
            if (newCount <= bestCount) continue;
            bestCount = newCount;
            bestState = worldState;
        }
        return bestState;
    }

    private boolean isGoodSupportCube(BlockState state) {
        if (state == null) {
            return false;
        }
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        if (block == Blocks.AIR || block == Blocks.STRUCTURE_VOID) {
            return false;
        }
        if (material == LegacyMaterial.air || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow || material == LegacyMaterial.water || material == LegacyMaterial.lava) {
            return false;
        }
        return Block.isShapeFullBlock(state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO));
    }

    private int findTemplateFoundationLayer(List<StructureTemplate.StructureBlockInfo> blocks) {
        int y;
        int[] counts = new int[256];
        int maxCount = 0;
        for (StructureTemplate.StructureBlockInfo info : blocks) {
            int y2;
            if (info == null || info.pos() == null || info.state() == null || !this.isValidFoundationSource(info.state()) || (y2 = info.pos().getY()) < 0 || y2 >= counts.length) continue;
            int n = y2;
            counts[n] = counts[n] + 1;
            maxCount = Math.max(maxCount, counts[y2]);
        }
        if (maxCount <= 0) {
            return -1;
        }
        int minimumUsefulCount = Math.max(6, maxCount / 3);
        for (y = 0; y < counts.length; ++y) {
            if (counts[y] < minimumUsefulCount) continue;
            return y;
        }
        for (y = 0; y < counts.length; ++y) {
            if (counts[y] <= 0) continue;
            return y;
        }
        return -1;
    }

    private static java.lang.reflect.Field palettesField;

    @SuppressWarnings("unchecked")
    private List<StructureTemplate.StructureBlockInfo> getTemplateBlocks(StructureTemplate template) {
        try {
            if (palettesField == null) {
                palettesField = StructureTemplate.class.getDeclaredField("palettes");
                palettesField.setAccessible(true);
            }
            List<StructureTemplate.Palette> palettes = (List<StructureTemplate.Palette>) palettesField.get(template);
            return palettes.isEmpty() ? Collections.emptyList() : palettes.get(0).blocks();
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }

    private void duplicateFoundationDown(ServerLevel world, BlockPos sourcePos, BlockState sourceState) {
        BlockPos pos = sourcePos.below();
        int placed = 0;
        while (pos.getY() > 1 && placed < 32) {
            BlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            LegacyMaterial material = LegacyMaterial.of(state);
            if (block == Blocks.SNOW || material == LegacyMaterial.snow) {
                world.setBlock(pos, sourceState, 2);
                pos = pos.below();
                ++placed;
                continue;
            }
            if (!this.shouldFillSupport(world, pos)) break;
            world.setBlock(pos, sourceState, 2);
            pos = pos.below();
            ++placed;
        }
    }

    private boolean isValidFoundationSource(BlockState state) {
        if (state == null) {
            return false;
        }
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block != Blocks.AIR && block != Blocks.STRUCTURE_VOID && block != Blocks.SNOW && block != Blocks.SHORT_GRASS && block != Blocks.DEAD_BUSH && block != Blocks.OAK_LEAVES && block != Blocks.ACACIA_LEAVES && block != Blocks.OAK_LOG && block != Blocks.ACACIA_LOG && material != LegacyMaterial.air && material != LegacyMaterial.plants && material != LegacyMaterial.vine && material != LegacyMaterial.leaves && material != LegacyMaterial.snow && material != LegacyMaterial.water && material != LegacyMaterial.lava;
    }

    private boolean shouldFillSupport(ServerLevel world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || block == Blocks.WATER || block == Blocks.WATER || block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.FROSTED_ICE || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow || material == LegacyMaterial.water;
    }

    /** The world position of a template position for the rotation the template is placed with (the same transform StructureTemplate#placeInWorld applies). */
    private BlockPos transformLocal(BlockPos origin, int localX, int localY, int localZ, Vec3i size, Rotation rotation) {
        return origin.offset(StructureTemplate.calculateRelativePosition(new StructurePlaceSettings().setMirror(Mirror.NONE).setRotation(rotation), new BlockPos(localX, localY, localZ)));
    }

    private void scatterLogPiles(ServerLevel world, RandomSource random, BlockPos center, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, int buildingCount, VillageStyle style) {
        if (world == null || wallExclusions == null || wallExclusions.isEmpty()) {
            return;
        }
        Bounds bounds = this.computeExclusionBounds(wallExclusions, center);
        bounds.minX -= 10;
        bounds.maxX += 10;
        bounds.minZ -= 10;
        bounds.maxZ += 10;
        int pileCount = 1 + random.nextInt(2 + Math.max(0, style.logPileBias));
        if (buildingCount >= 6 && random.nextBoolean()) {
            ++pileCount;
        }
        for (int i = 0; i < pileCount; ++i) {
            BlockPos surface;
            int z;
            int x;
            for (int tries = 0; tries < 64 && (this.isInsideExclusionWithPadding(x = this.randomBetween(random, bounds.minX + 8, bounds.maxX - 8), z = this.randomBetween(random, bounds.minZ + 8, bounds.maxZ - 8), wallExclusions, 8) || (surface = this.findTerrainSurface(world, BlockPos.containing(x, 0, z))) == null || this.isBadVillageSurface(world.getBlockState(surface.below())) || !this.placeLogPile(world, random, surface)); ++tries) {
            }
        }
    }

    private Bounds computeExclusionBounds(List<SRPColdVillageWallGenerator.WallExclusion> exclusions, BlockPos fallbackCenter) {
        Bounds bounds = new Bounds();
        bounds.minX = Integer.MAX_VALUE;
        bounds.maxX = Integer.MIN_VALUE;
        bounds.minZ = Integer.MAX_VALUE;
        bounds.maxZ = Integer.MIN_VALUE;
        for (SRPColdVillageWallGenerator.WallExclusion exclusion : exclusions) {
            if (exclusion == null) continue;
            bounds.minX = Math.min(bounds.minX, exclusion.minX);
            bounds.maxX = Math.max(bounds.maxX, exclusion.maxX);
            bounds.minZ = Math.min(bounds.minZ, exclusion.minZ);
            bounds.maxZ = Math.max(bounds.maxZ, exclusion.maxZ);
        }
        if (bounds.minX == Integer.MAX_VALUE) {
            bounds.minX = fallbackCenter.getX() - 16;
            bounds.maxX = fallbackCenter.getX() + 16;
            bounds.minZ = fallbackCenter.getZ() - 16;
            bounds.maxZ = fallbackCenter.getZ() + 16;
        }
        return bounds;
    }

    private boolean isInsideExclusionWithPadding(int x, int z, List<SRPColdVillageWallGenerator.WallExclusion> exclusions, int extraPadding) {
        for (SRPColdVillageWallGenerator.WallExclusion exclusion : exclusions) {
            if (exclusion == null || x < exclusion.minX - extraPadding || x > exclusion.maxX + extraPadding || z < exclusion.minZ - extraPadding || z > exclusion.maxZ + extraPadding) continue;
            return true;
        }
        return false;
    }

    private int randomBetween(RandomSource random, int min, int max) {
        if (max <= min) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    private boolean placeLogPile(ServerLevel world, RandomSource random, BlockPos surface) {
        BlockPos base;
        BlockPos target;
        int i;
        boolean axisX = random.nextBoolean();
        int length = 2 + random.nextInt(3);
        int dir = random.nextBoolean() ? 1 : -1;
        BlockState logState = SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.DEADHEAD)).setValue((Property)RotatedPillarBlock.AXIS, (axisX ? Direction.Axis.X : Direction.Axis.Z));
        for (i = 0; i < length; ++i) {
            target = axisX ? surface.offset(i * dir, 0, 0) : surface.offset(0, 0, i * dir);
            base = this.findTerrainSurface(world, target);
            if (base != null && this.canPlaceLogPileAt(world, base)) continue;
            return false;
        }
        for (i = 0; i < length; ++i) {
            target = axisX ? surface.offset(i * dir, 0, 0) : surface.offset(0, 0, i * dir);
            base = this.findTerrainSurface(world, target);
            if (base == null) continue;
            world.setBlock(base, logState, 2);
        }
        if (length >= 3 && random.nextBoolean()) {
            BlockPos topTarget;
            BlockPos blockPos = topTarget = axisX ? surface.offset(dir, 1, 0) : surface.offset(0, 1, dir);
            if (world.isEmptyBlock(topTarget)) {
                world.setBlock(topTarget, logState, 2);
            }
        }
        return true;
    }

    private boolean canPlaceLogPileAt(ServerLevel world, BlockPos pos) {
        if (pos == null || pos.getY() <= 1) {
            return false;
        }
        BlockState below = world.getBlockState(pos.below());
        if (this.isBadVillageSurface(below)) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.snow || state.canBeReplaced();
    }

    private void spawnVillagers(ServerLevel world, RandomSource random, BlockPos center, int count) {
        if (world.isClientSide) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            BlockPos pos = this.findVillagerSpawnPos(world, random, center);
            if (pos == null) continue;
            Villager villager = new Villager(EntityType.VILLAGER, world);
            villager.setPos((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
            villager.finalizeSpawn((ServerLevel) villager.level(), world.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
            world.addFreshEntity((Entity)villager);
        }
    }

    private BlockPos findVillagerSpawnPos(ServerLevel world, RandomSource random, BlockPos center) {
        for (int tries = 0; tries < 32; ++tries) {
            int z;
            int x = center.getX() + random.nextInt(45) - 22;
            BlockPos pos = this.findTerrainSurface(world, BlockPos.containing(x, 0, z = center.getZ() + random.nextInt(45) - 22));
            if (pos == null || !world.isEmptyBlock(pos) || !world.isEmptyBlock(pos.above()) || !world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP)) continue;
            return pos;
        }
        return null;
    }

    private void fillBlacksmithChests(ServerLevel world, BlockPos origin, StructureTemplate template, Rotation rotation) {
        Vec3i size = template.getSize();
        int maxX = Math.max(size.getX(), size.getZ()) + 4;
        int maxY = size.getY() + 4;
        int maxZ = Math.max(size.getX(), size.getZ()) + 4;
        // the template is placed with its rotation, so the box around the origin is searched in all four directions
        for (int x = -maxX; x <= maxX; ++x) {
            for (int y = -2; y <= maxY; ++y) {
                for (int z = -maxZ; z <= maxZ; ++z) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockEntity te = world.getBlockEntity(pos);
                    if (!(te instanceof ChestBlockEntity)) continue;
                    this.fillChest(world, (ChestBlockEntity)te, world.random);
                }
            }
        }
    }

    private void fillChest(ServerLevel world, ChestBlockEntity chest, RandomSource random) {
        for (int i = 0; i < chest.getContainerSize(); ++i) {
            chest.setItem(i, ItemStack.EMPTY);
        }
        LootTable table = world.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.VILLAGE_WEAPONSMITH);
        List<ItemStack> loot = new ArrayList<>(table.getRandomItems(new LootParams.Builder(world).create(LootContextParamSets.EMPTY), random.nextLong()));
        Collections.shuffle(loot, new java.util.Random(random.nextLong()));
        for (ItemStack stack : loot) {
            this.addRandomStack(chest, stack, random);
        }
        int berries = 2 + random.nextInt(4);
        this.addRandomStack(chest, new ItemStack(SRPItems.itemThornshadeBerry.get(), berries), random);
    }

    private void addRandomStack(Container inventory, ItemStack stack, RandomSource random) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        for (int tries = 0; tries < 80; ++tries) {
            int slot = random.nextInt(inventory.getContainerSize());
            if (!inventory.getItem(slot).isEmpty()) continue;
            inventory.setItem(slot, stack.copy());
            return;
        }
    }

    private static final class Bounds {
        int minX;
        int maxX;
        int minZ;
        int maxZ;

        private Bounds() {
        }
    }

    private static final class VillageStyle {
        final int spreadPercent;
        final int optionalBias;
        final int extraVillagers;
        final int logPileBias;

        VillageStyle(int spreadPercent, int optionalBias, int extraVillagers, int logPileBias) {
            this.spreadPercent = spreadPercent;
            this.optionalBias = optionalBias;
            this.extraVillagers = extraVillagers;
            this.logPileBias = logPileBias;
        }
    }
}

