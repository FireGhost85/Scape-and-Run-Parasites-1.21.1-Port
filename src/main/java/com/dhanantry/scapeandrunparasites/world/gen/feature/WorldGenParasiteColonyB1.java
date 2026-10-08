package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WorldGenParasiteColonyB1
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteColonyB1(boolean notify, int stage) {
        super(notify, stage);
        this.wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        this.floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        BlockPos enter = posss;
        this.replaceCircleGround(worldIn, posss.below(), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(2), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(3), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        int missing = 40;
        int height = 22 + rand.nextInt(3);
        int xx = 2;
        int zz = 2;
        int tic = 2;
        int cool = 3;
        int changeX = 0;
        int changeZ = 0;
        for (int i = 0; i < height; ++i) {
            --changeZ;
            if (worldIn.random.nextInt(2) == 0 && --changeX <= 0) {
                if (worldIn.random.nextInt(3) == 0) {
                    xx = Math.min(9, xx + 2);
                    zz = Math.min(9, zz + 2);
                } else {
                    xx = Math.max(3, xx - 1);
                    zz = Math.max(3, zz - 1);
                }
                changeX = cool;
            }
            this.generateCircle(SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BONE)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss, xx, zz, 1, 20000, 6);
            int hx = xx - tic;
            int hz = zz - tic;
            this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, posss, hx, hz, 1, 20000, 6);
            posss = posss.above(1);
        }
        BlockPos he = posss;
        int aa = zz;
        int bonusH = rand.nextInt(5);
        posss = posss.above(18 + bonusH - zz / 2 * 2);
        if (rand.nextBoolean()) {
            this.generateSphere(worldIn, posss, zz + 1, 2, rand, false, 1, false, 1, 1, 5, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FLESH)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.AIR.defaultBlockState(), missing);
        }
        this.generateDNAHelix(Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, he.below(4), aa - 2, 2, 11 + bonusH);
        this.generateDNAHelix(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, he.below(3), aa - 2, 2, 11 + bonusH);
        this.generateDNAHelix(Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, he.below(2), aa - 2, 2, 11 + bonusH);
        return true;
    }
}

