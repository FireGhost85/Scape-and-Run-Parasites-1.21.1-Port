package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class EntityAIFlightAttack
extends Goal {
    private final EntityParasiteBase parent;
    private int delay;
    private double distance;
    private boolean xRay;
    private int fromF;

    public EntityAIFlightAttack(EntityParasiteBase in, double distance) {
        this.parent = in;
        this.delay = 0;
        this.distance = distance * distance;
    }

    public EntityAIFlightAttack(EntityParasiteBase in, double distance, boolean ignoreSee, int floorS) {
        this(in, distance);
        this.xRay = ignoreSee;
        this.fromF = floorS;
    }

    public boolean canUse() {
        return this.parent.getWait() == 0 && this.parent.srpTicks <= 10;
    }

    public void tick() {
        this.parent.reduceIdleChance(2, 1);
        if (this.parent.getTarget() != null) {
            if (this.parent.getTarget() instanceof EntityParasiteBase) {
                this.parent.setTarget(null);
                this.delay = 0;
                this.parent.setParasiteStatus(0);
                if (this.parent.getParasiteType() != 61) {
                    this.parent.getMoveControl().setWantedPosition(this.parent.getX(), this.parent.getY(), this.parent.getZ(), 1.0);
                }
                return;
            }
            if (this.parent.level().getEntity(this.parent.getTarget().getId()) == null) {
                this.parent.setTarget(null);
                this.delay = 0;
                this.parent.setParasiteStatus(0);
                if (this.parent.getParasiteType() != 61) {
                    this.parent.getMoveControl().setWantedPosition(this.parent.getX(), this.parent.getY(), this.parent.getZ(), 1.0);
                }
                return;
            }
            LivingEntity target = this.parent.getTarget();
            if (target instanceof Player && !this.canAttackPlayer((Player)target)) {
                this.parent.setTarget(null);
                this.delay = 0;
                this.parent.setParasiteStatus(0);
            }
            if (!this.parent.hasLineOfSight((Entity)target)) {
                if (this.xRay) {
                    if (this.parent.level().random.nextInt(5) == 0) {
                        ++this.delay;
                    }
                } else {
                    ++this.delay;
                }
            } else {
                this.delay = target.distanceToSqr((Entity)this.parent) >= this.distance ? ++this.delay : 0;
            }
            if (this.delay >= 6) {
                this.parent.setTarget(null);
                this.parent.setParasiteStatus(0);
                this.delay = 0;
                if (this.parent.getParasiteType() != 61) {
                    this.parent.getMoveControl().setWantedPosition(this.parent.getX(), this.parent.getY(), this.parent.getZ(), 1.0);
                }
            }
        } else {
            this.attackSurr();
        }
    }

    protected void attackSurr() {
        BlockPos posi = this.parent.blockPosition();
        if (this.fromF != 0 && this.parent.level().random.nextInt(this.fromF) == 0 && (posi = ParasiteEventEntity.getFloor(this.parent.level(), posi, 25)) == null) {
            posi = this.parent.blockPosition();
        }
        AABB axisalignedbb = new AABB(posi).inflate(this.parent.getAttribute(Attributes.FOLLOW_RANGE).getValue());
        List<? extends LivingEntity> moblist = this.parent.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob == null) continue;
            if (mob instanceof Player) {
                if (!this.canAttackPlayer((Player)mob)) continue;
                this.parent.setTarget(mob);
                return;
            }
            if (!(mob instanceof Mob) || !SRPConfig.mobattacking || ParasiteEventEntity.checkEntity((LivingEntity)((Mob)mob), SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite) || this.fromF != 0 && mob.distanceToSqr((Entity)this.parent) > this.distance || mob == this.parent || !this.canTargetEntity(mob) || !this.parent.hasLineOfSight((Entity)mob) && !this.xRay || !(mob.distanceToSqr((Entity)this.parent) < 1024.0) || !mob.isAlive() || !this.parent.canAttackType(mob.getType())) continue;
            this.parent.setTarget(mob);
            this.parent.setParasiteStatus(1);
            return;
        }
    }

    private boolean canTargetEntity(LivingEntity in) {
        return !(in instanceof EntityParasiteBase) && !(in instanceof Animal) && !(in instanceof Creeper) && !(in instanceof WaterAnimal);
    }

    private boolean canAttackPlayer(Player in) {
        if (in.getAbilities().invulnerable) {
            return false;
        }
        return SRPEntityUtil.isSuitableTarget((Mob)this.parent, (LivingEntity)in, (boolean)false, (boolean)true);
    }
}

