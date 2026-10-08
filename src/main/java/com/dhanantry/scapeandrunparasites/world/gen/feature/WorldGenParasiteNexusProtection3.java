package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenParasiteNexusProtection3
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteNexusProtection3(boolean notify, int stage) {
        super(notify, stage);
        this.wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        this.floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        int max;
        int min;
        int radius = 8;
        int steps = 64;
        BlockState pillarBlock = SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FUNGUS));
        BlockState pillarBlock2 = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        List<BlockPos> circle = this.getCirclePoints(posss, radius, steps);
        for (BlockPos pos : circle) {
            min = 1;
            max = 5;
            this.generatePillar(worldIn, pos, worldIn.random.nextInt(max - min + 1) + min, pillarBlock, pillarBlock2);
        }
        pillarBlock = SRPBlocks.ParasiteFog.get().defaultBlockState();
        while (radius > 0) {
            circle = this.getCirclePoints(posss, --radius, steps);
            for (BlockPos pos : circle) {
                min = 7;
                max = 14;
                this.generatePillar(worldIn, pos, worldIn.random.nextInt(max - min + 1) + min, pillarBlock, pillarBlock);
            }
        }
        this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss.below(5), 8, 8, 5, 2, 0);
        this.generateCircle(SRPBlocks.ParasiteFog.get().defaultBlockState(), SRPBlocks.ParasiteFog.get().defaultBlockState(), worldIn, worldIn.random, posss.below(5), 4, 4, 5, 20000000, 0);
        this.replaceCircleGround(worldIn, posss.below(), 8, pillarBlock2);
        return true;
    }
}

