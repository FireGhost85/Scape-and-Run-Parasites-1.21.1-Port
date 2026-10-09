package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidEntityStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAvoidOrAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
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
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class EntityMudo
extends EntityParasiteBase {
    private static final EntityDataAccessor<Byte> CLIMBING = SynchedEntityData.defineId(EntityMudo.class, EntityDataSerializers.BYTE);

    public EntityMudo(EntityType<? extends EntityMudo> type, Level worldIn) {
        super(type, worldIn);
        this.type = (byte)5;
        this.MiniDamage = SRPConfigMobs.mudoMinDamage;
        this.xpReward = SRPAttributes.XP_LiTTLE;
        this.attackSpeedT = 10;
    }

    @Override
    public int getParasiteIDRegister() {
        return 12;
    }

    @Override
    public void applyBonuses(SRPSaveData sabe, Level world) {
        super.applyBonuses(sabe, world);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        if (SRPConfigSystems.useEvolution) {
            byte phase = sabe.getEvolutionPhase(DimKeys.of(world));
            if (phase >= SRPConfigSystems.evolutionMudoAttack) {
                if (phase >= SRPConfigSystems.evolutionAssimilatedDehiding) {
                    this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 10, true, false, new Predicate<Mob>(){

                        public boolean test(@Nullable Mob entity) {
                            return !(entity instanceof Monster) && !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                        }
                    }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
                } else {
                    this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 10, true, false, new Predicate<Mob>(){

                        public boolean test(@Nullable Mob entity) {
                            return !entity.hasEffect(SRPPotions.COTH_E) && !(entity instanceof Monster) && !(entity instanceof WaterAnimal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                        }
                    }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
                }
            } else {
                this.goalSelector.addGoal(2, new EntityAIMudoInfest(this, 1.0));
                this.goalSelector.addGoal(4, new EntityAIAvoidOrAttack(this, 0.0f, 10, 2));
                this.goalSelector.addGoal(5, new EntityAIAvoidEntityStatus<Mob>(this, Mob.class, new Predicate<Mob>(){

                    public boolean test(@Nullable Mob entity) {
                        return !(entity instanceof WaterAnimal) && !(entity instanceof EntityParasiteBase) && !(entity instanceof Animal) && !(entity instanceof Villager);
                    }
                }, 8.0f, 1.3));
            }
        }
    }

    @Override
    public void applyGene(boolean[] kool, float[] goon) {
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        super.setTarget(entitylivingbaseIn);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityAISkill(this, 40, 100, 5, true, 14));
        this.setskillLeapValues(0.7f, 2.5, 0);
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, -1.0));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal((Mob)this, 0.4f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        if (SRPConfigMobs.mudoAnimalAttacking && !SRPConfigSystems.useEvolution) {
            this.targetSelector.addGoal(4, new NearestAttackableTargetGoal((PathfinderMob)this, Animal.class, true));
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.srpTicks == 10 && !this.level().isClientSide && this.killcount >= (double)SRPConfigMobs.mudoTunnelValue && this.phaseCreated < SRPConfigMobs.mudoTunnelPhase && this.getRandom().nextInt(30) == 0) {
            if (this.getTarget() != null) {
                return;
            }
            if (this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR && this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below())) {
                this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.buglin.get().defaultBlockState());
                this.killcount -= (double)SRPConfigMobs.mudoTunnelValue;
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.MUDO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.MUDO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.MUDO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.MUDO_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, 32.0);
        return builder;
    }

    @Override
    public int getMaxManualVariants() {
        return 8;
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof LivingEntity) {
            switch (this.getSkin()) {
                case 5: {
                    SRPPotions.applyStackPotion(SRPPotions.VIRA_E, (LivingEntity)entityIn, 100, 0);
                    break;
                }
                case 6: {
                    SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)entityIn, 100, 0);
                }
            }
            ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
            if (!((LivingEntity)entityIn).hasEffect(SRPPotions.COTH_E)) {
                ((LivingEntity)entityIn).addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
        return flag;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        if (this.killcount >= 0.0) {
            this.killcount += 1.0;
        }
        if (!this.level().isClientSide && SRPConfigSystems.useEvolution) {
            SRPSaveData data = SRPSaveData.get(this.level());
            data.setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueKill, true, this.level(), true, 37);
        }
        if (!entityLivingIn.hasEffect(SRPPotions.COTH_E)) {
            entityLivingIn.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
        }
        ParasiteEventEntity.convertEntity(entityLivingIn, entityLivingIn.getPersistentData(), true, SRPConfigSystems.COTHVictimParasite);
        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, false, false));
        this.setWait(10);
        if (!this.level().isClientSide && this.killcount > (double)SRPConfigMobs.mudoMangler && !this.isRemoved() && ParasiteEventEntity.canSpawnNext) {
            ParasiteEventEntity.spawnNext(this, new EntityNuuh(SRPEntities.MANGLER.get(), this.level()), true, false);
        }
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 60.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    public double getMountedYOffset() {
        return this.getBbHeight() * 0.5f;
    }

    protected PathNavigation createNavigation(Level worldIn) {
        return new WallClimberNavigation((Mob)this, worldIn);
    }

    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.setBesideClimbableBlock(this.horizontalCollision);
            if (this.srpTicks == 10 && this.killcount > (double)SRPConfigMobs.mudoMangler && ParasiteEventEntity.canSpawnNext) {
                ParasiteEventEntity.spawnNext(this, new EntityNuuh(SRPEntities.MANGLER.get(), this.level()), true, true);
            }
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

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIMBING, (byte) (0));
    }

    public boolean onClimbable() {
        return this.isBesideClimbableBlock();
    }

    public boolean isBesideClimbableBlock() {
        return ((Byte)this.entityData.get(CLIMBING) & 1) != 0;
    }

    public void setBesideClimbableBlock(boolean climbing) {
        byte b0 = (Byte)this.entityData.get(CLIMBING);
        if (this.getTarget() != null) {
            if (!this.hasLineOfSight((Entity)this.getTarget())) {
                if (this.distanceToSqr((Entity)this.getTarget()) < 100.0) {
                    b0 = (byte)(b0 & 0xFFFFFFFE);
                    this.entityData.set(CLIMBING, (byte) (b0));
                    return;
                }
            } else if (this.getTarget().getY() + 1.0 < this.getY()) {
                b0 = (byte)(b0 & 0xFFFFFFFE);
                this.entityData.set(CLIMBING, (byte) (b0));
                return;
            }
        }
        b0 = climbing ? (byte)(b0 | 1) : (byte)(b0 & 0xFFFFFFFE);
        this.entityData.set(CLIMBING, (byte) (b0));
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 && SRPConfigMobs.lodoEnabled || this.canChangeVariant) {
                    ParasiteEventEntity.spawnNext(this, new EntityLodo(SRPEntities.BUGLIN.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        if (!this.level().isClientSide) {
            this.rollTextureVariantWeighted();
        }
        return floo;
    }

    private void rollTextureVariantWeighted() {
        float r = this.getRandom().nextFloat();
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant || this.canChangeVariant) {
            switch (this.getRandom().nextInt(2)) {
                case 0: {
                    this.setSkin(5);
                    break;
                }
                case 1: {
                    this.setSkin(6);
                }
            }
        } else if (r < 2.0E-4f) {
            this.setSkin(2);
        } else if (r < 0.05f) {
            this.setSkin(7);
        } else if (r < 0.15f) {
            this.setSkin(1);
        } else if (r < 0.25f) {
            this.setSkin(4);
        } else if (r < 0.4f) {
            this.setSkin(3);
        }
    }

    protected SoundEvent getAmbientSound() {
        if (this.getParasiteStatus() != 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.MUDO_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MUDO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MUDO_DEATH.get();
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), this.getSoundVolume(), this.getVoicePitch());
    }

    protected SoundEvent getStepSound() {
        return SRPSounds.SMALL_STEPS.get();
    }

    static class EntityAIMudoInfest
    extends Goal {
        private final EntityMudo parent;
        private int count;

        public EntityAIMudoInfest(EntityMudo animal, double speedIn) {
            this.parent = animal;
            this.count = 0;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            ++this.count;
            if (this.count >= 20) {
                boolean tar = this.parent.getTarget() == null;
                boolean nav = this.parent.getNavigation().isDone();
                String na = "";
                if (!tar) {
                    na = this.parent.getTarget().getName().getString();
                }
                this.count = 0;
                return this.parent.getTarget() == null && this.parent.getNavigation().isDone();
            }
            return false;
        }

        public void tick() {
            AABB axisalignedbb = new AABB(this.parent.getX(), this.parent.getY(), this.parent.getZ(), this.parent.getX() + 1.0, this.parent.getY() + 1.0, this.parent.getZ() + 1.0).inflate(12.0, 3.0, 12.0);
            List<? extends LivingEntity> moblist = this.parent.level().getEntitiesOfClass(LivingEntity.class, axisalignedbb);
            for (LivingEntity mob : moblist) {
                if (mob == this.parent || mob instanceof Monster) continue;
                if (!mob.hasEffect(SRPPotions.COTH_E) && this.parent.hasLineOfSight((Entity)mob) && this.parent.getNavigation().moveTo((Entity)mob, 1.3) && this.parent.distanceToSqr((Entity)mob) < 9.0) {
                    this.spawnLingeringCloud();
                    this.parent.playSound(SRPSounds.MUDO_CLOUD.get(), 2.0f, 1.0f);
                }
                if (this.parent.getNavigation().isDone()) continue;
                return;
            }
        }

        private void spawnLingeringCloud() {
            AreaEffectCloud entityareaeffectcloud = new AreaEffectCloud(this.parent.level(), this.parent.getX(), this.parent.getY(), this.parent.getZ());
            entityareaeffectcloud.setRadius(this.parent.getBbWidth() * 4.0f);
            entityareaeffectcloud.setRadiusOnUse(-0.5f);
            entityareaeffectcloud.setWaitTime(10);
            entityareaeffectcloud.setDuration(entityareaeffectcloud.getDuration() * 2);
            entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float)entityareaeffectcloud.getDuration());
            entityareaeffectcloud.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 1, false, false));
            this.parent.level().addFreshEntity((Entity)entityareaeffectcloud);
        }
    }
}

