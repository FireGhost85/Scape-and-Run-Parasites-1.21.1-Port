package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenParasiteNexusProtection2
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteNexusProtection2(boolean notify, int stage) {
        super(notify, stage);
        this.wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        this.floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        int radius = 6;
        int height = 5;
        BlockState pillarBlock = Blocks.STONE.defaultBlockState();
        this.generateRandomPillar(worldIn, posss.below(1), radius, height, pillarBlock);
        radius = 9;
        height = 9;
        pillarBlock = Blocks.STONE.defaultBlockState();
        this.generateRandomPillar(worldIn, posss.below(1), radius, height, pillarBlock);
        return true;
    }

    public void generateRandomPillar(Level worldIn, BlockPos center, int radius, int height, BlockState blockState) {
        RandomSource rand = RandomSource.create();
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        BlockPos basePos = this.getCirclePoint(center, radius, theta);
        if ((basePos = ParasiteEventEntity.getFloor(worldIn, basePos, 7)) == null) {
            return;
        }
        basePos = basePos.below();
        String out = "beckon_";
        int outt = worldIn.random.nextInt(4) + 2;
        out = out + outt + "x" + outt;
        switch (outt) {
            case 2: {
                out = out + "_1";
                break;
            }
            case 3: {
                switch (worldIn.random.nextInt(5) + 1) {
                    case 1: {
                        out = out + "_1";
                        break;
                    }
                    case 2: {
                        out = out + "_2";
                        break;
                    }
                    case 3: {
                        out = out + "_3";
                        break;
                    }
                    case 4: {
                        out = out + "_4";
                        break;
                    }
                    case 5: {
                        out = out + "_5";
                    }
                }
                break;
            }
            case 4: {
                switch (worldIn.random.nextInt(2) + 1) {
                    case 1: {
                        out = out + "_1";
                        break;
                    }
                    case 2: {
                        out = out + "_2";
                    }
                }
                break;
            }
            case 5: {
                out = out + "_1";
            }
        }
        System.out.println("ds aa222 " + out);
        WorldGenCustomStructures.generateInPosition(new WorldGenStructure(out), rand, worldIn, basePos, 0, 0, 0);
    }
}

