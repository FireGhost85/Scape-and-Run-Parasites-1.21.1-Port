package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class EntityAISkill
extends Goal {
    private final EntityParasiteBase parentEntity;
    private int sCooldown;
    private int attackTimer = 0;
    private int attacking = 0;
    private boolean checV;
    private int distanceC;
    private int distanceL;
    private byte attID;
    private boolean ignoreStatus;

    public EntityAISkill(EntityParasiteBase para, int cooldown, int miniDistance, boolean needVisual, int attackID) {
        this.parentEntity = para;
        this.sCooldown = cooldown;
        this.distanceC = miniDistance * miniDistance;
        this.checV = needVisual;
        this.attID = (byte)attackID;
        this.distanceL = 0;
        this.ignoreStatus = false;
    }

    public EntityAISkill(EntityParasiteBase para, int cooldown, int miniDistance, int maxDistance, boolean needVisual, int attackID) {
        this(para, cooldown, miniDistance, needVisual, attackID);
        this.distanceL = maxDistance * maxDistance;
    }

    public EntityAISkill(EntityParasiteBase para, int cooldown, int miniDistance, int maxDistance, boolean needVisual, int attackID, boolean iS) {
        this(para, cooldown, miniDistance, maxDistance, needVisual, attackID);
        this.ignoreStatus = iS;
    }

    public boolean canUse() {
        if (this.ignoreStatus) {
            return true;
        }
        if (this.attID == 13 || this.attID == 31) {
            return true;
        }
        return (this.parentEntity.getParasiteStatus() > 0 && this.parentEntity.getParasiteStatus() < 3 || this.attacking >= 1) && this.parentEntity.getGeneMod(5);
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
        if (this.attacking >= 1) {
            ++this.attacking;
            this.parentEntity.doSpecialSkill(this.attID);
            if (this.parentEntity.getFinished(this.attID)) {
                this.attacking = 0;
                this.attackTimer = 0;
                this.parentEntity.setFinished(this.attID, false);
            }
        } else if (this.parentEntity.getTarget() != null) {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            boolean flag = false;
            double dis = this.parentEntity.distanceToSqr((Entity)entitylivingbase);
            if (dis < (double)this.distanceC && dis >= (double)this.distanceL) {
                if (this.checV) {
                    if (this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                        ++this.attackTimer;
                    }
                } else {
                    ++this.attackTimer;
                }
            }
            if (this.parentEntity.hasEffect(SRPPotions.RAGE_E)) {
                ++this.attackTimer;
            }
            if (this.parentEntity.getSkin() == 120 && this.attID == 13) {
                ++this.attackTimer;
            }
            if (this.attackTimer >= this.sCooldown) {
                ++this.attacking;
            }
        }
    }
}

