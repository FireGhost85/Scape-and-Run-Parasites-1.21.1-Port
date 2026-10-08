package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockBiomeCore;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.Property;

public class WorldGenParasiteNodeCore
extends WorldGenParasiteGenAbstract {
    private int core;
    private int type;

    public WorldGenParasiteNodeCore(boolean notify, int stage, int biomeType) {
        super(notify);
        this.core = stage;
        this.type = biomeType;
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        switch (this.type) {
            case 3: {
                this.placeHeartHarlequin(worldIn, rand, position);
                break;
            }
            default: {
                this.placeHeartShrouded(worldIn, rand, position);
            }
        }
        this.placeCore(worldIn, position, this.core);
        return true;
    }

    private boolean placeHeartShrouded(Level worldIn, RandomSource rand, BlockPos position) {
        switch (this.core) {
            case 1: {
                int z;
                int zs;
                int xs;
                int yyy;
                BlockPos helper = position;
                BlockPos helperTwo = position;
                BlockPos helperTwo2 = position;
                int down = 5;
                while (down >= 0 && helper.below().getY() >= 1) {
                    helper = helper.below();
                    this.placeTrunk(worldIn, helper);
                    --down;
                    for (yyy = 0; yyy <= 0; ++yyy) {
                        for (xs = -1; xs <= 1; ++xs) {
                            for (zs = -1; zs <= 1; ++zs) {
                                helperTwo = BlockPos.containing(helper.getX() + xs, helper.getY(), helper.getZ() + zs);
                                this.placeTrunk(worldIn, helperTwo);
                                for (z = 0; z <= 3; ++z) {
                                    this.placeTrunk(worldIn, this.directionToGrow(helperTwo, z, false));
                                    if (xs != 0 || zs != 0) continue;
                                    helperTwo2 = helperTwo;
                                    for (int kkk = 0; kkk <= 2; ++kkk) {
                                        helperTwo = this.directionToGrow(helperTwo, z, false);
                                    }
                                    this.placeDirt(worldIn, helperTwo);
                                    helperTwo = helperTwo2;
                                }
                            }
                        }
                    }
                }
                down = 2;
                while (down >= 0 && helper.below().getY() >= 1) {
                    helper = helper.below();
                    this.placeTrunk(worldIn, helper);
                    --down;
                    for (yyy = 0; yyy <= 0; ++yyy) {
                        for (xs = -1; xs <= 1; ++xs) {
                            for (zs = -1; zs <= 1; ++zs) {
                                helperTwo = BlockPos.containing(helper.getX() + xs, helper.getY(), helper.getZ() + zs);
                                this.placeTrunk(worldIn, helperTwo);
                                for (z = 0; z <= 3; ++z) {
                                    if (worldIn.getBlockState(this.directionToGrow(helperTwo, z, false)).getBlock() == SRPBlocks.ParasiteRubbleDense.get()) continue;
                                    this.placeDirt(worldIn, this.directionToGrow(helperTwo, z, false));
                                }
                            }
                        }
                    }
                }
                this.placeDirt(worldIn, position.below());
                worldIn.updateBlockTick(position.below(), worldIn.getBlockState(position.below()).getBlock(), 60, 5);
                helper = position;
                for (int i = 0; i <= 3; ++i) {
                    int o;
                    helper = this.directionToGrow(position, i, false);
                    if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                        this.placeTrunk(worldIn, helper.below());
                    }
                    this.placeTrunk(worldIn, helper);
                    BlockPos rootH = helper;
                    for (o = 0; o < 2; ++o) {
                        helper = o == 0 ? this.directionToGrow(rootH, (i + 1) % 4, false) : this.directionToGrow(rootH, (i + 3) % 4, false);
                        if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                            this.placeTrunk(worldIn, helper.below());
                        }
                        this.placeTrunk(worldIn, helper);
                    }
                    helper = this.directionToGrow(rootH, i, false);
                    if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                        this.placeTrunk(worldIn, helper.below());
                    }
                    this.placeTrunk(worldIn, helper);
                    rootH = helper;
                    for (o = 0; o < 2; ++o) {
                        helper = o == 0 ? this.directionToGrow(rootH, (i + 1) % 4, false) : this.directionToGrow(rootH, (i + 3) % 4, false);
                        if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                            this.placeTrunk(worldIn, helper.below());
                        }
                        this.placeTrunk(worldIn, helper);
                    }
                    helper = this.directionToGrow(rootH, i, false);
                    if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                        this.placeTrunk(worldIn, helper.below());
                    }
                    this.placeTrunk(worldIn, helper);
                }
                position = position.above();
                this.placeTrunk(worldIn, position);
                this.placePeri(worldIn, position);
                position = position.above();
                this.placeTrunk(worldIn, position);
                break;
            }
            case 2: {
                int o;
                int i;
                BlockPos atm;
                position = position.above(2);
                this.placeLiquid(worldIn, position);
                for (int i2 = 0; i2 < 3; ++i2) {
                    this.placePeri(worldIn, position);
                    this.placeLiquid(worldIn, position);
                    position = position.above();
                }
                BlockPos root = atm = position;
                int dir = 0;
                for (i = 0; i <= 3; ++i) {
                    root = atm = this.getDirectionRoot(position.below(), i, 2);
                    for (o = 0; o < 2; ++o) {
                        if (o == 0) {
                            if (rand.nextInt(2) != 0) continue;
                            dir = (i + 1) % 4;
                            atm = this.directionToGrow(root, dir, false);
                            this.placeTen(worldIn, atm);
                            atm = this.directionToGrow(atm, i, false);
                            this.placeTen(worldIn, atm);
                            while (!worldIn.getBlockState(atm.below()).isCollisionShapeFullBlock(worldIn, atm.below()) && atm.below().getY() >= 1) {
                                atm = rand.nextInt(1) == 0 ? this.directionToGrow(atm, i * 10, true) : this.directionToGrow(atm, i, false);
                                atm = this.placeColumn(worldIn, atm, rand.nextInt(2) + 2, rand, 0.0);
                            }
                            this.placeDirt(worldIn, atm.below());
                            continue;
                        }
                        if (rand.nextInt(2) != 0) continue;
                        dir = (i + 3) % 4;
                        atm = this.directionToGrow(root, dir, false);
                        this.placeTen(worldIn, atm);
                        atm = this.directionToGrow(atm, i, false);
                        this.placeTen(worldIn, atm);
                        while (!worldIn.getBlockState(atm.below()).isCollisionShapeFullBlock(worldIn, atm.below()) && atm.below().getY() >= 1) {
                            atm = rand.nextInt(1) == 0 ? this.directionToGrow(atm, i * 10 + 1, true) : this.directionToGrow(atm, i, false);
                            this.placeTen(worldIn, atm);
                            atm = this.placeColumn(worldIn, atm, rand.nextInt(2) + 2, rand, 0.0);
                        }
                        this.placeDirt(worldIn, atm.below());
                    }
                }
                this.placeLiquid(worldIn, position);
                for (i = 0; i < 5; ++i) {
                    this.placePeri(worldIn, position);
                    this.placeLiquid(worldIn, position);
                    position = position.above();
                }
                root = atm = position;
                for (i = 0; i <= 3; ++i) {
                    root = atm = this.getDirectionRoot(position.below(), i, 2);
                    for (o = 0; o < 2; ++o) {
                        if (o == 0) {
                            if (rand.nextInt(2) != 0) continue;
                            dir = (i + 1) % 4;
                            atm = this.directionToGrow(root, dir, false);
                            this.placeTen(worldIn, atm);
                            atm = this.directionToGrow(atm, i, false);
                            this.placeTen(worldIn, atm);
                            while (!worldIn.getBlockState(atm.below()).isCollisionShapeFullBlock(worldIn, atm.below()) && atm.below().getY() >= 1) {
                                atm = rand.nextInt(1) == 0 ? this.directionToGrow(atm, i * 10, true) : this.directionToGrow(atm, i, false);
                                this.placeTen(worldIn, atm);
                                atm = this.placeColumn(worldIn, atm, rand.nextInt(2) + 1, rand, 0.0);
                            }
                            this.placeDirt(worldIn, atm.below());
                            continue;
                        }
                        if (rand.nextInt(2) != 0) continue;
                        dir = (i + 3) % 4;
                        atm = this.directionToGrow(root, dir, false);
                        this.placeTen(worldIn, atm);
                        atm = this.directionToGrow(atm, i, false);
                        this.placeTen(worldIn, atm);
                        while (!worldIn.getBlockState(atm.below()).isCollisionShapeFullBlock(worldIn, atm.below()) && atm.below().getY() >= 1) {
                            atm = rand.nextInt(1) == 0 ? this.directionToGrow(atm, i * 10 + 1, true) : this.directionToGrow(atm, i, false);
                            this.placeTen(worldIn, atm);
                            atm = this.placeColumn(worldIn, atm, rand.nextInt(2) + 1, rand, 0.0);
                        }
                        this.placeDirt(worldIn, atm.below());
                    }
                }
                this.placeLiquid(worldIn, position);
                for (i = 0; i < 5; ++i) {
                    this.placePeri(worldIn, position);
                    this.placeLiquid(worldIn, position);
                    position = position.above();
                }
                this.placeTrunk(worldIn, position);
                break;
            }
        }
        return true;
    }

    private boolean placeHeartBoils(Level worldIn, RandomSource rand, BlockPos position) {
        switch (this.core) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
        }
        return true;
    }

    private boolean placeHeartHarlequin(Level worldIn, RandomSource rand, BlockPos position) {
        switch (this.core) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
        }
        return true;
    }

    private boolean placeHeartDemen(Level worldIn, RandomSource rand, BlockPos position) {
        switch (this.core) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
        }
        return true;
    }

    private void placeTen(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)));
    }

    private void placeTrunk(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.BIOME)));
    }

    private void placeCore(Level worldIn, BlockPos pos, int stage) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.BiomeHeart.get().defaultBlockState().setValue((Property)BlockBiomeCore.ACTIVE, Integer.valueOf(stage)));
    }

    private void placeDirt(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteStain.get().defaultBlockState());
    }

    private void placeLiquid(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.DeadBlood.get().defaultBlockState());
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
            this.placeTen(worldIn, newPos);
            newPos = newPos.below();
            ++current;
            ++times;
        }
        this.placeTen(worldIn, newPos);
        return newPos;
    }

    private void placePeri(Level worldIn, BlockPos position) {
        BlockPos helper = position;
        for (int i = 0; i <= 3; ++i) {
            helper = this.directionToGrow(position, i, false);
            if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                this.placeTrunk(worldIn, helper.below());
            }
            this.placeTrunk(worldIn, helper);
            BlockPos rootH = helper;
            for (int o = 0; o < 2; ++o) {
                helper = o == 0 ? this.directionToGrow(rootH, (i + 1) % 4, false) : this.directionToGrow(rootH, (i + 3) % 4, false);
                if (!worldIn.getBlockState(helper.below()).isCollisionShapeFullBlock(worldIn, helper.below())) {
                    this.placeTrunk(worldIn, helper.below());
                }
                this.placeTrunk(worldIn, helper);
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

