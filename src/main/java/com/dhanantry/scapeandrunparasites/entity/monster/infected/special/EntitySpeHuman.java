package com.dhanantry.scapeandrunparasites.entity.monster.infected.special;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPAssimara;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.neoforged.neoforge.network.PacketDistributor;

public class EntitySpeHuman
extends EntityPAssimara {
    private int host;
    double randomX;
    double randomZ;

    public EntitySpeHuman(EntityType<? extends EntitySpeHuman> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 1;
        this.type = (byte)11;
    }

    @Override
    public int getParasiteIDRegister() {
        return 324;
    }

    @Override
    public int canSpawnByIDData() {
        return SRPConfigMobs.infhumanCanSpawnAssimilatedNat;
    }

    @Override
    public int getIDSpawn() {
        return 6;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.08));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.5, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 20, 0, 1.0, true, 3, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPAssimara.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.MARHUMAN_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.MARHUMAN_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.27);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.MARHUMAN_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.MARHUMAN_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.assimaraFollow);
        return builder;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 60.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.getHealth() <= 0.0f || this.deathTime > 0 || this.isRemoved()) {
            if (this.isPassenger()) {
                this.stopRiding();
            }
            this.setTarget(null);
            this.setParasiteStatus(6);
            return;
        }
        if (!this.level().isClientSide && this.srpTicks == 10 && this.getTarget() != null && this.getTarget().distanceToSqr((Entity)this) < 6.25 && !this.getTarget().isVehicle()) {
            this.setParasiteStatus(3);
            this.startRiding((Entity)this.getTarget(), true);
            this.getTarget().addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));
            if (this.getTarget() instanceof Player) {
                this.getServer().getPlayerList().broadcastAll(new ClientboundSetPassengersPacket(this.getTarget()));
            }
        }
        if (this.getVehicle() instanceof LivingEntity) {
            LivingEntity riding = (LivingEntity)this.getVehicle();
            this.randomPush(riding, 0.13);
            riding.addEffect(new MobEffectInstance(SRPPotions.NOVISION_E, 20, 0, false, false));
            riding.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, false, false));
            riding.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 0, false, false));
            riding.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, -200, false, false));
            if (!this.level().isClientSide && this.srpTicks == 10) {
                this.doHurtTarget((Entity)riding);
            }
        }
    }

    public void randomPush(LivingEntity player, double strength) {
        if (this.srpTicks == 10 || this.randomX == 0.0) {
            this.randomX = (Math.random() - 0.5) * 2.0;
            this.randomZ = (Math.random() - 0.5) * 2.0;
            double magnitude = Math.sqrt(this.randomX * this.randomX + this.randomZ * this.randomZ);
            if (magnitude == 0.0) {
                return;
            }
            this.randomX /= magnitude;
            this.randomZ /= magnitude;
        }
        Mot.addX(player, this.randomX * strength);
        Mot.addZ(player, this.randomZ * strength);
        if (this.srpTicks == 10 && player instanceof ServerPlayer && !this.level().isClientSide) {
            PacketDistributor.sendToPlayer((ServerPlayer)player, new QlipShakePayload(250, 0, true, false, 4.0f));
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
                    } else if (this.getVehicle() == null) {
                        this.setParasiteStatus(Math.min(k, 1));
                    }
                } else {
                    this.setParasiteStatus(0);
                    this.setTarget(null);
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (this.getHealth() <= 0.0f || this.deathTime > 0 || this.isRemoved()) {
            return false;
        }
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && this.getRandom().nextDouble() < (double)SRPConfig.infectedBleedingChance && entityIn instanceof LivingEntity) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.73f;
    }


    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INFECTEDHUMAN_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INFECTEDHUMAN_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INFECTEDHUMAN_DEATH.get();
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
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            this.setSkin(this.level().random.nextInt(3) + 1);
        }
        return floo;
    }
}

