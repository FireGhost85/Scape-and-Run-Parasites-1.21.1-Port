package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WorldGenParasiteTenFlower
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteTenFlower(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        int i = 12;
        boolean flag = true;
        if (worldIn.getBlockState(position.below()).getBlock() == Blocks.AIR) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
            int dir;
            int zs;
            int xs;
            for (int j = position.getY(); j <= position.getY() + 1 + i; ++j) {
                int k = 4;
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
                            this.placeTrunk(worldIn, BlockPos.containing(current.getX() + xs, current.getY(), current.getZ() + zs));
                        }
                    }
                }
            }
            current = position;
            lag = rand.nextInt(2) + 1;
            for (int yyy = 0; yyy <= lag; ++yyy) {
                for (xs = -1; xs <= 1; ++xs) {
                    for (zs = -1; zs <= 1; ++zs) {
                        this.placeTrunk(worldIn, BlockPos.containing(current.getX() + xs, current.getY(), current.getZ() + zs));
                    }
                }
                current = current.above();
            }
            BlockPos helper = current = current.below();
            int times = 0;
            int dirHelper = 0;
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                helper = this.getDirectionRoot(helper, dir, 2);
                this.placeTrunk(worldIn, helper);
            }
            current = current.above();
            for (dir = 0; dir <= 3; ++dir) {
                helper = current;
                lag = rand.nextInt(2) + 3;
                for (times = 1; times <= lag; ++times) {
                    helper = this.getDirectionRoot(helper, dir, 1);
                    this.placeTrunk(worldIn, helper);
                }
                helper = helper.above();
                this.placeTrunk(worldIn, helper);
                helper = this.getDirectionRoot(helper, dir, 1);
                this.placeTrunk(worldIn, helper);
                BlockPos posHelper0 = helper;
                for (int help = 1; help <= 3; help += 2) {
                    BlockPos posHelper1;
                    int firrr;
                    helper = posHelper0;
                    dirHelper = (dir + help) % 4;
                    helper = this.getDirectionRoot(helper, dirHelper, 1);
                    helper = helper.above();
                    BlockPos posHelper2 = helper = this.placeColumn(worldIn, helper, rand.nextInt(3) + 1, rand, 0.0);
                    int n = firrr = help == 1 && (dir == 0 || dir == 3) ? 0 : 1;
                    if (dir == 2 && help == 3) {
                        firrr = 0;
                    }
                    if (dir == 1 && help == 3) {
                        firrr = 0;
                    }
                    helper = posHelper2;
                    helper = this.directionToGrow(helper.below(), dir * 10 + firrr, true);
                    helper = posHelper1 = (helper = this.placeColumn(worldIn, helper, rand.nextInt(3) + 1, rand, 0.0));
                    helper = this.directionToGrow(helper, dir * 10 + firrr, true);
                    helper = this.placeColumn(worldIn, helper.below(), rand.nextInt(3) + 1, rand, 0.0);
                }
            }
            current = this.placeColumn(worldIn, current, rand.nextInt(4) + 4, rand, 0.0);
            for (dir = 0; dir <= 3; ++dir) {
                helper = this.placeColumn(worldIn, this.getDirectionRoot(current, dir, 1), rand.nextInt(3) + 2, rand, 0.0);
                int glob = 3;
                helper = helper.below(2);
                for (int kkk = 0; kkk <= glob; ++kkk) {
                    if (kkk == 0) {
                        this.placeColumn(worldIn, this.getDirectionRoot(helper, dir, 1), 1, rand, 0.0);
                        helper = helper.above();
                    }
                    helper = this.getDirectionRoot(helper, dir, kkk == 0 ? 2 : 1);
                    helper = this.placeColumn(worldIn, helper, rand.nextInt(4) + 1, rand, 0.0);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void placeTrunk(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)));
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

    private BlockPos placeColumn(Level worldIn, BlockPos pos, int in, RandomSource rand, double extraChance) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            this.placeTrunk(worldIn, newPos);
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

