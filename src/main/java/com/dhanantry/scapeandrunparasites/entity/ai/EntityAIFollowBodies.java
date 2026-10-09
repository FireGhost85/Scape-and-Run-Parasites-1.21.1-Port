package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAIFollowBodies
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private final EntityCanHaveBodies worm;
    private int digCool;
    private int digTicks;
    private double push;
    private double movementV;
    private boolean check;
    private byte checkMove;
    private double evolutionNeed;
    private int digSkillCooldown;
    private int digSkill;
    EntityCanHaveBodies follo;
    LivingEntity follo2;

    public EntityAIFollowBodies(EntityParasiteBase in, double pushDistance, double movement, int digCooldown, double evolution, int digSkillCool) {
        this.parent = in;
        this.worm = (EntityCanHaveBodies)(in);
        this.digCool = digCooldown;
        this.push = pushDistance;
        this.movementV = movement;
        this.check = false;
        this.checkMove = 0;
        this.evolutionNeed = evolution;
        this.digSkillCooldown = digSkillCool;
    }

    public boolean canUse() {
        return this.parent.isAlive();
    }

    public void tick() {
        ++this.digSkill;
        if (this.worm.getFollowing() != null) {
            this.followHead();
        } else {
            this.diggingTeleport();
        }
    }

    private void followHead() {
        EntityCanHaveBodies follo = (EntityCanHaveBodies)this.getFather(this.worm.getFollowing());
        if (follo == null) {
            this.worm.setFollowing(null);
            this.parent.discard();
            return;
        }
        LivingEntity follo2 = (LivingEntity)follo.getEntity();
        if (!follo2.isAlive()) {
            this.parent.hurt(this.parent.damageSources().generic(), 1000000.0f);
            return;
        }
        this.parent.setKillC(follo.getKillPoints());
        if (this.parent.getKillC() > this.evolutionNeed && ParasiteEventEntity.canSpawnNext) {
            this.parent.discard();
        }
        this.parent.targetNewCool = 10;
        this.parent.aiWander.setExecutionValues(999999, 2.0f);
        this.parent.setParasiteStatus(-77);
        double dis = this.parent.distanceToSqr((Entity)follo2);
        double checkDis = this.push;
        boolean flag = follo.getDigging();
        if (this.parent.srpTicks == 10) {
            this.worm.setDigging(flag);
            this.worm.bodyPartEffect();
        }
        if (this.worm.getBodiesT() - 1 >= this.worm.getBodyNumber()) {
            checkDis = 0.2;
        }
        if (dis > checkDis * checkDis) {
            double deltaZ;
            double deltaY;
            double deltaX;
            double distance;
            double str = 0.19;
            if (flag) {
                if (dis > 0.25) {
                    this.parent.lookAt((Entity)follo2);
                }
            } else {
                this.parent.lookAt((Entity)follo2);
            }
            if (dis > 9.0) {
                this.parent.copyPosition((Entity)follo2);
            }
            if ((distance = Math.sqrt((deltaX = follo2.getX() - this.parent.getX()) * deltaX + (deltaY = follo2.getY() - this.parent.getY()) * deltaY + (deltaZ = follo2.getZ() - this.parent.getZ()) * deltaZ)) == 0.0) {
                return;
            }
            Mot.addX(this.parent, (deltaX /= distance) * str);
            Mot.addY(this.parent, (deltaY /= distance) * str);
            Mot.addZ(this.parent, (deltaZ /= distance) * str);
            this.parent.setWorkTask(false);
        }
    }

    private void diggingTeleport() {
        if (this.parent.getKillC() > this.evolutionNeed && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this.parent, this.worm.getEvolution(this.parent.level()), true, true);
        }
        if (this.parent.srpTicks == 10) {
            if (this.digCool > 0) {
                --this.digCool;
                if (this.digCool == 3) {
                    this.parent.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.movementV);
                    this.digSkill = 0;
                }
                return;
            }
            if (this.parent.getX() == this.parent.xo && this.parent.getZ() == this.parent.zo) {
                this.checkMove = (byte)(this.checkMove + 1);
                if (this.checkMove >= 2 && !this.worm.getDigging()) {
                    this.parent.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.movementV);
                }
            }
            if (this.worm.getDigging()) {
                this.parent.getNavigation().stop();
                this.parent.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
                this.parent.setParasiteStatus(-5);
                this.worm.setDigging(true);
                ++this.digTicks;
                if (this.digTicks > this.worm.getBodyLength() + 2) {
                    if (this.worm.getTargetPos() != null) {
                        if (this.parent.getTarget() != null) {
                            this.worm.setTargetPos(this.parent.getTarget().blockPosition());
                        }
                        if (ParasiteEventEntity.teleportDigging(this.parent, 10.0f, this.worm.getTargetPos(), 4, 1)) {
                            this.worm.setTargetPos(null);
                        }
                    }
                    if (this.digTicks > this.worm.getBodyLength() * 2 + 2) {
                        this.digTicks = 0;
                        this.digCool = 5;
                        this.worm.setDigging(false);
                        this.parent.setParasiteStatus(0);
                    }
                }
            } else if (this.parent.getTarget() != null && this.parent.onGround()) {
                double distanceEn = this.parent.distanceToSqr((Entity)this.parent.getTarget());
                if ((distanceEn > 196.0 || !this.parent.hasLineOfSight((Entity)this.parent.getTarget()) && distanceEn > 49.0) && this.digSkill > this.digSkillCooldown) {
                    this.digSkill = 0;
                    this.worm.setDigging(true);
                    this.worm.setTargetPos(this.parent.getTarget().blockPosition());
                }
            } else {
                this.worm.setDigging(false);
                this.worm.setTargetPos(null);
            }
        }
    }

    private Entity getFather(UUID uuid) {
        List serverList = SRPEntityUtil.allEntities(this.parent.level());
        for (int x = 0; x < serverList.size(); ++x) {
            if (!uuid.equals(((Entity)serverList.get(x)).getUUID())) continue;
            return (Entity)serverList.get(x);
        }
        return null;
    }
}

