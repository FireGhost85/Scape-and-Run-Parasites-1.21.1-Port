package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenerator;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;

public class BiomeParasiteDemen
extends BiomeParasiteBase {
    public static WorldGenAbstractTree treeP = new WorldGenParasiteTree(false);
    public static WorldGenAbstractTree treePT = new WorldGenParasiteTreeThin(false);
    public WorldGenerator grassP1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 1);
    public WorldGenerator grassP2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 2);

    public BiomeParasiteDemen() {
        super(new Biome.BiomeProperties("Parasite Biome Demen").setBaseHeight(0.13f).setHeightVariation(0.5f));
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
        return SRPConfigWorld.biomeFourSkyColor;
    }

    public int getGrassColorAtPos(BlockPos blockPos) {
        return SRPConfigWorld.biomeFourGrassColor;
    }

    public int getFoliageColorAtPos(BlockPos blockPos) {
        return SRPConfigWorld.biomeFourFoliageColor;
    }

    public int getWaterColorMultiplier() {
        return SRPConfigWorld.biomeFourWaterColor;
    }

    @Override
    public float getRedValue() {
        return SRPConfigWorld.biomeFourFogRed / 255.0f;
    }

    @Override
    public float getGreenValue() {
        return SRPConfigWorld.biomeFourFogGreen / 255.0f;
    }

    @Override
    public float getBlueValue() {
        return SRPConfigWorld.biomeFourFogBlue / 255.0f;
    }

    @Override
    public String[] getBlockList() {
        return null;
    }

    @Override
    public String getDirt() {
        return null;
    }

    @Override
    public String getGravel() {
        return null;
    }

    @Override
    public String getLog() {
        return null;
    }

    @Override
    public String getStone() {
        return null;
    }

    @Override
    public String getCobblestone() {
        return null;
    }

    @Override
    public String getSand() {
        return null;
    }

    @Override
    public String getSandstone() {
        return null;
    }

    @Override
    public String getLeaves() {
        return null;
    }

    @Override
    public String getLeavesG() {
        return null;
    }

    @Override
    public String getPlank() {
        return null;
    }

    @Override
    public String getBush() {
        return null;
    }

    @Override
    public void spawnGenFeatureParasite(Level worldIn, BlockPos pos, RandomSource rand) {
    }

    @Override
    public void spawnGenRoofParasite(Level worldIn, BlockPos pos, RandomSource rand) {
    }
}

