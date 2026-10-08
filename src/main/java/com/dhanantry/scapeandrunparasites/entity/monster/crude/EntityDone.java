package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LiquidBlock;

public class EntityDone
extends EntityPMalleable {
    private static final EntityDataAccessor<Integer> TARGET_ENTITY = SynchedEntityData.defineId(EntityDone.class, EntityDataSerializers.INT);
    private LivingEntity targetedEntity;
    private int pulling;
    private boolean canPull;

    public EntityDone(EntityType<? extends EntityDone> type, Level worldIn) {
        super(type, worldIn);
        this.borderOrb = -1;
        this.canModRender = 1;
        this.type = (byte)11;
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.primitiveWalls, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.primitiveWalls, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        }
        this.xpReward = SRPAttributes.XP_PRIMITIVE;
        this.damageCap = SRPConfig.primitiveCap;
        this.canD = SRPConfig.primitivedespawn;
        this.type = (byte)31;
        this.foodSteal = SRPConfig.primitiveFoodSteal;
        this.pointCap = SRPConfig.primitivePointCap;
        this.pointReduction = SRPConfig.primitivePointRed;
        this.chanceLearn = SRPConfig.primitiveChanceLe;
        this.chanceLearnFire = SRPConfig.primitiveChanceLeFire;
        this.DamageTypeCap = SRPConfig.primitivePointDamCap;
        this.MiniDamage = SRPConfig.primitiveMinDamage;
        this.regen = SRPConfig.primitiveRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.primitiveOneMindDeathV;
        this.regenEff = 1;
        this.valueEvDeath = SRPConfig.primitiveLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 319;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TARGET_ENTITY, 0);
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.3, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPMalleable.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.DONE_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.DONE_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.DONE_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.DONE_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
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
                    this.attackEntityAsMobMinimum(this.getTarget(), 0.02f);
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

    @Override
    public void applyGene(boolean[] kool, float[] goon) {
        super.applyGene(kool, goon);
        this.geneWaterleap = true;
    }

    @Override
    protected void handleWater(boolean check) {
        if (check && this.level().getBlockState(this.blockPosition()).getBlock() instanceof LiquidBlock && this.getTarget() != null) {
            ++this.liquidLeap;
            if (this.liquidLeap > 8) {
                this.liquidLeap = 8;
            }
        }
        if (this.liquidLeap >= 1) {
            if (this.geneWaterleap) {
                double h = 0.3;
                double str = 1.2;
                this.getNavigation().stop();
                LivingEntity entitylivingbase = this.getTarget();
                if (entitylivingbase != null) {
                    Block bl = this.level().getBlockState(this.blockPosition()).getBlock();
                    if (!this.level().getBlockState(this.blockPosition()).getCollisionShape(this.level(), this.blockPosition()).isEmpty()) {
                        h = 0.3;
                        str = 1.0;
                    }
                    --this.liquidLeap;
                    double dd0 = entitylivingbase.getX() - this.getX();
                    double dd1 = entitylivingbase.getZ() - this.getZ();
                    float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                    Mot.addX(this, dd0 / (double)f * str * (double)0.8f + this.getDeltaMovement().x * (double)0.2f);
                    Mot.addZ(this, dd1 / (double)f * str * (double)0.8f + this.getDeltaMovement().z * (double)0.2f);
                    Mot.setY(this, h);
                    this.lookAt((Entity)entitylivingbase);
                }
            } else {
                --this.liquidLeap;
            }
        }
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && !this.hasTargetedEntity()) {
            this.setTargetedEntity(entityIn.getId());
            ((LivingEntity)entityIn).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 3, false, false));
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
        return SRPSounds.DONE_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        if (this.getRandom().nextBoolean() && this.getHitStatus() > 0) {
            return SRPSounds.MOBSILENCE.get();
        }
        return SRPSounds.DONE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.DONE_DEATH.get();
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    @Override
    protected void spawnGore() {
        int range = 2;
        double i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = BlockPos.containing(i3, i1, l);
                Block block = this.level().getBlockState(blockpos).getBlock();
                Block blockDown = this.level().getBlockState(blockpos.below()).getBlock();
                if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(3) != 0) continue;
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
            }
        }
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.primitiveRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        for (int i = 0; i < 4 && SRPConfig.paraGore; ++i) {
            double d0 = (float)this.getX() + this.level().random.nextFloat();
            double d1 = (float)this.getY() + this.level().random.nextFloat();
            double d2 = (float)this.getZ() + this.level().random.nextFloat();
            double d3 = d0 - this.getX();
            double d4 = d1 - this.getY();
            double d5 = d2 - this.getZ();
            double d6 = (float)Math.sqrt((double)(d3 * d3 + d4 * d4 + d5 * d5));
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.5 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
            bomb.setType((byte)2);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.15, 0.55);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    public void spawnEffectsGore() {
        for (int i = 0; i <= 60; ++i) {
            if (i % 4 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 2, -1, -1);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        return floo;
    }
}

