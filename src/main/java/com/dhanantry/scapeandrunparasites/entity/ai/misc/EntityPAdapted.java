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
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.neoforge.event.entity.living.MobDespawnEvent;

public abstract class EntityPAdapted
extends EntityPMalleable {
    public EntityPAdapted(EntityType<? extends EntityPAdapted> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.adaptedWalls, false, null, SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        if (SRPConfig.canOrbAttack) {
            this.goalSelector.addGoal(2, new EntityAISkill(this, 80, 4, false, 21));
        }
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.adaptedWalls, false, entity -> !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite), SRPConfig.adaptedSneakPen, SRPConfig.adaptedInviPen));
        }
        this.xpReward = SRPAttributes.XP_ADAPTED;
        this.canD = SRPConfig.adapteddespawn;
        this.damageCap = SRPConfig.adaptedCap;
        this.canModRender = 1;
        this.type = (byte)41;
        this.fuseOrb = 16;
        this.orbStartTimer = 15;
        this.foodSteal = SRPConfig.adaptedFoodSteal;
        this.orbItemCool = SRPConfig.adaptedItemOrbCooldown * 20;
        this.pointCap = SRPConfig.adaptedPointCap;
        this.pointReduction = SRPConfig.adaptedPointRed;
        this.chanceLearn = SRPConfig.adaptedChanceLe;
        this.chanceLearnFire = SRPConfig.adaptedChanceLeFire;
        this.DamageTypeCap = SRPConfig.adaptedPointDamCap;
        this.MiniDamage = SRPConfig.adaptedMinDamage;
        this.regen = SRPConfig.adaptedRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.adaptedOneMindDeathV;
        this.regenEff = 10;
        this.foodRott = SRPConfig.adaptedFoodChance;
        this.foodRootNumber = SRPConfig.adaptedFoodAmount;
        this.cothSpread = SRPConfigSystems.cothAdapted;
        this.valueEvDeath = SRPConfig.adaptedLoosingEPValue;
        this.setScentHPMultiplier(0.75f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public void checkDespawn() {
        if (!SRPConfigSystems.useEvolution) {
            super.checkDespawn();
        } else if (!this.level().isClientSide) {
            MobDespawnEvent.Result result;
            boolean flag = this.removeWhenFarAway(0.0);
            if (this.isPersistenceRequired()) {
                this.noActionTime = 0;
            } else if ((this.noActionTime & 0x1F) == 31 && (result = SRPEntityUtil.despawnResult(this)) != MobDespawnEvent.Result.DEFAULT) {
                if (result == MobDespawnEvent.Result.DENY) {
                    this.noActionTime = 0;
                } else {
                    SRPSaveData data = SRPSaveData.get(this.level());
                    if (this.tickCount > 10) {
                        data.setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueEvolutionDespawn, true, this.level(), true, 34);
                    }
                    this.spawnCyst();
                    this.storeBefDes();
                    this.discard();
                }
            } else {
                Player entity = this.level().getNearestPlayer((Entity)this, -1.0);
                if (entity != null) {
                    SRPSaveData data;
                    double d0 = entity.getX() - this.getX();
                    double d1 = entity.getY() - this.getY();
                    double d2 = entity.getZ() - this.getZ();
                    double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                    if (flag && d3 > 16384.0) {
                        data = SRPSaveData.get(this.level());
                        if (this.tickCount > 10) {
                            data.setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueEvolutionDespawn, true, this.level(), true, 35);
                        }
                        this.spawnCyst();
                        this.storeBefDes();
                        this.discard();
                    }
                    if (this.noActionTime > 600 && this.getRandom().nextInt(800) == 0 && d3 > 1024.0 && flag) {
                        data = SRPSaveData.get(this.level());
                        if (this.tickCount > 10) {
                            data.setTotalKills(DimKeys.of(this.level()), SRPConfigSystems.valueEvolutionDespawn, true, this.level(), true, 36);
                        }
                        this.spawnCyst();
                        this.storeBefDes();
                        this.discard();
                    } else if (d3 < 1024.0) {
                        this.noActionTime = 0;
                    }
                }
            }
        }
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
        player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, duration, amplifier, false, false));
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
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        LivingEntity attacker;
        if (this.level().isClientSide) {
            return super.hurt(source, amount);
        }
        if (source.getEntity() instanceof LivingEntity && (attacker = (LivingEntity)source.getEntity()).hasEffect(SRPPotions.KILLADA_E)) {
            int amp = Objects.requireNonNull(attacker.getEffect(SRPPotions.KILLADA_E)).getAmplifier();
            float totalRed = Mth.clamp((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            return super.hurt(source, Math.max(0.0f, reduced));
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)52);
        double i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        int counttt = 0;
        for (int k2 = -1 * range; k2 <= range && SRPConfig.paraGore; ++k2) {
            for (int l2 = -1 * range; l2 <= range; ++l2) {
                double i3 = l1 + (double)k2;
                double l = i2 + (double)l2;
                BlockPos blockpos = BlockPos.containing(i3, i1, l);
                Block block = this.level().getBlockState(blockpos).getBlock();
                Block blockDown = this.level().getBlockState(blockpos.below()).getBlock();
                if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(4) != 0) continue;
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreAda.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
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
            bomb.setType((byte)3);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(3, 100);
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreAda.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType())).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.adaptedRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        this.attackEntityFromCap(5);
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
                target.removeEffect(SRPPotions.KILLPRI_E);
                target.removeEffect(SRPPotions.KILLPUR_E);
                target.removeEffect(SRPPotions.KILLFER_E);
                target.removeEffect(SRPPotions.KILLCRU_E);
                target.removeEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLADA_E, target, 1200, count);
            }
        }
        return flag;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 60.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    @Override
    public void spawnEffectsGore() {
        for (int i = 0; i <= 80; ++i) {
            if (i % 3 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
        }
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        return super.scaryOrbEffect(in, mobs);
    }
}

