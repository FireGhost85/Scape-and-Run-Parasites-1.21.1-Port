package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;

public class WorldGenParasiteSpine
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteSpine(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        int atmm;
        BlockPos twoCurrent;
        int partner;
        BlockPos current;
        int i = 28;
        boolean flag = true;
        if (worldIn.getBlockState(position.below()).getBlock() == Blocks.AIR) {
            return false;
        }
        if (position.getY() >= 1 && position.getY() + i + 1 <= worldIn.getHeight()) {
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
            current = position;
            partner = rand.nextInt(4);
            twoCurrent = this.directionToGrow(current, partner, false);
            atmm = (partner + 1) % 4;
            while (!worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && current.below().getY() >= 1) {
                current = current.below();
                twoCurrent = twoCurrent.below();
                this.placeTrunk(worldIn, current);
                this.placeTrunk(worldIn, twoCurrent);
                for (int z = 0; z <= 3; ++z) {
                    this.placeTrunk(worldIn, this.directionToGrow(current, z, false));
                    this.placeTrunk(worldIn, this.directionToGrow(twoCurrent, z, false));
                }
            }
        } else {
            return false;
        }
        current = position;
        twoCurrent = this.directionToGrow(current, partner, false);
        this.placeStair(worldIn, current, atmm, true, 1, false);
        current = this.placeColumn(worldIn, current, 4, rand, partner, 27, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        this.placeColumn(worldIn, this.directionToGrow(position, (atmm + 2) % 4, false), 5, rand, partner, -1, false);
        this.placeStair(worldIn, twoCurrent, atmm, true, 1, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, 4, rand, (partner + 2) % 4, 27, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        this.placeColumn(worldIn, this.directionToGrow(this.directionToGrow(position, partner, false), (atmm + 2) % 4, false), 5, rand, partner, -1, false);
        this.placeStair(worldIn, twoCurrent.below(4), partner, true, 1, false);
        this.placeStair(worldIn, current.below(4), (partner + 2) % 4, true, 1, false);
        current = this.directionToGrow(current.above(), atmm, true);
        current = this.placeColumn(worldIn, current, 2, rand, partner, 14, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.above(), atmm, true);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, 2, rand, (partner + 2) % 4, 14, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        int randomG = rand.nextInt(4) + 3;
        current = this.directionToGrow(current.above(), atmm, true);
        this.placeStair(worldIn, current.below(), (atmm + 2) % 4, false, 0, false);
        current = this.placeColumn(worldIn, current, randomG, rand, partner, 7, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.above(), atmm, true);
        this.placeStair(worldIn, twoCurrent.below(), (atmm + 2) % 4, false, 0, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 7, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        randomG = rand.nextInt(5) + 6;
        current = this.directionToGrow(current.above(), atmm, true);
        this.placeStair(worldIn, current.below(), (atmm + 2) % 4, false, 0, false);
        current = this.placeColumn(worldIn, current, randomG, rand, partner, 2, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.above(), atmm, true);
        this.placeStair(worldIn, twoCurrent.below(), (atmm + 2) % 4, false, 0, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 2, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        randomG = rand.nextInt(5) + 6;
        current = this.directionToGrow(current.above(), atmm, true);
        this.placeStair(worldIn, current.below(), (atmm + 2) % 4, false, 0, false);
        current = this.placeColumn(worldIn, current, randomG, rand, partner, 0, true);
        this.placeStair(worldIn, current, atmm, true, 0, false);
        twoCurrent = this.directionToGrow(twoCurrent.above(), atmm, true);
        this.placeStair(worldIn, twoCurrent.below(), (atmm + 2) % 4, false, 0, false);
        twoCurrent = this.placeColumn(worldIn, twoCurrent, randomG, rand, (partner + 2) % 4, 0, true);
        this.placeStair(worldIn, twoCurrent, atmm, true, 0, false);
        return true;
    }

    private void placeStair(Level worldIn, BlockPos pos, int direction, boolean bottom, int times, boolean slab) {
        switch (direction) {
            case 0: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.north(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.SOUTH).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.north(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.SOUTH).setValue((Property)StairBlock.HALF, Half.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.north(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.get().defaultBlockState().setValue((Property)SlabBlock.TYPE, SlabType.TOP));
                break;
            }
            case 1: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.east(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.WEST).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.east(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.WEST).setValue((Property)StairBlock.HALF, Half.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.east(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.get().defaultBlockState().setValue((Property)SlabBlock.TYPE, SlabType.TOP));
                break;
            }
            case 3: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.west(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.EAST).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.west(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.EAST).setValue((Property)StairBlock.HALF, Half.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.west(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.get().defaultBlockState().setValue((Property)SlabBlock.TYPE, SlabType.TOP));
                break;
            }
            default: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.south(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.NORTH).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.south(times), SRPBlocks.ParasiteRubbleBoneStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.NORTH).setValue((Property)StairBlock.HALF, Half.TOP));
                if (!slab) break;
                this.setBlockAndNotifyAdequately(worldIn, pos.south(times + 1), SRPBlocks.ParasiteRubbleSlabHalf.get().defaultBlockState().setValue((Property)SlabBlock.TYPE, SlabType.TOP));
            }
        }
    }

    private void placeTrunk(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteRubble.get().defaultBlockState());
    }

    private BlockPos placeColumn(Level worldIn, BlockPos pos, int in, RandomSource rand, int direction, int slabs, boolean stair) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            if (times % 2 == 0 && stair) {
                if (slabs > 20) {
                    this.placeStair(worldIn, newPos, direction, false, 2, false);
                    slabs -= 10;
                } else if (slabs > 10) {
                    this.placeStair(worldIn, newPos, direction, false, 2, false);
                    this.placeStair(worldIn, newPos.above(), (direction + 2) % 4, true, -2, false);
                    this.placeStair(worldIn, newPos.above(), direction, false, 3, (slabs -= 10) > 5);
                } else {
                    this.placeStair(worldIn, newPos, direction, false, 2, slabs > 0);
                    --slabs;
                }
            }
            this.placeTrunk(worldIn, newPos);
            newPos = newPos.above();
            ++current;
            ++times;
        }
        return newPos;
    }

    private BlockPos directionToGrow(BlockPos atm, int choice, boolean reverse) {
        if (reverse) {
            switch (choice) {
                case 0: {
                    atm = atm.south();
                    break;
                }
                case 1: {
                    atm = atm.west();
                    break;
                }
                case 3: {
                    atm = atm.east();
                    break;
                }
                default: {
                    atm = atm.north();
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

