package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EntityRof
extends EntityPStationary {
    private String[] mobL;
    public LivingEntity targetScent;
    public int minmob;
    public int maxmob;
    private float attackTimer;
    private boolean upT;

    public EntityRof(EntityType<? extends EntityRof> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 0;
        this.type = (byte)40;
        this.buriedT = 5.7;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
        this.valueEvDeath = 0;
        this.delayBuried = 40;
    }

    @Override
    public int getParasiteIDRegister() {
        return 308;
    }

    protected void registerGoals() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPStationary.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, 100.0);
        builder.add(Attributes.ARMOR, 100.0);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 16.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.upT) {
            this.attackTimer += 0.2f;
            if (this.attackTimer > 3.0f) {
                this.upT = false;
            }
        } else {
            this.attackTimer -= 0.1f;
        }
        if (!this.level().isClientSide) {
            if (this.getTarget() != null && this.getTarget().distanceToSqr((Entity)this) > 256.0) {
                this.level().broadcastEntityEvent((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.tickCount > 160 && this.targetScent == null) {
                this.level().broadcastEntityEvent((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.tickCount == 100) {
                this.upT = true;
                this.attackTimer = 0.0f;
                this.level().broadcastEntityEvent((Entity)this, (byte)112);
            }
            if (!this.up && this.tickCount > 120 && !this.buried() && this.targetScent != null) {
                for (int i = 0; i <= this.maxmob; ++i) {
                    this.spawnWaves();
                    if (i < this.minmob || this.getRandom().nextInt(3) != 0) continue;
                    this.targetScent = null;
                }
                this.playSound(SRPSounds.ROF_SPIT_OUT.get(), 1.2f, this.getVoicePitch());
                this.targetScent = null;
            }
        }
    }

    @Override
    protected void retreat(boolean dead) {
        if (this.up) {
            this.playSound(SRPSounds.ROF_EMERGE.get(), 1.0f, this.getVoicePitch() - 0.1f);
            this.setParasiteStatus(3);
            this.buried += this.getBuriedSpeed();
            if (this.buried > this.buriedT + 0.7 && !this.level().isClientSide) {
                this.discard();
            }
        }
    }

    @Override
    public double getBuriedSpeed() {
        return 0.12;
    }

    public void setMob(String[] in) {
        this.mobL = in;
    }

    private Mob getMobFromList() {
        String mob = this.mobL[this.getRandom().nextInt(this.mobL.length)];
        return (Mob)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(mob), (Level)this.level());
    }

    public int spawnWaves() {
        if (this.mobL == null) {
            return 0;
        }
        Mob out = this.getMobFromList();
        if (out == null) {
            return 0;
        }
        out.finalizeSpawn((ServerLevel) out.level(), this.level().getCurrentDifficultyAt(out.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        out.setTarget(this.getTarget());
        double d0 = (float)this.getX() + this.level().random.nextFloat();
        double d1 = (float)this.getY() + this.level().random.nextFloat();
        double d2 = (float)this.getZ() + this.level().random.nextFloat();
        double d3 = d0 - this.getX();
        double d4 = d1 - this.getY();
        double d5 = d2 - this.getZ();
        double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
        d3 /= d6;
        d4 /= d6;
        d5 /= d6;
        double d7 = 0.5 / (d6 / 4.0 + 0.1);
        d4 = d4 * d7 * 6.0;
        out.copyPosition((Entity)this);
        Mot.setPosY(out, this.getY() + (double)this.getBbHeight() + 0.5);
        this.setMotion((LivingEntity)out, d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.03, 1.2);
        this.level().addFreshEntity((Entity)out);
        out.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 1200, 1, false, false));
        out.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 15, false, false));
        return 1;
    }

    public void setMotion(LivingEntity in, double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        Mot.setX(in, xSpeedIn * (Math.random() * 2.0 - 1.0));
        Mot.setY(in, ySpeedIn);
        Mot.setZ(in, zSpeedIn * (Math.random() * 2.0 - 1.0));
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.8f;
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    public void setminMax(int minn, int maxx) {
        this.minmob = minn;
        this.maxmob = maxx;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 112) {
            this.upT = true;
            this.attackTimer = 0.0f;
        } else {
            super.handleEntityEvent(id);
        }
    }
}

