package com.dhanantry.scapeandrunparasites.client.model.entity.pure;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.world.entity.Entity;

public class ModelRond
extends ModelSRP {
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        EntityParasiteBase parasite = (EntityParasiteBase)entityIn;
        byte i = parasite.getParasiteStatus();
        if (i == 0) {
            if (!parasite.getStillAni()) {
                float GS = 2.1f;
                float f = 0.3f;
            }
        } else if (!(i != 1 && i != 2 || parasite.getStillAni())) {
            float GS = 2.1f;
            float f = 0.3f;
        }
    }
}

