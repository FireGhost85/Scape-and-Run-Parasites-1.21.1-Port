package com.dhanantry.scapeandrunparasites.client.legacy;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** The 1.12 ModelBase: models build {@link ModelRenderer}s and draw them from {@link #render}. */
public abstract class ModelBase {
    public float swingProgress;
    public boolean isRiding;
    public boolean isChild = true;
    public List<ModelRenderer> boxList = new ArrayList<>();
    public int textureWidth = 64;
    public int textureHeight = 32;

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }

    public void setLivingAnimations(LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
    }

    public static void copyModelAngles(ModelRenderer source, ModelRenderer dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }

    public void setModelAttributes(ModelBase model) {
        this.swingProgress = model.swingProgress;
        this.isRiding = model.isRiding;
        this.isChild = model.isChild;
    }
}
