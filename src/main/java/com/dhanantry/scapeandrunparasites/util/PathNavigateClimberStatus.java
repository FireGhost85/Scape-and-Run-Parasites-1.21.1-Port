package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

/** The 1.12 PathNavigateClimber (spider) with the parasite status check of the original. */
public class PathNavigateClimberStatus
extends GroundPathNavigation {
    private BlockPos targetPosition;

    public PathNavigateClimberStatus(Mob entityLivingIn, Level worldIn) {
        super(entityLivingIn, worldIn);
    }

    @Nullable
    @Override
    public Path createPath(BlockPos pos, int accuracy) {
        this.targetPosition = pos;
        return super.createPath(pos, accuracy);
    }

    @Nullable
    @Override
    public Path createPath(Entity entityIn, int accuracy) {
        this.targetPosition = entityIn.blockPosition();
        return super.createPath(entityIn, accuracy);
    }

    @Override
    public boolean moveTo(Entity entityIn, double speedIn) {
        Path path = this.createPath(entityIn, 0);
        if (path != null) {
            return this.moveTo(path, speedIn);
        }
        this.targetPosition = entityIn.blockPosition();
        this.speedModifier = speedIn;
        return true;
    }

    @Override
    public void tick() {
        if (!this.isDone()) {
            super.tick();
            return;
        }
        if (this.targetPosition == null) {
            return;
        }
        if (((EntityParasiteBase)this.mob).getParasiteStatus() > 2) {
            return;
        }
        double d0 = this.mob.getBbWidth() * this.mob.getBbWidth();
        if (!(this.mob.distanceToSqr(Vec3.atCenterOf(this.targetPosition)) >= d0)) {
            this.targetPosition = null;
            return;
        }
        if (this.mob.getY() > (double)this.targetPosition.getY()) {
            BlockPos blockPos = BlockPos.containing(this.targetPosition.getX(), Mth.floor(this.mob.getY()), this.targetPosition.getZ());
            if (!(this.mob.distanceToSqr(Vec3.atCenterOf(blockPos)) >= d0)) {
                this.targetPosition = null;
                return;
            }
        }
        this.mob.getMoveControl().setWantedPosition((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speedModifier);
    }
}
