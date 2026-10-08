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
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenParasiteColonyBS2
extends WorldGenParasiteColonyBase {
    private BlockState floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    private BlockState tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
    private BlockState wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));

    public WorldGenParasiteColonyBS2(boolean notify, int stage) {
        super(notify, stage);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        int hz;
        int hx;
        int i;
        BlockPos enter = posss;
        this.replaceCircleGround(worldIn, posss.below(), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(2), 8, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(3), 8, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        int missing = 40;
        int radius = 7;
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        int height = 28;
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
        posss = enter;
        radius = 7;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 47;
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
        posss = enter;
        radius = 2;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        height = 17;
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
        return true;
    }
}

