package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class EntityLodo
extends EntityParasiteBase {
    private int totalGrowtime = 10;
    private int actualGrowtime = 0;
    protected double buried;

    public EntityLodo(EntityType<? extends EntityLodo> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = SRPAttributes.XP_LiTTLE;
        this.totalGrowtime = this.getRandom().nextInt(60) + 60;
        this.killcount = -10.0;
        this.type = 1;
        this.buried = -1.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 5;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new AvoidEntityGoal((PathfinderMob)this, LivingEntity.class, (Predicate)new Predicate<LivingEntity>(){

            public boolean test(@Nullable LivingEntity entity) {
                return !(entity instanceof WaterAnimal) && !(entity instanceof Creeper) && !(entity instanceof EntityParasiteBase) && !(entity instanceof Animal);
            }
        }, 8.0f, 1.0, 1.0, (Predicate<LivingEntity>)(e -> true)));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.LODO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.LODO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.2);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.LODO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.LODO_KD_RESISTANCE);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.growTimer();
        this.growStage();
        this.buried();
    }

    public boolean buried() {
        if (this.buried == 1.0) {
            this.level().playLocalSound(this.x() + 0.5, this.y() + 0.5, this.z() + 0.5, SRPSounds.LODO_EMERGE.get(), SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
        if (this.buried >= 0.0) {
            this.getNavigation().stop();
            Mot.setPosX(this, this.xo);
            Mot.setPosZ(this, this.zo);
            BlockState id = this.level().getBlockState(BlockPos.containing(this.getX(), this.getY(), this.getZ()).below());
            this.buried -= 0.02;
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02);
            }
            return true;
        }
        if (this.getParasiteStatus() == 3) {
            this.setParasiteStatus(0);
        }
        return false;
    }

    private double x() {
        return this.getX();
    }

    private double y() {
        return this.getY();
    }

    private double z() {
        return this.getZ();
    }

    @Override
    protected void doPush(Entity entityIn) {
        super.doPush(entityIn);
        if (entityIn instanceof LivingEntity && this.tickCount % 20 == 0) {
            SRPPotions.applyStackPotion(SRPPotions.COTH_E, (LivingEntity)entityIn, 100, 0);
        }
    }

    protected void growStage() {
        if (!this.level().isClientSide && (this.actualGrowtime > this.totalGrowtime && ParasiteEventEntity.canSpawnNext || this.killcount > 1000.0)) {
            this.playSound(SRPSounds.LODO_MUDO.get(), 1.0f, 1.0f);
            ParasiteEventEntity.spawnNext(this, new EntityMudo(SRPEntities.RUPTER.get(), this.level()), true, false);
        }
    }

    protected void growTimer() {
        if (!this.level().isClientSide && this.tickCount % 20 == 0) {
            ++this.actualGrowtime;
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.3f;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.LODO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.LODO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.LODO_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.MOBSILENCE.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("ruptergrow", this.actualGrowtime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ruptergrow", 99)) {
            this.actualGrowtime = compound.getInt("ruptergrow");
        }
    }

    public void setFloorTimer() {
        this.buried = 1.0;
    }

    public double getFloorTimer() {
        return this.buried;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 50) {
            this.buried = 1.0;
        } else {
            super.handleEntityEvent(id);
        }
    }
}

