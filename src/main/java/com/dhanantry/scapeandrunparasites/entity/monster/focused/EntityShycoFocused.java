package com.dhanantry.scapeandrunparasites.entity.monster.focused;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanClimb;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPFocused;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.phys.AABB;

public class EntityShycoFocused
extends EntityPFocused
implements EntityCutomAttack,
EntityBodyParts,
EntityCanClimb {
    private float attackTimer;
    private boolean up;
    private double extraDamage;
    private double currentDamage;
    private double hpLeft;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityShycoFocused.class, EntityDataSerializers.BYTE);

    public EntityShycoFocused(EntityType<? extends EntityShycoFocused> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.extraDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
    }

    @Override
    public int getParasiteIDRegister() {
        return 83;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 4.0));
        this.goalSelector.addGoal(9, new EntityAIBlockResidue(this, 2));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 3, 32));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 25, 10, 4.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPFocused.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.SHYCO_ARMOR + SRPAttributes.SHYCO_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.SHYCO_KD_RESISTANCE + SRPAttributes.SHYCO_A_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getRandom().nextDouble() < this.hpLeft && this.level().isClientSide) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
        }
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2);
            if ((double)this.attackTimer > 1.2) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1);
        }
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    public byte climbStatus() {
        return (Byte)this.entityData.get(CLIMBING);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, (byte) (0));
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(CLIMBING);
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
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag) {
            this.currentDamage = this.extraDamage * (1.0 - (double)(this.getHealth() / this.getMaxHealth()) * SRPAttributes.SHYCO_A_I_DAMAGE);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.level().isClientSide) {
            return false;
        }
        boolean flag = this.hurt(source, amount);
        if (!flag) {
            return false;
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            entityIn.hurt(this.damageSources().mobAttack(this), (float)this.currentDamage);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.3f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ASHYCO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.ASHYCO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ASHYCO_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP.get(), 0.15f, 1.0f);
    }

    @Override
    public void setDead() {
        super.discard();
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)12);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(3.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob instanceof EntityParasiteBase) {
                if (this.getTarget() != mob) continue;
                this.setTarget(null);
                return false;
            }
            if (mob == this || !this.hasLineOfSight((Entity)mob) || !this.doHurtTarget((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.level().random.nextInt(3) == 0) {
            this.setSkin(1);
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH) * 0.5);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE) * 2.0);
            this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        }
        return floo;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected EntityPCosmical getThis() {
        return new EntityShycoFocused((EntityType<? extends EntityShycoFocused>)this.getType(), this.level());
    }
}

