package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanPullMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityRanracAdapted
extends EntityPAdapted
implements EntityCanPullMobs {
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityRanracAdapted.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private EntityHitbox abdomen;
    private EntityHitbox head;
    private int pulling;
    private boolean canPull;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityRanracAdapted.class, EntityDataSerializers.BYTE);
    private int border;
    private boolean skillpulling;

    public EntityRanracAdapted(EntityType<? extends EntityRanracAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.abdomen = new EntityHitbox((Mob)this, -1.6f, 1.5f, 1.7f, 1.9f, 2.0f, 0.75f);
        this.head = new EntityHitbox((Mob)this, 1.6f, 1.3f, 1.5f, 0.9f, 0.9f, 1.25f);
        this.noCulling = true;
        this.canPull = true;
        this.hitboxes = new EntityHitbox[]{this.abdomen, this.head};
    }

    @Override
    public int getParasiteIDRegister() {
        return 58;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.11));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 20, 300, 5, true, 1));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0f, true));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        if (SRPConfig.parasiteGenResidue) {
            this.goalSelector.addGoal(9, new EntityAIBlockResidue(this, 2));
        }
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 3, 32));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ENTITY, 0);
        builder.define(CLIMBING, (byte) (0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.RANRAC_HEALTH + SRPAttributes.RANRAC_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.RANRAC_ARMOR + SRPAttributes.RANRAC_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.33);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.RANRAC_KD_RESISTANCE + SRPAttributes.RANRAC_A_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.RANRAC_ATTACK_DAMAGE + SRPAttributes.RANRAC_A_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
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
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 5, false, false));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 5, false, false));
                    this.getLookControl().setLookAt((Entity)this.getTargetedEntity(), 30.0f, 30.0f);
                    if (this.srpTicks == 5 || this.srpTicks == 15) {
                        this.lookAt((Entity)this.getTargetedEntity());
                    }
                    this.setParasiteStatus(3);
                    if (this.srpTicks == 10 && this.getTarget() instanceof ServerPlayer) {
                        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)this.getTarget(), new QlipShakePayload(250, 0, true, false, 4.0f));
                    }
                    ++this.pulling;
                    if (this.pulling > 400) {
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
        if (this.getTargetedEntity() != null && this.distanceToSqr((Entity)this.getTargetedEntity()) > 0.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.6;
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            str = 0.2;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(target, (deltaX /= distance) * str);
            Mot.addY(target, (deltaY /= distance) * str);
            Mot.addZ(target, (deltaZ /= distance) * str);
        }
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
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

    public boolean isBesideClimbableBlock() {
        return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(CLIMBING);
        if (this.getTarget() != null) {
            if (!this.hasLineOfSight((Entity)this.getTarget())) {
                if (this.distanceToSqr((Entity)this.getTarget()) < 100.0) {
                    b0 = (byte)(b0 & 0xFFFFFFFE);
                    this.entityData.set(CLIMBING, (byte) (b0));
                    return;
                }
            } else if (this.getTarget().getY() + 1.0 < this.getY()) {
                b0 = (byte)(b0 & 0xFFFFFFFE);
                this.entityData.set(CLIMBING, (byte) (b0));
                return;
            }
        }
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.entityData.set(CLIMBING, (byte) (b0));
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new PathNavigateClimberStatus((Mob)this, worldIn);
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
        mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 5, false, false));
    }

    @Override
    public int getAcceleration() {
        return 4;
    }

    @Override
    public boolean checkAttackTarget(LivingEntity check) {
        return this.getTarget() == check;
    }

    public void push(Entity entityIn) {
        if (this.getTargetedEntity() == entityIn) {
            return;
        }
        super.push(entityIn);
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    public boolean attackEntityFromPart(net.neoforged.neoforge.entity.PartEntity<?> part, DamageSource source, float damage) {
        if (this.getRandom().nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 80, 0);
        }
        return super.hurt(source, damage);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
                    break;
                }
                case 6: {
                    SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
                }
            }
        }
        return flag;
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (this.level().isClientSide) {
            return;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.5f;
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.arachnidaadaptedOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityRanrac(SRPEntities.PRI_ARACHNIDA.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(3)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                    break;
                }
                case 2: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ARANRAC_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ARANRAC_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ARANRAC_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.HEAVY_STEPS_MULTIPLE.get();
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
        if (this.border == 2) {
            float v = this.getRandom().nextFloat() * 0.4f + 1.0f;
            this.playSound(SRPSounds.ATTACKRANRAC.get(), 4.0f, v);
            ++this.border;
            return;
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
        double d3 = entitylivingbase.getBoundingBox().maxY + (double)(entitylivingbase.getBbHeight() + 0.8f) - (0.5 + this.getY() + (double)(this.getBbHeight() / 1.0f));
        double d4 = entitylivingbase.getZ() - (this.getZ() + vec3d.z);
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

