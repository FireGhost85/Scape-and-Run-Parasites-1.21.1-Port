package com.dhanantry.scapeandrunparasites.world.star;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPColdVillageWallGenerator;
import java.lang.reflect.Field;
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
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;

public class SRPColdVillageGenerator
implements IWorldGenerator {
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

    public void generate(RandomSource random, int chunkX, int chunkZ, Level world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world == null || world.dimensionType() == null || !DimKeys.of(world).equals(DimKeys.normalize("0"))) {
            return;
        }
        if (SRPWorldEntitySpawner.starType != 1) {
            return;
        }
        if (!this.isVillageChunk(world, chunkX, chunkZ)) {
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

    private boolean isVillageChunk(Level world, int chunkX, int chunkZ) {
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
        RandomSource rand = world.setRandomSeed(gridX, gridZ, 10387312);
        gridX *= 20;
        gridZ *= 20;
        return chunkX == (gridX += rand.nextInt(15)) && chunkZ == (gridZ += rand.nextInt(15));
    }

    private boolean isValidColdVillageBiome(Level world, BlockPos center) {
        int radius = 20;
        int checked = 0;
        int valid = 0;
        for (int x = -radius; x <= radius; x += 8) {
            for (int z = -radius; z <= radius; z += 8) {
                ++checked;
                if (!this.isColdVillageBiome(world.getBiome(center.offset(x, 0, z)).value())) continue;
                ++valid;
            }
        }
        return checked > 0 && valid >= Math.max(1, checked * 2 / 3);
    }

    private boolean isColdVillageBiome(Biome biome) {
        return biome == Biomes.icePlains || biome == Biomes.coldTaiga || biome == Biomes.coldTaigaHills || biome == Biomes.coldBeach || biome == Biomes.frozenRiver;
    }

    private void generateVillage(Level world, RandomSource random, BlockPos center) {
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

    private int placePatternBuildings(Level world, RandomSource random, BlockPos center, Rotation rotation, VillageStyle style, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions) {
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

    private boolean placeAnchoredScaled(Level world, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, ResourceLocation structure, BlockPos center, int offsetX, int offsetZ, Rotation rotation, boolean blacksmith) {
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

    private void addWallExclusion(Level world, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, ResourceLocation structure, BlockPos origin, Rotation rotation, int padding) {
        if (world.getMinecraftServer() == null || wallExclusions == null) {
            return;
        }
        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        Template template = manager.getTemplate(world.getMinecraftServer(), structure);
        if (template == null) {
            return;
        }
        BlockPos size = template.getSize();
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

    private boolean placeAnchored(Level world, ResourceLocation structure, BlockPos entrancePos, Rotation rotation, boolean blacksmith) {
        if (world.getMinecraftServer() == null) {
            return false;
        }
        TemplateManager manager = world.getSaveHandler().getStructureTemplateManager();
        Template template = manager.getTemplate(world.getMinecraftServer(), structure);
        if (template == null) {
            return false;
        }
        if (this.isBadBuildArea(world, entrancePos, 6)) {
            return false;
        }
        this.clearTreeBlocksForTemplate(world, template, entrancePos, rotation);
        PlacementSettings settings = new PlacementSettings().setMirror(Mirror.NONE).setRotation(rotation).setIgnoreEntities(false).setChunk(null).setReplacedBlock(null).setIgnoreStructureBlock(false);
        template.addBlocksToWorld(world, entrancePos, settings);
        this.buildRubbleSupports(world, template, entrancePos, rotation);
        if (blacksmith) {
            this.fillBlacksmithChests(world, entrancePos, template, rotation);
        }
        return true;
    }

    private boolean isIceLakeArea(Level world, BlockPos center, int radius) {
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

    private boolean isBadBuildArea(Level world, BlockPos center, int radius) {
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

    private BlockPos findTerrainSurface(Level world, BlockPos pos) {
        BlockPos p = world.getHeight(BlockPos.containing(pos.getX(), 0, pos.getZ())).below();
        while (p.getY() > 1) {
            BlockState state = world.getBlockState(p);
            if (!this.isSurfaceJunk(state)) {
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

    private boolean isSurfaceJunk(BlockState state) {
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow;
    }

    private void clearTreeBlocksForTemplate(Level world, Template template, BlockPos origin, Rotation rotation) {
        BlockPos size = template.getSize();
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

    private void buildRubbleSupports(Level world, Template template, BlockPos origin, Rotation rotation) {
        List<Template.BlockInfo> blocks = this.getTemplateBlocks(template);
        if (blocks == null || blocks.isEmpty()) {
            return;
        }
        BlockPos size = template.getSize();
        int foundationY = this.findTemplateFoundationLayer(blocks);
        if (foundationY < 0) {
            return;
        }
        BlockState averageSupportState = this.findAverageSupportState(world, blocks, origin, size, rotation, foundationY);
        for (Template.BlockInfo info : blocks) {
            BlockPos sourcePos;
            BlockState sourceState;
            if (info == null || info.pos == null || info.blockState == null || info.pos.getY() != foundationY || !this.isValidFoundationSource(info.blockState) || !this.isValidFoundationSource(sourceState = world.getBlockState(sourcePos = this.transformLocal(origin, info.pos.getX(), info.pos.getY(), info.pos.getZ(), size, rotation)))) continue;
            BlockState supportState = this.isGoodSupportCube(sourceState) ? sourceState : averageSupportState;
            this.duplicateFoundationDown(world, sourcePos, supportState);
        }
    }

    private BlockState findAverageSupportState(Level world, List<Template.BlockInfo> blocks, BlockPos origin, BlockPos size, Rotation rotation, int foundationY) {
        HashMap<BlockState, Integer> counts = new HashMap<BlockState, Integer>();
        BlockState bestState = Blocks.DIRT.defaultBlockState();
        int bestCount = 0;
        for (Template.BlockInfo info : blocks) {
            BlockPos worldPos;
            BlockState worldState;
            if (info == null || info.pos == null || info.blockState == null || info.pos.getY() != foundationY || !this.isValidFoundationSource(info.blockState) || !this.isValidFoundationSource(worldState = world.getBlockState(worldPos = this.transformLocal(origin, info.pos.getX(), info.pos.getY(), info.pos.getZ(), size, rotation))) || !this.isGoodSupportCube(worldState)) continue;
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
        return state.isFullCube();
    }

    private int findTemplateFoundationLayer(List<Template.BlockInfo> blocks) {
        int y;
        int[] counts = new int[256];
        int maxCount = 0;
        for (Template.BlockInfo info : blocks) {
            int y2;
            if (info == null || info.pos == null || info.blockState == null || !this.isValidFoundationSource(info.blockState) || (y2 = info.pos.getY()) < 0 || y2 >= counts.length) continue;
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

    private List<Template.BlockInfo> getTemplateBlocks(Template template) {
        try {
            Field field = ReflectionHelper.findField(Template.class, (String)"blocks", (String)"blocks");
            field.setAccessible(true);
            return (List)field.get(template);
        }
        catch (Throwable throwable) {
            return Collections.emptyList();
        }
    }

    private void duplicateFoundationDown(Level world, BlockPos sourcePos, BlockState sourceState) {
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

    private boolean shouldFillSupport(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        LegacyMaterial material = LegacyMaterial.of(state);
        return block == Blocks.AIR || block == Blocks.SNOW || block == Blocks.SHORT_GRASS || block == Blocks.DEAD_BUSH || block == Blocks.OAK_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.OAK_LOG || block == Blocks.ACACIA_LOG || block == Blocks.WATER || block == Blocks.WATER || block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.FROSTED_ICE || material == LegacyMaterial.plants || material == LegacyMaterial.vine || material == LegacyMaterial.leaves || material == LegacyMaterial.snow || material == LegacyMaterial.water;
    }

    private BlockPos transformLocal(BlockPos origin, int localX, int localY, int localZ, BlockPos size, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_90: {
                return origin.offset(size.getZ() - 1 - localZ, localY, localX);
            }
            case CLOCKWISE_180: {
                return origin.offset(size.getX() - 1 - localX, localY, size.getZ() - 1 - localZ);
            }
            case COUNTERCLOCKWISE_90: {
                return origin.offset(localZ, localY, size.getX() - 1 - localX);
            }
        }
        return origin.offset(localX, localY, localZ);
    }

    private void scatterLogPiles(Level world, RandomSource random, BlockPos center, List<SRPColdVillageWallGenerator.WallExclusion> wallExclusions, int buildingCount, VillageStyle style) {
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

    private boolean placeLogPile(Level world, RandomSource random, BlockPos surface) {
        BlockPos base;
        BlockPos target;
        int i;
        boolean axisX = random.nextBoolean();
        int length = 2 + random.nextInt(3);
        int dir = random.nextBoolean() ? 1 : -1;
        BlockState logState = SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.DEADHEAD)).setValue((Property)BlockRotatedPillar.AXIS, (axisX ? Direction.Axis.X : Direction.Axis.Z));
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

    private boolean canPlaceLogPileAt(Level world, BlockPos pos) {
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

    private void spawnVillagers(Level world, RandomSource random, BlockPos center, int count) {
        if (world.isClientSide) {
            return;
        }
        for (int i = 0; i < count; ++i) {
            BlockPos pos = this.findVillagerSpawnPos(world, random, center);
            if (pos == null) continue;
            Villager villager = new Villager(world);
            villager.setPos((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
            villager.finalizeSpawn((ServerLevel) villager.level(), world.getCurrentDifficultyAt(pos), MobSpawnType.MOB_SUMMONED, null);
            world.addFreshEntity((Entity)villager);
        }
    }

    private BlockPos findVillagerSpawnPos(Level world, RandomSource random, BlockPos center) {
        for (int tries = 0; tries < 32; ++tries) {
            int z;
            int x = center.getX() + random.nextInt(45) - 22;
            BlockPos pos = this.findTerrainSurface(world, BlockPos.containing(x, 0, z = center.getZ() + random.nextInt(45) - 22));
            if (pos == null || !world.isEmptyBlock(pos) || !world.isEmptyBlock(pos.above()) || !world.getBlockState(pos.below()).isSideSolid((BlockGetter)world, pos.below(), Direction.UP)) continue;
            return pos;
        }
        return null;
    }

    private void fillBlacksmithChests(Level world, BlockPos origin, Template template, Rotation rotation) {
        if (world.isClientSide || !(world instanceof ServerLevel)) {
            return;
        }
        BlockPos size = template.getSize();
        int maxX = Math.max(size.getX(), size.getZ()) + 4;
        int maxY = size.getY() + 4;
        int maxZ = Math.max(size.getX(), size.getZ()) + 4;
        for (int x = -2; x <= maxX; ++x) {
            for (int y = -2; y <= maxY; ++y) {
                for (int z = -2; z <= maxZ; ++z) {
                    BlockPos pos = origin.offset(x, y, z);
                    BlockEntity te = world.getBlockEntity(pos);
                    if (!(te instanceof TileEntityChest)) continue;
                    this.fillChest((ServerLevel)world, (TileEntityChest)te, world.random);
                }
            }
        }
    }

    private void fillChest(ServerLevel world, TileEntityChest chest, RandomSource random) {
        for (int i = 0; i < chest.getSizeInventory(); ++i) {
            chest.setInventorySlotContents(i, ItemStack.EMPTY);
        }
        LootTable table = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
        LootContext context = new LootContext.Builder(world).build();
        List<? extends ItemStack> loot = table.generateLootForPools(random, context);
        Collections.shuffle(loot, random);
        for (ItemStack stack : loot) {
            this.addRandomStack((AbstractContainerMenu)chest, stack, random);
        }
        int berries = 2 + random.nextInt(4);
        this.addRandomStack((AbstractContainerMenu)chest, new ItemStack(SRPItems.itemThornshadeBerry.get(), berries), random);
    }

    private void addRandomStack(AbstractContainerMenu inventory, ItemStack stack, RandomSource random) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        for (int tries = 0; tries < 80; ++tries) {
            int slot = random.nextInt(inventory.getSizeInventory());
            if (!inventory.getStackInSlot(slot).isEmpty()) continue;
            inventory.setInventorySlotContents(slot, stack.copy());
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

