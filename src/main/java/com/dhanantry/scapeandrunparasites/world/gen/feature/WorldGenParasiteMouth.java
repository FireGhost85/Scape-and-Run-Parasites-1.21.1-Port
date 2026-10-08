package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;

public class WorldGenParasiteMouth
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteMouth(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        this.genMouth(worldIn, rand, position);
        int rana = rand.nextInt(7) + 1;
        if (rand.nextBoolean()) {
            rana *= -1;
        }
        int x = rana + position.getX();
        int z = position.getZ();
        if (x == 7) {
            rana = rand.nextInt(7) + 1;
            if (rand.nextBoolean()) {
                rana *= -1;
            }
            z += rana;
        } else {
            rana = 7;
            if (rand.nextBoolean()) {
                rana *= -1;
            }
            z += rana;
        }
        BlockPos newp = BlockPos.containing(x, position.getY(), z);
        newp = ParasiteEventEntity.getFloor(worldIn, newp, 5);
        if (newp != null) {
            this.genMouth(worldIn, rand, newp);
        }
        return true;
    }

    private void genMouth(Level worldIn, RandomSource rand, BlockPos position) {
        for (int yyy = 0; yyy <= 2; ++yyy) {
            for (int xs = -2; xs <= 2; ++xs) {
                for (int zs = -2; zs <= 2; ++zs) {
                    if (worldIn.getBlockState(BlockPos.containing(position.getX() + xs, position.getY() + yyy, position.getZ() + zs)).getBlock() == Blocks.AIR) continue;
                    return;
                }
            }
        }
        BlockPos helper = position;
        this.placePeri(worldIn, position, 1, 2);
        this.placePeri(worldIn, position, 2, 1);
        BlockPos current = position;
        int i = 7;
        while (!(worldIn.getBlockState(current.below()).isCollisionShapeFullBlock(worldIn, current.below()) && i < 0 || current.below().getY() <= 2)) {
            --i;
            this.setBlockAndNotifyAdequately(worldIn, current, Blocks.AIR.defaultBlockState());
            current = current.below();
            this.placePeri(worldIn, current, 5, 1);
        }
        this.placeFlesh(worldIn, current);
    }

    private void placeMouth(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteMouth.get().defaultBlockState());
    }

    private void placeFlesh(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)));
    }

    private void placeFleshStair(Level worldIn, BlockPos pos, int direction, boolean bottom, int times) {
        switch (direction) {
            case 0: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.north(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.SOUTH).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    this.placeColumn(worldIn, pos.north(times).below(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.north(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.SOUTH).setValue((Property)StairBlock.HALF, Half.TOP));
                break;
            }
            case 1: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.east(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.WEST).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    this.placeColumn(worldIn, pos.east(times).below(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.east(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.WEST).setValue((Property)StairBlock.HALF, Half.TOP));
                break;
            }
            case 3: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.west(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.EAST).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    this.placeColumn(worldIn, pos.west(times).below(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.west(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.EAST).setValue((Property)StairBlock.HALF, Half.TOP));
                break;
            }
            default: {
                if (bottom) {
                    this.setBlockAndNotifyAdequately(worldIn, pos.south(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.NORTH).setValue((Property)StairBlock.HALF, Half.BOTTOM));
                    this.placeColumn(worldIn, pos.south(times).below(), 2, null, 0.0);
                    break;
                }
                this.setBlockAndNotifyAdequately(worldIn, pos.south(times), SRPBlocks.ParasiteStainFleshStair.get().defaultBlockState().setValue((Property)StairBlock.FACING, Direction.NORTH).setValue((Property)StairBlock.HALF, Half.TOP));
            }
        }
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
        int current = pos.getY();
        int currentY = pos.getY();
        int atm = current;
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in && currentY > 2) {
            --currentY;
            this.placeFlesh(worldIn, newPos);
            newPos = newPos.below();
            ++current;
            ++times;
        }
        return newPos;
    }

    private void placePeri(Level worldIn, BlockPos position, int type, int area) {
        BlockPos helper = position;
        for (int i = 0; i <= 3; ++i) {
            helper = this.getDirectionRoot(position, i, area);
            this.placeColumn(worldIn, helper.below(), 2, null, 0.0);
            if (type == 5) {
                this.placeFlesh(worldIn, helper);
            } else if (type == 1) {
                this.placeFleshStair(worldIn, helper, i, true, 0);
            } else {
                this.placeMouth(worldIn, helper);
            }
            BlockPos rootH = helper;
            for (int o = 0; o < 2; ++o) {
                helper = o == 0 ? this.directionToGrow(rootH, (i + 1) % 4, false) : this.directionToGrow(rootH, (i + 3) % 4, false);
                this.placeColumn(worldIn, helper.below(), 2, null, 0.0);
                if (type == 5) {
                    this.placeFlesh(worldIn, helper);
                    continue;
                }
                if (type == 1) {
                    this.placeFleshStair(worldIn, helper, i, true, 0);
                    continue;
                }
                this.placeMouth(worldIn, helper);
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
                    atm = atm.south();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.north();
                    break;
                }
                case 20: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 21: {
                    atm = atm.south();
                    atm = atm.east();
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

    private void positionSides(int choice) {
    }
}

