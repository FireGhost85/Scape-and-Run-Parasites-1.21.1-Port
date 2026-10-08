package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WorldGenParasiteBall
extends WorldGenParasiteGenAbstract {
    public WorldGenParasiteBall(boolean notify) {
        super(notify);
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        int i = 12;
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
            int extra = rand.nextInt(4);
            BlockPos current = position.above(2 + extra);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure("ball"), RandomSource.create(), worldIn, current, 3, 0, 3);
            BlockPos center = BlockPos.containing(position.getX(), position.getY(), position.getZ());
            boolean skip = true;
            for (int z = 1; z <= 4; ++z) {
                if (rand.nextDouble() <= 0.5 && skip) {
                    skip = false;
                    continue;
                }
                BlockPos root = center.above(3 + extra);
                root = this.getDirectionRoot(root, z, 3);
                this.placeThin(worldIn, root);
                root = root.below();
                this.placeThin(worldIn, root);
                root = this.getDirectionRoot(root, z, 1);
                this.placeThin(worldIn, root);
                while (!worldIn.getBlockState(root.below()).isCollisionShapeFullBlock(worldIn, root.below())) {
                    root = root.below();
                    this.placeThin(worldIn, root);
                }
                for (int j = 1; j <= 4; ++j) {
                    BlockPos roots = this.getDirectionRoot(root, j, 1);
                    if (worldIn.getBlockState(roots).isCollisionShapeFullBlock(worldIn, roots)) continue;
                    this.placeThin(worldIn, roots);
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private void placeThin(Level worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() == Blocks.AIR) {
            this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteThin.get().defaultBlockState());
        } else {
            this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, (BlockParasiteTrunk.EnumType.TREE)));
        }
    }

    private BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 1: {
                return center.north(times);
            }
            case 2: {
                return center.east(times);
            }
            case 3: {
                return center.south(times);
            }
        }
        return center.west(times);
    }
}

