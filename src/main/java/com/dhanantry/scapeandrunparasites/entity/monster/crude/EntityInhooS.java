package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCrude;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityInhooS
extends EntityPCrude {
    public boolean disloNumberTwenty = false;

    public EntityInhooS(EntityType<? extends EntityInhooS> type, Level worldIn) {
        super(type, worldIn);
        this.canModRender = 0;
        this.type = (byte)11;
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.infectedSneakPen, SRPConfig.infectedInviPen));
        }
    }

    @Override
    public int getParasiteIDRegister() {
        return 39;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 0.0));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPCrude.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.INHOOS_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.INHOOS_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.12);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.INHOOS_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.INHOOS_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.infectedFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (SRPConfigSystems.disloGiveBodies && this.isAlive() && this.srpTicks == 10 && this.disloNumberTwenty) {
            ParasiteEventEntity.spawnNext(this, ParasiteEventEntity.getRandomFeral(this.level()), true, false);
            return;
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return this.getBbHeight();
    }

    @Override
    protected void placeNidus() {
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.INHOOS_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.INHOOS_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.INHOOS_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SRPSounds.LITE_FLESH_SLIDE.get(), 0.3f, 1.0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("dsltwenty", this.disloNumberTwenty);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("dsltwenty", 99)) {
            this.disloNumberTwenty = compound.getBoolean("dsltwenty");
        }
    }
}

