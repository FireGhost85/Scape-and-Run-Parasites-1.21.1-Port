package com.dhanantry.scapeandrunparasites.entity.monster.infected.head;

import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityInfDragonEHead
extends EntityPInfected {
    public EntityInfDragonEHead(EntityType<? extends EntityInfDragonEHead> type, Level worldIn) {
        super(type, worldIn);
        this.killcount = -10.0;
        this.attackSpeedT = 15;
    }

    @Override
    public int getParasiteIDRegister() {
        return 70;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infdragoneCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(6, new AIFireballAttack(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal((Mob)this, 0.4f));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal((PathfinderMob)this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPInfected.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INFDRAGONE_HEADHEALTH);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INFDRAGONE_HEADDAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && SRPConfigSystems.disloGiveBodies && this.isAlive() && this.srpTicks == 10 && SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 20) >= 1) {
            ParasiteEventEntity.spawnNext(this, new EntityInfDragonE(SRPEntities.SIM_DRAGONE.get(), this.level()), true, false);
            return;
        }
    }

    public void setInWeb() {
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8f;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        super.causeFallDamage(distance, damageMultiplier * 0.3f, source);
        return false;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }

    static class AIFireballAttack
    extends Goal {
        private final EntityInfDragonEHead parentEntity;
        public int attackTimer;

        public AIFireballAttack(EntityInfDragonEHead ghast) {
            this.parentEntity = ghast;
        }

        public boolean canUse() {
            return this.parentEntity.getTarget() != null;
        }

        public void start() {
            this.attackTimer = 0;
        }

        public void stop() {
        }

        public void tick() {
            LivingEntity entitylivingbase = this.parentEntity.getTarget();
            double d0 = 64.0;
            if (entitylivingbase == null) {
                return;
            }
            if (entitylivingbase.distanceToSqr((Entity)this.parentEntity) < 4096.0 && this.parentEntity.hasLineOfSight((Entity)entitylivingbase)) {
                Level world = this.parentEntity.level();
                ++this.attackTimer;
                if (this.parentEntity.hasEffect(SRPPotions.RAGE_E)) {
                    ++this.attackTimer;
                }
                if (this.attackTimer == 15) {
                    double d1 = 4.0;
                    Vec3 vec3d = this.parentEntity.getViewVector(1.0f);
                    double d2 = entitylivingbase.getX() - (this.parentEntity.getX() + vec3d.x * 4.0);
                    double d3 = entitylivingbase.getBoundingBox().minY + (double)(entitylivingbase.getBbHeight() / 2.0f) - (0.5 + this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f));
                    double d4 = entitylivingbase.getZ() - (this.parentEntity.getZ() + vec3d.z * 4.0);
                    world.levelEvent((Player)null, 1016, this.parentEntity.blockPosition(), 0);
                    EntityProjectileDragonE entitylargefireball = new EntityProjectileDragonE(SRPEntities.MISSILE.get(), world, (LivingEntity)this.parentEntity, d2, d3, d4);
                    Mot.setPosX(entitylargefireball, this.parentEntity.getX() + vec3d.x);
                    Mot.setPosY(entitylargefireball, this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f) + 0.5);
                    Mot.setPosZ(entitylargefireball, this.parentEntity.getZ() + vec3d.z);
                    world.addFreshEntity((Entity)entitylargefireball);
                    this.attackTimer = 0;
                    for (int i = 0; i <= 2; ++i) {
                        this.parentEntity.level().addParticle(ParticleTypes.FLAME, this.parentEntity.getX() + vec3d.x * 4.0, this.parentEntity.getY() + (double)(this.parentEntity.getBbHeight() / 2.0f) + 0.5, this.parentEntity.getZ() + vec3d.z * 4.0, 0.0, -1.0, 0.0);
                    }
                }
            } else if (this.attackTimer > 0) {
                --this.attackTimer;
            }
        }
    }
}

