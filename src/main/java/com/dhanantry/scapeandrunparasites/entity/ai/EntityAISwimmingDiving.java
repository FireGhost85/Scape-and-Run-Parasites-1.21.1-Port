package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;

public class EntityAISwimmingDiving
extends Goal {
    private final Mob parent;
    private double yMotion;

    public EntityAISwimmingDiving(Mob parentIn, double y) {
        this.parent = parentIn;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP));
        this.yMotion = y;
        if (parentIn.getNavigation() instanceof GroundPathNavigation) {
            ((GroundPathNavigation)parentIn.getNavigation()).setCanFloat(true);
        } else if (parentIn.getNavigation() instanceof FlyingPathNavigation) {
            ((FlyingPathNavigation)parentIn.getNavigation()).setCanFloat(true);
        }
    }

    public boolean canUse() {
        if (this.parent.isInWater() || this.parent.isInLava()) {
            LivingEntity target;
            if (this.parent.getTarget() != null && ((target = this.parent.getTarget()).isInWater() || target.isInLava()) && target.distanceToSqr(this.parent.getX(), target.getY(), this.parent.getZ()) < 25.0 && target.getY() - this.parent.getY() < -1.0) {
                Mot.addY(this.parent, -(this.yMotion));
                return false;
            }
            return true;
        }
        return false;
    }

    public void tick() {
        if (this.parent.getRandom().nextFloat() < 0.8f) {
            this.parent.getJumpControl().jump();
        }
    }
}

