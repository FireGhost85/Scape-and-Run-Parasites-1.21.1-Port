package com.dhanantry.scapeandrunparasites.entity.monster.inborn;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EntityMor
extends EntityParasiteBase {
    public EntityMor(EntityType<? extends EntityMor> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = SRPAttributes.XP_LiTTLE;
        this.goalSelector.removeGoal(this.folow);
        this.type = (byte)7;
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 305;
    }

    protected void registerGoals() {
    }

    public static AttributeSupplier.Builder createAttributes() {
        AttributeSupplier.Builder builder = EntityParasiteBase.createAttributes();
        
        builder.add(Attributes.MAX_HEALTH, SRPAttributes.LODO_HEALTH);
        builder.add(Attributes.ARMOR, SRPAttributes.LODO_ARMOR);
        builder.add(Attributes.MOVEMENT_SPEED, 0.3);
        builder.add(Attributes.ATTACK_DAMAGE, SRPAttributes.LODO_ATTACK_DAMAGE);
        builder.add(Attributes.KNOCKBACK_RESISTANCE, SRPAttributes.LODO_KD_RESISTANCE);
        builder.add(Attributes.FOLLOW_RANGE, 16.0);
        return builder;
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.8f;
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

