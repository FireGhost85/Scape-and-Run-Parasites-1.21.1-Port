package com.dhanantry.scapeandrunparasites.entity.monster.deterrent;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EntityLeemB
extends EntityPStationary {
    public EntityLeemB(EntityType<? extends EntityLeemB> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 0;
        this.type = (byte)40;
        this.MiniDamage = SRPConfig.turretMinDamage;
        this.valueEvDeath = 0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 314;
    }

    protected void registerGoals() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityPStationary.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, 20.0);
        builder.add(Attributes.ARMOR, 10.0);
        builder.add(Attributes.MOVEMENT_SPEED, 0.0);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        builder.add(Attributes.FOLLOW_RANGE, 16.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        if (this.srpTicks == 5) {
            this.removeEffect(SRPPotions.PIVOT_E);
        }
    }

    @Override
    public int hasResistance(String damage, byte type) {
        return 0;
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 1.8f;
    }
}

