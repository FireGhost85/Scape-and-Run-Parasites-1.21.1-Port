package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class EntityAIBlockResidue
extends Goal {
    private final EntityParasiteBase parent;
    private int ticks = 0;
    private int range;
    private int blockBreakCounter;

    public EntityAIBlockResidue(EntityParasiteBase ghast, int range) {
        this.parent = ghast;
        this.range = range;
        this.blockBreakCounter = 160;
    }

    public boolean canUse() {
        return this.parent.getTarget() == null && !this.parent.isInWater() && this.parent.getGeneMod(8);
    }

    public void stop() {
        this.parent.setParasiteStatus(0);
        this.blockBreakCounter = 160;
    }

    public void tick() {
        if (this.blockBreakCounter > 0) {
            if (this.parent.level().random.nextInt(5) == 0) {
                --this.blockBreakCounter;
            }
        } else {
            --this.blockBreakCounter;
            if (this.blockBreakCounter == -1) {
                this.parent.setParasiteStatus(25);
                this.parent.getNavigation().stop();
                this.parent.playSound(SRPSounds.ADAPTED_V.get(), 2.0f, 1.0f);
            }
            if (this.blockBreakCounter == -40) {
                this.parent.playSound(SRPSounds.ADAPTED_V.get(), 2.0f, 1.0f);
            }
            this.parent.particleStatus((byte)13);
            if (this.blockBreakCounter == -60) {
                double i1 = Mth.floor((double)(this.parent.getY() + 0.1));
                double l1 = this.parent.getX();
                double i2 = this.parent.getZ();
                Level world = this.parent.level();
                if (this.blockBreakCounter == -63) {
                    // empty if block
                }
                for (int k2 = -1 * this.range; k2 <= 1 * this.range; ++k2) {
                    for (int l2 = -1 * this.range; l2 <= 1 * this.range; ++l2) {
                        double i3 = l1 + (double)k2;
                        double l = i2 + (double)l2;
                        BlockPos blockpos = BlockPos.containing(i3, i1, l);
                        Block block = this.parent.level().getBlockState(blockpos).getBlock();
                        Block blockDown = this.parent.level().getBlockState(blockpos.below()).getBlock();
                        if (block != Blocks.AIR || blockDown == Blocks.AIR || !world.getBlockState(blockpos.below()).isCollisionShapeFullBlock(world, blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.parent.level().random.nextInt(2) != 0) continue;
                        this.parent.level().setBlockAndUpdate(blockpos, BlockIds.legacyState(SRPBlocks.InfestRemain.get(), 1));
                    }
                }
            }
            if (this.blockBreakCounter == -100) {
                this.parent.setParasiteStatus(0);
                this.blockBreakCounter = 200;
            }
        }
    }
}

