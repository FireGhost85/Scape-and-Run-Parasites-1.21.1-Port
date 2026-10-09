package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackSwell;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIFlightLimits;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanFly;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class EntityButhol
extends EntityParasiteBase
implements EntityCanFly {
    private EntityAIFlightLimits flightLimit;
    private int maxY;
    protected static final EntityDataAccessor<Byte> VEX_FLAGS = SynchedEntityData.defineId(EntityButhol.class, EntityDataSerializers.BYTE);

    public EntityButhol(EntityType<? extends EntityButhol> type, Level worldIn) {
        super(type, worldIn);
        this.flightLimit = new EntityAIFlightLimits(this, this.maxY, true);
        this.maxY = SRPAttributes.BUTHOL_MAXY;
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.moveControl = new AIMoveControl(this);
        this.setNoGravity(true);
        this.xpReward = SRPAttributes.XP_INFECTED * 2;
        this.goalSelector.removeGoal(this.folow);
        if (this.maxY != 256) {
            this.goalSelector.addGoal(3, this.flightLimit);
        }
        this.fuseTime = 30;
        this.killcount = -10.0;
        this.type = (byte)31;
    }

    @Override
    public int getParasiteIDRegister() {
        return 11;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new EntityAIAttackSwell(this));
        this.goalSelector.addGoal(3, new EntityAIFlightAttack(this, 32.0));
        this.goalSelector.addGoal(4, new AIChargeAttack());
        this.goalSelector.addGoal(6, new AIMoveRandom());
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.BUTHOL_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.BUTHOL_ARMOR);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.BUTHOL_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.BUTHOL_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.onGround() && !this.level().isClientSide) {
            this.moveControl.setWantedPosition(this.getX(), this.getY() + 5.0, this.getZ(), 0.5);
        }
    }

    @Override
    protected void tickDeath() {
        if (this.isOnFire()) {
            super.tickDeath();
        } else {
            this.setSelfeState(1);
            this.dyingBurst(true, 1);
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.4f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.CARRIER_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.CARRIER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.CARRIER_DEATH.get();
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, movement);
        this.checkInsideBlocks();
    }

    public void tick() {
        if (this.isAlive()) {
            this.lastActiveTime = this.timeSinceIgnited;
            this.dyingBurst(false, 1);
        }
        this.setNoGravity(true);
        super.tick();
    }

    @Override
    protected void selfExplode() {
        switch (this.getSkin()) {
            case 1: {
                boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && SRPConfigMobs.ButholGriefing;
                ParasiteEventEntity.createExplosion(this.level(), (Entity)this, this.getX(), this.getY(), this.getZ(), 4.0f, flag);
                if (!this.level().isClientSide) {
                    AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(4.0);
                    List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
                    for (LivingEntity mob : moblist) {
                        if (mob instanceof EntityParasiteBase) continue;
                        mob.hurt(this.damageSources().wither(), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 1);
                        mob.addEffect(new MobEffectInstance(SRPPotions.VOMIT_E, 400, 0, false, true));
                    }
                    this.playSound(SRPSounds.BUTHOL_BOOM.get(), 1.0f, 1.0f);
                    this.dead = true;
                    this.discard();
                    this.spawnLingeringCloud();
                }
                return;
            }
        }
        boolean flag = EventHooks.canEntityGrief((Level)this.level(), (Entity)this) && SRPConfigMobs.ButholGriefing;
        ParasiteEventEntity.createExplosion(this.level(), (Entity)this, this.getX(), this.getY(), this.getZ(), 4.0f, flag);
        if (!this.level().isClientSide) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(4.0);
            List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob instanceof EntityParasiteBase) continue;
                SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 400, 1);
                mob.addEffect(new MobEffectInstance(SRPPotions.VOMIT_E, 400, 0, false, true));
            }
            this.playSound(SRPSounds.BUTHOL_BOOM.get(), 1.0f, 1.0f);
            this.dead = true;
            this.discard();
            this.spawnLingeringCloud();
            ParasiteSummon.spawnM(this, SRPConfigMobs.butholMobs, 0, false, SRPEntityUtil.getCustomNameTag(this));
        }
    }

    private void spawnLingeringCloud() {
        switch (this.getSkin()) {
            case 1: {
                EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
                entityareaeffectcloud.setRadius(this.getBbWidth() * 3.5f, 0.5f);
                entityareaeffectcloud.setWaitTime(10);
                entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
                entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
                entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 2));
                entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 2, false, false));
                entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 3600, 2, false, false));
                this.level().addFreshEntity((Entity)entityareaeffectcloud);
                return;
            }
        }
        EntityToxicCloud entityareaeffectcloud = new EntityToxicCloud(SRPEntities.CLOUDTOXIC.get(), this.level(), this.getX(), this.getY(), this.getZ());
        entityareaeffectcloud.setRadius(this.getBbWidth() * 3.5f, 0.5f);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() / 2);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
        entityareaeffectcloud.addEffect(new MobEffectInstance(MobEffects.POISON, 300, 0));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
        entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, 3600, 0, false, false));
        this.level().addFreshEntity((Entity)entityareaeffectcloud);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.setSkin(1);
                }
            }
        }
        return floo;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VEX_FLAGS, (byte) (0));
    }

    private boolean getVexFlag(int mask) {
        byte i = (Byte)this.entityData.get(VEX_FLAGS);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = ((Byte)this.entityData.get(VEX_FLAGS)).byteValue();
        i = value ? (i |= mask) : (i &= ~mask);
        this.entityData.set(VEX_FLAGS, (byte) (((byte)(i & 0xFF))));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(1, charging);
    }

    class AIChargeAttack
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        public AIChargeAttack() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (EntityButhol.this.getTarget() != null && !EntityButhol.this.getMoveControl().hasWanted() && EntityButhol.this.getRandom().nextInt(7) == 0) {
                return EntityButhol.this.distanceToSqr((Entity)EntityButhol.this.getTarget()) > 4.0;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return EntityButhol.this.getMoveControl().hasWanted() && EntityButhol.this.isCharging() && EntityButhol.this.getTarget() != null && EntityButhol.this.getTarget().isAlive();
        }

        public void start() {
            LivingEntity entitylivingbase = EntityButhol.this.getTarget();
            Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
            EntityButhol.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0);
            EntityButhol.this.setCharging(true);
        }

        public void stop() {
            EntityButhol.this.setCharging(false);
        }

        public void tick() {
            LivingEntity entitylivingbase = EntityButhol.this.getTarget();
            if (entitylivingbase != null && entitylivingbase.isAlive()) {
                if (EntityButhol.this.getBoundingBox().intersects(entitylivingbase.getBoundingBox())) {
                    EntityButhol.this.doHurtTarget((Entity)entitylivingbase);
                    EntityButhol.this.setCharging(false);
                } else {
                    double d0 = EntityButhol.this.distanceToSqr((Entity)entitylivingbase);
                    if (d0 < 9.0) {
                        Vec3 vec3d = entitylivingbase.getEyePosition(1.0f);
                        EntityButhol.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0);
                    }
                }
            }
        }
    }

    class AIMoveRandom
    extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

        public AIMoveRandom() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !EntityButhol.this.getMoveControl().hasWanted() && EntityButhol.this.getRandom().nextInt(7) == 0;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void tick() {
            BlockPos blockpos = EntityButhol.this.blockPosition();
            for (int i = 0; i < 3; ++i) {
                BlockPos blockpos1 = blockpos.offset(EntityButhol.this.getRandom().nextInt(15) - 7, EntityButhol.this.getRandom().nextInt(11) - 5, EntityButhol.this.getRandom().nextInt(15) - 7);
                if (!EntityButhol.this.level().isEmptyBlock(blockpos1)) continue;
                EntityButhol.this.moveControl.setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                if (EntityButhol.this.getTarget() != null) break;
                EntityButhol.this.getLookControl().setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class AIMoveControl
    extends MoveControl {
        public AIMoveControl(EntityButhol vex) {
            super((Mob)vex);
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                double d0 = this.getWantedX() - EntityButhol.this.getX();
                double d1 = this.getWantedY() - EntityButhol.this.getY();
                double d2 = this.getWantedZ() - EntityButhol.this.getZ();
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if ((d3 = (double)(float)Math.sqrt((double)d3)) < EntityButhol.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    Mot.mulX(EntityButhol.this, 0.5);
                    Mot.mulY(EntityButhol.this, 0.5);
                    Mot.mulZ(EntityButhol.this, 0.5);
                } else {
                    Mot.addX(EntityButhol.this, d0 / d3 * 0.05 * this.speedModifier);
                    Mot.addY(EntityButhol.this, d1 / d3 * 0.05 * this.speedModifier);
                    Mot.addZ(EntityButhol.this, d2 / d3 * 0.05 * this.speedModifier);
                    if (EntityButhol.this.getTarget() == null) {
                        EntityButhol.this.setYRot(-((float)Mth.atan2((double)EntityButhol.this.getDeltaMovement().x, (double)EntityButhol.this.getDeltaMovement().z)) * 57.295776f);
        EntityButhol.this.yBodyRot = EntityButhol.this.getYRot();
                    } else {
                        double d4 = EntityButhol.this.getTarget().getX() - EntityButhol.this.getX();
                        double d5 = EntityButhol.this.getTarget().getZ() - EntityButhol.this.getZ();
                        EntityButhol.this.setYRot(-((float)Mth.atan2((double)d4, (double)d5)) * 57.295776f);
        EntityButhol.this.yBodyRot = EntityButhol.this.getYRot();
                    }
                }
            }
        }
    }
}

