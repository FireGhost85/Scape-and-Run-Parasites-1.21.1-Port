package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityAIDodAttack
extends Goal {
    private final EntityPDispatcher parent;
    private byte stage;
    private double tickss;
    private byte maxID;
    private int maxDodT;
    private ArrayList<Integer> resistanceI = new ArrayList();
    float maxHardness;
    private int cooldownNak;

    public EntityAIDodAttack(EntityPDispatcher dod, int CURRENTstage, int limit, float hardness) {
        this.parent = dod;
        this.tickss = 20.0;
        this.stage = (byte)CURRENTstage;
        this.maxID = (byte)limit;
        this.maxDodT = CURRENTstage * 3;
        this.maxHardness = hardness;
        this.cooldownNak = 0;
    }

    public boolean canUse() {
        this.tickss -= 1.0;
        --this.cooldownNak;
        if (this.tickss < 0.0) {
            this.tickss = 80.0;
            if (this.parent.getTarget() != null) {
                this.tickss = 10.0;
            }
            return true;
        }
        return false;
    }

    public void stop() {
    }

    public void tick() {
        double follow = 0.0;
        AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate(this.parent.getAttribute(Attributes.FOLLOW_RANGE).getValue());
        LivingEntity target = this.parent.getTarget();
        this.unhide(axisalignedbb);
        this.storePara(axisalignedbb);
        if (target == null) {
            this.findT(follow, axisalignedbb);
        } else {
            if (!this.parent.getMobList().isEmpty() && this.parent.level().random.nextInt(3) != 0) {
                int sel = this.parent.level().random.nextInt(this.stage * 2);
                if (sel < this.parent.getMobList().size()) {
                    for (int i = 0; i <= sel; ++i) {
                        this.summonTentacles(target, follow, axisalignedbb, true);
                    }
                }
            } else {
                switch (this.stage) {
                    case 1: {
                        this.parent.checkID();
                        if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(SRPEntities.SEIZER.get(), this.parent.level()), 3, 1, true)) {
                            // empty if block
                        }
                        break;
                    }
                    case 2: {
                        this.parent.checkID();
                        if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(SRPEntities.SEIZER.get(), this.parent.level()), 3, 1, true)) {
                            // empty if block
                        }
                        break;
                    }
                    case 3: {
                        if (this.parent.level().random.nextBoolean()) {
                            this.parent.checkID();
                            if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.spawnDet(target, new EntityUnvo(SRPEntities.SENTRY.get(), this.parent.level()), 6, 4, true)) {
                                // empty if block
                            }
                        } else {
                            this.parent.checkID();
                            if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.checkNak() && this.spawnDet(target, new EntityNak(SRPEntities.SEIZER.get(), this.parent.level()), 3, 1, true)) {
                                // empty if block
                            }
                        }
                        break;
                    }
                    case 4: {
                        if (this.parent.level().random.nextBoolean()) {
                            this.parent.checkID();
                            if (this.parent.getActualParasites() < this.parent.getTotalParasites() && this.spawnDet(target, new EntityUnvo(SRPEntities.SENTRY.get(), this.parent.level()), 6, 4, true)) {
                                // empty if block
                            }
                            break;
                        }
                        this.parent.checkID();
                        if (this.parent.getActualParasites() >= this.parent.getTotalParasites() || !this.checkNak() || this.spawnDet(target, new EntityNak(SRPEntities.SEIZER.get(), this.parent.level()), 3, 1, true)) {
                            // empty if block
                        } else {
                            break;
                        }
                    }
                }
            }
            if (!target.isAlive()) {
                this.parent.setTarget(null);
            }
        }
    }

    private void findT(double follow, AABB axisalignedbb) {
        List<? extends LivingEntity> moblist = this.parent.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob == null) continue;
            if (mob instanceof Player) {
                if (!this.canAttackPlayer((Player)mob)) continue;
                this.parent.setTarget(mob);
                return;
            }
            if (!(mob instanceof Mob) || ParasiteEventEntity.checkEntity((LivingEntity)((Mob)mob), SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite) || mob == this.parent || !this.canTargetEntity(mob) || !(mob.distanceToSqr((Entity)this.parent) < 1024.0) || !mob.isAlive() || !this.parent.canAttackType(mob.getType())) continue;
            this.parent.setTarget(mob);
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

    private void summonTentacles(LivingEntity in, double follow, AABB axisalignedbb, boolean dod) {
        if (this.parent.level().random.nextInt(2) != 0) {
            return;
        }
        this.parent.checkID();
        this.checkdodts();
        if (this.resistanceI.size() >= this.maxDodT && dod) {
            return;
        }
        if (this.parent.getActualParasites() >= this.parent.getTotalParasites() && this.parent.getMobList().size() <= 0 || this.spawnDet(in, new EntityDodT(SRPEntities.DISPATCHERTEN.get(), this.parent.level()), 5, 3, dod)) {
            // empty if block
        }
    }

    private void checkdodts() {
        for (int i = 0; i < this.resistanceI.size(); ++i) {
            Entity flag = this.parent.level().getEntity(this.resistanceI.get(i).intValue());
            if (flag == null) {
                this.resistanceI.remove(i);
                i = 0;
                continue;
            }
            if (flag instanceof EntityDodT) {
                EntityDodT flagg = (EntityDodT)flag;
                if (flagg.isAlive()) continue;
                this.resistanceI.remove(i);
                i = 0;
                continue;
            }
            this.resistanceI.remove(i);
            i = 0;
        }
    }

    private boolean spawnDet(LivingEntity in, EntityPStationary entityout, int max, int mini, boolean addId) {
        boolean tele;
        int x;
        int count;
        List serverList;
        boolean flagN;
        boolean bl = flagN = entityout.getParasiteIDRegister() == 72;
        if (flagN) {
            serverList = SRPEntityUtil.allEntities(this.parent.level());
            count = 0;
            for (x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityNak)) continue;
                ++count;
            }
            if (count >= 7) {
                entityout.discard();
                return false;
            }
            if (this.cooldownNak > 0) {
                entityout.discard();
                return false;
            }
        }
        if (entityout.getParasiteIDRegister() == 30) {
            serverList = SRPEntityUtil.allEntities(this.parent.level());
            count = 0;
            for (x = 0; x < serverList.size(); ++x) {
                if (!(serverList.get(x) instanceof EntityUnvo)) continue;
                ++count;
            }
            if (count >= 7) {
                return false;
            }
        }
        boolean bl2 = tele = entityout.getParasiteIDRegister() == 74;
        if (tele) {
            List serverList2 = SRPEntityUtil.allEntities(this.parent.level());
            int count2 = 0;
            for (int x2 = 0; x2 < serverList2.size(); ++x2) {
                if (!(serverList2.get(x2) instanceof EntityParasiteBase)) continue;
                ++count2;
            }
            if (count2 >= SRPConfig.worldMobCap) {
                return false;
            }
        }
        if (this.parent.getMobList().size() <= 0 && tele) {
            entityout.discard();
            return false;
        }
        RandomSource rand = RandomSource.create();
        double x3 = in.getX();
        double y = in.getY();
        double z = in.getZ();
        double randomx = rand.nextInt(max) + mini;
        double randomz = rand.nextInt(max) + mini;
        double negative = rand.nextInt(2);
        if (negative == 0.0) {
            randomx *= -1.0;
        }
        if ((negative = (double)rand.nextInt(2)) == 0.0) {
            randomz *= -1.0;
        }
        int limit = 0;
        boolean flag = true;
        while (flag) {
            if (limit >= 5) {
                entityout.discard();
                return false;
            }
            BlockPos pos = BlockPos.containing(x3 + randomx, y, z + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.parent.level(), pos, 5)) != null) {
                BlockState state = this.parent.level().getBlockState(pos);
                if (this.parent.level().getBlockState(pos.below()).isCollisionShapeFullBlock(this.parent.level(), pos.below())) {
                    float bHard = 0.0f;
                    BlockState state2 = this.parent.level().getBlockState(pos.below());
                    float atm = state2.getDestroySpeed(this.parent.level(), pos.below());
                    if (atm <= 0.0f) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    bHard += atm;
                    state2 = this.parent.level().getBlockState(pos.below(2));
                    atm = state2.getDestroySpeed(this.parent.level(), pos.below(2));
                    if (atm <= 0.0f) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    bHard += atm;
                    state2 = this.parent.level().getBlockState(pos.below(3));
                    atm = state2.getDestroySpeed(this.parent.level(), pos.below(3));
                    if (atm <= 0.0f) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    if ((bHard += atm) >= this.maxHardness) {
                        randomx = rand.nextInt(max) + mini;
                        randomz = rand.nextInt(max) + mini;
                        negative = rand.nextInt(2);
                        if (negative == 0.0) {
                            randomx *= -1.0;
                        }
                        if ((negative = (double)rand.nextInt(2)) == 0.0) {
                            randomz *= -1.0;
                        }
                        ++limit;
                        continue;
                    }
                    AABB axisalignedbb = new AABB((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).inflate(2.0);
                    List<? extends LivingEntity> moblist = this.parent.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    if (moblist.isEmpty()) {
                        entityout.moveTo(x3 + randomx, y, z + randomz, this.parent.getYRot(), this.parent.getXRot());
                        if (!this.parent.level().noCollision(entityout, entityout.getBoundingBox())) {
                            randomx = rand.nextInt(max) + mini;
                            randomz = rand.nextInt(max) + mini;
                            negative = rand.nextInt(2);
                            if (negative == 0.0) {
                                randomx *= -1.0;
                            }
                            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                                randomz *= -1.0;
                            }
                            ++limit;
                            continue;
                        }
                        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.parent.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                        entityout.setTarget(in);
                        if (tele) {
                            this.resistanceI.add(entityout.getId());
                            String name = this.parent.getParasiteStored();
                            if (name == null) {
                                entityout.discard();
                                return false;
                            }
                            ((EntityDodT)entityout).setTele(name, this.stage);
                        } else {
                            this.parent.setActualParasites(1);
                            this.parent.addID(entityout.getId(), 1);
                        }
                        entityout.setPeek(true);
                        entityout.setBuried();
                        if (this.parent.isOnFire()) {
                            entityout.igniteForSeconds(8);
                        }
                        this.parent.level().addFreshEntity((Entity)entityout);
                        this.tickss = 10.0;
                        this.parent.level().broadcastEntityEvent((Entity)entityout, (byte)50);
                        if (entityout.getParasiteIDRegister() == 74) {
                            ((EntityDodT)entityout).setFather(this.parent);
                        } else if (flagN) {
                            ((EntityNak)entityout).setFather(this.parent);
                            this.cooldownNak = 40;
                        }
                        flag = false;
                        return true;
                    }
                }
            }
            randomx = rand.nextInt(max) + mini;
            randomz = rand.nextInt(max) + mini;
            negative = rand.nextInt(2);
            if (negative == 0.0) {
                randomx *= -1.0;
            }
            if ((negative = (double)rand.nextInt(2)) == 0.0) {
                randomz *= -1.0;
            }
            ++limit;
        }
        entityout.discard();
        return false;
    }

    private void unhide(AABB axisalignedbb) {
        List<? extends LivingEntity> moblist = this.parent.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            int key;
            if (mob == this.parent || mob instanceof EntityParasiteBase || !mob.onGround() || mob.isInWater() || mob instanceof Player) continue;
            if (!(mob instanceof WaterAnimal) && this.parent.level().random.nextInt(1) == 0 && moblist.size() < this.parent.getMobList().size()) {
                this.summonTentacles(mob, 0.0, axisalignedbb, true);
            }
            if (!mob.hasEffect(SRPPotions.COTH_E)) continue;
            CompoundTag tags = mob.getPersistentData();
            mob.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 600, 5));
            if (!tags.contains("srpcothimmunity") || (key = tags.getInt("srpcothimmunity")) < 1) continue;
            tags.putInt("srpcothimmunity", key += 10);
        }
    }

    private void storePara(AABB axisalignedbb) {
        List<? extends EntityParasiteBase> moblist = this.parent.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!mob.isAlive() || mob instanceof EntityPStationary || mob instanceof EntityPPreeminent || mob instanceof EntityPCosmical || mob instanceof EntityCanHaveBodies || mob.getTarget() != null || !mob.onGround() || mob.tickCount <= 100 || mob.canBeStored() != 0) continue;
            this.parent.storeParasite(mob);
        }
    }

    private boolean checkNak() {
        AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate(7.0, 5.0, 7.0);
        List moblist = this.parent.level().getEntitiesOfClass(EntityNak.class, axisalignedbb);
        return moblist.isEmpty();
    }
}

