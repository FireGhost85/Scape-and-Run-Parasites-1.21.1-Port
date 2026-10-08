package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackProjectile;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanShoot;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityUnvo
extends EntityPStationary
implements EntityCanShoot {
    public EntityUnvo(EntityType<? extends EntityUnvo> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = SRPAttributes.XP_ADAPTED * 2;
        this.type = (byte)40;
        this.buriedT = 5.1;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
    }

    @Override
    public int getParasiteIDRegister() {
        return 30;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new EntityAIAttackProjectile(this, 20, 1, 3));
        this.goalSelector.addGoal(3, new MeleeAttackGoal((PathfinderMob)this, 1.0, false));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPStationary.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.UNVO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.UNVO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.UNVO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.turretFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    /** 1.12 getYOffset (rider raised by 1.0 on the Esor) as the 1.21 vehicle attachment point. */
    @Override
    public Vec3 getVehicleAttachmentPoint(Entity vehicle) {
        if (vehicle instanceof EntityEsor) {
            return new Vec3(0.0, -1.0, 0.0);
        }
        return super.getVehicleAttachmentPoint(vehicle);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.6f;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.UNVO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.UNVO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.UNVO_DEATH.get();
    }

    public Fireball getProj(double accelX, double accelY, double accelZ) {
        this.playSound(SRPSounds.EMANA_SHOOTING.get(), 2.0f, 1.0f);
        LivingEntity entitylivingbase = this.getTarget();
        accelY = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 4.0f) - (1.0 + this.getY() + (double)(this.getBbHeight() / 2.0f));
        EntityProjectileSpineball ball = new EntityProjectileSpineball(SRPEntities.SPINEBALL.get(), this.level(), (LivingEntity)this, accelX, accelY, accelZ, SRPAttributes.UNVO_RANGE_ATTACK_DAMAGE);
        ball.setDurationAmplifier(SRPConfigMobs.unvoPoisonDuration, SRPConfigMobs.unvoPoisonAmplifier);
        ball.setGearDamage(SRPConfigMobs.unvoGearD);
        return ball;
    }

    @Override
    public void playProjSound() {
        this.playSound(SRPSounds.UNVO_SHOOTING.get(), 2.0f, 1.0f);
    }
}

