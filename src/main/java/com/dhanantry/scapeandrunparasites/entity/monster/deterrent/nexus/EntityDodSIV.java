package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIAncientSummon;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIDodAttack;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.Arrays;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;

public class EntityDodSIV
extends EntityPDispatcher
implements EntityBodyParts {
    private EntityBodyModel head;
    private int ticksss;
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);

    public EntityDodSIV(EntityType<? extends EntityDodSIV> type, Level worldIn) {
        super(type, worldIn);
        this.noCulling = true;
        this.xpReward = SRPAttributes.XP_ADAPTED * 4;
        this.buried = 0.1;
        this.setParasiteStatus(3);
        this.damageCap = SRPConfig.nexussivCap;
        this.pointCap = SRPConfig.nexussivPointCap;
        this.pointReduction = SRPConfig.nexussivPointRed;
        this.chanceLearn = SRPConfig.nexussivChanceLe;
        this.chanceLearnFire = SRPConfig.nexussivChanceLeFire;
        this.DamageTypeCap = SRPConfig.nexussivPointDamCap;
        this.totalP = SRPConfigMobs.dodsivTotalActiveMobs;
        this.mobID = new int[3];
        this.mobPT = new int[3];
        this.stage = (byte)4;
        Arrays.fill(this.mobID, -777);
        this.head = new EntityBodyModel(this, 1.2f, 2.5f, 1.0f, 0.0f, 6.3f, -1, 1, false, 0.2f);
        this.head.setSkin(0);
        this.valueEvDeath = SRPConfig.nexussivLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 79;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.goalSelector.addGoal(2, new EntityAIDodAttack(this, 4, 52, 30.0f));
        this.goalSelector.addGoal(4, new EntityAIAncientSummon(this, 120, 4, new String[]{"srparasites:ancientpod;1;1"}));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPDispatcher.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.DODSIV_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.DODSIV_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.DODSIV_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, SRPConfig.nexussivFollow * (double)SRPConfigMobs.dodsivFollowRangeMult);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.head.tick();
        this.placeColony();
    }

    @Override
    public boolean attackEntityBodyFrom(DamageSource source, float amount, int id, boolean notify) {
        if (this.getRandom().nextBoolean()) {
            SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 80, 0);
        }
        return this.hurt(source, amount * 3.0f);
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        super.discard();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 2.5f;
    }

    private void placeColony() {
        if (this.getRandom().nextInt(10) != 0) {
            return;
        }
        if (this.tickCount < 1200) {
            return;
        }
        if (this.level().isClientSide) {
            return;
        }
        ++this.ticksss;
        if (this.ticksss < 200) {
            return;
        }
        this.ticksss = 0;
        if (ParasiteEventWorld.numberofColonies(this.level()) >= 1) {
            this.ticksss = -1000;
            return;
        }
        int range = 7;
        int attemp = 3;
        int mini = 5;
        while (attemp > 0) {
            --attemp;
            double randomx = this.getRandom().nextInt(range);
            double randomz = this.getRandom().nextInt(range);
            double negative = this.getRandom().nextInt(2);
            randomx = negative == 0.0 ? randomx * -1.0 - (double)mini : (randomx += (double)mini);
            negative = this.getRandom().nextInt(2);
            randomz = negative == 0.0 ? randomz * -1.0 - (double)mini : (randomz += (double)mini);
            BlockPos pos = BlockPos.containing(this.getX() + randomx, this.getY(), this.getZ() + randomz);
            if ((pos = ParasiteEventEntity.getFloor(this.level(), pos, 5)) == null || ParasiteEventWorld.placeColonyInWorld(this.level(), pos) != 1) continue;
            this.ticksss = -1000;
            return;
        }
        this.ticksss = -100;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossInfo.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void setCustomNameTag(String name) {
        SRPEntityUtil.setCustomNameTag(this, name);
        this.bossInfo.setName(this.getDisplayName());
    }

    public void addTrackingPlayer(ServerPlayer player) {
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossInfo.removePlayer(player);
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        boolean flag = super.hurt(source, amount);
        if (flag && source.getEntity() != null && source.getEntity() instanceof ServerPlayer) {
            this.bossInfo.addPlayer((ServerPlayer)source.getEntity());
        }
        return flag;
    }

    @Override
    public void setBodyPartDead(int id) {
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return SRPConfig.rsDespawn;
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.DODSIV_ATTACK_DAMAGE;
    }

    @Override
    public boolean storeParasite(EntityParasiteBase in) {
        if (super.storeParasite(in)) {
            return true;
        }
        if (this.storeLodo(in, true)) {
            return true;
        }
        if (this.storeInf(in, true)) {
            return true;
        }
        if (this.storeCrude(in, true)) {
            return true;
        }
        if (this.storeMudo(in, true)) {
            return true;
        }
        if (this.storeMangler(in, true)) {
            return true;
        }
        this.storeAll(in);
        return false;
    }

    @Override
    public void die(DamageSource cause) {
        if (!this.level().isClientSide) {
            if (SRPConfigWorld.coloniesActivated || this.canChangeVariant) {
                if (ParasiteEventWorld.numberofColonies(this.level()) >= 1 || this.canChangeVariant) {
                    ParasiteEventEntity.checkColony(this.level(), cause, this);
                    ParasiteEventEntity.spawnNext(this, new EntityDodSIII(SRPEntities.DISPATCHER_SIII.get(), this.level()), true, false);
                } else {
                    super.die(cause);
                }
            } else {
                super.die(cause);
            }
        }
    }

    @Override
    protected boolean onDeathDislo(DamageSource cause) {
        ParasiteEventWorld.setDisloWorldPhase(this.level(), SRPAttributes.EVENTPARANEXUSIVD, SRPConfigSystems.chanceEventParaNexusIVD, 0, null);
        return super.onDeathDislo(cause);
    }
}

