package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelTendrilBano
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer jointMLT0;
    public ModelRenderer JD;
    public ModelRenderer tacle;
    public ModelRenderer jointMLT1;
    public ModelRenderer tacle_1;
    public ModelRenderer jointMLT2;
    public ModelRenderer tacle_2;
    public ModelRenderer jointMLT3;
    public ModelRenderer tacle_3;
    public ModelRenderer jointMLT4;
    public ModelRenderer tacle_4;
    public ModelRenderer jointMLT5;
    public ModelRenderer tacle_5;
    public ModelRenderer jointMLT6;
    public ModelRenderer tacle_6;

    public ModelTendrilBano() {
        this.textureWidth = 100;
        this.textureHeight = 64;
        this.tacle_1 = new ModelRenderer(this, 19, 0);
        this.tacle_1.setRotationPoint(0.0f, -1.0f, 1.0f);
        this.tacle_1.addBox(-2.0f, -3.0f, -2.0f, 4, 6, 19, 0.0f);
        this.setRotateAngle(this.tacle_1, -0.4712389f, 0.0f, 0.0f);
        this.tacle_5 = new ModelRenderer(this, 54, 30);
        this.tacle_5.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tacle_5.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 11, 0.0f);
        this.setRotateAngle(this.tacle_5, -1.3089969f, 0.0f, 0.0f);
        this.tacle_6 = new ModelRenderer(this, 73, 34);
        this.tacle_6.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tacle_6.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 11, 0.0f);
        this.setRotateAngle(this.tacle_6, -0.89011794f, 0.0f, 0.0f);
        this.jointMLT5 = new ModelRenderer(this, 50, 0);
        this.jointMLT5.setRotationPoint(0.0f, 0.0f, 17.0f);
        this.jointMLT5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.tacle = new ModelRenderer(this, 0, 0);
        this.tacle.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tacle.addBox(-3.0f, -3.0f, -2.0f, 6, 6, 13, 0.0f);
        this.setRotateAngle(this.tacle, 0.0f, 0.0f, (float)Math.PI);
        this.tacle_3 = new ModelRenderer(this, 0, 25);
        this.tacle_3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tacle_3.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 18, 0.0f);
        this.setRotateAngle(this.tacle_3, -0.9773844f, 0.0f, 0.0f);
        this.JD = new ModelRenderer(this, 8, 0);
        this.JD.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.JD.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.jointMLT3 = new ModelRenderer(this, 33, 0);
        this.jointMLT3.setRotationPoint(0.0f, 0.0f, 18.0f);
        this.jointMLT3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 21.3f, 9.0f);
        this.mainbody.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.mainbody, 0.0f, (float)Math.PI, 0.0f);
        this.jointMLT4 = new ModelRenderer(this, 46, 0);
        this.jointMLT4.setRotationPoint(0.0f, 0.0f, 15.9f);
        this.jointMLT4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.jointMLT6 = new ModelRenderer(this, 54, 0);
        this.jointMLT6.setRotationPoint(0.0f, 0.0f, 10.0f);
        this.jointMLT6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.jointMLT0 = new ModelRenderer(this, 4, 0);
        this.jointMLT0.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.jointMLT0.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.tacle_4 = new ModelRenderer(this, 26, 30);
        this.tacle_4.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tacle_4.addBox(-2.5f, -2.5f, 0.0f, 5, 5, 18, 0.0f);
        this.setRotateAngle(this.tacle_4, -1.2566371f, 0.0f, 0.0f);
        this.tacle_2 = new ModelRenderer(this, 44, 4);
        this.tacle_2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tacle_2.addBox(-2.5f, -2.5f, -2.0f, 5, 5, 21, 0.0f);
        this.setRotateAngle(this.tacle_2, -1.1868238f, 0.0f, 0.0f);
        this.jointMLT1 = new ModelRenderer(this, 25, 0);
        this.jointMLT1.setRotationPoint(0.0f, 0.0f, 10.5f);
        this.jointMLT1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.jointMLT2 = new ModelRenderer(this, 29, 0);
        this.jointMLT2.setRotationPoint(0.0f, 0.0f, 17.5f);
        this.jointMLT2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.jointMLT1.addChild(this.tacle_1);
        this.jointMLT5.addChild(this.tacle_5);
        this.jointMLT6.addChild(this.tacle_6);
        this.tacle_4.addChild(this.jointMLT5);
        this.JD.addChild(this.tacle);
        this.jointMLT3.addChild(this.tacle_3);
        this.jointMLT0.addChild(this.JD);
        this.tacle_2.addChild(this.jointMLT3);
        this.tacle_3.addChild(this.jointMLT4);
        this.tacle_5.addChild(this.jointMLT6);
        this.mainbody.addChild(this.jointMLT0);
        this.jointMLT4.addChild(this.tacle_4);
        this.jointMLT2.addChild(this.tacle_2);
        this.tacle.addChild(this.jointMLT1);
        this.tacle_1.addChild(this.jointMLT2);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.mainbody.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        float f1 = Mth.sin((float)(ageInTicks * 0.06f + 0.0f)) * 0.2f;
        float f2 = Mth.sin((float)(ageInTicks * 0.07f + 8.0f)) * 0.3f;
        float f3 = Mth.sin((float)(ageInTicks * 0.06f + 4.0f)) * 0.4f;
        this.jointMLT1.rotateAngleX = f1;
        this.jointMLT2.rotateAngleX = f2;
        this.jointMLT3.rotateAngleX = f3;
        this.jointMLT4.rotateAngleX = f1;
        this.jointMLT5.rotateAngleX = f2;
        this.jointMLT6.rotateAngleX = f3;
    }
}

