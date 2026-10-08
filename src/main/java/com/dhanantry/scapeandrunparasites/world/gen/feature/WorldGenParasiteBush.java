package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenParasiteBush
extends WorldGenParasiteGenAbstract {
    private final BlockState tallGrassState;
    private int type;
    private boolean parasite;

    public WorldGenParasiteBush(boolean notify, BlockParasiteBush.EnumType variant, int type) {
        super(notify);
        this.tallGrassState = SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (variant));
        this.type = type;
        this.parasite = true;
    }

    public WorldGenParasiteBush(boolean notify, BlockInfestedBush.EnumType variant, int type) {
        super(notify);
        this.tallGrassState = SRPBlocks.InfestedBush.get().defaultBlockState().setValue(BlockInfestedBush.VARIANT, (variant));
        this.type = type;
        this.parasite = false;
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        if (this.type == 3) {
            this.flowerGen(worldIn, rand, position);
            return true;
        }
        if (this.type == 4) {
            for (int i = 0; i < 128; ++i) {
                BlockPos blockpos = position.offset(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                if (this.parasite) {
                    if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || !SRPBlocks.ParasiteBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                    this.VinGen(worldIn, rand, blockpos);
                    continue;
                }
                if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || !SRPBlocks.InfestedBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                this.VinGen(worldIn, rand, blockpos);
            }
            this.VinGen(worldIn, rand, position);
            return true;
        }
        BlockState iblockstate = worldIn.getBlockState(position);
        while ((iblockstate.getBlock() == Blocks.AIR || iblockstate.is(BlockTags.LEAVES)) && position.getY() > 0) {
            position = position.below();
            iblockstate = worldIn.getBlockState(position);
        }
        switch (this.type) {
            case 1: {
                for (int i = 0; i < 128; ++i) {
                    BlockPos blockpos = position.offset(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                    if (this.parasite) {
                        if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || !SRPBlocks.ParasiteBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                        worldIn.setBlock(blockpos, this.tallGrassState, 2);
                        continue;
                    }
                    if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || !SRPBlocks.InfestedBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                    worldIn.setBlock(blockpos, this.tallGrassState, 2);
                }
                break;
            }
            case 2: {
                for (int i = 0; i < 4; ++i) {
                    BlockPos blockpos = position.offset(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
                    if (this.parasite) {
                        if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || !SRPBlocks.ParasiteBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                        worldIn.setBlock(blockpos, this.tallGrassState, 2);
                        continue;
                    }
                    if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || !SRPBlocks.InfestedBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                    worldIn.setBlock(blockpos, this.tallGrassState, 2);
                }
                break;
            }
        }
        return true;
    }

    public boolean flowerGen(Level worldIn, RandomSource rand, BlockPos position) {
        for (int i = 0; i < 64; ++i) {
            BlockPos blockpos = position.offset(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (this.parasite) {
                if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || blockpos.getY() >= 255 || !SRPBlocks.ParasiteBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
                worldIn.setBlock(blockpos, this.tallGrassState, 2);
                continue;
            }
            if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR || blockpos.getY() >= 255 || !SRPBlocks.InfestedBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || !this.checkFloor(worldIn, blockpos)) continue;
            worldIn.setBlock(blockpos, this.tallGrassState, 2);
        }
        return true;
    }

    public boolean VinGen(Level worldIn, RandomSource rand, BlockPos position) {
        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos = position.offset(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.getBlockState(blockpos).getBlock() != Blocks.AIR) continue;
            int j = 1 + rand.nextInt(rand.nextInt(3) + 3);
            for (int k = 0; k < j; ++k) {
                if (this.parasite) {
                    if (!SRPBlocks.ParasiteBush.get().defaultBlockState().canSurvive(worldIn, blockpos.above(k)) || worldIn.getBlockState(blockpos.above(k)).getBlock() != Blocks.AIR) continue;
                    worldIn.setBlock(blockpos.above(k), this.tallGrassState, 2);
                    continue;
                }
                if (!SRPBlocks.InfestedBush.get().defaultBlockState().canSurvive(worldIn, blockpos) || worldIn.getBlockState(blockpos.above(k)).getBlock() != Blocks.AIR) continue;
                worldIn.setBlock(blockpos.above(k), this.tallGrassState, 2);
            }
        }
        return true;
    }

    private boolean checkFloor(Level worldIn, BlockPos position) {
        return worldIn.getBlockState(position.below()).isCollisionShapeFullBlock(worldIn, position.below());
    }
}

