package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteDecorator;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import java.util.Collection;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class BiomeParasiteBase
extends Biome {
    protected WorldGenAbstractTree tree;

    public BiomeParasiteBase(Biome.BiomeProperties properties) {
        super(properties);
    }

    public void setBlocks() {
        String[] blockList = this.getDirt().split(":");
        Block one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
        this.topBlock = one.getStateFromMeta(Integer.parseInt(blockList[2]));
        blockList = this.getStone().split(":");
        one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
        this.fillerBlock = one.getStateFromMeta(Integer.parseInt(blockList[2]));
        this.theBiomeDecorator = new BiomeParasiteDecorator(this);
    }

    public void mobListClear() {
        this.spawnableCreatureList.clear();
        this.spawnableMonsterList.clear();
        this.spawnableWaterCreatureList.clear();
        this.spawnableCaveCreatureList.clear();
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
        Material mat = lookingState.getMaterial();
        Block lookingBlock = lookingState.getBlock();
        if (lookingBlock instanceof IMetaName) {
            return 0;
        }
        if (mat == Material.air || mat == Material.water || mat == Material.lava) {
            return 0;
        }
        ResourceLocation rl = lookingBlock.builtInRegistryHolder().key().location();
        if (rl == null) {
            return 0;
        }
        String lookingName = rl.toString();
        if (BiomeParasiteBase.transformBlockList(helper, worldIn, lookingName, currentMeta = lookingBlock.getMetaFromStatePlaceholder(lookingState), blockList = this.getBlockList())) {
            return 1;
        }
        if (ParasiteEventWorld.blockException(worldIn, helper, lookingBlock, lookingState, SRPConfigWorld.blockBBiomeList, SRPConfigWorld.blockBBiomeListWhite, SRPConfigWorld.biomeBlockIMaxH)) {
            return 0;
        }
        if (BiomeParasiteBase.transformBlockList(helper, worldIn, lookingName, currentMeta, blockList)) {
            return 1;
        }
        if (lookingBlock.isWood((BlockGetter)worldIn, helper) || lookingBlock == SRPBlocks.InfestedTrunk.get()) {
            blockList = this.getLog().split(":");
            Block one2 = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one2.getStateFromMeta(Integer.parseInt(blockList[2])));
            return 1;
        }
        if (lookingBlock.isLeaves(lookingState, (BlockGetter)worldIn, helper)) {
            BlockPos below;
            blockList = this.getLeaves().split(":");
            Block one3 = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one3.getStateFromMeta(Integer.parseInt(blockList[2])));
            if (rand.nextInt(100) < 30 && worldIn.isEmptyBlock(below = helper.below())) {
                blockList = this.getLeavesG().split(":");
                one3 = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
                BlockState growth = BiomeParasiteBase.setFacingIfPresent(one3.getStateFromMeta(Integer.parseInt(blockList[2])), Direction.DOWN);
                worldIn.setBlock(below, growth, 2);
            }
            return 1;
        }
        if (lookingBlock == SRPBlocks.InfestedStain.get()) {
            blockList = this.getDirt().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
        }
        if (mat == Material.ground || mat == Material.grass) {
            blockList = this.getDirt().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock instanceof BlockSand) {
            blockList = this.getSand().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock instanceof BlockSandStone) {
            blockList = this.getSandstone().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock == Blocks.GRAVEL || mat == Material.sand) {
            blockList = this.getGravel().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (lookingBlock == Blocks.COBBLESTONE) {
            blockList = this.getCobblestone().split(":");
            one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
            worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (mat == Material.rock) {
            if (lookingBlock == SRPBlocks.BiomeHeart.get() || lookingBlock == SRPBlocks.ColonyHeart.get()) {
                return 0;
            }
            if (lookingBlock == Blocks.OBSIDIAN) {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.OBSIDIAN)));
            } else if (lookingBlock.builtInRegistryHolder().key().location().toString().contains("brick")) {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)));
            } else {
                blockList = this.getStone().split(":");
                one = Block.getBlockFromName((String)(blockList[0] + ":" + blockList[1]));
                worldIn.setBlockAndUpdate(helper, one.getStateFromMeta(Integer.parseInt(blockList[2])));
            }
            this.spawnGenFeatureParasite(worldIn, helper.above(), rand);
            this.spawnGenRoofParasite(worldIn, helper.below(), rand);
            return 1;
        }
        if (mat == Material.plants) {
            worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)));
            return 1;
        }
        if (mat == Material.ice || mat == Material.packedIce) {
            worldIn.setBlockAndUpdate(helper, SRPBlocks.BloodyIce.get().defaultBlockState());
            return 1;
        }
        if (mat == Material.iron) {
            worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.METAL)));
            return 1;
        }
        if (mat == Material.wood && lookingBlock != SRPBlocks.InfestedTrunk.get()) {
            if (lookingBlock.builtInRegistryHolder().key().location().toString().contains("mushroom")) {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FUNGUS)));
            } else {
                worldIn.setBlockAndUpdate(helper, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.WOOD)));
            }
            return 1;
        }
        return 0;
    }

    private static BlockState setFacingIfPresent(BlockState state, Direction face) {
        Collection props = state.getPropertyNames();
        for (Property p : props) {
            Property pf;
            if (!p.getName().equalsIgnoreCase("facing") || p.getValueClass() != Direction.class || !(pf = p).getAllowedValues().contains(face)) continue;
            return state.setValue(pf, face);
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
            if (!nm[0].equals(src[0]) || !nm[1].equals(src[1]) || meta != Integer.parseInt(src[2]) || (putting = Block.getBlockFromName((String)(dst[0] + ":" + dst[1]))) == null) continue;
            int dstMeta = Integer.parseInt(dst[2]);
            worldIn.setBlock(helper, putting.getStateFromMeta(dstMeta), 3);
            return true;
        }
        return false;
    }
}

