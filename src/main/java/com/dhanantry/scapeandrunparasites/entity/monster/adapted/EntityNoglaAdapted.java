package com.dhanantry.scapeandrunparasites.entity.monster.adapted;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.EntityHitbox;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockResidue;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAdapted;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.EntityBodyDeadPayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
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
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class EntityNoglaAdapted
extends EntityPAdapted
implements EntityBodyParts {
    private EntityBody leftTendril;
    private EntityBody rightTendril;
    private float leftTendrilHealth;
    private float rightTendrilHealth;
    private EntityHitbox head;
    private int attacking;
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean skillCharge;

    public EntityNoglaAdapted(EntityType<? extends EntityNoglaAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.leftTendril = new EntityBody(this, 0.6f, 2.0f, 1.0f, 0.9f, 0.9f, 1, 1, true);
        this.rightTendril = new EntityBody(this, 0.6f, 2.0f, 1.0f, 0.9f, 0.9f, -1, 2, true);
        this.leftTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.rightTendrilHealth = (float)((double)this.getMaxHealth() * SRPConfig.tendrilHealth);
        this.head = new EntityHitbox((Mob)this, 1.6f, 1.35f, 1.9f, 0.9f, 0.9f, 1.25f);
        this.hitboxes = new EntityHitbox[]{this.head};
        this.skillCharge = false;
    }

    @Override
    public int getParasiteIDRegister() {
        return 54;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.11));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 40, 32, 8, true, 1));
        if (SRPConfig.parasiteGenResidue) {
            this.goalSelector.addGoal(9, new EntityAIBlockResidue(this, 2));
        }
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 3, 32));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 25, 10, 4.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAdapted.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.NOGLA_HEALTH + SRPAttributes.NOGLA_A_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.NOGLA_ARMOR + SRPAttributes.NOGLA_A_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.31234);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.NOGLA_KD_RESISTANCE + SRPAttributes.NOGLA_A_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.NOGLA_ATTACK_DAMAGE + SRPAttributes.NOGLA_A_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.adaptedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.leftTendrilHealth > 0.0f) {
            this.leftTendril.tick();
        }
        if (this.rightTendrilHealth > 0.0f) {
            this.rightTendril.tick();
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1));
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
                tendril.setSkin(2);
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
                tendril.setSkin(2);
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
        return 2.4f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityNogla(SRPEntities.PRI_REEKER.get(), this.level()), true, false);
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
        return SRPSounds.ANOGLA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ANOGLA_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ANOGLA_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.noglaadaptedOrbEffects, mobs);
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS_TWO.get(), 0.15f, 1.0f);
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
        if (this.attacking < 20) {
            LivingEntity entitylivingbase;
            this.level().broadcastEntityEvent((Entity)this, (byte)100);
            if (this.attacking == 2) {
                float v = this.getRandom().nextFloat() * 0.4f + 1.0f;
                this.playSound(SRPSounds.ATTACKNOGLA.get(), 4.0f, v);
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
}

