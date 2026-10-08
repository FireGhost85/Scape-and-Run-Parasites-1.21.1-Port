package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection2;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class EntityPBeckon
extends EntityPStationaryArchitect {
    int lifeLeftBeckon = -1000;

    public EntityPBeckon(EntityType<? extends EntityPBeckon> type, Level worldIn) {
        super(type, worldIn);
        this.setScentHPMultiplier(1.0f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.lifeLeftBeckon > 0) {
            --this.lifeLeftBeckon;
            if (this.lifeLeftBeckon <= 0 && this.lifeLeftBeckon > -500) {
                this.hurt(this.damageSources().fellOutOfWorld(), 5000000.0f);
            }
        }
    }

    public void setLifeB(int in) {
        this.lifeLeftBeckon = in;
    }

    public int getLifeB() {
        return this.lifeLeftBeckon;
    }

    @Override
    public void generateStructure() {
        if (SRPConfig.nexusStructures) {
            WorldGenParasiteNexusProtection2 p1 = new WorldGenParasiteNexusProtection2(false, 1);
            p1.generate(this.level(), RandomSource.create(), this.blockPosition());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("beckonlifeleft", this.lifeLeftBeckon);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("beckonlifeleft", 99)) {
            this.lifeLeftBeckon = compound.getInt("beckonlifeleft");
        }
    }
}

