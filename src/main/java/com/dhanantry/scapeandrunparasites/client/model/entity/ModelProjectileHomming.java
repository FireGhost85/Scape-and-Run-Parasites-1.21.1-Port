package com.dhanantry.scapeandrunparasites.client.model.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import net.minecraft.world.entity.Entity;

public class ModelProjectileHomming
extends ModelBase {
    public ModelRenderer renderer;

    public ModelProjectileHomming() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.renderer = new ModelRenderer(this);
        this.renderer.setTextureOffset(0, 0).addBox(-4.0f, -4.0f, -1.0f, 8, 8, 2, 0.0f);
        this.renderer.setTextureOffset(0, 10).addBox(-1.0f, -4.0f, -4.0f, 2, 8, 8, 0.0f);
        this.renderer.setTextureOffset(20, 0).addBox(-4.0f, -1.0f, -4.0f, 8, 2, 8, 0.0f);
        this.renderer.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.renderer.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.renderer.rotateAngleY = netHeadYaw * ((float)Math.PI / 180);
        this.renderer.rotateAngleX = headPitch * ((float)Math.PI / 180);
    }
}

