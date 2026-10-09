package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.world.entity.Entity;

public class ModelNULL
extends ModelSRP {
    public ModelNULL() {
        this.textureWidth = 64;
        this.textureHeight = 32;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }
}

