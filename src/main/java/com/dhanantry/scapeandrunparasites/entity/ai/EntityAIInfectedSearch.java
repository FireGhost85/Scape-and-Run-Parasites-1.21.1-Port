package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanMelt;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityAIInfectedSearch
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private Level world;
    private int parentID;
    private double moveSpeed;
    private EntityParasiteBase[] array = new EntityParasiteBase[4];
    private int delay;

    public EntityAIInfectedSearch(EntityParasiteBase animal, double speedIn) {
        this.parent = animal;
        this.world = animal.level();
        this.moveSpeed = speedIn;
        this.parentID = animal.getId();
    }

    public boolean canUse() {
        if (this.parent.tickCount % 40 == 0) {
            return false;
        }
        if (this.parent.getTarget() != null) {
            return false;
        }
        return this.LeshCount();
    }

    public void stop() {
    }

    public void tick() {
        if (this.parent.tickCount % 20 != 0 || this.parent.getKillC() < 0.0) {
            return;
        }
        int count = 0;
        AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate(this.parent.getAttribute(Attributes.FOLLOW_RANGE).getValue());
        List<? extends EntityParasiteBase> moblist = this.parent.level().getEntitiesOfClass(EntityPInfected.class, axisalignedbb);
        List<? extends EntityParasiteBase> moblist2 = this.parent.level().getEntitiesOfClass(EntityLesh.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist2) {
            if (mob == null) continue;
            ++count;
        }
        if (count >= 3) {
            EntityCanMelt meh = (EntityCanMelt)(this.parent);
            meh.melt();
            return;
        }
        for (EntityParasiteBase mob : moblist) {
            EntityCanMelt meh;
            if (mob == null || mob == this.parent || !(mob instanceof EntityCanMelt) || !this.parent.hasLineOfSight((Entity)mob) || mob.getTarget() != null || (meh = (EntityCanMelt)(mob)).isMelting()) continue;
            meh.melt();
            if (++count < 3) continue;
            EntityCanMelt moh = (EntityCanMelt)(this.parent);
            moh.melt();
            return;
        }
        EntityCanMelt out = (EntityCanMelt)(this.parent);
        out.melt();
    }

    protected boolean LeshCount() {
        int countL = 0;
        int countI = 0;
        boolean more = false;
        AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate(this.parent.getAttribute(Attributes.FOLLOW_RANGE).getValue());
        List<? extends EntityParasiteBase> moblist = this.parent.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            EntityCanMelt meh;
            if (mob == null) continue;
            if (mob instanceof EntityLesh && this.parent.hasLineOfSight((Entity)mob)) {
                countL += ((EntityLesh)mob).getTimes();
            }
            if (mob == this.parent || !this.parent.hasLineOfSight((Entity)mob) || !(mob instanceof EntityCanMelt) || mob.getTarget() != null || (meh = (EntityCanMelt)(mob)).isMelting()) continue;
            ++countI;
        }
        if (countL >= 1 && countL <= 3) {
            EntityCanMelt meh = (EntityCanMelt)(this.parent);
            meh.melt();
            return false;
        }
        return countI >= 3;
    }
}

