package com.dhanantry.scapeandrunparasites.util.convert;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.block.IStagedBlock;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.ticks.TickPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BeckonBlockInfestation {
    private static final Logger INFEST_LOG = LogManager.getLogger((String)"SRP-Infest");
    private static final boolean DEBUG = false;
    public static int blockInfestedCount;
    private static final HashMap<LegacyMaterial, BlockState> conversionCache;
    private static final HashMap<BlockMetaKey, BlockState> customConvertCache;
    private static int customConvertHash;

    public static void beckonInfestation(Level worldIn, BlockPos pos, RandomSource rand, int stage, boolean fromVenkrol) {
        int rangeY;
        int range;
        if (blockInfestedCount > SRPConfig.BlockInfestedLimit) {
            return;
        }
        if (worldIn.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        int pivot = 0;
        switch (stage) {
            case 1: {
                range = SRPConfigMobs.venkrolRange - 1;
                rangeY = SRPConfigMobs.venkrolRangeY;
                break;
            }
            case 2: {
                range = SRPConfigMobs.venkrolsiiRange - 1;
                rangeY = SRPConfigMobs.venkrolsiiRangeY;
                break;
            }
            default: {
                range = SRPConfigMobs.venkrolsiiiRange - 1;
                rangeY = SRPConfigMobs.venkrolsiiiRangeY;
            }
        }
        AABB axisalignedbb = new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).inflate((double)range, (double)rangeY, (double)range);
        List<? extends EntityParasiteBase> moblist = worldIn.getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        if (fromVenkrol) {
            if (moblist.isEmpty() && stage < 4) {
                BeckonBlockInfestation.spawnBeckonFromInfestation(worldIn, pos, stage);
                return;
            }
            if (!moblist.isEmpty() && stage < 4) {
                int flagV = 0;
                for (EntityParasiteBase mob : moblist) {
                    if (flagV == 2) break;
                    if (mob.hasEffect(SRPPotions.PIVOT_E)) {
                        pivot = Math.max(mob.getEffect(SRPPotions.PIVOT_E).getAmplifier() + 1, pivot);
                    }
                    flagV = 0;
                    BlockPos venPos = mob.blockPosition();
                    if (SRPEntityUtil.lightBrightness(worldIn, venPos.above()) < (float)SRPConfigSystems.rsBlockLight / 15.0f) {
                        ++flagV;
                    }
                    if (worldIn.getBrightness(LightLayer.BLOCK, venPos.above()) >= SRPConfigSystems.rsBlockLight) continue;
                    ++flagV;
                }
                if (flagV < 2) {
                    return;
                }
            }
        }
        int covertedBlocks = BeckonBlockInfestation.blockConversions(worldIn, pos, rand, stage);
        if (stage >= 4) {
            if (worldIn.getBlockState(pos).getBlock() == SRPBlocks.InfestedStain.get()) {
                worldIn.setBlockAndUpdate(pos, SRPBlocks.optionalDirt);
            } else if (worldIn.getBlockState(pos).getBlock() == SRPBlocks.InfestedRubble.get()) {
                worldIn.setBlockAndUpdate(pos, SRPBlocks.optionalRub);
            }
            if (worldIn.getBlockState(pos.above()).getBlock() == Blocks.AIR || worldIn.getBlockState(pos.above()).getBlock() == SRPBlocks.ParasiteBush.get()) {
                worldIn.setBlockAndUpdate(pos.above(), SRPBlocks.InfestRemain.get().defaultBlockState());
            }
        } else if (covertedBlocks != 0) {
            blockInfestedCount += covertedBlocks;
            if (SRPConfigSystems.useEvolution) {
                if (pivot != 0) {
                    SRPSaveData.get(worldIn).setTotalKills(DimKeys.of(worldIn), SRPConfigSystems.valueBlock * covertedBlocks * (SRPConfigSystems.pivotPointMultiplier * pivot), true, worldIn, true, 53);
                } else {
                    SRPSaveData.get(worldIn).setTotalKills(DimKeys.of(worldIn), SRPConfigSystems.valueBlock * covertedBlocks, true, worldIn, true, 54);
                }
            }
        }
    }

    private static void generateInfestationFeatures(Level worldIn, BlockPos pos, RandomSource rand) {
        BeckonBlockInfestation.spawnGenFeatureInfested(worldIn, pos.above(), rand);
        BeckonBlockInfestation.spawnGenRoofInfested(worldIn, pos.below(), rand);
    }

    public static void spawnGenFeatureInfested(Level worldIn, BlockPos pos, RandomSource rand) {
        WorldGenParasiteBush gen;
        double bonus = 0.0;
        if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR && worldIn.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
            pos = ParasiteEventEntity.getFloor(worldIn, pos, 10);
            bonus = 3.0E-5;
            if (pos == null) {
                return;
            }
        }
        if (rand.nextInt(20) == 0) {
            gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.SPINE, 4);
            gen.generate(worldIn, rand, pos);
        }
        if (rand.nextInt(20) == 0) {
            if (rand.nextInt(2) == 0) {
                gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.ARC, 2);
                gen.generate(worldIn, rand, pos);
            } else {
                gen = new WorldGenParasiteBush(false, BlockInfestedBush.EnumType.GRASS1, 1);
                gen.generate(worldIn, rand, pos);
            }
        }
    }

    public static void spawnGenRoofInfested(Level worldIn, BlockPos pos, RandomSource rand) {
        if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
            return;
        }
        if (rand.nextInt(5) == 0) {
            worldIn.setBlockAndUpdate(pos, SRPBlocks.InfestedBush.get().defaultBlockState().setValue(BlockInfestedBush.VARIANT, (BlockInfestedBush.EnumType.VINE)));
            pos = pos.below();
            if (rand.nextInt(4) == 0) {
                if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                    return;
                }
                worldIn.setBlockAndUpdate(pos, SRPBlocks.InfestedBush.get().defaultBlockState().setValue(BlockInfestedBush.VARIANT, (BlockInfestedBush.EnumType.VINE)));
                pos = pos.below();
                if (rand.nextInt(3) == 0) {
                    if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                        return;
                    }
                    worldIn.setBlockAndUpdate(pos, SRPBlocks.InfestedBush.get().defaultBlockState().setValue(BlockInfestedBush.VARIANT, (BlockInfestedBush.EnumType.VINE)));
                }
            }
        }
    }

    private static void rebuildCustomConvertCacheIfNeeded() {
        String[] list = SRPConfigWorld.infestationConvertBlocks;
        int h = 1;
        if (list != null) {
            for (String s : list) {
                if (s == null) continue;
                h = 31 * h + s.hashCode();
            }
        }
        if (h == customConvertHash) {
            return;
        }
        customConvertHash = h;
        customConvertCache.clear();
        if (list == null) {
            return;
        }
        for (String entry : list) {
            String[] parts;
            if (entry == null || (entry = entry.trim()).isEmpty() || (parts = entry.split(";", 2)).length != 2) continue;
            String left = parts[0].trim();
            String right = parts[1].trim();
            ParsedState src = BeckonBlockInfestation.parseStateToken(left);
            ParsedState dst = BeckonBlockInfestation.parseStateToken(right);
            if (src == null || dst == null || dst.block == Blocks.AIR && !"minecraft:air".equals(right)) continue;
            customConvertCache.put(new BlockMetaKey(src.block, src.meta), dst.state);
        }
    }

    private static ParsedState parseStateToken(String token) {
        ResourceLocation rl;
        if (token == null || token.isEmpty()) {
            return null;
        }
        String id = token.trim();
        int meta = -1;
        String[] split = id.split(":");
        if (split.length == 3) {
            id = split[0] + ":" + split[1];
            try {
                meta = Integer.parseInt(split[2]);
            }
            catch (Exception e) {
                meta = -1;
            }
        }
        try {
            rl = ResourceLocation.parse(id);
        }
        catch (Exception e) {
            return null;
        }
        if (!BuiltInRegistries.BLOCK.containsKey(rl)) {
            // a 1.12 name of the default list (log, leaves, reeds, stained_glass:3 ...): the block the legacy tables of BlockIds give
            BlockState legacy = BlockIds.tryParse(token.trim());
            return legacy == null ? null : new ParsedState(legacy.getBlock(), -1, legacy);
        }
        Block b = (Block)BuiltInRegistries.BLOCK.get(rl);
        if (b == null) {
            return null;
        }
        if (b == Blocks.AIR && !"minecraft:air".equals(id)) {
            return null;
        }
        if (meta < 0 && b instanceof LiquidBlock) {
            meta = 0;
        }
        BlockState st = meta >= 0 ? BlockIds.legacyState(b, meta) : b.defaultBlockState();
        return new ParsedState(b, meta, st);
    }

    private static BlockState getCustomConvertedState(Block lookingBlock, BlockState lookingState, int stage) {
        if (stage >= 4) {
            return null;
        }
        BeckonBlockInfestation.rebuildCustomConvertCacheIfNeeded();
        int meta = BlockIds.legacyMeta(lookingState);
        BlockState dst = customConvertCache.get(new BlockMetaKey(lookingBlock, meta));
        if (dst == null) {
            dst = customConvertCache.get(new BlockMetaKey(lookingBlock, -1));
        }
        if (dst == null) {
            return null;
        }
        return BeckonBlockInfestation.createStagedState(dst, stage);
    }

    private static BlockState createStagedState(BlockState baseState, int stage) {
        if (stage < 0) {
            stage = 0;
        }
        if (stage > 5) {
            stage = 5;
        }
        if (baseState.getBlock() instanceof IStagedBlock) {
            return ((IStagedBlock)baseState.getBlock()).withStage(baseState, stage);
        }
        return baseState;
    }

    private static int blockConversions(Level worldIn, BlockPos pos, RandomSource rand, int stage) {
        int convertedBlocks = 0;
        if (stage < 0) {
            stage = 0;
        }
        if (stage > 5) {
            stage = 5;
        }
        for (int dir = 0; dir <= 5; ++dir) {
            BlockState custom;
            BlockPos helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            BlockState lookingState = worldIn.getBlockState(helper);
            Block lookingBlock = lookingState.getBlock();
            LegacyMaterial mat = LegacyMaterial.of(lookingState);
            if (lookingBlock instanceof IMetaName || BeckonBlockInfestation.isSrpInfestedBlock(lookingBlock)) continue;
            if (stage < 4 && !BeckonBlockInfestation.isSrpBlock(lookingBlock) && (custom = BeckonBlockInfestation.getCustomConvertedState(lookingBlock, lookingState, stage)) != null) {
                worldIn.setBlock(helper, custom, 3);
                ++convertedBlocks;
                BeckonBlockInfestation.generateInfestationFeatures(worldIn, helper, rand);
                continue;
            }
            if (stage <= 3) {
                BlockState copy;
                BlockState dst;
                if (BeckonBlockInfestation.isCobbleBlock(lookingBlock, lookingState)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedCobblestone.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isPathBlock(lookingBlock)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedSand.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isFenceBlock(lookingBlock)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedFence.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isAnyPlanks(lookingBlock, lookingState)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedPlanks.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isGlassBlock(lookingBlock, lookingState)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedGlass.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isGlassPaneBlock(lookingBlock)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.INFESTED_GLASS_PANE.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isFurnaceBlock(lookingBlock)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.INFESTED_FURNACE.get().defaultBlockState(), stage), 3);
                    continue;
                }
                if (BeckonBlockInfestation.isStoneStairs(lookingBlock, lookingState)) {
                    dst = BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedStoneStairs.get().defaultBlockState(), stage);
                    copy = BeckonBlockInfestation.copyCommonProps(lookingState, dst);
                    ++convertedBlocks;
                    worldIn.setBlock(helper, copy, 3);
                    continue;
                }
                if (BeckonBlockInfestation.isWoodStairs(lookingBlock, lookingState)) {
                    dst = BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedPlanksStairs.get().defaultBlockState(), stage);
                    copy = BeckonBlockInfestation.copyCommonProps(lookingState, dst);
                    ++convertedBlocks;
                    worldIn.setBlock(helper, copy, 3);
                    continue;
                }
                if (worldIn.getBlockState(helper).is(BlockTags.LOGS)) {
                    ++convertedBlocks;
                    worldIn.setBlock(helper, BeckonBlockInfestation.createStagedState(SRPBlocks.InfestedTrunk.get().defaultBlockState(), stage), 3);
                    continue;
                }
            }
            if (ParasiteEventWorld.blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigSystems.blockBList, SRPConfigSystems.blockBListWhite, SRPConfigSystems.rsBlockIMaxH) && stage <= 3) continue;
            if (lookingBlock instanceof BlockBase) {
                if (lookingBlock == SRPBlocks.BiomeHeart.get() || lookingBlock == SRPBlocks.ColonyHeart.get() || BeckonBlockInfestation.isSrpInfestedBlock(lookingBlock)) continue;
                int lookM = BlockIds.legacyMeta(lookingState);
                if (!conversionCache.containsKey(mat)) continue;
                BlockState newState = BeckonBlockInfestation.createStagedState(conversionCache.get(mat), stage);
                if (stage >= 4 && (lookM <= 1 || stage == 5)) {
                    worldIn.setBlock(helper, newState, 3);
                    worldIn.scheduleTick(helper, newState.getBlock(), 40, TickPriority.byValue(5));
                    continue;
                }
                if (lookM >= stage || stage >= 4) continue;
                worldIn.setBlock(helper, newState, 3);
                ++convertedBlocks;
                BeckonBlockInfestation.generateInfestationFeatures(worldIn, helper, rand);
                continue;
            }
            if (stage >= 4 || !conversionCache.containsKey(mat) || BeckonBlockInfestation.isSrpBlock(lookingBlock)) continue;
            BlockState infestedState = BeckonBlockInfestation.createStagedState(conversionCache.get(mat), stage);
            worldIn.setBlock(helper, infestedState, 3);
            ++convertedBlocks;
            BeckonBlockInfestation.generateInfestationFeatures(worldIn, helper, rand);
        }
        return convertedBlocks;
    }

    private static void spawnBeckonFromInfestation(Level worldIn, BlockPos pos, int stage) {
        if (worldIn.random.nextDouble() < SRPConfigSystems.rsVenkrolEmpty && stage != 1 && (worldIn.getBlockState(pos.above()).getBlock() == Blocks.AIR || worldIn.getBlockState(pos.above()).getBlock() instanceof BushBlock)) {
            if (SRPEntityUtil.lightBrightness(worldIn, pos.above()) > 0.46666667f || worldIn.getBrightness(LightLayer.BLOCK, pos.above()) > 7) {
                return;
            }
            List<? extends Entity> serverList = SRPEntityUtil.allEntities(worldIn);
            int count = 0;
            for (Entity entity : serverList) {
                if (!(entity instanceof EntityPBeckon) || ++count <= SRPConfig.nexusVenkrolCap && !(BeckonBlockInfestation.getDistanceSq(pos, entity) < (double)(SRPConfig.nexusVenkrolDis * SRPConfig.nexusVenkrolDis))) continue;
                return;
            }
            EntityVenkrol out = new EntityVenkrol(SRPEntities.BECKON_SI.get(), worldIn);
            out.moveTo((double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, 0.0f, 0.0f);
            worldIn.addFreshEntity((Entity)out);
            out.cannotDespawn(true);
        }
    }

    private static double getDistanceSq(BlockPos pos, Entity entityIn) {
        double d0 = (double)pos.getX() - entityIn.getX();
        double d1 = (double)pos.getY() - entityIn.getY();
        double d2 = (double)pos.getZ() - entityIn.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    private static boolean isSrpInfestedBlock(Block b) {
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        if (!"srparasites".equals(rl.getNamespace())) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("infest");
    }

    private static boolean isSrpBlock(Block b) {
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        return rl != null && "srparasites".equals(rl.getNamespace());
    }

    private static boolean isPathBlock(Block b) {
        if (b == Blocks.DIRT_PATH || b instanceof DirtPathBlock) {
            return true;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("path");
    }

    private static boolean isFenceBlock(Block b) {
        if (b instanceof FenceBlock) {
            return true;
        }
        if (b instanceof FenceGateBlock) {
            return false;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("fence") && !path.contains("gate");
    }

    private static boolean isAnyPlanks(Block b, BlockState s) {
        if (b.defaultBlockState().is(BlockTags.PLANKS)) {
            return true;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("planks") || path.endsWith("_plank") || path.contains("wood_plank");
    }

    private static boolean isGlassBlock(Block b, BlockState s) {
        if (b instanceof TransparentBlock) {
            return true;
        }
        if (LegacyMaterial.of(s) == LegacyMaterial.glass && !(b instanceof IronBarsBlock)) {
            return true;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("glass") && !path.contains("pane");
    }

    private static boolean isGlassPaneBlock(Block b) {
        if (b instanceof IronBarsBlock) {
            return true;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("pane");
    }

    private static boolean isFurnaceBlock(Block b) {
        return b == Blocks.FURNACE || b instanceof FurnaceBlock;
    }

    private static boolean isStoneStairs(Block b, BlockState s) {
        if (b instanceof StairBlock && LegacyMaterial.of(s) == LegacyMaterial.rock) {
            return true;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("stairs") && (path.contains("stone") || path.contains("brick") || path.contains("sandstone"));
    }

    private static boolean isWoodStairs(Block b, BlockState s) {
        if (b instanceof StairBlock && LegacyMaterial.of(s) == LegacyMaterial.wood) {
            return true;
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("stairs") && (path.contains("oak") || path.contains("spruce") || path.contains("birch") || path.contains("jungle") || path.contains("acacia") || path.contains("dark_oak") || path.contains("wood"));
    }

    private static BlockState copyCommonProps(BlockState from, BlockState to) {
        BlockState out = to;
        for (Property propTo : to.getProperties()) {
            if (!from.getProperties().contains(propTo)) continue;
            try {
                Property raw = propTo;
                Comparable val = from.getValue(raw);
                if (val == null || !raw.getPossibleValues().contains(val)) continue;
                out = out.setValue(raw, val);
            }
            catch (Exception exception) {}
        }
        return out;
    }

    private static boolean isCobbleBlock(Block b, BlockState s) {
        if (b == Blocks.COBBLESTONE) {
            return true;
        }
        try {
            if (b == Blocks.MOSSY_COBBLESTONE) {
                return true;
            }
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        ResourceLocation rl = b.builtInRegistryHolder().key().location();
        if (rl == null) {
            return false;
        }
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        return path.contains("cobblestone") && !path.contains("wall") && !path.contains("stairs");
    }

    static {
        conversionCache = new HashMap();
        conversionCache.put(LegacyMaterial.ground, SRPBlocks.InfestedStain.get().defaultBlockState());
        conversionCache.put(LegacyMaterial.grass, SRPBlocks.InfestedStain.get().defaultBlockState());
        conversionCache.put(LegacyMaterial.sand, SRPBlocks.InfestedSand.get().defaultBlockState());
        conversionCache.put(LegacyMaterial.rock, SRPBlocks.InfestedRubble.get().defaultBlockState());
        customConvertCache = new HashMap();
        customConvertHash = 0;
    }

    private static class ParsedState {
        private final Block block;
        private final int meta;
        private final BlockState state;

        private ParsedState(Block block, int meta, BlockState state) {
            this.block = block;
            this.meta = meta;
            this.state = state;
        }
    }

    private static class BlockMetaKey {
        private final Block block;
        private final int meta;

        private BlockMetaKey(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BlockMetaKey)) {
                return false;
            }
            BlockMetaKey other = (BlockMetaKey)obj;
            return this.block == other.block && this.meta == other.meta;
        }

        public int hashCode() {
            return System.identityHashCode(this.block) * 31 + this.meta;
        }
    }
}

