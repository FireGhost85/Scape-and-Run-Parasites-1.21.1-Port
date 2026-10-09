package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class EntityAIAvoidOrAttack
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private int tickss;
    private float light;
    private int rangexz;
    private int rangey;

    public EntityAIAvoidOrAttack(EntityParasiteBase in, float lightR, int xz, int y) {
        this.parent = in;
        this.tickss = 20;
        this.light = lightR;
        this.rangexz = xz;
        this.rangey = y;
        this.parent.setWorkTask(false);
    }

    public boolean canUse() {
        --this.tickss;
        if (this.tickss < 0) {
            this.tickss = 20;
            return this.parent.getParasiteFollowing() == null;
        }
        return false;
    }

    public void tick() {
        if (this.parent.hasEffect(SRPPotions.RAGE_E)) {
            this.parent.setWorkTask(true);
            return;
        }
        if (this.parent.getLightLevelDependentMagicValue() >= this.light) {
            boolean flag = true;
            AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate((double)this.rangexz, (double)this.rangey, (double)this.rangexz);
            List<? extends EntityParasiteBase> moblist = this.parent.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !mob.isAlive()) continue;
                if (flag) {
                    flag = false;
                    this.parent.setWorkTask(true);
                }
                if (this.parent.getParasiteFollowing() == null) {
                    if (mob.getParasiteFollowing() == null) {
                        this.parent.setParasiteToFollow(mob);
                    } else if (mob.getParasiteFollowing().getParasiteType() >= 11) {
                        this.parent.setParasiteToFollow(mob.getParasiteFollowing());
                    }
                }
                mob.setWorkTask(true);
            }
            if (flag) {
                this.parent.setWorkTask(false);
            }
        } else {
            this.parent.setWorkTask(true);
        }
    }
}

