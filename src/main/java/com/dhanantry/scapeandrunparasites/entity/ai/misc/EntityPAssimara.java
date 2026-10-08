package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public abstract class EntityPAssimara
extends EntityPInfected {
    public EntityPAssimara(EntityType<? extends EntityPAssimara> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = SRPAttributes.XP_INFECTED;
        this.damageCap = SRPConfig.assimaraCap;
        this.canD = SRPConfig.assimaradespawn;
        this.MiniDamage = SRPConfig.assimaraMinDamage;
        this.oneMindDeathValue = SRPConfig.assimaraOneMindDeathV;
        this.foodSteal = 0.5f;
        this.valueEvDeath = SRPConfig.assimaraLoosingEPValue;
        this.thisMelting = false;
        this.setScentHPMultiplier(2.5f);
    }

    @Override
    public void setHost(String mobname) {
    }
}

