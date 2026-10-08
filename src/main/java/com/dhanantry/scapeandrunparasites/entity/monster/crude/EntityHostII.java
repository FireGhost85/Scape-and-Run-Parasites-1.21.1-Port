package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeRangeSwitch;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackRangedStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityHostII
extends EntityPMalleable
implements RangedAttackMob,
EntityCutomAttack {
    private boolean open;
    private float buried;
    private int blockI;
    private int buriedC;
    private static final EntityDataAccessor<Boolean> UP = SynchedEntityData.defineId(EntityHostII.class, EntityDataSerializers.BOOLEAN);
    private float attackTimer;
    private boolean up;
    private int border;
    private boolean skillshockwave;

    public EntityHostII(EntityType<? extends EntityHostII> type, Level worldIn) {
        super(type, worldIn);
        this.borderOrb = -1;
        this.goalSelector.removeGoal(this.folow);
        this.setPathfindingMalus(PathType.WATER, -1.0f);
        this.noCulling = true;
        this.canModRender = 0;
        this.type = (byte)41;
        this.buried = 8.3f;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.adaptedWalls, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.adaptedWalls, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.xpReward = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.adapteddespawn;
        this.damageCap = SRPConfig.adaptedCap;
        this.foodSteal = SRPConfig.adaptedFoodSteal;
        this.pointCap = SRPConfig.adaptedPointCap;
        this.pointReduction = SRPConfig.adaptedPointRed;
        this.chanceLearn = SRPConfig.adaptedChanceLe;
        this.chanceLearnFire = SRPConfig.adaptedChanceLeFire;
        this.DamageTypeCap = SRPConfig.adaptedPointDamCap;
        this.MiniDamage = SRPConfig.adaptedMinDamage;
        this.regen = SRPConfig.adaptedRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.adaptedOneMindDeathV;
        this.regenEff = 2;
        this.adaptationCap = 0.95f;
    }

    @Override
    public int getParasiteIDRegister() {
        return 75;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 0.0, 5.0, true));
        this.goalSelector.addGoal(6, new EntityAIAttackMeleeRangeSwitch(this, 5.0f));
        this.goalSelector.addGoal(4, new EntityAIAttackRangedStatus(this, 1.3, 10, 16.0f, false));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 20, (int)SRPConfig.adaptedFollow, 2, false, 1, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(UP, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPMalleable.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.HERD_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.HERD_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.12);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.HERD_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        if (this.isNoAi()) {
            this.setParasiteStatus(3);
            this.setBurrowed(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            this.buriedC = 80;
            if (this.buriedC > 0) {
                --this.buriedC;
            }
            ++this.blockI;
            if (this.up) {
                this.attackTimer = (float)((double)this.attackTimer + 0.2);
                if (this.attackTimer > 1.0f) {
                    this.up = false;
                }
            } else {
                this.attackTimer = (float)((double)this.attackTimer - 0.1);
            }
            this.checkBurrowed();
            return;
        }
        super.aiStep();
        if (this.buriedC > 0) {
            --this.buriedC;
        }
        ++this.blockI;
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2);
            if (this.attackTimer > 1.0f) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1);
        }
        this.spawnRupters();
        this.checkBurrowed();
        this.checkSpeed();
        this.teleportByGround();
        if (!(this.getBurrowed() || this.getX() == this.xo && this.getZ() == this.zo)) {
            this.spawnGroundParticles();
        }
    }

    private void spawnRupters() {
        if (!SRPConfigMobs.nuuhEnabled) {
            return;
        }
        if (!this.level().isClientSide && !this.getBurrowed()) {
            AABB axisalignedbb;
            List<? extends LivingEntity> moblist;
            if (this.getTarget() != null && this.getRandom().nextInt(150) == 0) {
                AABB axisalignedbb2 = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(16.0);
                List moblist2 = this.level().getEntitiesOfClass(EntityNuuh.class, axisalignedbb2);
                if (moblist2.size() <= 4) {
                    EntityNuuh out = new EntityNuuh(SRPEntities.MANGLER.get(), this.level());
                    out.copyPosition((Entity)this);
                    this.level().addFreshEntity((Entity)out);
                    out.setTarget(this.getTarget());
                }
            } else if (this.getRandom().nextInt(400) == 0 && (moblist = this.level().getEntitiesOfClass(EntityNuuh.class, axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(16.0))).size() <= 3) {
                EntityNuuh out = new EntityNuuh(SRPEntities.MANGLER.get(), this.level());
                out.copyPosition((Entity)this);
                this.level().addFreshEntity((Entity)out);
            }
        }
    }

    private void checkBurrowed() {
        if (this.getBurrowed()) {
            if (this.getBbHeight() < 7.5f) {
                this.setSize(1.5f, this.getBbHeight() + 0.09f);
            }
            if (this.buried >= 0.0f) {
                this.buried -= 0.1f;
                this.spawnGroundParticles();
                this.open = false;
                this.level().broadcastEntityEvent((Entity)this, (byte)12);
            } else {
                this.open = true;
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
        } else {
            if (this.getBbHeight() > 0.25f) {
                this.setSize(1.5f, this.getBbHeight() - 0.09f);
            }
            if (this.buried < 8.3f) {
                this.buried += 0.1f;
                this.spawnGroundParticles();
                this.open = false;
                this.level().broadcastEntityEvent((Entity)this, (byte)12);
            }
        }
    }

    private void checkSpeed() {
        if (!this.level().isClientSide) {
            if (this.getTarget() != null) {
                if (this.distanceToSqr((Entity)this.getTarget()) <= 9.0) {
                    this.setParasiteStatus(3);
                    this.setBurrowed(true);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
                    this.buriedC = 80;
                } else if (this.buriedC <= 0) {
                    this.setParasiteStatus(0);
                    this.setBurrowed(false);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.12);
                }
            } else if (this.getBurrowed() && this.buriedC <= 0) {
                this.setParasiteStatus(0);
                this.setBurrowed(false);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.12);
            }
            if (this.blockI > 20) {
                this.blockI = 0;
                BlockState block = this.level().getBlockState(this.blockPosition().below());
                if (!(block.getBlock() instanceof IMetaName) && block.isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR) {
                    this.level().setBlockAndUpdate(this.blockPosition(), BlockIds.legacyState(SRPBlocks.InfestRemain.get(), 1));
                }
            }
        }
    }

    private void spawnGroundParticles() {
        BlockState state = this.level().getBlockState(this.blockPosition().below());
        if (state.getBlock() != Blocks.AIR) {
            BlockState id = state;
            for (int i = 0; i < 15; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 3.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 3.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02);
            }
        }
    }

    private void teleportByGround() {
        LivingEntity target;
        if (this.srpTicks != 10) {
            return;
        }
        if (!this.getBurrowed() && this.getRandom().nextInt(2) == 0 && this.getBbHeight() <= 0.25f && (target = this.getTarget()) != null && target.distanceToSqr((Entity)this) > 49.0 && ParasiteEventEntity.teleportDigging(this, 15.0f, target.blockPosition(), 5, 1)) {
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 30, false, false));
        }
    }

    public void addVelocity(double x, double y, double z) {
        if (this.getBurrowed()) {
            super.push(x, y, z);
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        if (this.level().isClientSide) {
            return false;
        }
        if (source.is(DamageTypes.IN_WALL)) {
            return false;
        }
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            return super.hurt(source, amount);
        }
        if (!this.getBurrowed() && this.getBbHeight() < 1.0f) {
            if (source.is(DamageTypes.DROWN)) {
                return false;
            }
            if (source.getEntity() == null) {
                return super.hurt(source, amount);
            }
            return false;
        }
        return super.hurt(source, amount);
    }

    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        if (this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && !this.getBurrowed()) {
            return;
        }
        if (this.getRandom().nextDouble() < 0.9 && !this.getBurrowed()) {
            if (this.getNavigation().getPath() == null) {
                this.getNavigation().moveTo((Entity)target, 0.5);
            }
            return;
        }
        this.buriedC = 120;
        if (this.getBurrowed() && this.getBbHeight() > 1.0f) {
            EntityProjectileSpineball entitylargefireball;
            if (this.getRandom().nextInt(4) == 0 && target.distanceToSqr((Entity)this) < 225.0 && target.distanceToSqr((Entity)this) > 25.0) {
                double d0 = target.getY() + (double)target.getEyeHeight() - (double)1.1f;
                double d1 = target.getX() + target.getDeltaMovement().x - this.getX();
                double d2 = d0 - this.getY();
                double d3 = target.getZ() + target.getDeltaMovement().z - this.getZ();
                float f = (float)Math.sqrt((double)(d1 * d1 + d3 * d3));
                EntityBomb bomb = new EntityBomb(SRPEntities.BOMB.get(), this.level(), this, false);
                bomb.setFuse(40);
                bomb.setStren(0.0f);
                bomb.setSkin(1);
                bomb.setDamage((float)SRPAttributes.HERD_BOMBDAMAGE, 5);
                bomb.setXRot(bomb.getXRot() - (-20.0f));
                bomb.shootTwo(d1, d2 + (double)(f * 0.2f), d3, 0.75f, 8.0f);
                this.level().addFreshEntity((Entity)bomb);
                bomb.updateSTR();
            }
            if (this.getRandom().nextInt(2) == 0) {
                return;
            }
            double d1 = 4.0;
            Vec3 vec3d = this.getViewVector(1.0f);
            double d2 = target.getX() - (this.getX() + vec3d.x);
            double d3 = target.getBoundingBox().minY + (double)(target.getBbHeight() / 2.0f) - (2.5 + this.getY() + (double)(this.getBbHeight() / 1.5f));
            double d4 = target.getZ() - (this.getZ() + vec3d.z);
            EntityProjectileSpineball ball = entitylargefireball = new EntityProjectileSpineball(SRPEntities.SPINEBALL.get(), this.level(), (LivingEntity)this, d2, d3, d4, SRPAttributes.EMANA_RANGED_DAMAGE);
            ball.setDurationAmplifier(SRPConfigMobs.emanaPoisonDuration, SRPConfigMobs.emanaPoisonAmplifier);
            ball.setGearDamage(SRPConfigMobs.emanaGearD);
            Mot.setPosX(entitylargefireball, this.getX() + vec3d.x);
            Mot.setPosY(entitylargefireball, this.getY() + (double)this.getEyeHeight() - 0.2);
            Mot.setPosZ(entitylargefireball, this.getZ() + vec3d.z);
            this.level().addFreshEntity((Entity)entitylargefireball);
        } else {
            this.setParasiteStatus(3);
            this.setBurrowed(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            this.getNavigation().stop();
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (!this.getBurrowed() || this.getBbHeight() < 1.0f) {
            if (entityIn == null) {
                return false;
            }
            if (this.distanceToSqr(entityIn) > 4.0) {
                return false;
            }
            this.setParasiteStatus(3);
            this.setBurrowed(true);
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
            this.buriedC = 160;
            return false;
        }
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            this.buriedC += 160;
            if (this.getRandom().nextInt(3) == 0) {
                // empty if block
            }
        }
        return flag;
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        this.up = true;
        this.attackTimer = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)13);
        boolean flag = false;
        if (!this.getBurrowed() || this.getBbHeight() < 1.0f) {
            return false;
        }
        this.playSound(SRPSounds.SWIPE.get(), 1.0f, 1.0f);
        this.playSound(SRPSounds.SWIPE.get(), 1.0f, 1.25f);
        this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate(3.0);
        List<? extends LivingEntity> moblist = this.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
        for (LivingEntity mob : moblist) {
            if (mob == this || mob instanceof EntityParasiteBase || !this.hasLineOfSight((Entity)mob) || !this.doHurtTarget((Entity)mob)) continue;
            flag = true;
        }
        return flag;
    }

    public boolean isPushedByFluid() {
        return super.isPushedByFluid();
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityHost(SRPEntities.HOST.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance potioneffectIn) {
        return super.canBeAffected(potioneffectIn);
    }

    protected SoundEvent getAmbientSound() {
        return !this.getBurrowed() ? SRPSounds.HOST_UN.get() : SRPSounds.HOST_EM.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HOST_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.HOST_DEATH.get();
    }

    public float getVoicePitch() {
        return (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 0.5f;
    }

    public boolean getBurrowed() {
        return (Boolean)this.entityData.get(UP);
    }

    public void setBurrowed(boolean in) {
        this.entityData.set(UP, in);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.getBbHeight();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    public float getBurrowTimer() {
        return this.buried;
    }

    public boolean getOpen() {
        return this.open;
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.open = true;
        } else if (id == 12) {
            this.open = false;
        } else if (id == 13) {
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

    public void setAggressive(boolean swingingArms) {
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
            this.border = 0;
            return;
        }
        if (this.border == 2) {
            this.spawnShock();
            this.up = true;
            this.attackTimer = 0.0f;
            this.level().broadcastEntityEvent((Entity)this, (byte)12);
            this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        }
        if (this.border > 3) {
            this.skillshockwave = true;
            this.border = 0;
        }
    }

    private void spawnShock() {
        EntityWave wa = new EntityWave(SRPEntities.WAVE.get(), this.level());
        float f19 = Mth.sin((float)(this.getYRot() * ((float)Math.PI / 180) - this.rotA * 0.01f));
        float f14 = 0.17453292f;
        float f16 = Mth.cos((float)f14);
        float f4 = Mth.cos((float)(this.getYRot() * ((float)Math.PI / 180) - this.rotA * 0.01f));
        wa.teleportTo(this.getX() + -1.0 * (double)(f19 * 3.0f * f16), this.getY(), this.getZ() - -1.0 * (double)(f4 * 3.0f * f16));
        wa.setDamages(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.3, this.MiniDamage, 1, 60);
        this.level().addFreshEntity((Entity)wa);
        wa.setTarget(this.getTarget());
    }

    private net.minecraft.world.entity.EntityDimensions srpSize;

    /** The 1.12 setSize(width, height): the entity dimensions are replaced and the bounding box refreshed. */
    protected void setSize(float width, float height) {
        this.srpSize = net.minecraft.world.entity.EntityDimensions.scalable(width, height);
        this.refreshDimensions();
    }

    @Override
    protected net.minecraft.world.entity.EntityDimensions getDefaultDimensions(net.minecraft.world.entity.Pose pose) {
        return this.srpSize != null ? this.srpSize : super.getDefaultDimensions(pose);
    }
}
