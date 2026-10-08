package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAINearestAttackableTargetStatus;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.EventHooks;

public abstract class EntityPStationary
extends EntityPMalleable {
    protected double buried;
    protected double buriedT;
    protected boolean up = false;
    protected boolean onlyPeek;
    protected int peeking;
    protected boolean relocate;
    protected int delayBuried;

    public EntityPStationary(EntityType<? extends EntityPStationary> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.aiWander);
        this.goalSelector.removeGoal(this.folow);
        this.goalSelector.removeGoal(this.jumpT);
        if (SRPConfig.mobattacking) {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, true, null, 1.0, 1.0f));
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Mob>(this, Mob.class, 0, true, true, new Predicate<Mob>(){

                public boolean test(@Nullable Mob entity) {
                    return !(entity instanceof WaterAnimal) && !(entity instanceof Animal) && !(entity instanceof Villager) && !ParasiteEventEntity.checkEntity((LivingEntity)entity, SRPConfig.mobattackingBlackList, SRPConfig.mobattackingBlackListWhite);
                }
            }, 1.0, 1.0f));
        } else {
            this.targetSelector.addGoal(4, new EntityAINearestAttackableTargetStatus<Player>(this, Player.class, 0, true, true, null, 1.0, 1.0f));
        }
        this.onlyPeek = false;
        this.relocate = false;
        this.valueEvDeath = SRPConfig.turretLoosingEPValue;
        this.delayBuried = 0;
        this.setScentHPMultiplier(1.5f);
    }

    @Override
    public void aiStep() {
        if (this.isNoAi()) {
            return;
        }
        if (!this.level().isClientSide) {
            Mot.setPosX(this, this.xo);
            Mot.setPosZ(this, this.zo);
            if (!this.onGround()) {
                Mot.addY(this, -(0.5));
            }
            if (SRPConfigSystems.useEvolution && this.srpTicks == 20 && SRPConfigSystems.damageStationaryRS && ParasiteEventEntity.getRSchance(this.level()) == 0.0) {
                this.hurt(this.damageSources().fellOutOfWorld(), 1.0f);
            }
        }
        this.buried();
        this.liquidLeap = -100;
        super.aiStep();
        if (this.onlyPeek && this.tickCount > 100) {
            if (this.getTarget() != null) {
                if (!this.getTarget().isAlive()) {
                    this.setTarget(null);
                } else {
                    this.peeking = 0;
                }
            } else {
                ++this.peeking;
                if (this.peeking > 100) {
                    this.level().broadcastEntityEvent((Entity)this, (byte)51);
                    this.up = true;
                }
            }
        }
        this.retreat(true);
    }

    public void setPeek(boolean in) {
        this.onlyPeek = in;
    }

    @Override
    public void setAttackTarget(LivingEntity entitylivingbaseIn) {
        if (this.buried > 0.0 || this.peeking > 50) {
            return;
        }
        super.setTarget(entitylivingbaseIn);
    }

    public boolean buried() {
        if (this.buried > 0.0) {
            BlockState state = this.level().getBlockState(this.blockPosition().below());
            if (state.getBlock() != Blocks.AIR) {
                BlockState id = state;
                for (int i = 0; i < 10; ++i) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, id), this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY(), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02, this.getRandom().nextGaussian() * 0.02);
                }
            }
            --this.delayBuried;
            if (this.delayBuried > 0) {
                return true;
            }
            if (!this.up) {
                this.buried -= this.getBuriedSpeed();
            }
            return true;
        }
        if (this.getParasiteStatus() == 3) {
            this.setParasiteStatus(0);
        } else if (!this.up) {
            this.buried = -0.1;
        }
        return false;
    }

    public void setBuried() {
        this.setParasiteStatus(3);
        this.buried = this.buriedT;
    }

    protected void retreat(boolean dead) {
        if (this.up) {
            this.buried += this.getBuriedSpeed();
            this.setParasiteStatus(3);
            if (this.buried > this.buriedT && !this.level().isClientSide) {
                if (dead) {
                    this.discard();
                } else if (this.getTarget() != null && ParasiteEventEntity.teleportDigging(this, 10.0f, this.getTarget().blockPosition(), 5, 2)) {
                    this.level().broadcastEntityEvent((Entity)this, (byte)52);
                    this.up = false;
                }
            }
        }
    }

    public double getBuriedSpeed() {
        return 0.08;
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        LivingEntity attacker;
        if (this.level().isClientSide) {
            return super.hurt(source, amount);
        }
        if (source.getEntity() instanceof LivingEntity && (attacker = (LivingEntity)source.getEntity()).hasEffect(SRPPotions.KILLNEX_E)) {
            int amp = attacker.getEffect(SRPPotions.KILLNEX_E).getAmplifier();
            float totalRed = Mth.clamp((float)(SRPConfigSystems.parasiteKillingReduction * ((float)amp + 1.0f)), (float)0.0f, (float)0.95f);
            float reduced = amount * (1.0f - totalRed);
            return super.hurt(source, Math.max(0.0f, reduced));
        }
        boolean flag = super.hurt(source, amount);
        if (flag && source.is(DamageTypes.IN_WALL) && this.getBlockH() != 0.0f) {
            this.skillBreakBlocks();
        }
        return flag;
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
                target.removeEffect(SRPPotions.KILLFER_E);
                target.removeEffect(SRPPotions.KILLCRU_E);
                SRPPotions.applyStackPotion(SRPPotions.KILLNEX_E, target, 1200, count);
            }
        }
        return flag;
    }

    public double getFloorTimer() {
        if (this.isNoAi()) {
            return -0.1;
        }
        return this.buried;
    }

    public void push(Entity entityIn) {
    }

    @Override
    protected void doPush(Entity entityIn) {
        if (entityIn instanceof FishingHook) {
            entityIn.discard();
        }
    }

    protected void collideWithNearbyEntities() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(1.1), EntitySelector.NO_SPECTATORS);
        if (!list.isEmpty()) {
            int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (i > 0 && list.size() > i - 1 && this.getRandom().nextInt(4) == 0) {
                int j = 0;
                for (int k = 0; k < list.size(); ++k) {
                    if (((Entity)list.get(k)).isPassenger()) continue;
                    ++j;
                }
                if (j > i - 1) {
                    this.hurt(this.damageSources().cramming(), 6.0f);
                }
            }
            for (int l = 0; l < list.size(); ++l) {
                Entity entity = (Entity)list.get(l);
                this.doPush(entity);
            }
        }
    }

    public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
    }

    public boolean isPushedByFluid() {
        return false;
    }

    public void addPotionEffect(MobEffectInstance potioneffectIn) {
        if (potioneffectIn.getEffect() == MobEffects.LEVITATION) {
            return;
        }
        super.addEffect(potioneffectIn);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("parasitedepeek", this.onlyPeek);
        compound.putBoolean("parasiteuppp", this.up);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("parasitedepeek", 99)) {
            this.setPeek(compound.getBoolean("parasitedepeek"));
        }
        if (compound.contains("parasiteuppp", 99)) {
            this.up = compound.getBoolean("parasiteuppp");
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 50) {
            this.buried = this.buriedT;
        } else if (id == 51) {
            this.up = true;
        } else if (id == 52) {
            this.onlyPeek = true;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void skillBreakBlocks() {
        if (this.getBlockH() == 0.0f) {
            return;
        }
        int blocksbroke = 0;
        int i1 = Mth.floor((double)(this.getY() + 0.1));
        double l1 = this.getX();
        double i2 = this.getZ();
        boolean flag = false;
        int Brangeatm = this.BGrange;
        int offsetT = -1;
        for (int k2 = -1 * this.BGrange; k2 <= this.BGrange; ++k2) {
            for (int l2 = -1 * this.BGrange; l2 <= this.BGrange; ++l2) {
                for (int j = 1 + offsetT; j <= this.BGheight + offsetT; ++j) {
                    String name;
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = this.level().getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    float bHard = iblockstate.getDestroySpeed(this.level(), blockpos);
                    if (!(bHard <= this.getBlockH()) || !(bHard >= 0.0f) || block instanceof IMetaName && block != SRPBlocks.ParasiteCanister.get() || block == SRPBlocks.BiomeHeart.get() || block == SRPBlocks.ColonyHeart.get() || block == SRPBlocks.ParasiteRubbleDense.get() || block == SRPBlocks.ParasiteCanisterActive.get() || block == SRPBlocks.dodN.get() || this.blockException(name = block.builtInRegistryHolder().key().location().toString()) || block == Blocks.AIR || !iblockstate.canEntityDestroy(this.level(), blockpos, this) || !EventHooks.onEntityDestroyBlock((LivingEntity)this, (BlockPos)blockpos, (BlockState)iblockstate)) continue;
                    if (SRPConfig.cystActive) {
                        boolean bl = flag = this.destroyBlockPos(blockpos, false) || flag;
                        if (SRPConfig.doTileDrops) {
                            this.addToBlockInv(BlockIds.stateString(iblockstate));
                        }
                    } else {
                        this.destroyBlockPos(blockpos, SRPConfig.doTileDrops);
                    }
                    ++blocksbroke;
                }
            }
        }
        this.BGrange = Brangeatm;
        this.SkillBGflag = true;
    }

    public boolean canBeCollidedWith() {
        return true;
    }
}

