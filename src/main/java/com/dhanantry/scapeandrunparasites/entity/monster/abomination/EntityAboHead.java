package com.dhanantry.scapeandrunparasites.entity.monster.abomination;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISwimmingDiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class EntityAboHead
extends EntityParasiteBase {
    public EntityAboHead(EntityType<? extends EntityAboHead> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.pureSneakPen, SRPConfig.pureInviPen));
        }
        this.xpReward = SRPAttributes.XP_PURE;
        this.type = (byte)51;
        this.foodSteal = SRPConfig.pureFoodSteal;
        this.MiniDamage = SRPConfigMobs.nuuhMinDamage;
        this.oneMindDeathValue = SRPConfig.pureOneMindDeathV;
        this.attackSpeedT = 6;
    }

    @Override
    public int getParasiteIDRegister() {
        return 76;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new EntityAISwimmingDiving((Mob)this, 0.12));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.NUUH_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.NUUH_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.272037);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.NUUH_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.NUUH_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.9f;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MOBSILENCE.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }
}

