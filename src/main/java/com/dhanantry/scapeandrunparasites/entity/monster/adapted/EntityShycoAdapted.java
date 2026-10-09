package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanClimb;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWaveShock;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.EntityBodyDeadPayload;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.PathNavigateClimberStatus;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityShycoAdapted
extends EntityPAdapted
implements EntityCutomAttack,
EntityBodyParts,
EntityCanClimb {
    private float attackTimer;
    private boolean up;
    private double extraDamage;
    private double currentDamage;
    private double hpLeft;
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityShycoAdapted.class, EntityDataSerializers.BYTE);
    private int border;
    private boolean skillshockwave;

    public EntityShycoAdapted(EntityType<? extends EntityShycoAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.extraDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
        this.leftTendril = new EntityBody(this, 0.6f, 2.0f, 1.0f, 0.7f, 1.4f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 0.6f, 2.0f, 1.0f, 0.7f, 1.4f, -1, 2, true);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
    }

    @Override
    public int getParasiteIDRegister() {
        return 51;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.11));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 80, (int)(SRPConfig.adaptedFollow * 0.7), 2, false, 1));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 4.0));
        if (SRPConfig.parasiteGenResidue) {
            this.goalSelector.addGoal(9, new EntityAIBlockResidue(this, 2));
        }
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 3, 32));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 25, 10, 4.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.SHYCO_ARMOR + SRPAttributes.SHYCO_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.SHYCO_KD_RESISTANCE + SRPAttributes.SHYCO_A_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
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
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.tick();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.tick();
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
        if (this.leftTendril.getPartId() == id) {
            this.leftTendrilHealth -= amount;
            if (this.leftTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(1);
                tendril.copyPosition(this.leftTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.leftTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendrilHealth -= amount;
            if (this.rightTendrilHealth <= 0.0f) {
                EntityTendril tendril = new EntityTendril(SRPEntities.TENDRIL.get(), this.level());
                tendril.setSkin(1);
                tendril.copyPosition(this.rightTendril);
                this.level().addFreshEntity((Entity)tendril);
                this.rightTendril.discard();
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
                this.cutResistances(SRPConfig.adaptedPointDamCap / 2);
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new EntityBodyDeadPayload(this.getId(), id));
            }
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
        if (this.leftTendril.getPartId() == id) {
            this.leftTendril.discard();
        } else if (this.rightTendril.getPartId() == id) {
            this.rightTendril.discard();
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (entityIn instanceof LivingEntity) {
                entityIn.hurt(this.damageSources().mobAttack(this), (float)this.currentDamage);
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
            this.maybeLaunchTarget(entityIn);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 3.3f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityShyco(SRPEntities.PRI_LONGARMS.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ASHYCO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ASHYCO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ASHYCO_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.shycoadaptedOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS.get(), 0.15f, 1.0f);
    }

    @Override
    public void setDead() {
        if (this.leftTendril != null) {
            this.leftTendril.discard();
        }
        if (this.rightTendril != null) {
            this.rightTendril.discard();
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
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (this.level().isClientSide) {
            return;
        }
        if (entityIn instanceof LivingEntity && !(entityIn instanceof EntityParasiteBase) && this.getSkin() == 5) {
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(4)) {
                case 0: {
                    this.setSkin(1);
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((SRPAttributes.SHYCO_HEALTH + SRPAttributes.SHYCO_A_HEALTH) * 0.5);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((SRPAttributes.SHYCO_ATTACK_DAMAGE + SRPAttributes.SHYCO_A_ATTACK_DAMAGE) * 2.0);
                    this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
                    break;
                }
                case 1: {
                    this.setSkin(5);
                    break;
                }
                case 2: {
                    this.setSkin(6);
                    break;
                }
                case 3: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("parasiteleftTendril", this.leftTendrilHealth);
        compound.putFloat("parasiterightTendril", this.rightTendrilHealth);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasiteleftTendril", 99)) {
            this.leftTendrilHealth = compound.getFloat("parasiteleftTendril");
            if (this.leftTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)11);
            }
        }
        if (compound.contains("parasiterightTendril", 99)) {
            this.rightTendrilHealth = compound.getFloat("parasiterightTendril");
            if (this.rightTendrilHealth <= 0.0f) {
                this.level().broadcastEntityEvent((Entity)this, (byte)22);
            }
        }
    }

    public float getAttackTimer() {
        return this.attackTimer;
    }

    public float getLeft() {
        return this.leftTendrilHealth;
    }

    public float getRight() {
        return this.rightTendrilHealth;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 11) {
            this.leftTendrilHealth = 0.0f;
        } else if (id == 12) {
            this.up = true;
            this.attackTimer = 0.0f;
        } else if (id == 22) {
            this.rightTendrilHealth = 0.0f;
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
        if (!this.onGround()) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        this.setParasiteStatus(10);
        this.getNavigation().stop();
        if (this.border == 0) {
            float v = this.getRandom().nextFloat() * 0.4f + 1.0f;
            this.playSound(SRPSounds.ATTACKSHYCO.get(), 4.0f, v);
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
        if (!this.hasLineOfSight((Entity)this.getTarget())) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (this.getTarget().getY() >= this.getY() + 4.0 || this.getTarget().getY() < this.getY() - 2.0) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
        if (this.border == 3) {
            this.spawnShock();
            this.up = true;
            this.attackTimer = 0.0f;
            this.level().broadcastEntityEvent((Entity)this, (byte)12);
            this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        }
        if (this.border > 4) {
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

