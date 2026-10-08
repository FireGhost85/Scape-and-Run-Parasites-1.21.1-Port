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

public class WorldGenParasiteColonyBS3
extends WorldGenParasiteColonyBase {
    private BlockState floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    private BlockState tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
    private BlockState wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));

    public WorldGenParasiteColonyBS3(boolean notify, int stage) {
        super(notify, stage);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        BlockPos enter = posss;
        this.replaceCircleGround(worldIn, posss.below(), 12, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(2), 8, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        this.replaceCircleGround(worldIn, posss.below(3), 8, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        int radius = 3;
        double theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        int missing = 40;
        this.generateSphere(worldIn, posss, 2, 10, rand, false, 3, false, 2, 1, 2, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL)), SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)), Blocks.AIR.defaultBlockState(), missing);
        posss = enter;
        radius = 6;
        theta = rand.nextDouble() * 2.0 * Math.PI;
        posss = this.getCirclePoint(posss, radius, theta);
        this.generateSphere(worldIn, posss, 2, 10, rand, false, 3, false, 4, 2, 4, SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL)), SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS)), Blocks.AIR.defaultBlockState(), missing);
        return true;
    }
}

