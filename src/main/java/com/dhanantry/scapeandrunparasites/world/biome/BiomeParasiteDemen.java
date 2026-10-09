package com.dhanantry.scapeandrunparasites.world.biome;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenAbstractTree;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenerator;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteBush;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTree;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeThin;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class BiomeParasiteDemen
extends BiomeParasiteBase {
    public static WorldGenAbstractTree treeP = new WorldGenParasiteTree(false);
    public static WorldGenAbstractTree treePT = new WorldGenParasiteTreeThin(false);
    public WorldGenerator grassP1 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.EYE, 1);
    public WorldGenerator grassP2 = new WorldGenParasiteBush(false, BlockParasiteBush.EnumType.POP, 2);


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

