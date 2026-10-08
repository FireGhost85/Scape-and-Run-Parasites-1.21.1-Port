package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityAta
extends EntityParasiteBase {
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityAta.class, EntityDataSerializers.BYTE);
    private int lifespan = 0;

    public EntityAta(EntityType<? extends EntityAta> type, Level worldIn) {
        super(type, worldIn);
        this.type = (byte)5;
        this.lifespan = 0;
        this.goalSelector.removeGoal(this.folow);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.attackSpeedT = 6;
    }

    @Override
    public int getParasiteIDRegister() {
        return 91;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 20, 100, 5, true, 14));
        this.setskillLeapValues(0.4f, 1.0, 0);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.12));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal((Mob)this, 0.4f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        ++this.lifespan;
        if (this.lifespan > 1200) {
            PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 11));
            this.discard();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.ATA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.ATA_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.34559);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.ATA_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.ATA_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        return false;
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (entityIn == this.getTarget()) {
            LivingEntity target = (LivingEntity)entityIn;
            if (target.getHealth() <= target.getMaxHealth() * SRPConfigSystems.hijackHealth) {
                if (ParasiteEventEntity.convertEntityFeral((LivingEntity)entityIn, entityIn.getPersistentData(), true, SRPConfigSystems.COTHVictimParasite) || ParasiteEventEntity.hijackEntity((LivingEntity)entityIn, SRPConfigSystems.HIJACKVictimParasite)) {
                    PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 11));
                    PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 11));
                } else {
                    entityIn.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                    PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
                }
            } else {
                entityIn.hurt(this.damageSources().mobAttack(this), (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            }
            PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
            this.playSound(SRPSounds.BUTHOL_BOOM.get(), 0.3f, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 120, 2);
            this.discard();
        }
    }

    @Override
    protected void tickDeath() {
        PacketDistributor.sendToAllPlayers(new ParticlePayload(this.getX(), this.getY(), this.getZ(), 0.5f, 0.5f, 10));
        this.discard();
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 60.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    public double getMountedYOffset() {
        return this.getBbHeight() * 0.5f;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation((Mob)this, worldIn);
    }

    public void tick() {
        super.tick();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, (byte) (0));
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
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

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8f;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }
}

