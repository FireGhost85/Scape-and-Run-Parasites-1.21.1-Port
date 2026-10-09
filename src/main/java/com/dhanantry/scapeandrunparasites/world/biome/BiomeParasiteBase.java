package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class BiomeParasiteBase {
    protected WorldGenAbstractTree tree;

    private static final java.util.Map<net.minecraft.resources.ResourceKey<Biome>, BiomeParasiteBase> INSTANCES = new java.util.HashMap<>();

    /** The block palette / feature helper of a parasite biome (the biome itself is a data driven 1.21 biome). */
    public static BiomeParasiteBase get(net.minecraft.resources.ResourceKey<Biome> key) {
        return INSTANCES.computeIfAbsent(key, k -> k.equals(com.dhanantry.scapeandrunparasites.block.SRPBlockLinks.BIOME_HARLEQUIN) ? new BiomeParasiteHarlequin() : new BiomeParasiteShrouded());
    }

    public abstract float getRedValue();

    public abstract float getGreenValue();

    public abstract float getBlueValue();

    public abstract String[] getBlockList();

    public abstract String getDirt();

    public abstract String getGravel();

    public abstract String getSand();

    public abstract String getLog();

    public abstract String getStone();

    public abstract String getCobblestone();

    public abstract String getSandstone();

    public abstract String getLeaves();

    public abstract String getLeavesG();

    public abstract String getPlank();

    public abstract String getBush();

    public abstract void spawnGenFeatureParasite(Level var1, BlockPos var2, RandomSource var3);

    public abstract void spawnGenRoofParasite(Level var1, BlockPos var2, RandomSource var3);

    public int convertBlock(BlockPos helper, Level worldIn, RandomSource rand) {
        Block one;
        String[] blockList;
        int currentMeta;
        BlockState lookingState = worldIn.getBlockState(helper);
        LegacyMaterial mat = LegacyMaterial.of(lookingState);
        Block lookingBlock = lookingState.getBlock();
        if (lookingBlock instanceof IMetaName) {
            return 0;
        }
        if (mat == LegacyMaterial.air || mat == LegacyMaterial.water || mat == LegacyMaterial.lava) {
            return 0;
        }
        ResourceLocation rl = lookingBlock.builtInRegistryHolder().key().location();
        if (rl == null) {
            return 0;
        }
        String lookingName = rl.toString();
        if (BiomeParasiteBase.transformBlockList(helper, worldIn, lookingName, currentMeta = BlockIds.legacyMeta(lookingState), blockList = this.getBlockList())) {
            return 1;
        }
        if (ParasiteEventWorld.blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigWorld.blockBBiomeList, SRPConfigWorld.blockBBiomeListWhite, SRPConfigWorld.biomeBlockIMaxH)) {
            return 0;
        }
        if (BiomeParasiteBase.transformBlockList(helper, worldIn, lookingName, currentMeta, blockList)) {
            return 1;
        }
        if (worldIn.getBlockState(helper).is(BlockTags.LOGS) || lookingBlock == SRPBlocks.InfestedTrunk.get()) {
            blockList = this.getLog().split(":");
            Block one2 = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            return 1;
        }
        if (lookingState.is(BlockTags.LEAVES)) {
            BlockPos below;
            blockList = this.getLeaves().split(":");
            Block one3 = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            if (rand.nextInt(100) < 30 && worldIn.isEmptyBlock(below = helper.below())) {
                blockList = this.getLeavesG().split(":");
                one3 = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
                BlockState growth = BiomeParasiteBase.setFacingIfPresent(BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]), Direction.DOWN);
                worldIn.setBlock(below, growth, 2);
            }
            return 1;
        }
        if (lookingBlock == SRPBlocks.InfestedStain.get()) {
            blockList = this.getDirt().split(":");
            one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
        }
        if (mat == LegacyMaterial.ground || mat == LegacyMaterial.grass) {
            blockList = this.getDirt().split(":");
            one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock instanceof net.minecraft.world.level.block.SandBlock) {
            blockList = this.getSand().split(":");
            one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock.builtInRegistryHolder().key().location().getPath().contains("sandstone")) {
            blockList = this.getSandstone().split(":");
            one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock == Blocks.GRAVEL || mat == LegacyMaterial.sand) {
            blockList = this.getGravel().split(":");
            one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock == Blocks.COBBLESTONE) {
            blockList = this.getCobblestone().split(":");
            one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
            worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (mat == LegacyMaterial.rock) {
            if (lookingBlock == SRPBlocks.BiomeHeart.get() || lookingBlock == SRPBlocks.ColonyHeart.get()) {
                return 0;
            }
            if (lookingBlock == Blocks.OBSIDIAN) {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.OBSIDIAN)));
            } else if (lookingBlock.builtInRegistryHolder().key().location().toString().contains("brick")) {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)));
            } else {
                blockList = this.getStone().split(":");
                one = BlockIds.parseBlock(blockList[0] + ":" + blockList[1]);
                worldIn.setBlockAndUpdate(helper, BlockIds.parse(blockList[0] + ":" + blockList[1] + ":" + blockList[2]));
            }
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (mat == LegacyMaterial.plants) {
            worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)));
            return 1;
        }
        if (mat == LegacyMaterial.ice || mat == LegacyMaterial.packedIce) {
            worldIn.setBlockAndUpdate(helper, SRPBlocks.BloodyIce.get().defaultBlockState());
            return 1;
        }
        if (mat == LegacyMaterial.iron) {
            worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.METAL)));
            return 1;
        }
        if (mat == LegacyMaterial.wood && lookingBlock != SRPBlocks.InfestedTrunk.get()) {
            if (lookingBlock.builtInRegistryHolder().key().location().toString().contains("mushroom")) {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FUNGUS)));
            } else {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.WOOD)));
            }
            return 1;
        }
        return 0;
    }

    @SuppressWarnings("unchecked")
    private static BlockState setFacingIfPresent(BlockState state, Direction face) {
        for (Property<?> p : state.getProperties()) {
            if (!p.getName().equalsIgnoreCase("facing") || p.getValueClass() != Direction.class || !((Property<Direction>) p).getPossibleValues().contains(face)) continue;
            return state.setValue((Property<Direction>) p, face);
        }
        return state;
    }

    public static boolean transformBlockList(BlockPos helper, Level worldIn, String name, int meta, String[] rules) {
        if (rules == null || rules.length == 0) {
            return false;
        }
        for (String rule : rules) {
            Block putting;
            String[] nm;
            String[] dst;
            String[] src;
            String[] parts = rule.split(";");
            if (parts.length != 2 || (src = parts[0].split(":")).length != 3 || (dst = parts[1].split(":")).length != 3 || (nm = name.split(":")).length != 2) continue;
            if ("minecraft:glass".equals(name)) {
                System.out.println("[SRP] transformBlockList evaluating GLASS: rule=" + rule + " meta=" + meta);
            }
            if (!nm[0].equals(src[0]) || !nm[1].equals(src[1]) || meta != Integer.parseInt(src[2]) || (putting = BlockIds.parseBlock(dst[0] + ":" + dst[1])) == null) continue;
            int dstMeta = Integer.parseInt(dst[2]);
            worldIn.setBlock(helper, BlockIds.legacyState(putting, dstMeta), 3);
            return true;
        }
        return false;
    }
}

