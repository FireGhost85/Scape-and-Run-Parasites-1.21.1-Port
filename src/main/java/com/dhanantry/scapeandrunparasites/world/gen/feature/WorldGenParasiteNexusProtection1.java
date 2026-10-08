package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class WorldGenParasiteNexusProtection1
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteNexusProtection1(boolean notify, int stage) {
        super(notify, stage);
        this.wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        this.floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        this.generateSphere(worldIn, posss, 3, 3, rand, false, 6, false, 1, 1, 5, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)), SRPBlocks.ParasiteFog.get().defaultBlockState(), 2);
        this.generateCircle(SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)), SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FLESH)), worldIn, worldIn.random, posss.below(5), 6, 6, 5, 2, 0);
        this.generateCircle(SRPBlocks.ParasiteFog.get().defaultBlockState(), SRPBlocks.ParasiteFog.get().defaultBlockState(), worldIn, worldIn.random, posss.below(5), 4, 4, 5, 20000000, 0);
        this.replaceCircleGround(worldIn, posss, 8, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER)));
        return true;
    }
}

