package com.dhanantry.scapeandrunparasites.entity.monster.crude;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAttackMeleeStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIGetFollowers;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EntityMes
extends EntityPMalleable {
    public EntityMes(EntityType<? extends EntityMes> type, Level worldIn) {
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
        return 80;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(0, new FloatGoal((Mob)this));
        this.goalSelector.addGoal(3, new EntityAIAttackMeleeStatus(this, 1.0, false, 0.0));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
        this.goalSelector.addGoal(6, new EntityAIGetFollowers(this, 1, 16));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPMalleable.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.THRALL_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.THRALL_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.4);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.THRALL_KD_RESISTANCE);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.THRALL_ATTACK_DAMAGE);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.primitiveFollow);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        boolean flag = super.doHurtTarget(entityIn);
        if (flag && entityIn instanceof Player && ((Player)entityIn).getName().getString().equals(SRPEntityUtil.getCustomNameTag(this))) {
            entityIn.hurt(this.damageSources().mobAttack(this), (float)(this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * 0.5));
        }
        return flag;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.73f;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityInfPlayer(SRPEntities.SIM_ADVENTURER.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ZOMBIE_STEP;
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(this.getStepSound(), 0.15f, 1.0f);
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
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
            }
        }
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
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
    protected boolean applyColdBiome() {
        boolean flag;
        Biome c = this.level().getBiome(this.blockPosition()).value();
        boolean bl = flag = this.getRandom().nextInt(1) == 0 && this.canChangeVariant;
        if (c.coldEnoughToSnow(this.blockPosition()) || flag) {
            switch (this.getRandom().nextInt(5)) {
                case 0: {
                    this.setSkin(121);
                    break;
                }
                case 1: {
                    this.setSkin(122);
                    break;
                }
                case 2: {
                    this.setSkin(123);
                    break;
                }
                case 3: {
                    this.setSkin(124);
                    break;
                }
                case 4: {
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 1.5);
                    this.setSkin(125);
                }
            }
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 0.6);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 1.4);
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue() * 1.3);
            return true;
        }
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SRPSounds.MES_GROWL.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SRPSounds.MES_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return SRPSounds.MES_DEATH.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData livingdata) {
        SpawnGroupData floo = super.finalizeSpawn(levelAccessor, difficulty, spawnType, livingdata);
        SRPReference.setSimPlayerNameSub1(this);
        if (this.getRandom().nextDouble() < SRPConfig.variantChance || this.phaseCreated >= SRPConfigSystems.evolutionParasiteAlwaysVariant) {
            switch (2) {
                case 2: {
                    this.setSkin(1);
                    this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(SRPAttributes.THRALL_HEALTH * 0.5);
                    this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(SRPAttributes.THRALL_ATTACK_DAMAGE * 1.5);
                    this.setHealth((float)this.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
                }
            }
        }
        return floo;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
    }
}

