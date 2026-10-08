package com.dhanantry.scapeandrunparasites.entity.monster.primitive;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatusAOE;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCutomAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPrimitive;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWaveShock;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityShyco
extends EntityPPrimitive
implements EntityCutomAttack {
    private float attackTimer;
    private boolean up;
    private double extraDamage;
    private double currentDamage;
    private double hpLeft;
    private int border;
    private boolean skillshockwave;

    public EntityShyco(EntityType<? extends EntityShyco> type, Level worldIn) {
        super(type, worldIn);
        this.extraDamage = this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
    }

    @Override
    public int getParasiteIDRegister() {
        return 1;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.095));
        this.goalSelector.addGoal(2, new EntityAISkill(this, 100, (int)(SRPConfig.primitiveFollow * 0.7), 2, false, 1));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatusAOE(this, 1.3, false, 8.0, 2.5));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 2, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPrimitive.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.SHYCO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.SHYCO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.SHYCO_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.SHYCO_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
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
            this.attackTimer = (float)((double)this.attackTimer + 0.3);
            if ((double)this.attackTimer > 2.0) {
                this.up = false;
            }
        } else {
            this.attackTimer = (float)((double)this.attackTimer - 0.15);
        }
        if (this.getRandom().nextDouble() < this.hpLeft && this.level().isClientSide) {
            this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 106, 0);
        }
        if (!this.level().isClientSide && this.tickCount % 20 == 0 && this.killcount > SRPConfig.adaptedKills && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityShycoAdapted(SRPEntities.ADA_LONGARMS.get(), this.level()), true, true);
        }
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

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag) {
            this.currentDamage = this.extraDamage * (1.0 - (double)(this.getHealth() / this.getMaxHealth()) * SRPAttributes.SHYCO_I_DAMAGE);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (entityIn instanceof LivingEntity) {
                entityIn.hurt(this.damageSources().mobAttack(this), (float)this.currentDamage);
                switch (this.getSkin()) {
                    case 5: {
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 40, 0);
                        break;
                    }
                    case 6: {
                        SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 40, 0);
                    }
                }
            }
            this.maybeLaunchTarget(entityIn);
        }
        this.hpLeft = 1.0f - this.getHealth() / this.getMaxHealth();
        return flag;
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
        AABB axisalignedbb = new AABB(entityIn.getX(), entityIn.getY(), entityIn.getZ(), entityIn.getX() + 1.0, entityIn.getY() + 1.0, entityIn.getZ() + 1.0).inflate(1.5);
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
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
        if (!this.level().isClientSide && this.killcount > SRPConfig.adaptedKills && !this.isRemoved() && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityShycoAdapted(SRPEntities.ADA_LONGARMS.get(), this.level()), true, true);
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.SHYCO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.SHYCO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.SHYCO_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.shycoOrbEffects, mobs);
        }
        return flag;
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

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.MONSTER_STEP.get(), 0.15f, 1.0f);
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
        if (!this.onGround()) {
            this.skillshockwave = true;
            this.setParasiteStatus(0);
            this.border = 0;
            return;
        }
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
        if (this.border == 4) {
            this.spawnShock();
            this.up = true;
            this.attackTimer = 0.0f;
            this.level().broadcastEntityEvent((Entity)this, (byte)12);
            this.playSound(SRPSounds.SWIPE.get(), 2.0f, 1.0f);
        }
        if (this.border > 5) {
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
        wa.setDamages(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.3, this.MiniDamage, 1, 12);
        this.level().addFreshEntity((Entity)wa);
        wa.setTarget(this.getTarget());
    }
}

