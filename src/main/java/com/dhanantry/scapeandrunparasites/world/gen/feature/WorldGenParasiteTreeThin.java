package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WorldGenParasiteTreeThin
extends WorldGenParasiteTreeAbstract {
    public WorldGenParasiteTreeThin(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        BlockPos current;
        int i = 16;
        boolean flag = true;
        if (worldIn.getBlockState(position.below()).getBlock() == Blocks.AIR) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 1;
                if (j == position.getY()) {
                    k = 0;
                }
                if (j >= position.getY() + 1 + i - 2) {
                    k = 2;
                }
                BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
                for (int l = position.getX() - k; l <= position.getX() + k && flag; ++l) {
                    for (int i1 = position.getZ() - k; i1 <= position.getZ() + k && flag; ++i1) {
                        if (j >= 0 && j < worldIn.getHeight()) {
                            if (this.isReplaceable(worldIn, (BlockPos)blockpos$mutableblockpos.set(l, j, i1))) continue;
                            flag = false;
                            continue;
                        }
                        flag = false;
                    }
                }
            }
            if (!flag) {
                return false;
            }
            current = position;
            while (!worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && current.below().getY() >= 1) {
                current = current.below();
                this.placeTrunk(worldIn, current);
            }
        } else {
            return false;
        }
        current = position;
        current = this.placeColumn(worldIn, current, 3, rand, 0.0, 4);
        this.grow(worldIn, current.north(), rand, 1);
        this.grow(worldIn, current.east(), rand, 2);
        this.grow(worldIn, current.south(), rand, 3);
        this.grow(worldIn, current.west(), rand, 4);
        return true;
    }

    private void placeThin(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteThin.get().defaultBlockState());
    }

    private void placeCanister(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteCanister.get().defaultBlockState().setValue(BlockParasiteCanister.VARIANT, (BlockParasiteCanister.EnumType.LUMP)));
    }

    private void placeTrunk(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.TREE)));
    }

    private BlockPos placeColumn(Level worldIn, BlockPos pos, int times, RandomSource rand, double extraChance, int minimum) {
        int current;
        int atm = current = pos.getY();
        BlockPos newPos = pos;
        times = rand.nextInt(times) + minimum;
        if (worldIn.getBlockState(pos.below()).getBlock() == Blocks.AIR && rand.nextDouble() <= extraChance) {
            this.placeCanister(worldIn, pos.below());
        }
        while (current < atm + times && minimum != 1) {
            this.placeThin(worldIn, newPos);
            newPos = newPos.above();
            ++current;
        }
        this.placeThin(worldIn, newPos);
        return newPos;
    }

    private void grow(Level worldIn, BlockPos pos, RandomSource rand, int direction) {
        pos = this.placeColumn(worldIn, pos, 2, rand, 0.3, 2);
        if (rand.nextInt(3) == 0) {
            pos = this.directionToGrow(pos, direction);
            pos = this.placeColumn(worldIn, pos, 3, rand, 0.5, 2);
            if (rand.nextInt(5) == 0) {
                pos = this.directionToGrow(pos, direction);
                pos = this.placeColumn(worldIn, pos, 3, rand, 0.7, 2);
            } else {
                pos = this.directionToGrow(pos, direction);
                pos = this.placeColumn(worldIn, pos, 1, rand, 0.7, 1);
            }
        } else {
            pos = this.directionToGrow(pos, direction);
            pos = this.placeColumn(worldIn, pos, 1, rand, 0.5, 1);
        }
    }

    private BlockPos directionToGrow(BlockPos pos, int choice) {
        switch (choice) {
            case 1: {
                return pos.north();
            }
            case 2: {
                return pos.east();
            }
            case 3: {
                return pos.south();
            }
        }
        return pos.west();
    }
}

