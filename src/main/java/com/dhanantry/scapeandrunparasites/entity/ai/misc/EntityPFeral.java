package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIEvade;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSpawn;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;

public abstract class EntityPFeral
extends EntityParasiteBase
implements EntityCanSpawn {
    protected float regen;
    protected int regenEff;
    protected int regenUse;

    public EntityPFeral(EntityType<? extends EntityPFeral> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, false, null, SRPConfig.feralSneakPen, SRPConfig.feralInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, false, entity -> !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite), SRPConfig.feralSneakPen, SRPConfig.feralInviPen));
        }
        this.goalSelector.addGoal(2, new EntityAIEvade(this, 30, 5, 3.0));
        this.goalSelector.removeGoal(this.folow);
        this.xpReward = SRPAttributes.XP_FERAL;
        this.damageCap = SRPConfig.feralCap;
        this.canD = SRPConfig.feraldespawn;
        this.MiniDamage = SRPConfig.feralMinDamage;
        this.oneMindDeathValue = SRPConfig.feralOneMindDeathV;
        this.foodSteal = SRPConfig.feralFoodSteal;
        this.regen = SRPConfig.feralRegen * SRPConfig.globalHealthMultiplier;
        this.regenEff = 10;
        this.cothSpread = SRPConfigSystems.cothFeral;
        this.valueEvDeath = SRPConfig.feralLoosingEPValue;
        this.attackSpeedT = 14;
        this.setScentHPMultiplier(1.5f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            if (this.getRandom().nextInt(25) == 0) {
                for (int i = 0; i <= 1; ++i) {
                    this.spawnParticlesGoreBox(SRPEnumParticle.GSPLASH, 0, -1, -1, 0.1, 0.0);
                }
            }
        } else if (this.srpTicks == 10 && !this.isRemoved() && this.getHealth() > 0.0f && !this.isOnFire() && this.regen > 0.0f && this.killcount > 1.0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + this.regen);
            --this.regenUse;
            if (this.regenUse <= 0) {
                this.killcount -= 1.0;
                this.regenUse = this.regenEff;
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
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        LivingEntity attacker;
        if (this.level().isClientSide) {
            return super.hurt(source, amount);
        }
        if (source.getEntity() instanceof LivingEntity && (attacker = (LivingEntity)source.getEntity()).hasEffect(SRPPotions.KILLFER_E)) {
            int amp = Objects.requireNonNull(attacker.getEffect(SRPPotions.KILLFER_E)).getAmplifier();
            float totalRed = Mth.clamp((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            return super.hurt(source, Math.max(0.0f, reduced));
        }
        boolean tookDamage = super.hurt(source, amount);
        if (tookDamage && source.getEntity() instanceof LivingEntity && this.getRandom().nextDouble() < SRPConfig.feralMult) {
            double d2;
            double d5;
            double d1;
            double d4;
            double d0 = (float)this.getX() + this.level().random.nextFloat();
            double d3 = d0 - this.getX();
            double d6 = (float)Math.sqrt((double)(d3 * d3 + (d4 = (d1 = (double)((float)this.getY() + this.level().random.nextFloat())) - this.getY()) * d4 + (d5 = (d2 = (double)((float)this.getZ() + this.level().random.nextFloat())) - this.getZ()) * d5));
            if (d6 < 1.0E-4) {
                d6 = 1.0E-4;
            }
            d3 /= d6;
            d4 /= d6;
            d5 /= d6;
            double d7 = 0.8 / (d6 / 4.0 + 0.1);
            d3 = d3 * (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)) * 0.5;
            d4 = d4 * d7 * 1.0;
            d5 = d5 * d7 * 0.5;
            EntityGore bomb = new EntityGore(SRPEntities.GORE.get(), this.level());
            bomb.setType((byte)111);
            bomb.entityName = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()) != null ? Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType())).toString() : "unknown";
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3, d4, d5, 0.2, 0.5);
            this.level().addFreshEntity((Entity)bomb);
        }
        return tookDamage;
    }

    @Override
    protected void attackEntityFromEffects(int range, int count) {
        this.particleStatus((byte)51);
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
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
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
            bomb.setType((byte)1);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.2, 0.8);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    protected void spawnGore() {
        this.attackEntityFromEffects(2, 100);
        List<? extends Entity> serverList = SRPEntityUtil.allEntities(this.level());
        int count = 0;
        for (Entity entity : serverList) {
            if (!(entity instanceof EntityParasiteBase)) continue;
            ++count;
        }
        if (count < SRPConfig.worldMobCap) {
            EntityAta ata = new EntityAta(SRPEntities.GNAT.get(), this.level());
            ata.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)ata);
        }
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType())).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.feralRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        this.attackEntityFromCap(3);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        float after;
        float dealt;
        if (!(entityIn instanceof LivingEntity)) {
            return super.doHurtTarget(entityIn);
        }
        LivingEntity target = (LivingEntity)entityIn;
        float before = target.getHealth() + target.getAbsorptionAmount();
        boolean hit = super.doHurtTarget(entityIn);
        if (hit && !this.level().isClientSide && (dealt = Math.max(0.0f, before - (after = target.getHealth() + target.getAbsorptionAmount()))) > 0.0f) {
            this.fearPlayer(target, dealt);
        }
        return hit;
    }

    @Override
    public void onKillEntity(LivingEntity entityLivingIn) {
        super.onKillEntity(entityLivingIn);
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
                target.removeEffect(SRPPotions.KILLADA_E);
                target.removeEffect(SRPPotions.KILLPUR_E);
                target.removeEffect(SRPPotions.KILLCRU_E);
                target.removeEffect(SRPPotions.KILLNEX_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLFER_E, target, 1200, count);
            }
        }
        return flag;
    }

    @Override
    public void spawnEffectsGore() {
        int i;
        for (i = 0; i <= 100; ++i) {
            if (i % 5 != 0) continue;
            this.spawnParticlesGore(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
        for (i = 0; i <= 20; ++i) {
            if (i % 5 == 0) {
                this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
            }
            if (i % 5 != 0) continue;
            this.spawnParticles(SRPEnumParticle.GSPLASH, 0, -1, -1);
        }
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

