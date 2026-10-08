package com.dhanantry.scapeandrunparasites.entity.monster.pure;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvadeDash;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPure;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWaveShock;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

public class EntityGanro
extends EntityPPure
implements EntityCutomAttack,
EntityBodyParts {
    private float attackTimer;
    private boolean up;
    public EntityBody tendril1;
    public EntityBody tendril2;
    private byte left1;
    private byte right2;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityGanro.class, EntityDataSerializers.BYTE);
    private int attacking;
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean skillCharge;
    private int border;
    private boolean skillshockwave;

    public EntityGanro(EntityType<? extends EntityGanro> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.tendril1 = new EntityBody(this, 0.7f, 0.9f, 1.0f, 0.7f, 3.7f, 1, 1, true);
        this.tendril2 = new EntityBody(this, 0.7f, 0.9f, 1.0f, 0.7f, 3.7f, -1, 2, true);
        this.left1 = 1;
        this.right2 = 1;
        this.adaptationCap = 0.95f;
        this.noCulling = true;
        this.skillCharge = false;
    }

    @Override
    public int getParasiteIDRegister() {
        return 33;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 80, 100, 10, true, 14));
        this.setskillLeapValues(1.2f, 2.5, 5);
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, (int)(SRPConfig.pureFollow * 0.7), 2, false, 2));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.12));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 4.0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, 32, 8, true, 1));
        this.goalSelector.addGoal(2, new EntityAIEvadeDash(this, 20, 2, 4, 3.0, 15));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPure.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.GANRO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.GANRO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.27);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.GANRO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.GANRO_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.pureFollow);
        return builder;
    }

    private void maybeLaunchTarget(Entity target) {
        if (this.level().isClientSide || !(target instanceof LivingEntity)) {
            return;
        }
        if (this.getRandom().nextFloat() >= 0.1f) {
            return;
        }
        boolean isPlayer = target instanceof Player;
        double BASE_Y = 1.05;
        double VERT = isPlayer ? 0.525 : 1.05;
        double HORIZ = 0.4;
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 1.0E-4) {
            dx = this.getRandom().nextDouble() - 0.5;
            dz = this.getRandom().nextDouble() - 0.5;
            len = Math.sqrt(dx * dx + dz * dz);
        }
        target.push((dx /= len) * 0.4, VERT, (dz /= len) * 0.4);
        target.hurtMarked = true;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.up) {
            this.attackTimer = (float)((double)this.attackTimer + 0.2);
            if ((double)this.attackTimer > 1.5) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.1);
        }
        this.tendril1.tick();
        this.tendril2.tick();
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
        }
    }

    @Override
    protected boolean spawnT(LivingEntity in, int range2, int mini2, int check, int type) {
        if (this.hasLineOfSight((Entity)in)) {
            return false;
        }
        if (this.getRandom().nextInt(4) == 0) {
            return super.spawnT(in, range2, mini2, check, type);
        }
        return super.spawnT(in, range2, mini2, check, 2);
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
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            this.maybeLaunchTarget(entityIn);
        }
        return flag;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.5f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.GANRO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.GANRO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.GANRO_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.ganroOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public void setDead() {
        if (this.tendril1 != null) {
            this.tendril1.discard();
        }
        if (this.tendril2 != null) {
            this.tendril2.discard();
        }
        super.discard();
    }

    @Override
    public boolean attackEntityAsMobAOE(Entity entityIn) {
        if (this.borderOrb != 0) {
            return false;
        }
        this.up = true;
        this.attackTimer = 0.0f;
        this.level().broadcastEntityEvent((Entity)this, (byte)12);
        boolean flag = false;
        this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(2.0);
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
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.getRandom().nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 80, 0);
        }
        return this.hurt(source, amount * 3.0f);
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.tendril1.getId() == id) {
            this.tendril1.discard();
        } else if (this.tendril2.getId() == id) {
            this.tendril2.discard();
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(1)) {
                case 0: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    public byte getLeft() {
        return this.left1;
    }

    public byte getRight() {
        return this.right2;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.left1 = 0;
        } else if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else if (id == 22) {
            this.right2 = 0;
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
            case 2: {
                return this.skillshockwave;
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
            case 2: {
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
                this.charge();
                return;
            }
            case 2: {
                this.shockwave();
                return;
            }
        }
        super.doSpecialSkill(id);
    }

    private void charge() {
        ++this.attacking;
        this.miniCapA = true;
        if (this.attacking < 20) {
            LivingEntity entitylivingbase;
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            if (this.attacking == 2) {
                float v = (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.4f + 2.0f;
                this.playSound(this.getHurtSound(this.damageSources().generic()), 4.0f, v);
            }
            if ((entitylivingbase = this.getTarget()) == null || !this.onGround() || this.isInWater() || entitylivingbase.getY() > this.getY() && entitylivingbase.onGround()) {
                this.skillCharge = true;
                this.attacking = 0;
                this.miniCapA = false;
                this.setParasiteStatus(0);
                return;
            }
            if (!entitylivingbase.isAlive()) {
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
                this.targetX = this.getX() + 15.0 * (entitylivingbase.getX() - this.getX()) / dis;
                this.targetY = this.getY() + 15.0 * (entitylivingbase.getY() - this.getY()) / dis;
                this.targetZ = this.getZ() + 15.0 * (entitylivingbase.getZ() - this.getZ()) / dis;
            }
        }
        if (this.attacking == 20) {
            this.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, 3.0);
        }
        if (this.attacking >= 20) {
            for (LivingEntity mob : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().expandTowards(2.0, 0.0, 2.0))) {
                if (mob == this || mob instanceof EntityParasiteBase) continue;
                float f = (float)Mth.atan2((double)(mob.getZ() - this.getZ()), (double)(mob.getX() - this.getX()));
                EntityDamage damage = new EntityDamage(this.level(), mob.getX(), mob.getY(), mob.getZ(), f, (LivingEntity)this, 1.0f, false, 0.5f);
                this.level().addFreshEntity((Entity)damage);
            }
        }
        this.skillBreakBlocks();
        if (!this.onGround()) {
            Mot.mulX(this, 0.7);
            Mot.mulZ(this, 0.7);
        }
        if (this.attacking >= 60 && this.getX() == this.xo && this.getZ() == this.zo) {
            this.attacking = 0;
            this.miniCapA = false;
            this.skillCharge = true;
            this.setParasiteStatus(2);
        }
    }

    private void shockwave() {
        this.setParasiteStatus(100);
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
        if (this.border == 2) {
            this.spawnShock();
            this.up = true;
            this.attackTimer = 0.0f;
            this.level().broadcastEntityEvent((Entity)this, (byte)12);
            this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        }
        if (this.border > 3) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
        }
    }

    private void spawnShock() {
        EntityWaveShock wa = new EntityWaveShock(SRPEntities.WAVESHOCK.get(), this.level(), this);
        wa.teleportTo(this.getX(), this.getY(), this.getZ());
        if (!wa.level().noCollision((Entity)wa, wa.getBoundingBox())) {
            wa.discard();
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        wa.setDamages(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.3, this.MiniDamage, 1, 60);
        this.level().addFreshEntity((Entity)wa);
        wa.setTarget(this.getTarget());
    }
}

