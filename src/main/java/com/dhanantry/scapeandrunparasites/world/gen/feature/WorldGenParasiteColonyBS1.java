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

public class WorldGenParasiteColonyBS1
extends WorldGenParasiteColonyBase {
    private BlockState floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    private BlockState tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
    private BlockState wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));

    public WorldGenParasiteColonyBS1(boolean notify, int stage) {
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
        int radius = 4;
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        int height = 8;
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
        return true;
    }

    private BlockPos placeWallsBottom(Level worldIn, BlockPos position, int loop, RandomSource rand, BlockState state, int oY) {
        BlockPos currentP = position;
        int current = 0;
        while (current < loop) {
            BlockPos helpRoot = currentP;
            BlockPos helper = currentP;
            for (int i = 0; i <= 3; ++i) {
                int times;
                int o;
                BlockPos rootH = helper = this.getDirectionRoot(helpRoot, i, 2);
                for (o = 0; o < 1; ++o) {
                    for (times = 1; times <= 1; ++times) {
                        helper = o == 0 ? this.getDirectionRoot(rootH, (i + 1) % 4, times + 1) : this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                        if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                            this.placeBlock(worldIn, helper.below(), state);
                        }
                        this.placeBlock(worldIn, helper, state);
                    }
                }
                rootH = helper = this.getDirectionRoot(rootH, i, 1);
                for (o = 0; o < 2; ++o) {
                    for (times = 1; times <= 1; ++times) {
                        int llimitO;
                        int llimit;
                        BlockPos trunk;
                        int llimitO2;
                        BlockPos atm;
                        if (o == 0) {
                            helper = this.getDirectionRoot(rootH, (i + 1) % 4, times);
                            if (current == 2 && rand.nextInt(2) == 0) {
                                atm = helper;
                                atm = this.directionToGrow(atm, i, false);
                                this.placeBlock(worldIn, atm, this.tacle);
                                while (!worldIn.getBlockState(atm.below()).isCollisionShapeFullBlock(worldIn, atm.below()) && atm.below().getY() >= 1) {
                                    atm = rand.nextInt(1) == 0 ? this.directionToGrow(atm, i * 10, true) : this.directionToGrow(atm, i, false);
                                    int llimit2 = atm.getY() < oY ? 5 : 2;
                                    llimitO2 = atm.getY() < oY ? 4 : 0;
                                    atm = this.placeColumn(worldIn, atm, rand.nextInt(llimit2) + (2 + llimitO2), rand, 0.0, this.tacle);
                                }
                                this.placeBlock(worldIn, atm.below(), this.tacle);
                            }
                            if (current == loop - 3) {
                                trunk = helper;
                                if (worldIn.getBlockState((trunk = this.directionToGrow(trunk, i, false)).below(4)).getBlock() == Blocks.AIR) {
                                    BlockPos atm2 = helper;
                                    atm2 = this.directionToGrow(atm2, i, false);
                                    this.placeBlock(worldIn, atm2, this.tacle);
                                    while (!worldIn.getBlockState(atm2.below()).isCollisionShapeFullBlock(worldIn, atm2.below()) && atm2.below().getY() >= 1) {
                                        atm2 = rand.nextInt(1) == 0 ? this.directionToGrow(atm2, i * 10, true) : this.directionToGrow(atm2, i, false);
                                        llimit = atm2.getY() < oY ? 5 : 2;
                                        llimitO = atm2.getY() < oY ? 4 : 0;
                                        atm2 = this.placeColumn(worldIn, atm2, rand.nextInt(llimit) + (2 + llimitO), rand, 0.0, this.tacle);
                                    }
                                    this.placeBlock(worldIn, atm2.below(), this.tacle);
                                }
                            }
                        } else {
                            helper = this.getDirectionRoot(rootH, (i + 3) % 4, times);
                            if (current == 5 && times != 1 && rand.nextInt(2) == 0) {
                                atm = helper;
                                atm = this.directionToGrow(atm, i, false);
                                this.placeBlock(worldIn, atm, this.tacle);
                                while (!worldIn.getBlockState(atm.below()).isCollisionShapeFullBlock(worldIn, atm.below()) && atm.below().getY() >= 1) {
                                    atm = rand.nextInt(1) == 0 ? this.directionToGrow(atm, i * 10 + 1, true) : this.directionToGrow(atm, i, false);
                                    int llimit3 = atm.getY() < oY ? 5 : 2;
                                    llimitO2 = atm.getY() < oY ? 4 : 0;
                                    atm = this.placeColumn(worldIn, atm, rand.nextInt(llimit3) + (2 + llimitO2), rand, 0.0, this.tacle);
                                }
                                this.placeBlock(worldIn, atm.below(), this.tacle);
                            }
                            if (current == loop - 2 && times != 1) {
                                trunk = helper;
                                if (worldIn.getBlockState((trunk = this.directionToGrow(trunk, i, false)).below(5)).getBlock() == Blocks.AIR) {
                                    BlockPos atm3 = helper;
                                    atm3 = this.directionToGrow(atm3, i, false);
                                    this.placeBlock(worldIn, atm3, this.tacle);
                                    while (!worldIn.getBlockState(atm3.below()).isCollisionShapeFullBlock(worldIn, atm3.below()) && atm3.below().getY() >= 1) {
                                        atm3 = rand.nextInt(1) == 0 ? this.directionToGrow(atm3, i * 10 + 1, true) : this.directionToGrow(atm3, i, false);
                                        llimit = atm3.getY() < oY ? 5 : 2;
                                        llimitO = atm3.getY() < oY ? 4 : 0;
                                        atm3 = this.placeColumn(worldIn, atm3, rand.nextInt(llimit) + (2 + llimitO), rand, 0.0, this.tacle);
                                    }
                                    this.placeBlock(worldIn, atm3.below(), this.tacle);
                                }
                            }
                        }
                        if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                            this.placeBlock(worldIn, helper.below(), state);
                        }
                        this.placeBlock(worldIn, helper, state);
                    }
                }
                helper = this.getDirectionRoot(rootH, i, 0);
                if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                    this.placeBlock(worldIn, helper.below(), state);
                }
                this.placeBlock(worldIn, helper, state);
            }
            ++current;
            currentP = currentP.above();
        }
        return currentP;
    }

    private BlockPos placeWallsTopIn(Level worldIn, BlockPos position, int loop, boolean vine, RandomSource rand, int longer, BlockState state) {
        BlockPos currentP = position;
        int current = 0;
        while (current < loop) {
            BlockPos helpRoot = currentP;
            BlockPos helper = currentP;
            for (int i = 0; i <= 3; ++i) {
                int times;
                int o;
                BlockPos rootH = helper = this.getDirectionRoot(helpRoot, i, 3);
                for (o = 0; o < 2; ++o) {
                    for (times = 1; times <= 1; ++times) {
                        helper = o == 0 ? this.getDirectionRoot(rootH, (i + 1) % 4, times + 1) : this.getDirectionRoot(rootH, (i + 3) % 4, times + 1);
                        this.placeBlock(worldIn, helper, state);
                        if (!vine || current != 0 || !rand.nextBoolean()) continue;
                        this.addVines(worldIn, helper.below(), rand, longer);
                    }
                }
                rootH = helper = this.getDirectionRoot(rootH, i, 1);
                for (o = 0; o < 2; ++o) {
                    for (times = 1; times <= 1; ++times) {
                        helper = o == 0 ? this.getDirectionRoot(rootH, (i + 1) % 4, times) : this.getDirectionRoot(rootH, (i + 3) % 4, times);
                        this.placeBlock(worldIn, helper, state);
                        if (!vine || current != 0 || !rand.nextBoolean()) continue;
                        this.addVines(worldIn, helper.below(), rand, longer);
                    }
                }
                helper = this.getDirectionRoot(rootH, i, 0);
                this.placeBlock(worldIn, helper, state);
                if (!vine || current != 0 || !rand.nextBoolean()) continue;
                this.addVines(worldIn, helper.below(), rand, longer);
            }
            ++current;
            currentP = currentP.above();
        }
        return currentP;
    }
}

