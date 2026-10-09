package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class EntityAIGetFollowers
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private int version;
    private int searchRange;

    public EntityAIGetFollowers(EntityParasiteBase in, int mobVersion, int range) {
        this.parent = in;
        this.version = mobVersion;
        this.searchRange = range;
    }

    public boolean canUse() {
        return this.parent.tickCount % 20 == 0 && this.parent.getParasiteFollowing() == null && this.parent.getTarget() == null;
    }

    public void tick() {
        AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate((double)this.searchRange, 2.0, (double)this.searchRange);
        List<? extends EntityParasiteBase> moblist = this.parent.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        block0 : switch (this.version) {
            case 1: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !mob.isAlive() || mob.getParasiteType() >= 31) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 10) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
            case 2: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !mob.isAlive() || mob.getParasiteType() >= 41) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 30) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
            case 3: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !mob.isAlive() || mob.getParasiteType() >= 41) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 40) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
            case 4: {
                for (EntityParasiteBase mob : moblist) {
                    if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !mob.isAlive() || mob.getParasiteType() >= 61) continue;
                    if (mob.getParasiteFollowing() == null) {
                        mob.setParasiteToFollow(this.parent);
                        break block0;
                    }
                    if (mob.getParasiteFollowing().getParasiteType() > 60) continue;
                    mob.setParasiteToFollow(this.parent);
                    break block0;
                }
                break;
            }
        }
    }
}

