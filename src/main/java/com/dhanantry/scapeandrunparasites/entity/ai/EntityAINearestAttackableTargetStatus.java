package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.Team;

public class EntityAINearestAttackableTargetStatus<T extends LivingEntity>
extends Goal {
    protected Class<T> targetClass;
    protected final EntityParasiteBase taskOwner;
    private int targetChance;
    protected Sorter sorter;
    protected Predicate<? super T> targetEntitySelector;
    protected T targetEntity;
    protected double sneakPPP;
    protected float inviPPP;
    private int targetUnseenTicks;
    private boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    protected int unseenMemoryTicks = 60;
    protected boolean shouldCheckSight;

    public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, boolean checkSight) {
        this(creature, classTarget, checkSight, false);
    }

    public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, boolean checkSight, boolean onlyNearby) {
        this(creature, classTarget, 10, checkSight, onlyNearby, null, 0.8f, 0.7f);
    }

    public EntityAINearestAttackableTargetStatus(EntityParasiteBase creature, Class<T> classTarget, int chance, boolean checkSight, boolean onlyNearby, final @Nullable Predicate<? super T> targetSelector, double sneakP, float inviP) {
        this.taskOwner = creature;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = onlyNearby;
        this.targetClass = classTarget;
        this.targetChance = chance;
        this.sorter = new Sorter((Entity)creature);
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.targetEntitySelector = new Predicate<T>(){

            public boolean test(@Nullable T p_apply_1_) {
                if (p_apply_1_ == null) {
                    return false;
                }
                if (targetSelector != null && !targetSelector.test(p_apply_1_)) {
                    return false;
                }
                return !EntitySelector.NO_SPECTATORS.test(p_apply_1_) ? false : EntityAINearestAttackableTargetStatus.this.isSuitableTarget((LivingEntity)p_apply_1_, true);
            }
        };
        this.sneakPPP = sneakP;
        this.inviPPP = inviP;
    }

    public boolean canUse() {
        if (this.targetChance > 0 && this.taskOwner.getRandom().nextInt(this.targetChance) != 0 || this.taskOwner.getParasiteStatus() != 0) {
            return false;
        }
        if (this.targetClass != Player.class && this.targetClass != ServerPlayer.class) {
            List<T> list = this.taskOwner.level().getEntitiesOfClass(this.targetClass, this.getTargetableArea(this.getTargetDistance()), this.targetEntitySelector);
            if (list.isEmpty()) {
                return false;
            }
            Collections.sort(list, this.sorter);
            this.targetEntity = list.get(0);
            LivingEntity entitylivingbase = this.taskOwner.getTarget();
            if (entitylivingbase == null) {
                return true;
            }
            return !entitylivingbase.isAlive();
        }
        this.targetEntity = (T)(Object)this.getNearest(this.taskOwner.getX(), this.taskOwner.getY() + (double)this.taskOwner.getEyeHeight(), this.taskOwner.getZ(), this.getTargetDistance(), this.getTargetDistance(), (Predicate<Player>)(Predicate<?>)this.targetEntitySelector);
        return this.targetEntity != null;
    }

    public boolean canContinueToUse() {
        LivingEntity entitylivingbase = this.taskOwner.getTarget();
        if (entitylivingbase == null) {
            return false;
        }
        if (!entitylivingbase.isAlive()) {
            return false;
        }
        Team team = this.taskOwner.getTeam();
        Team team1 = entitylivingbase.getTeam();
        if (team != null && team1 == team) {
            return false;
        }
        double d0 = this.getTargetDistance();
        if (this.taskOwner.distanceToSqr((Entity)entitylivingbase) > d0 * d0) {
            return false;
        }
        if (this.shouldCheckSight || !this.taskOwner.getGeneMod(2)) {
            if (this.taskOwner.getSensing().hasLineOfSight((Entity)entitylivingbase)) {
                this.targetUnseenTicks = 0;
            } else if (++this.targetUnseenTicks > this.unseenMemoryTicks) {
                return false;
            }
        }
        if (entitylivingbase instanceof Player && ((Player)entitylivingbase).getAbilities().invulnerable) {
            return false;
        }
        this.taskOwner.setTarget(entitylivingbase);
        return true;
    }

    private Player getNearest(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Predicate<Player> predicate) {
        double d0 = -1.0;
        Player entityplayer = null;
        for (int j2 = 0; j2 < this.taskOwner.level().players().size(); ++j2) {
            Player entityplayer1 = (Player)this.taskOwner.level().players().get(j2);
            if (entityplayer1.getAbilities().invulnerable || !entityplayer1.isAlive() || entityplayer1.isSpectator() || predicate != null && !predicate.test(entityplayer1)) continue;
            double d1 = entityplayer1.distanceToSqr(posX, entityplayer1.getY(), posZ);
            double d2 = maxXZDistance;
            if (entityplayer1.isShiftKeyDown()) {
                d2 = maxXZDistance * this.sneakPPP;
            }
            if (entityplayer1.isInvisible()) {
                float f = entityplayer1.getArmorCoverPercentage();
                if (f < 0.1f && this.inviPPP != 1.0f) {
                    f = 0.1f;
                }
                d2 *= (double)(this.inviPPP * f);
            }
            if (!(maxYDistance < 0.0) && !(Math.abs(entityplayer1.getY() - posY) < maxYDistance * maxYDistance) || !(maxXZDistance < 0.0) && !(d1 < d2 * d2) || d0 != -1.0 && !(d1 < d0)) continue;
            d0 = d1;
            entityplayer = entityplayer1;
        }
        return entityplayer;
    }

    protected AABB getTargetableArea(double targetDistance) {
        return this.taskOwner.getBoundingBox().expandTowards(targetDistance, 4.0, targetDistance);
    }

    public void start() {
        double d0 = this.getTargetDistance();
        if (this.taskOwner.distanceToSqr((Entity)this.targetEntity) > d0 * d0) {
            this.targetEntity = null;
        }
        this.taskOwner.setTarget((LivingEntity)this.targetEntity);
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.targetUnseenTicks = 0;
    }

    protected boolean isSuitableTarget(@Nullable LivingEntity target, boolean includeInvincibles) {
        if (!EntityAINearestAttackableTargetStatus.isSuitableTarget((Mob)this.taskOwner, target, includeInvincibles, this.shouldCheckSight || !this.taskOwner.getGeneMod(2))) {
            return false;
        }
        if (!this.taskOwner.isWithinRestriction(target.blockPosition())) {
            return false;
        }
        if (this.nearbyOnly) {
            if (--this.targetSearchDelay <= 0) {
                this.targetSearchStatus = 0;
            }
            if (this.targetSearchStatus == 0) {
                int n = this.targetSearchStatus = this.canEasilyReach(target) ? 1 : 2;
            }
            if (this.targetSearchStatus == 2) {
                return false;
            }
        }
        return true;
    }

    protected double getTargetDistance() {
        AttributeInstance iattributeinstance = this.taskOwner.getAttribute(Attributes.FOLLOW_RANGE);
        return iattributeinstance == null ? 16.0 : iattributeinstance.getValue();
    }

    public static boolean isSuitableTarget(Mob attacker, @Nullable LivingEntity target, boolean includeInvincibles, boolean checkSight) {
        if (target == null) {
            return false;
        }
        if (target == attacker) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        if (!attacker.canAttackType(target.getType())) {
            return false;
        }
        if (attacker.isAlliedTo((Entity)target)) {
            return false;
        }
        if (attacker instanceof OwnableEntity && ((OwnableEntity)attacker).getOwnerUUID() != null) {
            if (target instanceof OwnableEntity && ((OwnableEntity)attacker).getOwnerUUID().equals(((OwnableEntity)target).getOwnerUUID())) {
                return false;
            }
            if (target == ((OwnableEntity)attacker).getOwner()) {
                return false;
            }
        } else if (target instanceof Player && !includeInvincibles && ((Player)target).getAbilities().invulnerable) {
            return false;
        }
        return !checkSight || attacker.getSensing().hasLineOfSight((Entity)target);
    }

    private boolean canEasilyReach(LivingEntity target) {
        int j;
        this.targetSearchDelay = 10 + this.taskOwner.getRandom().nextInt(5);
        Path path = this.taskOwner.getNavigation().createPath((Entity)target, 0);
        if (path == null) {
            return false;
        }
        Node pathpoint = path.getEndNode();
        if (pathpoint == null) {
            return false;
        }
        int i = pathpoint.x - Mth.floor((double)target.getX());
        return (double)(i * i + (j = pathpoint.z - Mth.floor((double)target.getZ())) * j) <= 2.25;
    }

    public static class Sorter
    implements Comparator<Entity> {
        private final Entity entity;

        public Sorter(Entity entityIn) {
            this.entity = entityIn;
        }

        @Override
        public int compare(Entity p_compare_1_, Entity p_compare_2_) {
            double d1;
            double d0 = this.entity.distanceToSqr(p_compare_1_);
            if (d0 < (d1 = this.entity.distanceToSqr(p_compare_2_))) {
                return -1;
            }
            return d0 > d1 ? 1 : 0;
        }
    }
}

