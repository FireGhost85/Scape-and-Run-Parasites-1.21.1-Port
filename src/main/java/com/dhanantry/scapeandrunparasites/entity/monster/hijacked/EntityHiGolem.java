package com.dhanantry.scapeandrunparasites.entity.monster.hijacked;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPHijacked;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityHiGolem
extends EntityPHijacked
implements EntityBodyParts {
    private EntityBody leftTendril;
    private int attacking;
    private float lockedHeadYaw = 0.0f;
    private boolean skillCharge;
    private BlockPos positi;
    private int positicool;
    public boolean chargeFlag;
    private int timeGrab;

    public EntityHiGolem(EntityType<? extends EntityHiGolem> type, Level worldIn) {
        super(type, worldIn);
        this.type = (byte)11;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.chargeFlag = false;
        this.leftTendril = new EntityBody(this, 0.1f, 0.1f, 1.0f, 0.0f, 0.0f, 1, 1, true);
    }

    @Override
    public int getParasiteIDRegister() {
        return 301;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.higolemCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 100, 32, 3, true, 1, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPHijacked.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.HIGOLEM_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.HIGOLEM_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.2725);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.HIGOLEM_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.HIGOLEM_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.hijackedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.leftTendril.tick();
        if (this.srpTicks == 10 && !this.level().isClientSide && this.getPassengers().size() == 0 && this.chargeFlag && this.positi == null) {
            this.leftTendril.setBodySize(0.1f, 0.1f);
            this.chargeFlag = false;
            this.level().broadcastEntityEvent((Entity)this, (byte)22);
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
                    } else if (this.positi == null) {
                        this.setParasiteStatus(Math.min(k, 1));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    public void updatePassenger(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            this.yHeadRot = this.lockedHeadYaw;
            if (this.chargeFlag) {
                this.leftTendril.setBodySize(1.2f, 2.8f);
            }
            if (!this.level().isClientSide) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 200, false, false));
                if (this.srpTicks == 10 && this.positi == null) {
                    if (passenger instanceof Player) {
                        Player player = (Player)passenger;
                        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)player, new QlipShakePayload(250, 0, true, false, 4.0f));
                        if (player.getAbilities().invulnerable) {
                            return;
                        }
                    }
                    this.doHurtTarget(passenger);
                    this.attackEntityAsMobMinimum((LivingEntity)passenger, this.getMiniDamage() * 5.0f);
                }
            }
            Vec3 vec3d = this.calculateViewVector(this.getXRot(), this.yHeadRot);
            Mot.setX(passenger, 0.0);
            Mot.setZ(passenger, 0.0);
            passenger.setPos(this.getX() + vec3d.x * 1.5, this.getY() + (double)this.getEyeHeight(), this.getZ() + vec3d.z * 1.5);
        }
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        return this.hurt(source, amount);
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getPartId() == id) {
            this.leftTendril.discard();
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.leftTendril.discard();
        }
        super.discard();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.73f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.HIGOLEM_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.HIGOLEM_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.HIGOLEM_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.chargeFlag = true;
        } else if (id == 22) {
            this.chargeFlag = false;
            this.leftTendril.setBodySize(0.1f, 0.1f);
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
                return this.skillCharge;
            }
        }
        return super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        switch (attID) {
            case 1: {
                this.skillCharge = in;
                return;
            }
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        switch (id) {
            case 1: {
                this.charge();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void charge() {
        ++this.attacking;
        this.miniCapA = true;
        if (this.positi != null) {
            this.chargeMoving();
            for (LivingEntity mob : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(2.0, 0.0, 2.0))) {
                if (mob == this || mob instanceof EntityParasiteBase || mob == this.getTarget()) continue;
                float f = (float)Mth.atan2((double)(mob.getZ() - this.getZ()), (double)(mob.getX() - this.getX()));
                EntityDamage damage = new EntityDamage(this.level(), mob.getX(), mob.getY(), mob.getZ(), f, (LivingEntity)this, 1.0f, false, 0.5f);
                this.level().addFreshEntity((Entity)damage);
            }
            this.skillBreakBlocks();
            if (!this.onGround()) {
                Mot.mulX(this, 0.7);
                Mot.mulZ(this, 0.7);
            }
        } else {
            if (this.attacking < 20) {
                LivingEntity entitylivingbase;
                this.level().broadcastEntityEvent((Entity)this, (byte)100);
                if (this.attacking == 2) {
                    float v = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4f + 2.0f;
                    this.playSound(this.getHurtSound(this.damageSources().generic()), 4.0f, v);
                }
                if ((entitylivingbase = this.getTarget()) == null || !this.onGround() || this.isInWater() || entitylivingbase.getY() > this.getY() + 3.0) {
                    this.skillCharge = true;
                    this.attacking = 0;
                    this.miniCapA = false;
                    this.setParasiteStatus(0);
                    return;
                }
                if (!entitylivingbase.isAlive() || !this.getPassengers().isEmpty()) {
                    this.skillCharge = true;
                    this.attacking = 0;
                    this.miniCapA = false;
                    this.setParasiteStatus(0);
                    return;
                }
                if (this.attacking <= 19) {
                    double dis = this.distanceTo((Entity)entitylivingbase);
                    this.setParasiteStatus(3);
                    this.getNavigation().stop();
                }
            }
            if (this.attacking == 20) {
                this.positi = this.getChargeDestination((Entity)this.getTarget(), 0.5);
                this.chargeFlag = true;
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
            if (this.attacking >= 60 && this.getX() == this.xo && this.getZ() == this.zo) {
                this.attacking = 0;
                this.miniCapA = false;
                this.skillCharge = true;
                this.setParasiteStatus(1);
            }
        }
    }

    private BlockPos getChargeDestination(Entity target, double distance) {
        double d0 = this.distanceToSqr(target);
        if (target != this) {
            this.timeGrab = (int)this.distanceTo(target);
        }
        if ((d0 < 9.0 || d0 >= 256.0) && target != this || !this.hasLineOfSight(target) && target != this) {
            return null;
        }
        if (target.getY() > this.getY() + 2.0) {
            return null;
        }
        Vec3 lookVec = target.getViewVector(1.0f);
        double startX = target.getX();
        double startY = target.getY() + (double)target.getEyeHeight();
        double startZ = target.getZ();
        Vec3 startVec = new Vec3(startX, startY, startZ);
        Vec3 destVec = startVec.add(lookVec.scale(distance));
        int bon = 1;
        this.lockedHeadYaw = this.yHeadRot;
        return BlockPos.containing(destVec.x * (double)bon, destVec.y, destVec.z * (double)bon);
    }

    private void chargeMoving() {
        this.yHeadRot = this.lockedHeadYaw;
        this.setParasiteStatus(3);
        ++this.positicool;
        double str = 0.35;
        double deltaX = (double)this.positi.getX() - this.getX();
        double deltaY = (double)this.positi.getY() - this.getY();
        double deltaZ = (double)this.positi.getZ() - this.getZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
        if (distance == 0.0) {
            return;
        }
        deltaY /= distance;
        Mot.addX(this, (deltaX /= distance) * str);
        Mot.addZ(this, (deltaZ /= distance) * str);
        this.lookAt(this.positi.getX(), this.positi.getY(), this.positi.getZ());
        if (this.getTarget() != null && this.distanceToSqr((Entity)this.getTarget()) <= 9.0 && this.getPassengers().isEmpty()) {
            this.getTarget().startRiding((Entity)this, true);
            if (this.getTarget() instanceof ServerPlayer) {
                Player player = (Player)this.getTarget();
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayer((ServerPlayer)player, new QlipShakePayload(2500, 0, true, false, 4.0f));
            }
        }
        if (this.positicool == 2) {
            this.positi = this.getChargeDestination((Entity)this, 1000.0);
        }
        if (this.positicool > this.timeGrab + 50) {
            this.positi = null;
            this.positicool = 0;
        }
    }
}

