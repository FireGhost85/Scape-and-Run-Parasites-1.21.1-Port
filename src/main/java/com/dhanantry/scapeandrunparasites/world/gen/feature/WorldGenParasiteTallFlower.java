package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenParasiteTallFlower
extends WorldGenParasiteGenAbstract {
    private BlockState plant = SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.PLANT));
    private BlockState petal = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
    private BlockState base = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH));

    public WorldGenParasiteTallFlower(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        int i = 25;
        boolean flag = true;
        if (worldIn.getBlockState(position.below()).getBlock() == Blocks.AIR) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            int dir;
            int zs;
            int xs;
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 2;
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
            int lag = 0;
            BlockPos current = position;
            while (!worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && current.below().getY() >= 1) {
                current = current.below();
                for (int yyy = 0; yyy <= lag; ++yyy) {
                    for (xs = -1; xs <= 1; ++xs) {
                        for (zs = -1; zs <= 1; ++zs) {
                            this.placeBlock(worldIn, BlockPos.containing(current.getX() + xs, current.getY(), current.getZ() + zs), this.base);
                        }
                    }
                }
            }
            current = position;
            lag = rand.nextInt(2) + 1;
            for (int yyy = 0; yyy < lag; ++yyy) {
                for (xs = -1; xs <= 1; ++xs) {
                    for (zs = -1; zs <= 1; ++zs) {
                        this.placeBlock(worldIn, BlockPos.containing(current.getX() + xs, current.getY(), current.getZ() + zs), this.base);
                    }
                }
                current = current.above();
            }
            BlockPos helper = current = current.below();
            boolean times = false;
            boolean dirHelper = false;
            current = current.above();
            this.placeBlock(worldIn, current, this.base);
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                helper = this.getDirectionRoot(helper, dir, 1);
                this.placeColumn(worldIn, helper, 2, rand, 0.0, this.base);
            }
            current = this.placeColumn(worldIn, current, 5, rand, 0.0, this.plant);
            if (rand.nextInt(8) != 0) {
                current = this.getDirectionRoot(current, rand.nextInt(4), 1);
            }
            current = this.placeColumn(worldIn, current, 5, rand, 0.0, this.plant);
            if (rand.nextInt(8) != 0) {
                current = this.getDirectionRoot(current, rand.nextInt(4), 1);
            }
            current = this.placeColumn(worldIn, current, 5, rand, 0.0, this.plant);
            if (rand.nextInt(5) != 0) {
                if (rand.nextInt(8) != 0) {
                    current = this.getDirectionRoot(current, rand.nextInt(4), 1);
                }
                current = this.placeColumn(worldIn, current, 5, rand, 0.0, this.plant);
            }
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                helper = this.getDirectionRoot(helper, dir, 1);
                this.placeBlock(worldIn, helper, this.petal);
            }
            current = this.placeColumn(worldIn, current, 1, rand, 0.0, this.petal);
            lag = 2;
            for (int yyy = 0; yyy < lag; ++yyy) {
                for (int xs2 = -1; xs2 <= 1; ++xs2) {
                    for (int zs2 = -1; zs2 <= 1; ++zs2) {
                        this.placeBlock(worldIn, BlockPos.containing(current.getX() + xs2, current.getY(), current.getZ() + zs2), this.petal);
                    }
                }
                current = current.above();
            }
            this.placeBlock(worldIn, current, this.petal);
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                helper = this.getDirectionRoot(helper.below(2), dir, 2);
                helper = this.placeColumn(worldIn, helper, 4, rand, 0.0, this.petal);
                helper = this.getDirectionRoot(helper, dir, 1);
                helper = this.placeColumn(worldIn, helper, 2, rand, 0.0, this.petal);
                helper = this.getDirectionRoot(helper, dir, 1);
                helper = this.placeColumn(worldIn, helper, 1, rand, 0.0, this.petal);
            }
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                helper = this.getDirectionRoot(helper.below(2), dir, 2);
                helper = this.directionToGrow(helper, (dir + 1) % 4, false);
                helper = this.directionToGrow(helper, (dir + 1) % 4, false);
                this.placeColumn(worldIn, helper, 3, rand, 0.0, this.petal);
            }
        } else {
            return false;
        }
        return true;
    }

    private void placeBlock(Level worldIn, BlockPos pos, BlockState state) {
        this.setBlockAndNotifyAdequately(worldIn, pos, state);
    }

    private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 0: {
                return center.north(times);
            }
            case 1: {
                return center.east(times);
            }
            case 2: {
                return center.south(times);
            }
        }
        return center.west(times);
    }

    private BlockPos placeColumn(Level worldIn, BlockPos pos, int in, RandomSource rand, double extraChance, BlockState state) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            this.placeBlock(worldIn, newPos, state);
            newPos = newPos.above();
            ++current;
            ++times;
        }
        return newPos;
    }

    private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
        atm = atm.above();
        if (sideCurse) {
            switch (choice) {
                case 0: {
                    atm = atm.north();
                    atm = atm.east();
                    break;
                }
                case 1: {
                    atm = atm.north();
                    atm = atm.west();
                    break;
                }
                case 10: {
                    atm = atm.east();
                    atm = atm.north();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.south();
                    break;
                }
                case 20: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 21: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 30: {
                    atm = atm.west();
                    atm = atm.north();
                    break;
                }
                default: {
                    atm = atm.west();
                    atm = atm.south();
                }
            }
            return atm;
        }
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 3: {
                atm = atm.west();
                break;
            }
            default: {
                atm = atm.south();
            }
        }
        return atm;
    }
}

