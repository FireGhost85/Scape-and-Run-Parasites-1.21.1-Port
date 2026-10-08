package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanColony;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public abstract class EntityPPreeminent
extends EntityPMalleable
implements EntityCanSummon,
EntityCanColony {
    protected int totalP;
    protected int actualP;
    protected int[] mobID;
    protected int[] mobPT;
    private int ggg;
    protected boolean canTeleportToo;

    public EntityPPreeminent(EntityType<? extends EntityPPreeminent> type, Level worldIn) {
        super(type, worldIn);
        this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, SRPConfig.preeminentWalls, false, null, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, SRPConfig.preeminentWalls, false, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, SRPConfig.preeminentSneakPen, SRPConfig.preeminentInviPen));
        }
        this.goalSelector.removeGoal(this.folow);
        this.borderOrb = -1;
        this.canModRender = 0;
        this.xpReward = SRPAttributes.XP_PREE;
        this.canD = SRPConfig.preeminentdespawn;
        this.damageCap = SRPConfig.preeminentCap;
        this.fuseTime = 70;
        this.type = (byte)61;
        this.killcount = 10.0;
        this.fuseOrb = 13;
        this.orbStartTimer = 15;
        this.foodSteal = SRPConfig.preeminentFoodSteal;
        this.orbItemCool = SRPConfig.preeminentItemOrbCooldown * 20;
        this.pointCap = SRPConfig.preeminentPointCap;
        this.pointReduction = SRPConfig.preeminentPointRed;
        this.chanceLearn = SRPConfig.preeminentChanceLe;
        this.chanceLearnFire = SRPConfig.preeminentChanceLeFire;
        this.DamageTypeCap = SRPConfig.preeminentPointDamCap;
        this.MiniDamage = SRPConfig.preeminentMinDamage;
        this.regen = SRPConfig.preeminentRegen * SRPConfig.globalHealthMultiplier;
        this.oneMindDeathValue = SRPConfig.preeminentOneMindDeathV;
        this.regenEff = 25;
        this.foodRott = SRPConfig.preeminentFoodChance;
        this.foodRootNumber = SRPConfig.preeminentFoodAmount;
        this.cothSpread = SRPConfigSystems.cothPure;
        this.totalP = SRPConfig.preeminentFlamTotal;
        this.mobID = new int[this.totalP];
        this.mobPT = new int[this.totalP];
        for (int i = 0; i < this.mobID.length; ++i) {
            this.mobID[i] = -777;
        }
        this.valueEvDeath = SRPConfig.preeminentLoosingEPValue;
        this.setScentHPMultiplier(0.25f);
    }

    @Override
    public boolean onlySpawnInside() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        ++this.ggg;
        if (this.ggg > 80) {
            this.ggg = 0;
        }
        if (this.getTarget() != null) {
            this.summonFlam(this.getTarget());
        }
    }

    @Override
    protected void fearPlayer(LivingEntity player) {
        try {
            if (player == null) {
                return;
            }
            if (!this.hasLineOfSight((Entity)player)) {
                return;
            }
            if (!player.hasEffect(SRPPotions.FEAR_E)) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 2, false, false));
            } else if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() < 2) {
                player.addEffect(new MobEffectInstance(SRPPotions.FEAR_E, 300, 2, false, false));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag) {
            // empty if block
        }
        return flag;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        if (distance >= 150.0f) {
            super.causeFallDamage(distance, damageMultiplier, damageSource);
        }
        return false;
    }

    protected boolean summonFlam(LivingEntity in) {
        if (this.level().isClientSide || !SRPConfigMobs.flamEnabled) {
            return false;
        }
        if (this.ggg != 40) {
            return false;
        }
        double dis = this.distanceToSqr((Entity)in);
        this.checkID();
        if (this.getActualParasites() < this.getTotalParasites()) {
            this.resetIdleTime();
            EntityFlam wa = new EntityFlam(SRPEntities.SUCCOR.get(), this.level());
            float f19 = Mth.sin((float)(this.getYRot() * ((float)Math.PI / 180) - this.rotA * 0.01f));
            float f14 = 0.17453292f;
            float f16 = Mth.cos((float)f14);
            float f4 = Mth.cos((float)(this.getYRot() * ((float)Math.PI / 180) - this.rotA * 0.01f));
            wa.teleportTo(this.getX() + -1.0 * (double)(f19 * 4.0f * f16), this.getY() + (double)this.getEyeHeight(), this.getZ() - -1.0 * (double)(f4 * 4.0f * f16));
            this.setActualParasites(1);
            this.addID(wa.getId(), 1);
            wa.setDamageATT(this);
            this.level().addFreshEntity((Entity)wa);
            wa.particleStatus((byte)8);
            wa.particleStatus((byte)8);
            wa.particleStatus((byte)8);
            wa.particleStatus((byte)8);
            wa.setTarget(this.getTarget());
            byte typeF = (byte)(this.getRandom().nextInt(3) + 1);
            if (typeF == 3) {
                if (this.getTarget().distanceToSqr((Entity)this) < 100.0 || !this.getTarget().onGround() || this.canTeleportToo) {
                    typeF = (byte)(this.getRandom().nextInt(2) + 1);
                } else {
                    this.canTeleportToo = true;
                }
            }
            wa.setFatherTo(this, typeF);
            if (this.hasEffect(MobEffects.INVISIBILITY)) {
                wa.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 60, 0, false, false));
            }
            this.ggg -= 100;
            return true;
        }
        return false;
    }

    public void setTeleFlam(boolean in) {
        this.canTeleportToo = in;
    }

    public boolean getTeleFlam() {
        return this.canTeleportToo;
    }

    @Override
    protected void spawnGore() {
        int range = 4;
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
                if (block != Blocks.AIR || blockDown == Blocks.AIR || !this.level().getBlockState(blockpos.below()).isCollisionShapeFullBlock(this.level(), blockpos.below()) || blockDown == SRPBlocks.InfestedStain.get() || this.level().random.nextInt(4) != 0) continue;
                this.level().setBlockAndUpdate(blockpos, SRPBlocks.gorePur.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.FLAT)));
            }
        }
        if (this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) && (this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock || this.level().getBlockState(this.blockPosition()).getBlock() == Blocks.AIR)) {
            this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.gorePur.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
            EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
            nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
            nnn.setParasite(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).toString());
            nnn.setSkin((byte)this.getSkin());
            nnn.setGoal(20 * SRPConfig.pureRemainValue);
            this.level().addFreshEntity((Entity)nnn);
        }
        for (int i = 0; i < 7 && SRPConfig.paraGore; ++i) {
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
            bomb.setType((byte)4);
            bomb.copyPosition((Entity)this);
            bomb.setMotion(d3 *= (d7 *= (double)(this.level().random.nextFloat() * this.level().random.nextFloat() + 0.3f)), d4, d5 *= d7, 0.25, 0.65);
            this.level().addFreshEntity((Entity)bomb);
        }
    }

    @Override
    public int getTotalParasites() {
        return this.totalP;
    }

    @Override
    public int getActualParasites() {
        return this.actualP;
    }

    @Override
    public void setActualParasites(int i) {
        this.actualP += i;
    }

    @Override
    public void addID(int id, int points) {
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            this.mobID[i] = id;
            this.mobPT[i] = points;
            return;
        }
    }

    @Override
    public int IDable() {
        int flag = 0;
        for (int i = 0; i < this.mobID.length; ++i) {
            if (this.mobID[i] != -777) continue;
            ++flag;
        }
        if (flag > this.totalP) {
            flag = this.totalP;
        }
        return flag;
    }

    @Override
    public void checkID() {
        for (int i = 0; i < this.mobID.length; ++i) {
            Entity flag;
            if (this.mobID[i] <= 0 || (flag = this.level().getEntity(this.mobID[i])) != null) continue;
            this.mobID[i] = -777;
            int negative = this.mobPT[i] * -1;
            this.setActualParasites(negative);
        }
    }

    @Override
    public int[] getIDList() {
        return this.mobID;
    }

    @Override
    public int[] getPointList() {
        return this.mobPT;
    }

    @Override
    public void spawnEffectsGore() {
    }

    @Override
    public boolean scaryOrbEffect(LivingEntity in, int mobs) {
        boolean flag = super.scaryOrbEffect(in, mobs);
        if (flag && in instanceof Player) {
            Player playerIn = (Player)in;
            if (playerIn.experienceLevel > 0) {
                playerIn.giveExperiencePoints(-SRPConfig.preeminentExpSteal);
                if (playerIn.experienceProgress < 0.0f) {
                    --playerIn.experienceLevel;
                    playerIn.experienceProgress = 1.0f;
                }
            } else {
                playerIn.giveExperiencePoints(-SRPConfig.preeminentExpSteal);
                if (playerIn.experienceProgress < 0.0f) {
                    playerIn.experienceProgress = 0.0f;
                }
            }
        }
        return flag;
    }
}

