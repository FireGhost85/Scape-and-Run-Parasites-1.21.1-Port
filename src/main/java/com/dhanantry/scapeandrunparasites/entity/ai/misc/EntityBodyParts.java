package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import net.minecraft.world.damagesource.DamageSource;

public interface EntityBodyParts {
    public boolean attackEntityBodyFrom(DamageSource var1, float var2, int var3, boolean var4);

    public void setBodyPartDead(int var1);
}

