package com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGiveEffectsArea;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIWaterLeapAtTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPPreeminent;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.phys.AABB;

public class EntityVesta
extends EntityPPreeminent
implements EntityBodyParts {
    private EntityBody head;

    public EntityVesta(EntityType<? extends EntityVesta> type, Level worldIn) {
        super(type, worldIn);
        this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        this.borderOrb = -1;
        this.canTeleportToo = true;
        this.noCulling = true;
        this.type = (byte)31;
        this.attackSpeedT = 10;
        this.head = new EntityBody(this, 3.8f, 3.8f, 1.0f, 3.1f, 1.6f, 1, 1, false, 0.2f);
    }

    @Override
    public int getParasiteIDRegister() {
        return 88;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.15));
        this.goalSelector.addGoal(2, new EntityAIWaterLeapAtTargetStatus(this, 0.7f, 1.5, 3, 20, 0));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 8.0));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 2, 16));
        this.goalSelector.addGoal(3, new EntityAIGiveEffectsArea(this, SRPAttributes.VESTA_CD, SRPAttributes.VESTA_RANGE, SRPConfigMobs.vestaeffects));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPPreeminent.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VESTA_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VESTA_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.242);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 2.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VESTA_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.preeminentFollow);
        return builder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.head.tick();
        if (this.srpTicks == 25) {
            AABB axisalignedbb = new AABB(this.blockPosition()).inflate(32.0);
            List<? extends EntityParasiteBase> moblist = this.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
            for (EntityParasiteBase mob : moblist) {
                SRPPotions.applyStackPotion(SRPPotions.LINK_E, (LivingEntity)mob, 6666, 0);
                SRPPotions.applyStackPotion(SRPPotions.FOSTER_E, (LivingEntity)mob, 6666, 0);
            }
        }
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
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            ParasiteEventEntity.orbApplyEffects(in, this, SRPConfigMobs.vestaOrbEffects, mobs);
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.5f;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.VESTA_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.VESTA_HURT.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.HEAVY_STEPS_MULTIPLE.get(), 0.15f, 1.0f);
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.VESTA_DEATH.get();
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
        this.particleStatus((byte)5);
    }

    protected float getSoundVolume() {
        return 5.0f;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (2) {
                case 2: {
                    this.setSkin(1);
                    this.getAttribute(Attributes.ARMOR).setBaseValue(SRPAttributes.VESTA_ARMOR * 1.5);
                    this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.1694);
                }
            }
        }
        return floo;
    }
}

