package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;

public abstract class EntityPPrimitive
extends EntityPMalleable {
    private static final float PRIMITIVE_BLOCK_CHANCE = 0.1f;

    public EntityPPrimitive(EntityType<? extends EntityPPrimitive> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.primitiveWalls, false, null, SRPConfig.primitiveSneakPen, SRPConfig.primitiveInviPen));
        if (SRPConfig.canOrbAttack) {
            this.goalSelector.addGoal(2, new EntityAISkill(this, 80, 4, false, 21));
        }
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
        this.canModRender = 1;
        this.type = (byte)31;
        this.fuseOrb = 11;
        this.orbStartTimer = 30;
        this.foodSteal = SRPConfig.primitiveFoodSteal;
        this.orbItemCool = SRPConfig.primitiveItemOrbCooldown * 20;
        this.pointCap = SRPConfig.primitivePointCap;
        this.pointReduction = SRPConfig.primitivePointRed;
        this.chanceLearn = SRPConfig.primitiveChanceLe;
        this.chanceLearnFire = SRPConfig.primitiveChanceLeFire;
        this.DamageTypeCap = SRPConfig.primitivePointDamCap;
        this.MiniDamage = SRPConfig.primitiveMinDamage;
        this.regen = SRPConfig.primitiveRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.primitiveOneMindDeathV;
        this.regenEff = 5;
        this.foodRott = SRPConfig.primitiveFoodChance;
        this.foodRootNumber = SRPConfig.primitiveFoodAmount;
        this.cothSpread = SRPConfigSystems.cothPrimitive;
        this.valueEvDeath = SRPConfig.primitiveLoosingEPValue;
        this.setScentHPMultiplier(1.5f);
    }

    @Override
    protected void fearPlayer(LivingEntity player) {
    }

    protected void fearPlayer(LivingEntity player, float damageDealt) {
        if (player == null || player.level().isClientSide) {
            return;
        }
        if (damageDealt <= 8.0f) {
            return;
        }
        int level = 1 + Math.max(0, (int)Math.floor((damageDealt - 8.0f) / 4.0f));
        int cap = 3;
        level = Math.min(level, cap);
        int duration = 300 + 40 * (level - 1);
        duration = Mth.clamp((int)duration, (int)200, (int)500);
        int amplifier = level - 1;
        player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, duration, amplifier, false, true));
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        LivingEntity attacker;
        if (this.canRandomBlock() && amount > 0.0f && !source.is(DamageTypeTags.BYPASSES_ARMOR) && this.tickCount > 5 && this.getRandom().nextFloat() < 0.1f) {
            Entity src;
            this.level().playSound(null, this.getX(), this.getY() + (double)this.getBbHeight() * 0.5, this.getZ(), SoundEvents.SHIELD_BLOCK, SoundSource.HOSTILE, 0.7f, 1.15f + this.getRandom().nextFloat() * 0.15f);
            if (this.level() instanceof ServerLevel) {
                ((ServerLevel)this.level()).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + (double)this.getBbHeight() * 0.6, this.getZ(), 6, 0.08, 0.08, 0.08, 0.01);
            }
            if ((src = source.getEntity()) instanceof LivingEntity) {
                this.setLastHurtByMob((LivingEntity)src);
            }
            return false;
        }
        if (this.level().isClientSide) {
            return super.hurt(source, amount);
        }
        if (source.getEntity() instanceof LivingEntity && (attacker = (LivingEntity)source.getEntity()).hasEffect(SRPPotions.KILLPRI_E)) {
            int amp = attacker.getEffect(SRPPotions.KILLPRI_E).getAmplifier();
            float totalRed = Mth.clamp((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            return super.hurt(source, Math.max(0.0f, reduced));
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)51);
        double i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        int counttt = 0;
        for (int k2 = -1 * range; k2 <= 1 * range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= 1 * range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = BlockPos.containing(i3, i1, l);
                Block block = this.level().getBlockState(blockpos).getBlock();
                Block blockDown = this.level().getBlockState(blockpos.below()).getBlock();
                if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(4) != 0) continue;
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.gorePri.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
                if (++counttt < count) continue;
                return;
            }
        }
    }

    @Override
    protected void attackEntityFromCap(int go) {
        for (int i = 0; i < go && SRPConfig.paraGore; ++i) {
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
            double d7 = 0.8 / (d6 / 4.0 + 0.1);
            d4 = d4 * d7 * 2.0;
            EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
            bomb.setType((byte)2);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(2, 100);
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.gorePri.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.primitiveRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        this.attackEntityFromCap(4);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        float afterA;
        float afterH;
        float dealt;
        if (!(entityIn instanceof LivingEntity)) {
            return super.doHurtTarget(entityIn);
        }
        LivingEntity target = (LivingEntity)entityIn;
        float beforeH = target.getHealth();
        float beforeA = target.getAbsorptionAmount();
        boolean hit = super.doHurtTarget(entityIn);
        if (hit && !this.level().isClientSide && (dealt = Math.max(0.0f, beforeH + beforeA - ((afterH = target.getHealth()) + (afterA = target.getAbsorptionAmount())))) > 0.0f) {
            this.fearPlayer(target, dealt);
        }
        return hit;
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        if (!SRPConfigSystems.disloSameVersionDyeing || !this.disloNumberTwentytwo) {
            return super.onDeathDislo(cause);
        }
        boolean flag = super.onDeathDislo(cause);
        if (!flag && cause.getEntity() instanceof LivingEntity) {
            int count = SRPSaveData.get(this.level()).getCurrentCode(DimKeys.of(this.level()), 22);
            LivingEntity target = (LivingEntity)cause.getEntity();
            if (count >= 1) {
                target.removeEffect(SRPPotions.KILLADA_E);
                target.removeEffect(SRPPotions.KILLPUR_E);
                target.removeEffect(SRPPotions.KILLFER_E);
                target.removeEffect(SRPPotions.KILLCRU_E);
                target.removeEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLPRI_E, target, 1200, count);
            }
        }
        return flag;
    }

    protected boolean canRandomBlock() {
        return true;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 40.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
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
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag) {
            // empty if block
        }
        return flag;
    }
}

