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

public class WorldGenParasiteColonyB3
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteColonyB3(boolean notify, int stage) {
        super(notify, stage);
        this.wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        this.floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        int hz;
        int hx;
        int i;
        BlockPos enter = posss;
        this.replaceCircleGround(worldIn, posss.below(), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(2), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(3), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        int missing = 40;
        this.generateSphere(worldIn, posss, 4, 3, rand, false, 6, false, 2, 1, 5, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL)), SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)), Blocks.AIR.defaultBlockState(), missing);
        posss = posss.above(12);
        int radius = 8;
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        int height = 20;
        int xx = 1;
        int zz = 2;
        int tic = 1;
        int cool = 1;
        int changeX = 0;
        int changeZ = 0;
        for (i = 0; i < height; ++i) {
            --changeZ;
            if (worldIn.random.nextInt(2) == 0 && --changeX <= 0) {
                if (worldIn.random.nextInt(2) == 0) {
                    xx = Math.min(3, xx + 1);
                    zz = Math.min(2, zz + 1);
                } else {
                    xx = Math.max(1, xx - 1);
                    zz = Math.max(1, zz - 1);
                }
                changeX = cool;
            }
            this.generateCircle(SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BONE)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss, xx, zz, 1, 20000, 6);
            hx = xx - tic;
            hz = zz - tic;
            this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, posss, hx, hz, 1, 20000, 6);
            posss = posss.above(1);
        }
        this.generateSphere(worldIn, posss, 3, 3, rand, false, 3, false, 2, 1, 5, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL)), SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)), Blocks.AIR.defaultBlockState(), missing);
        posss = posss.above(10);
        if (rand.nextBoolean()) {
            this.addEntrance(worldIn, rand, enter, 5);
            return true;
        }
        radius = 4;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 15;
        xx = 1;
        zz = 1;
        tic = 1;
        cool = 1;
        changeX = 0;
        changeZ = 0;
        for (i = 0; i < height; ++i) {
            --changeZ;
            if (worldIn.random.nextInt(2) == 0 && --changeX <= 0) {
                if (worldIn.random.nextInt(2) == 0) {
                    xx = Math.min(2, xx + 1);
                    zz = Math.min(1, zz + 1);
                } else {
                    xx = Math.max(1, xx - 1);
                    zz = Math.max(1, zz - 1);
                }
                changeX = cool;
            }
            this.generateCircle(SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BONE)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss, xx, zz, 1, 20000, 6);
            hx = xx - tic;
            hz = zz - tic;
            this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, posss, hx, hz, 1, 20000, 6);
            posss = posss.above(1);
        }
        this.generateSphere(worldIn, posss, 3, 3, rand, false, 3, false, 2, 1, 5, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL)), SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)), Blocks.AIR.defaultBlockState(), missing);
        this.addEntrance(worldIn, rand, enter, 5);
        return true;
    }
}

