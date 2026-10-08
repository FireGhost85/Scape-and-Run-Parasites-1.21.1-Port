package com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.level.Level;

public class EntityVenkrolSV
extends EntityPBeckon {
    private EntityBodyModel head;
    private EntityBodyModel base;
    private int ticksss;
    private final ServerBossEvent bossInfo = (ServerBossEvent)new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.PROGRESS).setDarkenScreen(false);

    public EntityVenkrolSV(EntityType<? extends EntityVenkrolSV> type, Level worldIn) {
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
        this.head = new EntityBodyModel(this, 2.2f, 3.7f, 1.0f, 0.0f, 13.3f, -1, 1, false, 0.2f);
        this.head.setSkin(3);
        this.base = new EntityBodyModel(this, 3.2f, 1.2f, 1.0f, 0.0f, 0.0f, -1, 2, false, 0.2f);
        this.base.setSkin(2);
        this.valueEvDeath = SRPConfig.nexussivLoosingEPValue;
    }

    @Override
    public int getParasiteIDRegister() {
        return 42;
    }

    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPBeckon.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.VENKROLSII_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.VENKROLSII_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.VENKROLSII_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 24.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.head.tick();
        this.base.tick();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 8.5f;
    }

    @Override
    public void setDead() {
        if (this.head != null) {
            this.head.discard();
        }
        if (this.base != null) {
            this.base.discard();
        }
        super.discard();
    }

    @Override
    public float getBombDamage() {
        return (float)SRPAttributes.VENKROLSIV_ATTACK_DAMAGE;
    }
}

