package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGiveEffectsArea;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public class EntityBano
extends EntityPPrimitive {
    private float body = 0.0f;

    public EntityBano(EntityType<? extends EntityBano> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    protected boolean canRandomBlock() {
        return false;
    }

    @Override
    public int getParasiteIDRegister() {
        return 17;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal((PathfinderMob)this, 1.0, false));
        this.goalSelector.addGoal(3, new EntityAIGiveEffectsArea(this, SRPAttributes.ZETMO_CD, SRPAttributes.ZETMO_RANGE, SRPConfigMobs.zetmoEffects));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 2, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPrimitive.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.ZETMO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.ZETMO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.19);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.ZETMO_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.ZETMO_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getParasiteStatus() == 15) {
            this.setBODY(0.07f);
        } else {
            this.setBODY(-0.07f);
        }
        if (!this.level().isClientSide && this.tickCount % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityBanoAdapted(SRPEntities.ADA_BOLSTER.get(), this.level()), true, true);
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
                }
            }
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
            SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.7f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityLesh(SRPEntities.MOVINGFLESH.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    public float getBODY() {
        return this.body;
    }

    public void setBODY(float in) {
        if ((in += this.getBODY()) > 1.9f) {
            in = 1.9f;
        }
        if (in < 0.0f) {
            in = 0.0f;
        }
        this.body = in;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.level().isClientSide && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityBanoAdapted(SRPEntities.ADA_BOLSTER.get(), this.level()), true, true);
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ZETMO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ZETMO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ZETMO_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.zetmoOrbEffects, mobs);
        }
        return flag;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(2)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(7);
                }
            }
        }
        return floo;
    }

    static class AITentaclePull
    extends Goal {
        private final EntityBano parentEntity;

        public AITentaclePull(EntityBano ghast) {
            this.parentEntity = ghast;
        }

        public boolean canUse() {
            return this.parentEntity.getTarget() != null || this.parentEntity.getBODY() != 0.0f;
        }

        public void start() {
        }

        public void stop() {
            this.parentEntity.setParasiteStatus(0);
        }

        public void tick() {
            if (this.parentEntity.getTarget() == null) {
                this.parentEntity.setParasiteStatus(0);
            } else if (this.parentEntity.getTarget().isRemoved()) {
                this.parentEntity.setParasiteStatus(0);
            } else {
                LivingEntity entitylivingbase = this.parentEntity.getTarget();
                if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < 169.0 && this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                    this.parentEntity.setParasiteStatus(15);
                    if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < 64.0 && entitylivingbase.distanceToSqr((Entity)this.parentEntity) > 4.0 && this.parentEntity.tickCount % 20 == 0) {
                        Vec3 vec3d = this.parentEntity.getViewVector(1.0f);
                        float f = (float)Mth.atan2((double)(entitylivingbase.getZ() - this.parentEntity.getZ()), (double)(entitylivingbase.getX() - this.parentEntity.getX()));
                        EntityDamage entityevokerfangs = new EntityDamage(this.parentEntity.level(), entitylivingbase.getX(), entitylivingbase.getY(), entitylivingbase.getZ(), f, (LivingEntity)this.parentEntity, 1.0f, true, 1.0f);
                        this.parentEntity.level().addFreshEntity((Entity)entityevokerfangs);
                    }
                } else {
                    this.parentEntity.setParasiteStatus(0);
                }
            }
        }
    }
}

