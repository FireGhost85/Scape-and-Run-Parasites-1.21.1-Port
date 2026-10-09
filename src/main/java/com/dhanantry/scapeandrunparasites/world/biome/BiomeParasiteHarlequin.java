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

public class BiomeParasiteHarlequin
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
        return SRPConfigWorld.biomeThreeFogRed / 255.0f;
    }

    @Override
    public float getGreenValue() {
        return SRPConfigWorld.biomeThreeFogGreen / 255.0f;
    }

    @Override
    public float getBlueValue() {
        return SRPConfigWorld.biomeThreeFogBlue / 255.0f;
    }

    @Override
    public String[] getBlockList() {
        return SRPConfigWorld.biomeThreeBlockList;
    }

    @Override
    public String getDirt() {
        return "srparasites:harlequinn_grass:0";
    }

    @Override
    public String getGravel() {
        return "srparasites:infestedsand:0";
    }

    @Override
    public String getLog() {
        return "srparasites:parasitetrunk:0";
    }

    @Override
    public String getStone() {
        return "srparasites:harleskinn_block:0";
    }

    @Override
    public String getCobblestone() {
        return "srparasites:harleskinn_block:0";
    }

    @Override
    public String getSand() {
        return "srparasites:harleskinn_block:0";
    }

    @Override
    public String getSandstone() {
        return "srparasites:harleskinn_block:0";
    }

    @Override
    public String getLeaves() {
        return "srparasites:alveoli:0";
    }

    @Override
    public String getLeavesG() {
        return "srparasites:alveoli_growth:0";
    }

    @Override
    public String getPlank() {
        return "srparasites:harleskinn_block:0";
    }

    @Override
    public String getBush() {
        return "srparasites:alveoli_growth:0";
    }

    @Override
    public void spawnGenFeatureParasite(Level worldIn, BlockPos pos, RandomSource rand) {
    }

    @Override
    public void spawnGenRoofParasite(Level worldIn, BlockPos pos, RandomSource rand) {
    }
}

