package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAINexusNest
extends Goal {
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

