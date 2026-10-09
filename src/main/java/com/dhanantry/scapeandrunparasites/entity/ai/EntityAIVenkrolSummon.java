package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class EntityAIVenkrolSummon
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityPStationaryArchitect parentEntity;
    private int attackTimer = 0;
    private int limit = 0;
    private int sLimit;
    private int sCooldown;
    private int sStage;
    private boolean locked;
    private boolean counted;
    private boolean flag;
    private int extraV;
    private double extraM;
    private int totalV;
    private double totalM;

    public EntityAIVenkrolSummon(EntityPStationaryArchitect venkrol, int limit, int cooldown, int stage, int CAV, double CAM) {
        this.parentEntity = venkrol;
        this.sCooldown = cooldown;
        this.sLimit = limit;
        this.sStage = stage;
        this.locked = false;
        this.flag = false;
        this.counted = false;
        if (!SRPConfigSystems.useEvolution && !SRPConfigSystems.rsIgnoreCooldownAtSpawn) {
            this.attackTimer = -this.sCooldown;
        }
        this.totalV = CAV;
        this.totalM = CAM;
    }

    public void start() {
        this.parentEntity.setParasiteStatus(1);
    }

    public boolean canUse() {
        return this.parentEntity.getTarget() != null;
    }

    public void stop() {
        this.limit = 0;
        this.parentEntity.setParasiteStatus(0);
    }

    public void tick() {
        if (this.locked) {
            --this.attackTimer;
            if (this.attackTimer <= -200) {
                this.locked = false;
                this.attackTimer = -this.sCooldown;
            }
        } else if (this.parentEntity.getTarget() == null) {
            if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        } else if (this.parentEntity.getTarget().isRemoved()) {
            if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        } else {
            LivingEntity target = this.parentEntity.getTarget();
            if (this.parentEntity.hasLineOfSight((Entity)target)) {
                ++this.attackTimer;
                this.parentEntity.level().broadcastEntityEvent((Entity)this.parentEntity, (byte)12);
                if (this.attackTimer % 20 == 0 && this.attackTimer > 0) {
                    String[] air;
                    String[] ground;
                    EntityPStationaryArchitect parent;
                    if (this.attackTimer == 20) {
                        this.extraV = this.countNeraby(this.parentEntity.getId());
                        this.extraM = Math.floor((double)this.extraV * this.totalM);
                        this.flag = this.extraV >= this.totalV;
                        this.setNearbyNonConted(this.parentEntity.getId());
                        parent = this.parentEntity;
                        parent.checkID();
                    }
                    parent = this.parentEntity;
                    parent.checkID();
                    if (parent.getActualParasites() < parent.getTotalParasites() && this.limit < this.sLimit) {
                        if (this.sStage == 4) {
                            if (ParasiteSummon.SummonM(this.parentEntity, SRPAttributes.VENKROLSIV_MOBTABLEG, 12, target.getX(), target.getY(), target.getZ(), target, false)) {
                                if (this.flag) {
                                    this.setNerabyLocked(this.parentEntity.getId());
                                }
                                ++this.limit;
                            }
                        } else {
                            ground = SRPAttributes.VENKROL_MOBTABLEG;
                            air = null;
                            switch (this.sStage) {
                                case 2: {
                                    ground = SRPAttributes.VENKROLSII_MOBTABLEG;
                                    air = SRPAttributes.VENKROLSII_MOBTABLEA;
                                    break;
                                }
                                case 3: {
                                    ground = SRPAttributes.VENKROLSIII_MOBTABLEG;
                                    air = SRPAttributes.VENKROLSIII_MOBTABLEA;
                                }
                            }
                            if (ParasiteEventEntity.spawnBiomassFromBeckon(this.parentEntity, this.sStage, target, true, ground, air)) {
                                if (this.flag) {
                                    this.setNerabyLocked(this.parentEntity.getId());
                                }
                                ++this.limit;
                                int j = this.parentEntity.getActualT();
                                this.parentEntity.setActualT(++j);
                            }
                        }
                    }
                    if (this.limit >= this.sLimit) {
                        if (this.flag) {
                            if (this.extraM > 0.0 && this.attackTimer > this.sLimit * 20) {
                                if (this.sStage == 4) {
                                    if (ParasiteSummon.SummonM(this.parentEntity, SRPAttributes.VENKROLSIV_MOBTABLEG, 5, this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getTarget(), false)) {
                                        this.extraM -= 1.0;
                                    }
                                } else {
                                    ground = SRPAttributes.VENKROL_MOBTABLEG;
                                    air = null;
                                    switch (this.sStage) {
                                        case 2: {
                                            ground = SRPAttributes.VENKROLSII_MOBTABLEG;
                                            air = SRPAttributes.VENKROLSII_MOBTABLEA;
                                            break;
                                        }
                                        case 3: {
                                            ground = SRPAttributes.VENKROLSIII_MOBTABLEG;
                                            air = SRPAttributes.VENKROLSIII_MOBTABLEA;
                                        }
                                    }
                                    if (ParasiteEventEntity.spawnBiomassFromBeckon(this.parentEntity, this.sStage, target, true, ground, air)) {
                                        this.extraM -= 1.0;
                                    }
                                }
                            } else if (this.extraM <= 0.0) {
                                this.extraM = 0.0;
                                this.flag = false;
                                this.attackTimer = -this.sCooldown;
                                this.setNearbyFree(this.parentEntity.getId());
                                this.limit = 0;
                            }
                        } else {
                            this.attackTimer = -this.sCooldown;
                            this.setNearbyFree(this.parentEntity.getId());
                            this.limit = 0;
                        }
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        }
    }

    public void setNerabyLocked(int id) {
        switch (this.sStage) {
            case 1: {
                AABB axisalignedbb = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrol> moblist = this.parentEntity.level().getEntitiesOfClass(EntityVenkrol.class, axisalignedbb);
                for (EntityVenkrol mob : moblist) {
                    if (mob.getId() == id || !mob.isAlive() || mob.summonV.getLocker() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setlocker(true);
                    mob.summonV.setNerabyLocked(id);
                }
                break;
            }
            case 2: {
                AABB axisalignedbbsii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSII> moblistsii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSII.class, axisalignedbbsii);
                for (EntityVenkrolSII mob : moblistsii) {
                    if (mob.getId() == id || !mob.isAlive() || mob.summonV.getLocker() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setlocker(true);
                    mob.summonV.setNerabyLocked(id);
                }
                break;
            }
            case 3: {
                AABB axisalignedbbsiii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSIII> moblistsiii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSIII.class, axisalignedbbsiii);
                for (EntityVenkrolSIII mob : moblistsiii) {
                    if (mob.getId() == id || !mob.isAlive() || mob.summonV.getLocker() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setlocker(true);
                    mob.summonV.setNerabyLocked(id);
                }
                break;
            }
        }
    }

    public void setNearbyFree(int id) {
        switch (this.sStage) {
            case 1: {
                AABB axisalignedbb = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrol> moblist = this.parentEntity.level().getEntitiesOfClass(EntityVenkrol.class, axisalignedbb);
                for (EntityVenkrol mob : moblist) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.summonV.getLocker() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setlocker(false);
                    mob.summonV.setCooldownZero(-this.sCooldown);
                    mob.summonV.setNearbyFree(id);
                }
                break;
            }
            case 2: {
                AABB axisalignedbbsii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSII> moblistsii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSII.class, axisalignedbbsii);
                for (EntityVenkrolSII mob : moblistsii) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.summonV.getLocker() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setlocker(false);
                    mob.summonV.setCooldownZero(-this.sCooldown);
                    mob.summonV.setNearbyFree(id);
                }
                break;
            }
            case 3: {
                AABB axisalignedbbsiii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSIII> moblistsiii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSIII.class, axisalignedbbsiii);
                for (EntityVenkrolSIII mob : moblistsiii) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.summonV.getLocker() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setlocker(false);
                    mob.summonV.setCooldownZero(-this.sCooldown);
                    mob.summonV.setNearbyFree(id);
                }
                break;
            }
        }
    }

    public void setCooldownZero(int in) {
        this.attackTimer = in;
    }

    public void setlocker(boolean in) {
        if (in) {
            this.attackTimer = -1;
        }
        this.locked = in;
    }

    public boolean getLocker() {
        return this.locked;
    }

    public int countNeraby(int id) {
        int count = 1;
        switch (this.sStage) {
            case 1: {
                AABB axisalignedbb = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrol> moblist = this.parentEntity.level().getEntitiesOfClass(EntityVenkrol.class, axisalignedbb);
                for (EntityVenkrol mob : moblist) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.hasLineOfSight((Entity)this.parentEntity) || mob.summonV.getCounted()) continue;
                    mob.summonV.setCounted(true);
                    count += mob.summonV.countNeraby(id);
                }
                break;
            }
            case 2: {
                AABB axisalignedbbsii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSII> moblistsii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSII.class, axisalignedbbsii);
                for (EntityVenkrolSII mob : moblistsii) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.hasLineOfSight((Entity)this.parentEntity) || mob.summonV.getCounted()) continue;
                    mob.summonV.setCounted(true);
                    count += mob.summonV.countNeraby(id);
                }
                break;
            }
            case 3: {
                AABB axisalignedbbsiii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSIII> moblistsiii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSIII.class, axisalignedbbsiii);
                for (EntityVenkrolSIII mob : moblistsiii) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.hasLineOfSight((Entity)this.parentEntity) || mob.summonV.getCounted()) continue;
                    mob.summonV.setCounted(true);
                    count += mob.summonV.countNeraby(id);
                }
                break;
            }
        }
        return count;
    }

    public void setNearbyNonConted(int id) {
        switch (this.sStage) {
            case 1: {
                AABB axisalignedbb = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrol> moblist = this.parentEntity.level().getEntitiesOfClass(EntityVenkrol.class, axisalignedbb);
                for (EntityVenkrol mob : moblist) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.summonV.getCounted() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setCounted(false);
                    mob.summonV.setNearbyNonConted(id);
                }
                break;
            }
            case 2: {
                AABB axisalignedbbsii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSII> moblistsii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSII.class, axisalignedbbsii);
                for (EntityVenkrolSII mob : moblistsii) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.summonV.getCounted() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setCounted(false);
                    mob.summonV.setNearbyNonConted(id);
                }
                break;
            }
            case 3: {
                AABB axisalignedbbsiii = new AABB(this.parentEntity.getX(), this.parentEntity.getY(), this.parentEntity.getZ(), this.parentEntity.getX() + 1.0, this.parentEntity.getY() + 1.0, this.parentEntity.getZ() + 1.0).inflate(this.parentEntity.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() + 5.0);
                List<? extends EntityVenkrolSIII> moblistsiii = this.parentEntity.level().getEntitiesOfClass(EntityVenkrolSIII.class, axisalignedbbsiii);
                for (EntityVenkrolSIII mob : moblistsiii) {
                    if (mob.getId() == id || !mob.isAlive() || !mob.summonV.getCounted() || !mob.hasLineOfSight((Entity)this.parentEntity)) continue;
                    mob.summonV.setCounted(false);
                    mob.summonV.setNearbyNonConted(id);
                }
                break;
            }
        }
    }

    public void setCounted(boolean in) {
        this.counted = in;
    }

    public boolean getCounted() {
        return this.counted;
    }
}

