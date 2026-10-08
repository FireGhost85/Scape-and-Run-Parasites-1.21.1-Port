package com.dhanantry.scapeandrunparasites.entity.monster.infected.special;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAssimara;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

public class EntitySpeEnderman
extends EntityPAssimara {
        private static final AttributeModifier ATTACKING_SPEED_BOOST = new AttributeModifier(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "attacking_speed_boost"), (double)0.15f, AttributeModifier.Operation.ADD_VALUE);
    private static final EntityDataAccessor<Boolean> SCREAMING = SynchedEntityData.defineId(EntitySpeEnderman.class, EntityDataSerializers.BOOLEAN);
    private int lastCreepySound;
    private int targetChangeTime;
    private int toTeleCool;
    private int spotCool;
    private String tpPlayerName;
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntitySpeEnderman.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int pulling;
    private boolean canPull;

    public EntitySpeEnderman(EntityType<? extends EntitySpeEnderman> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 0;
        this.type = (byte)14;
        this.goalSelector.removeGoal(this.folow);
        this.killcount = -10.0;
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.tpPlayerName = null;
        this.noCulling = true;
        this.canPull = true;
    }

    @Override
    public int getIDSpawn() {
        return 59;
    }

    @Override
    public int getParasiteIDRegister() {
        return 321;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infendermanCanSpawnAssimilatedNat;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.2, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 20, 0, 1.35, true, 7, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAssimara.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.MARENDERMAN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.MARENDERMAN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.1496);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.MARENDERMAN_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, 64.0);
        return builder;
    }

    @Override
    public void setAttackTarget(@Nullable LivingEntity entitylivingbaseIn) {
        boolean flag = this.getTarget() == null;
        super.setTarget(entitylivingbaseIn);
        AttributeInstance iattributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (entitylivingbaseIn == null) {
            this.targetChangeTime = 0;
            this.entityData.set(SCREAMING, false);
            iattributeinstance.removeModifier(ATTACKING_SPEED_BOOST.id());
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(SCREAMING, true);
            if (flag) {
                this.spotCool = SRPConfigMobs.infendermansaw;
                if (entitylivingbaseIn instanceof Player) {
                    if (this.tpPlayerName != null) {
                        if (!this.tpPlayerName.equals(((Player)entitylivingbaseIn).getName().getString())) {
                            this.tpPlayerName = ((Player)entitylivingbaseIn).getName().getString();
                            this.level().playSound((Player)null, entitylivingbaseIn.xo, entitylivingbaseIn.yo, entitylivingbaseIn.zo, SRPSounds.ASSENDERMAN_PORTAL.get(), this.getSoundSource(), 0.3f, 1.0f);
                        }
                    } else {
                        this.tpPlayerName = ((Player)entitylivingbaseIn).getName().getString();
                        this.level().playSound((Player)null, entitylivingbaseIn.xo, entitylivingbaseIn.yo, entitylivingbaseIn.zo, SRPSounds.ASSENDERMAN_PORTAL.get(), this.getSoundSource(), 0.3f, 1.0f);
                    }
                } else {
                    this.tpPlayerName = null;
                }
            }
            if (!iattributeinstance.hasModifier(ATTACKING_SPEED_BOOST.id())) {
                iattributeinstance.addTransientModifier(ATTACKING_SPEED_BOOST);
            }
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SCREAMING, false);
        builder.define(TARGET_ENTITY, 0);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 60.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    public void playEndermanSound() {
        if (this.tickCount >= this.lastCreepySound + 400) {
            this.lastCreepySound = this.tickCount;
            if (!this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY() + (double)this.getEyeHeight(), this.getZ(), SoundEvents.ENDERMAN_STARE, this.getSoundSource(), 2.5f, 1.0f, false);
            }
        }
    }

    public void notifyDataManagerChange(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (TARGET_ENTITY.equals(key)) {
            this.targetedEntity = null;
        }
    }

    public void push(Entity entityIn) {
        if (this.getTargetedEntity() != null && this.getTargetedEntity() == entityIn) {
            return;
        }
        super.push(entityIn);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL, this.getX() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), this.getY() + this.getRandom().nextDouble() * (double)this.getBbHeight() - 0.25, this.getZ() + (this.getRandom().nextDouble() - 0.5) * (double)this.getBbWidth(), (this.getRandom().nextDouble() - 0.5) * 2.0, -this.getRandom().nextDouble(), (this.getRandom().nextDouble() - 0.5) * 2.0);
            }
        } else {
            if (this.getTarget() != null && this.tickCount % 20 == 0 && this.distanceToSqr((Entity)this.getTarget()) > 4.0 && this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq) == 0) {
                this.teleportRandomly();
            }
            if (this.spotCool >= 0) {
                --this.spotCool;
            }
            if (this.toTeleCool >= 0) {
                --this.toTeleCool;
            }
            if (this.srpTicks == 10 && this.hasEffect(SRPPotions.RAGE_E)) {
                this.spotCool = 0;
                this.toTeleCool = 0;
            }
            if (!this.canPull) {
                --this.pulling;
                if (this.pulling == 0) {
                    this.canPull = true;
                }
            }
            if (this.getTarget() != null) {
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                    this.setTargetedEntity(0);
                } else if (this.hasLineOfSight((Entity)this.getTarget()) && this.distanceToSqr((Entity)this.getTarget()) > 0.0 && this.canPull && this.getTargetedEntity() != null) {
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1, false, false));
                    this.getTarget().addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 1, false, false));
                    this.lookAt((Entity)this.getTargetedEntity());
                    this.setParasiteStatus(3);
                    ++this.pulling;
                    if (this.pulling > 200 || this.distanceToSqr((Entity)this.getTarget()) > 9.0) {
                        this.setTargetedEntity(0);
                        this.canPull = false;
                    }
                } else {
                    this.setTargetedEntity(0);
                }
            } else {
                this.setTargetedEntity(0);
            }
        }
        this.jumping = false;
        if (this.getTargetedEntity() != null && this.distanceToSqr((Entity)this.getTargetedEntity()) > 0.0) {
            LivingEntity target = this.getTargetedEntity();
            target.stopRiding();
            double str = 0.3;
            double deltaX = this.getX() - target.getX();
            double deltaY = this.getY() - target.getY();
            double deltaZ = this.getZ() - target.getZ();
            str = 0.13;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
            if (distance == 0.0) {
                return;
            }
            Mot.addX(target, (deltaX /= distance) * str);
            Mot.addY(target, (deltaY /= distance) * str);
            Mot.addZ(target, (deltaZ /= distance) * str);
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
                    } else if (!this.canPull) {
                        this.setParasiteStatus(Math.min(k, 2));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    public void setTargetedEntity(int entityId) {
        if (!this.canPull && entityId != 0) {
            return;
        }
        this.pulling = 0;
        this.canPull = true;
        this.entityData.set(TARGET_ENTITY, entityId);
    }

    public boolean hasTargetedEntity() {
        if (!this.canPull) {
            return false;
        }
        return (Integer)this.entityData.get(TARGET_ENTITY) != 0;
    }

    public LivingEntity getTargetedEntity() {
        if (!this.hasTargetedEntity()) {
            return null;
        }
        if (this.level().isClientSide) {
            if (this.targetedEntity != null) {
                return this.targetedEntity;
            }
            Entity entity = this.level().getEntity(((Integer)this.entityData.get(TARGET_ENTITY)).intValue());
            if (entity instanceof LivingEntity) {
                this.targetedEntity = (LivingEntity)entity;
                return this.targetedEntity;
            }
            return null;
        }
        return this.getTarget();
    }

    protected boolean teleportRandomly() {
        if (this.spotCool > 0) {
            return false;
        }
        if (this.getTargetedEntity() != null) {
            return false;
        }
        if (this.getTargetedEntity() != null) {
            return false;
        }
        double d0 = this.getX() + (this.getRandom().nextDouble() - 0.5) * 64.0;
        double d1 = this.getY() + (double)(this.getRandom().nextInt(64) - 32);
        double d2 = this.getZ() + (this.getRandom().nextDouble() - 0.5) * 64.0;
        if (this.getTarget() != null && Math.sqrt(this.getTarget().distanceToSqr(d0, d1, d2)) < 10.0) {
            return false;
        }
        return this.teleportToPos(d0, d1, d2);
    }

    protected boolean teleportToEntity(Entity in, double dis) {
        double d1 = in.getX() + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = in.getY() + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = in.getZ() + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportToPos(d1, d2, d3);
    }

    protected boolean teleportToPos(double x, double y, double z, double dis) {
        double d1 = x + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = z + (this.getRandom().nextDouble() - 0.5) * dis;
        if (this.getTarget() != null && Math.sqrt(this.getTarget().distanceToSqr(d1, d2, d3)) < 10.0) {
            return false;
        }
        return this.teleportToPos(d1, d2, d3);
    }

    protected boolean teleportEntityTo(EntityParasiteBase in, double x, double y, double z, double dis) {
        double d1 = x + (this.getRandom().nextDouble() - 0.5) * dis;
        double d2 = y + (double)(this.getRandom().nextInt(16) - 8) * dis;
        double d3 = z + (this.getRandom().nextDouble() - 0.5) * dis;
        return this.teleportTo(in, d1, d2, d3);
    }

    private boolean teleportToPos(double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((LivingEntity)this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        boolean flag = this.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (flag) {
            this.level().playSound((Player)null, this.xo, this.yo, this.zo, SRPSounds.ASSENDERMAN_PORTAL.get(), this.getSoundSource(), 1.0f, 1.0f);
            this.playSound(SRPSounds.ASSENDERMAN_PORTAL.get(), 1.0f, 1.0f);
        }
        return flag;
    }


    private boolean teleportTo(EntityParasiteBase in, double x, double y, double z) {
        EntityTeleportEvent.EnderEntity event = new EntityTeleportEvent.EnderEntity((LivingEntity)this, x, y, z);
        if (NeoForge.EVENT_BUS.post(event).isCanceled()) {
            return false;
        }
        boolean flag = in.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true);
        if (flag) {
            in.level().playSound((Player)null, in.xo, in.yo, in.zo, SoundEvents.ENDERMAN_TELEPORT, in.getSoundSource(), 1.0f, 1.0f);
            in.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
        return flag;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        this.spotCool = 0;
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source instanceof DamageSource) {
            for (int i = 0; i < 64; ++i) {
                if (!this.teleportRandomly()) continue;
                return true;
            }
            return false;
        }
        boolean flag = super.hurt(source, amount);
        if (source.is(DamageTypeTags.BYPASSES_ARMOR) && this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq) == 0) {
            this.teleportRandomly();
        }
        return flag;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            if (!this.hasTargetedEntity()) {
                this.setTargetedEntity(entityIn.getId());
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3, false, false));
            }
            if (this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
                SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
            }
            if (this.getRandom().nextInt(SRPConfigMobs.infendermantelefreq) == 0) {
                this.teleportRandomly();
            }
        }
        return flag;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.tpPlayerName = null;
    }

    public boolean isScreaming() {
        return (Boolean)this.entityData.get(SCREAMING);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.getBbHeight() * 0.88f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.ASSENDERMAN_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.ASSENDERMAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.ASSENDERMAN_DEATH.get();
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }
}

