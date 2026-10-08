package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenerator;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBigBall;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteMouth;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteSpine;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTallFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTenFlower;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

public class BiomeParasiteShrouded
extends BiomeParasiteBase {
    public static WorldGenAbstractTree treeP = new WorldGenParasiteTree(false);
    public static WorldGenAbstractTree treePT = new WorldGenParasiteTreeThin(false);
    public WorldGenerator grassP1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 1);
    public WorldGenerator grassP2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 2);

    public BiomeParasiteShrouded() {
        super(new Biome.BiomeProperties("Parasite Biome Shrouded").setBaseHeight(0.13f).setHeightVariation(0.5f));
        this.theBiomeDecorator.treesPerChunk = 1;
        this.theBiomeDecorator.extraTreeChance = 0.0f;
        this.theBiomeDecorator.grassPerChunk = 15;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.deadBushPerChunk = 2;
        this.theBiomeDecorator.cactiPerChunk = 15;
        this.theBiomeDecorator.bigMushroomsPerChunk = 1;
    }

    public WorldGenAbstractTree genBigTreeChance(RandomSource rand) {
        if (rand.nextInt(3) == 0) {
            return treePT;
        }
        return treeP;
    }

    public WorldGenerator getRandomWorldGenForGrass(RandomSource rand) {
        if (rand.nextInt(2) == 0) {
            return this.grassP2;
        }
        return this.grassP1;
    }

    public void decorate(Level worldIn, RandomSource rand, BlockPos pos) {
        super.decorate(worldIn, rand, pos);
    }

    public void genTerrainBlocks(Level worldIn, RandomSource rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
        super.genTerrainBlocks(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
        double d0 = 1.0;
        if (d0 > 0.0) {
            int i = x & 0xF;
            int j = z & 0xF;
            for (int k = 255; k >= 0; --k) {
                if (chunkPrimerIn.getBlockState(j, k, i).getMaterialPlaceholder() == Material.air) continue;
                if (k != 62 || chunkPrimerIn.getBlockState(j, k, i).getBlock() == Blocks.WATER) break;
                chunkPrimerIn.setBlockState(j, k, i, WATER);
                break;
            }
        }
    }

    public int getSkyColorByTemp(float currentTemperature) {
        return SRPConfigWorld.biomeOneSkyColor;
    }

    public int getGrassColorAtPos(BlockPos blockPos) {
        return SRPConfigWorld.biomeOneGrassColor;
    }

    public int getFoliageColorAtPos(BlockPos blockPos) {
        return SRPConfigWorld.biomeOneFoliageColor;
    }

    public int getWaterColorMultiplier() {
        return SRPConfigWorld.biomeOneWaterColor;
    }

    @Override
    public float getRedValue() {
        return SRPConfigWorld.biomeOneFogRed / 255.0f;
    }

    @Override
    public float getGreenValue() {
        return SRPConfigWorld.biomeOneFogGreen / 255.0f;
    }

    @Override
    public float getBlueValue() {
        return SRPConfigWorld.biomeOneFogBlue / 255.0f;
    }

    @Override
    public String[] getBlockList() {
        return SRPConfigWorld.biomeOneBlockList;
    }

    @Override
    public String getDirt() {
        return "srparasites:parasitestain:0";
    }

    @Override
    public String getGravel() {
        return "srparasites:parasitestain:5";
    }

    @Override
    public String getLog() {
        return "srparasites:parasitetrunk:0";
    }

    @Override
    public String getStone() {
        return "srparasites:parasiterubble:2";
    }

    @Override
    public String getCobblestone() {
        return "srparasites:parasiterubble:0";
    }

    @Override
    public String getSand() {
        return "srparasites:parasitestain:1";
    }

    @Override
    public String getSandstone() {
        return "srparasites:parasiterubble:1";
    }

    @Override
    public String getLeaves() {
        return "minecraft:air:0";
    }

    @Override
    public String getLeavesG() {
        return "minecraft:air:0";
    }

    @Override
    public String getPlank() {
        return "srparasites:parasiteplank:0";
    }

    @Override
    public String getBush() {
        return "srparasites:parasitebush:0";
    }

    @Override
    public void spawnGenFeatureParasite(Level worldIn, BlockPos pos, RandomSource rand) {
        WorldGenParasiteGenAbstract gen;
        double bonus = 0.0;
        if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR && worldIn.getBlockState(pos).getFluidState().is(FluidTags.WATER)) {
            pos = ParasiteEventEntity.getFloor(worldIn, pos, 10);
            bonus = 3.0E-5;
            if (pos == null) {
                return;
            }
        }
        if (rand.nextDouble() < 0.0015) {
            if (rand.nextDouble() < 0.2) {
                WorldGenParasiteTreeThin gen2 = new WorldGenParasiteTreeThin(false);
                if (!gen2.generate(worldIn, rand, pos) && worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
                    worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteSapling.get().defaultBlockState().setValue(BlockParasiteSapling.VARIANT, (BlockParasiteSapling.EnumType.TREE)));
                }
                return;
            }
            WorldGenParasiteTree gen3 = new WorldGenParasiteTree(false);
            if (!gen3.generate(worldIn, rand, pos) && worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
                worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteSapling.get().defaultBlockState().setValue(BlockParasiteSapling.VARIANT, (BlockParasiteSapling.EnumType.TREETHIN)));
            }
            return;
        }
        if (rand.nextDouble() < 0.001) {
            WorldGenParasiteTallFlower gen4 = new WorldGenParasiteTallFlower(false);
            if (!gen4.generate(worldIn, rand, pos) && worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
                worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteSapling.get().defaultBlockState().setValue(BlockParasiteSapling.VARIANT, (BlockParasiteSapling.EnumType.FLOWERTALL)));
            }
            return;
        }
        if (rand.nextDouble() < 5.0E-4) {
            WorldGenParasiteSpine gen5 = new WorldGenParasiteSpine(false);
            if (!gen5.generate(worldIn, rand, pos)) {
                // empty if block
            }
            return;
        }
        if (rand.nextDouble() < 1.0E-4) {
            WorldGenParasiteTenFlower gen6 = new WorldGenParasiteTenFlower(false);
            if (!gen6.generate(worldIn, rand, pos)) {
                // empty if block
            }
            return;
        }
        if (rand.nextDouble() < 7.0E-5 + bonus) {
            WorldGenParasiteBall gen7 = new WorldGenParasiteBall(false);
            if (!gen7.generate(worldIn, rand, pos)) {
                // empty if block
            }
            return;
        }
        if (rand.nextDouble() < 2.0E-5 + bonus) {
            WorldGenParasiteBigBall gen8 = new WorldGenParasiteBigBall(false);
            if (!gen8.generate(worldIn, rand, pos)) {
                // empty if block
            }
            return;
        }
        if (rand.nextInt(500) == 0) {
            gen = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TENDRIL, 4);
            ((WorldGenParasiteBush)gen).generate(worldIn, rand, pos);
        }
        if (rand.nextInt(250) == 0) {
            switch (rand.nextInt(3)) {
                case 0: {
                    WorldGenParasiteBush gen1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 1);
                    gen1.generate(worldIn, rand, pos);
                    break;
                }
                case 1: {
                    WorldGenParasiteBush gen2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 2);
                    gen2.generate(worldIn, rand, pos);
                    break;
                }
                case 2: {
                    WorldGenParasiteBush gen3 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.TOOH, 3);
                    gen3.generate(worldIn, rand, pos);
                }
            }
        }
        if (rand.nextDouble() < 3.0E-4 && ((WorldGenParasiteMouth)(gen = new WorldGenParasiteMouth(false))).generate(worldIn, rand, pos)) {
            return;
        }
    }

    @Override
    public void spawnGenRoofParasite(Level worldIn, BlockPos pos, RandomSource rand) {
        if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
            return;
        }
        worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (BlockParasiteBush.EnumType.BINE)));
        pos = pos.below();
        if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
            return;
        }
        worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (BlockParasiteBush.EnumType.BINE)));
        pos = pos.below();
        if (rand.nextInt(2) == 0) {
            if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                return;
            }
            worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (BlockParasiteBush.EnumType.BINE)));
            pos = pos.below();
            if (rand.nextInt(2) == 0) {
                if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                    return;
                }
                worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (BlockParasiteBush.EnumType.BINE)));
                pos = pos.below();
                if (rand.nextInt(2) == 0) {
                    if (worldIn.getBlockState(pos).getBlock() != Blocks.AIR) {
                        return;
                    }
                    worldIn.setBlockAndUpdate(pos, SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (BlockParasiteBush.EnumType.BINE)));
                }
            }
        }
    }
}

