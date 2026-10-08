package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class EntityTonro
extends EntityPStationary
implements EntityCutomAttack {
    private float attackTimer;
    private boolean up;
    private int border;
    private boolean skillshockwave;

    public EntityTonro(EntityType<? extends EntityTonro> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = SRPAttributes.XP_ADAPTED * 2;
        this.type = (byte)40;
        this.buriedT = 7.5;
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
        return 29;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 0.0, false, 8.0, 7.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 20, 16, 1, true, 1));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPStationary.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.TONRO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.TONRO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.TONRO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 20.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.15);
            if (this.attackTimer > 1.0f) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.2);
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            Mot.addY(entityIn, 0.5000000059604645);
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)12);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE.get(), 3.0f, 1.0f);
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(2.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob == this || mob instanceof EntityParasiteBase || !this.hasLineOfSight((Entity)mob) || !this.doHurtTarget((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.8f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.TONRO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.TONRO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.TONRO_DEATH.get();
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else if (id == 100) {
            for (int i = 0; i <= 1; ++i) {
                this.spawnParticles(ParticleTypes.FLAME);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        switch (attID) {
            case 1: {
                return this.skillshockwave;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillshockwave = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.shockwave();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void shockwave() {
        this.setParasiteStatus(10);
        this.getNavigation().stop();
        if (this.border == 0) {
            float v = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4f + 2.0f;
            this.playSound(this.getHurtSound(this.damageSources().generic()), 4.0f, v);
            ++this.border;
            return;
        }
        if (this.border <= 2) {
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
        }
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.border;
        if (this.getTarget() == null) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (this.getTarget().getY() != this.getY()) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (this.border == 3) {
            this.spawnShock();
        }
        if (this.border == 5) {
            this.spawnShock();
        }
        if (this.border > 6) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
        }
    }

    private void spawnShock() {
        EntityWave wa = new EntityWave(SRPEntities.WAVE.get(), this.level());
        float f19 = Mth.sin((float)(this.getYRot() * ((float)Math.PI / 180) - this.rotA * 0.01f));
        float f14 = 0.17453292f;
        float f16 = Mth.cos((float)f14);
        float f4 = Mth.cos((float)(this.getYRot() * ((float)Math.PI / 180) - this.rotA * 0.01f));
        wa.teleportTo(this.getX() + -1.0 * (double)(f19 * 2.0f * f16), this.getY(), this.getZ() - -1.0 * (double)(f4 * 2.0f * f16));
        if (!wa.level().noCollision((Entity)wa, wa.getBoundingBox())) {
            wa.discard();
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        wa.setDamages(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.3, this.MiniDamage, 1, 12);
        this.level().addFreshEntity((Entity)wa);
        wa.setTarget(this.getTarget());
    }
}

