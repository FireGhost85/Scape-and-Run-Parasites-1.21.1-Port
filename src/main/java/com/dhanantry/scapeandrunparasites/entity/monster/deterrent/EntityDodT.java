package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class EntityDodT
extends EntityPStationary {
    private String paraToRelo;
    private EntityPDispatcher father;
    private EntityParasiteBase tele;
    private int stage;

    public EntityDodT(EntityType<? extends EntityDodT> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 0;
        this.type = (byte)40;
        this.buriedT = 4.0;
        this.damageCap = SRPConfig.turretCap;
        this.pointCap = SRPConfig.turretPointCap;
        this.pointReduction = SRPConfig.turretPointRed;
        this.chanceLearn = SRPConfig.turretChanceLe;
        this.chanceLearnFire = SRPConfig.turretChanceLeFire;
        this.DamageTypeCap = SRPConfig.turretPointDamCap;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.regen = SRPConfig.turretRegen;
        this.valueEvDeath = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 74;
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(8, new RandomLookAroundGoal((Mob)this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPStationary.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, 10.0);
        builder.add(Attributes.ARMOR, 10.0);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 16.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.getTarget() != null && this.getTarget().distanceToSqr((Entity)this) > 256.0) {
                this.level().broadcastEntityEvent((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.tickCount > 120 && this.paraToRelo == null && this.tele == null) {
                this.level().broadcastEntityEvent((Entity)this, (byte)51);
                this.up = true;
            }
            if (this.srpTicks == 10 && this.father != null && this.father.getTarget() != null) {
                this.lookAt((Entity)this.father.getTarget());
            }
            if (!this.up && this.tickCount > 60 && !this.buried()) {
                if (this.paraToRelo != null) {
                    EntityParasiteBase entityout = (EntityParasiteBase)SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(this.paraToRelo), (Level)this.level());
                    if (entityout != null) {
                        entityout.copyPosition((Entity)this);
                        entityout.finalizeSpawn((ServerLevel) entityout.level(), this.level().getCurrentDifficultyAt(entityout.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                        this.level().addFreshEntity((Entity)entityout);
                        this.applyEffectsDod(entityout);
                        this.paraToRelo = null;
                        entityout.particleStatus((byte)8);
                        entityout.particleStatus((byte)8);
                        if (this.isOnFire()) {
                            entityout.setHealth(entityout.getMaxHealth() * 0.5f);
                            entityout.igniteForSeconds(8);
                        }
                        if (this.father != null && this.father.isAlive()) {
                            entityout.setDodFatherID(this.father.getId());
                        }
                        this.particleStatus((byte)8);
                        this.particleStatus((byte)8);
                        this.particleStatus((byte)8);
                    }
                } else if (this.tele != null) {
                    this.tele.copyPosition((Entity)this);
                    this.tele.particleStatus((byte)7);
                    this.particleStatus((byte)7);
                    this.tele = null;
                }
            }
        }
    }

    @Override
    protected void retreat(boolean dead) {
        if (this.up) {
            this.buried += 0.08;
            if (this.buried > this.buriedT && !this.level().isClientSide) {
                this.discard();
            }
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (this.father != null && this.father.isAlive()) {
            this.father.addParaBack(this.paraToRelo);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Item wea = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if (wea != SRPItems.itembase.get()) {
            // empty if block
        }
        if (this.father != null) {
            this.father.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 1, false, false));
        }
        return super.mobInteract(player, hand);
    }

    public void setTele(String in, int s) {
        this.paraToRelo = in;
        this.stage = s;
    }

    public void setTele(EntityParasiteBase in) {
        this.tele = in;
    }

    public String getTele() {
        return this.paraToRelo;
    }

    protected void applyEffectsDod(EntityParasiteBase in) {
        String[] pots = SRPConfigMobs.dodsiEffects;
        switch (this.stage) {
            case 2: {
                pots = SRPConfigMobs.dodsiiEffects;
                break;
            }
            case 3: {
                pots = SRPConfigMobs.dodsiiiEffects;
                break;
            }
            case 4: {
                pots = SRPConfigMobs.dodsivEffects;
            }
        }
        if (pots.length == 0) {
            return;
        }
        String[] here = new String[3];
        for (String i : pots) {
            here = i.split(";");
            int ticks = Integer.parseInt(here[0]);
            int amp = Integer.parseInt(here[2]);
            Holder<MobEffect> potion = SRPEntityUtil.effect(here[1]);
            if (potion == null) continue;
            in.addEffect(new MobEffectInstance(potion, ticks, amp));
        }
    }

    public EntityPDispatcher getFather() {
        return this.father;
    }

    public void setFather(EntityPDispatcher in) {
        this.father = in;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.8f;
    }
}

