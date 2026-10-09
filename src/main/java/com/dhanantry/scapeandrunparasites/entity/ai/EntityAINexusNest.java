package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAINexusNest
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityPStationaryArchitect parent;
    private double tickss;

    public EntityAINexusNest(EntityPStationaryArchitect venkrol) {
        this.parent = venkrol;
        this.tickss = 20.0;
    }

    public boolean canUse() {
        return true;
    }

    public void tick() {
    }
}

