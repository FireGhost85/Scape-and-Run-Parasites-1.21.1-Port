package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;

public class EntityAIFlightLimits
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private int limit;
    private boolean fLimit;

    public EntityAIFlightLimits(EntityParasiteBase in, int limit, boolean flighLimit) {
        this.parent = in;
        this.limit = limit;
        this.fLimit = flighLimit;
    }

    public boolean canUse() {
        return true;
    }

    public void tick() {
        if (this.fLimit) {
            if (this.parent.getTarget() != null) {
                if (this.parent.getTarget().getY() + (double)this.limit > this.parent.getY()) {
                    Mot.addY(this.parent, -(0.04));
                }
            } else {
                boolean ground = this.howMuchNeg(this.parent.blockPosition().below(1), 1);
                if (ground) {
                    Mot.addY(this.parent, -(0.04));
                }
            }
        } else {
            boolean ground = this.howMuchPos(this.parent.blockPosition().below(1), 1);
            if (ground) {
                Mot.addY(this.parent, 0.04);
            }
        }
    }

    private boolean howMuchNeg(BlockPos y, int count) {
        if (y.getY() < 1) {
            return false;
        }
        if (count <= this.limit) {
            if (this.parent.level().getBlockState(y).getBlock() == Blocks.AIR) {
                return this.howMuchNeg(y.below(), ++count);
            }
            return false;
        }
        if (count > this.limit) {
            return true;
        }
        return true;
    }

    private boolean howMuchPos(BlockPos y, int count) {
        if (y.getY() < 1) {
            return false;
        }
        if (count <= this.limit) {
            if (this.parent.level().getBlockState(y).getBlock() == Blocks.AIR) {
                return this.howMuchPos(y.below(), ++count);
            }
            return true;
        }
        return count <= this.limit;
    }
}

