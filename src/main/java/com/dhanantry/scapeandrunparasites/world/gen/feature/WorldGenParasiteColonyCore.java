package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockColonyCore;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.Property;

public class WorldGenParasiteColonyCore
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteColonyCore(boolean notify, int stage) {
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
        posss = posss.below(7);
        int height = 12 + rand.nextInt(3);
        int xx = 2;
        int zz = 2;
        int tic = 6;
        int cool = 3;
        int changeX = 0;
        int changeZ = 0;
        for (int i2 = 0; i2 < height; ++i2) {
            --changeZ;
            if (worldIn.random.nextInt(2) == 0 && --changeX <= 0) {
                if (worldIn.random.nextInt(3) == 0) {
                    xx = Math.min(8, xx + 2);
                    zz = Math.min(8, zz + 2);
                } else {
                    xx = Math.max(5, xx - 1);
                    zz = Math.max(5, zz - 1);
                }
                changeX = cool;
            }
            this.generateCircle(SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FLESH)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss, xx, zz, 1, 20000, 6);
            int hx2 = xx - tic;
            int hz2 = zz - tic;
            this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, posss, hx2, hz2, 1, 20000, 6);
            posss = posss.above(1);
        }
        int aa = zz;
        int bonusH = rand.nextInt(5);
        height = 10 + rand.nextInt(5);
        int kil = 3;
        int sec = 2;
        double spa = height / sec;
        posss = posss.below(3);
        this.generateDNAHelix(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss, kil, sec, spa);
        this.generateDNAHelix(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.SACKFLESH)), worldIn, worldIn.random, posss.above(1), kil, sec, spa);
        this.generateDNAHelix(SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BONE)), worldIn, worldIn.random, posss.above(2), kil, sec, spa);
        this.generateDNAHelix(Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, posss.above(3), --kil, sec, spa);
        sec = 2;
        this.generateDNAHelix(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)), worldIn, worldIn.random, posss, kil += 2, sec, spa);
        posss = posss.above(height);
        this.generateSphere(worldIn, posss, 8, 6, rand, false, 6, false, 2, 1, 5, SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FLESH)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.AIR.defaultBlockState(), missing);
        BlockPos atm = posss = posss.above(22);
        int radius = 7;
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 7;
        xx = 1;
        zz = 2;
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
        posss = atm;
        radius = 7;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 27;
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
            this.generateCircle(SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FLESH)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss, xx, zz, 1, 20000, 6);
            hx = xx - tic;
            hz = zz - tic;
            this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), Blocks.BONE_BLOCK.defaultBlockState(), worldIn, worldIn.random, posss, hx, hz, 1, 20000, 6);
            posss = posss.above(1);
        }
        this.generateSphere(worldIn, posss, 3, 3, rand, false, 3, false, 2, 1, 5, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL)), SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)), Blocks.AIR.defaultBlockState(), missing);
        posss = atm;
        radius = 13;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 10;
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
        posss = atm;
        posss = posss.above(7);
        radius = 2;
        height = 8;
        xx = 1;
        zz = 2;
        tic = 1;
        cool = 1;
        changeX = 0;
        changeZ = 0;
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
            this.placeCore(worldIn, enter, 1);
            return true;
        }
        radius = 4;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 7;
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
        this.placeCore(worldIn, enter, 1);
        return true;
    }

    private void placeCore(Level worldIn, BlockPos pos, int stage) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ColonyHeart.get().defaultBlockState().setValue((Property)BlockColonyCore.ACTIVE, Integer.valueOf(stage)));
    }
}

