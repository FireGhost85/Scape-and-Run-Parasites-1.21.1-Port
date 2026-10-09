package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.world.entity.Entity;

public class ModelOrbVoid
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer kd;
    public ModelRenderer kd_1;
    public ModelRenderer mainbody_1;
    public ModelRenderer mainbody_2;

    public ModelOrbVoid() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.kd = new ModelRenderer(this, 0, 0);
        this.kd.setRotationPoint(0.0f, 28.0f, 0.0f);
        this.kd.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.kd, 0.7853982f, 0.0f, 0.0f);
        this.mainbody_1 = new ModelRenderer(this, 48, 16);
        this.mainbody_1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.mainbody_1.addBox(-8.0f, -12.0f, -8.0f, 16, 16, 16, 0.0f);
        this.setRotateAngle(this.mainbody_1, 0.0f, 0.7853982f, 0.0f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 28.0f, 0.0f);
        this.mainbody.addBox(-8.0f, -12.0f, -8.0f, 16, 16, 16, 0.0f);
        this.mainbody_2 = new ModelRenderer(this, 0, 32);
        this.mainbody_2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.mainbody_2.addBox(-8.0f, -12.0f, -8.0f, 16, 16, 16, 0.0f);
        this.setRotateAngle(this.mainbody_2, 0.0f, 0.7853982f, 0.0f);
        this.kd_1 = new ModelRenderer(this, 4, 0);
        this.kd_1.setRotationPoint(0.0f, 28.0f, 0.0f);
        this.kd_1.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.kd_1, -0.7853982f, 0.0f, 0.0f);
        this.kd.addChild(this.mainbody_1);
        this.kd_1.addChild(this.mainbody_2);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        double scale = 0.5;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.mainbody.offsetX, (float)this.mainbody.offsetY, (float)this.mainbody.offsetZ);
        GlStateManager.translate((float)(this.mainbody.rotationPointX * f5), (float)(this.mainbody.rotationPointY * f5), (float)(this.mainbody.rotationPointZ * f5));
        GlStateManager.scale((double)scale, (double)scale, (double)scale);
        GlStateManager.translate((float)(-this.mainbody.offsetX), (float)(-this.mainbody.offsetY), (float)(-this.mainbody.offsetZ));
        GlStateManager.translate((float)(-this.mainbody.rotationPointX * f5), (float)(-this.mainbody.rotationPointY * f5), (float)(-this.mainbody.rotationPointZ * f5));
        this.kd.render(f5);
        this.mainbody.render(f5);
        this.kd_1.render(f5);
        GlStateManager.popMatrix();
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float f1;
        this.mainbody.offsetY = f1 = -0.35f;
        this.kd.offsetY = f1;
        this.kd_1.offsetY = f1;
    }
}

