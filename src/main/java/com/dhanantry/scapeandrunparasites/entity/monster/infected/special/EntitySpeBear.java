package com.dhanantry.scapeandrunparasites.entity.monster.infected.special;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAssimara;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntitySpeBear
extends EntityPAssimara
implements EntityCanPullMobs {
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntitySpeBear.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int pulling;
    private boolean canPull;
    private boolean leapTo;
    private int border;
    private boolean skillpulling;

    public EntitySpeBear(EntityType<? extends EntitySpeBear> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 0;
        this.type = (byte)14;
        this.goalSelector.removeGoal(this.folow);
        this.killcount = -10.0;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.noCulling = true;
        this.canPull = true;
    }

    @Override
    public int getIDSpawn() {
        return 49;
    }

    @Override
    public int getParasiteIDRegister() {
        return 330;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infbearCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.2, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, 300, 5, true, 1, true));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0f, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAssimara.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.MARBEAR_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.MARBEAR_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.25);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.MARBEAR_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.MARBEAR_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ENTITY, 0);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getTarget() != null) {
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.hasLineOfSight((Entity)this.getTarget()) && this.distanceToSqr((Entity)this.getTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 1, false, false));
                    this.getLookControl().setLookAt((Entity)this.getTargetedEntity(), 30.0f, 30.0f);
                    if (this.srpTicks == 5 || this.srpTicks == 15) {
                        this.lookAt((Entity)this.getTargetedEntity());
                    }
                    this.attackEntityAsMobMinimum(this.getTarget(), 0.01f);
                    this.setParasiteStatus(3);
                    if (this.srpTicks == 10 && this.getTarget() instanceof ServerPlayer) {
                        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)this.getTarget(), new QlipShakePayload(250, 0, true, false, 4.0f));
                    }
                    ++this.pulling;
                    if (this.pulling > 200) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        this.jumping = false;
        if (this.getTargetedEntity() != null && this.distanceToSqr((Entity)this.getTargetedEntity()) > 0.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.3;
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            str = 0.1;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(target, (deltaX /= distance) * str);
            Mot.addY(target, (deltaY /= distance) * str);
            Mot.addZ(target, (deltaZ /= distance) * str);
        }
    }

    @Override
    protected void handleParasiteStatus() {
        byte k = this.getParasiteStatus();
        if (this.getAttackCooldownAni() != 0 || k == 1 || k == 2 || k == 3) {
            if (this.getAttackCooldownAni() != 0) {
                int i = this.getAttackCooldownAni() - 1;
                this.setAttackCooldownAni(i);
            }
            if (k == 1 || k == 2 || k == 3) {
                if (this.getTarget() != null) {
                    if (!this.getTarget().isAlive()) {
                        this.setTarget(null);
                        this.setParasiteStatus(0);
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    @Override
    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    @Override
    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)this.entityData.get(TARGET_ENTITY) != 0;
    }

    @Override
    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.level().isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(TARGET_ENTITY)).intValue());
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getTarget();
    }

    @Override
    public void setPStatus(int in) {
        this.setParasiteStatus(in);
    }

    @Override
    public void setPullingMobEffects(LivingEntity mob) {
        mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false));
    }

    @Override
    public int getAcceleration() {
        return 1;
    }

    @Override
    public boolean checkAttackTarget(LivingEntity check) {
        return this.getTarget() == check;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.3f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDBEAR_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDBEAR_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDBEAR_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillpulling;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillpulling = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.pullingE();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void pullingE() {
        if (!this.onGround() || !this.canPull) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (this.hasTargetedEntity()) {
            this.skillpulling = true;
            this.border = 0;
            return;
        }
        this.setParasiteStatus(11);
        this.getNavigation().stop();
        if (this.border == 0) {
            // empty if block
        }
        if (this.border <= 2) {
            // empty if block
        }
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.getTarget() == null) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (!this.hasLineOfSight((Entity)this.getTarget())) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        this.lookAt((Entity)this.getTarget());
        LivingEntity entitylivingbase = this.getTarget();
        Vec3 vec3d = this.getViewVector(1.0f);
        double d2 = entitylivingbase.getX() - (this.getX() + vec3d.x);
        double d4 = entitylivingbase.getZ() - (this.getZ() + vec3d.z);
        double d3 = this.getTarget().getBoundingBox().minY + (double)(this.getTarget().getBbHeight() / 4.0f) - (0.0 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        EntityProjectilePullball entitylargefireball = new EntityProjectilePullball(SRPEntities.PULLINGBALL.get(), this.level(), this, d2, d3, d4);
        Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
        Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
        Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
        this.level().addFreshEntity((Entity)entitylargefireball);
        if (this.border > 6) {
            this.skillpulling = true;
            this.setParasiteStatus(0);
            this.border = 0;
            this.pulling = 60;
        }
    }

    @Override
    public void resetPullSkill() {
        this.skillpulling = true;
        this.border = 0;
    }
}

