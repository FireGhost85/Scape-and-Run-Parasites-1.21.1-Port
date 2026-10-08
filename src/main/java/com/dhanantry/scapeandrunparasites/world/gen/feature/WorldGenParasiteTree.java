package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteTreeAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WorldGenParasiteTree
extends WorldGenParasiteTreeAbstract {
    public WorldGenParasiteTree(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        BlockPos current;
        int i = 20;
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
            int first = rand.nextInt(4);
            current = this.directionToGrow(current, first, false);
            this.placeTrunk(worldIn, current);
            while (!worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && current.below().getY() >= 1) {
                current = current.below();
                this.placeTrunk(worldIn, current);
            }
            current = position;
            current = this.directionToGrow(current, ++first, false);
            this.placeTrunk(worldIn, current);
            while (!worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && current.below().getY() >= 1) {
                current = current.below();
                this.placeTrunk(worldIn, current);
            }
            current = position;
            while (!worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && current.below().getY() >= 1) {
                current = current.below();
                this.placeTrunk(worldIn, current);
            }
            current = position;
            current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, -1.0);
            first = rand.nextInt(4);
            current = this.directionToGrow(current, first, false);
            current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, 0.3);
            if (rand.nextDouble() <= 0.3) {
                return true;
            }
            current = this.directionToGrow(current, rand.nextInt(4), false);
            current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, 0.3);
            if (rand.nextDouble() <= 0.3) {
                return true;
            }
        } else {
            return false;
        }
        current = this.directionToGrow(current, rand.nextInt(4), false);
        current = this.placeColumn(worldIn, current, rand.nextInt(3) + 5, rand, 0.3);
        return true;
    }

    private void placeTrunk(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.TREE)));
    }

    private void placeTrunk(Level worldIn, BlockPos pos, int direction) {
        switch (direction) {
            case 1: {
                this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.TREE)));
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
        }
    }

    private void placeCanister(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteCanister.get().defaultBlockState().setValue(BlockParasiteCanister.VARIANT, (BlockParasiteCanister.EnumType.SAC)));
    }

    private BlockPos placeColumn(Level worldIn, BlockPos pos, int in, RandomSource rand, double extraChance) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        if (worldIn.getBlockState(pos.below()).getBlock() == Blocks.AIR && rand.nextDouble() <= 0.3 + extraChance) {
            this.placeCanister(worldIn, pos.below());
        }
        while (current < atm + in) {
            if (times == 2 && extraChance != -1.0) {
                int btwo;
                int bone = rand.nextInt(4);
                if (bone == (btwo = rand.nextInt(4))) {
                    ++bone;
                }
                this.addBranchs(worldIn, newPos, rand, rand.nextInt(3) + 1, bone);
                this.addBranchs(worldIn, newPos, rand, rand.nextInt(3) + 1, btwo);
            }
            this.placeTrunk(worldIn, newPos);
            newPos = newPos.above();
            ++current;
            ++times;
        }
        this.placeTrunk(worldIn, newPos);
        return newPos;
    }

    private void addBranchs(Level worldIn, BlockPos pos, RandomSource rand, int size, int direction) {
        pos = this.directionToGrow(pos, direction, false);
        this.placeTrunk(worldIn, pos);
        pos = pos.above();
        pos = this.directionToGrow(pos, direction, false);
        int curve = direction * 10 + rand.nextInt(2);
        for (int current = 0; current < size; ++current) {
            this.placeTrunk(worldIn, pos);
            if (rand.nextDouble() <= 0.25) {
                this.placeCanister(worldIn, pos.below());
            }
            pos = rand.nextDouble() <= 0.5 ? this.directionToGrow(pos, direction, false) : this.directionToGrow(pos, curve, true);
        }
        pos = pos.below();
        this.placeTrunk(worldIn, pos);
        if (rand.nextDouble() <= 0.25) {
            this.placeCanister(worldIn, pos.below());
        }
        if (rand.nextDouble() <= 0.5) {
            pos = rand.nextDouble() <= 0.5 ? this.directionToGrow(pos.below(), direction, false) : this.directionToGrow(pos.below(), curve, true);
            this.placeTrunk(worldIn, pos);
            if (rand.nextDouble() <= 0.25) {
                this.placeCanister(worldIn, pos.below());
            }
        }
    }

    private BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
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
                case 30: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 31: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 20: {
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
            case 2: {
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

