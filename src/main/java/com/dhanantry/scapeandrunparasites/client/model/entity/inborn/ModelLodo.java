package com.dhanantry.scapeandrunparasites.client.model.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class ModelLodo
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer bodyfront;
    public ModelRenderer joint1;
    public ModelRenderer dec;
    public ModelRenderer dec_1;
    public ModelRenderer dec_2;
    public ModelRenderer dec_3;
    public ModelRenderer body;
    public ModelRenderer joint2;
    public ModelRenderer bodyMiddle;
    public ModelRenderer joint3;
    public ModelRenderer dec_4;
    public ModelRenderer dec_5;
    public ModelRenderer dec_6;
    public ModelRenderer dec_7;
    public ModelRenderer dec_8;
    public ModelRenderer dec_9;
    public ModelRenderer dec_10;
    public ModelRenderer body_1;
    public ModelRenderer joint4;
    public ModelRenderer bodyback;
    public ModelRenderer joint5;
    public ModelRenderer dec_11;
    public ModelRenderer dec_12;
    public ModelRenderer dec_13;
    public ModelRenderer dec_14;
    public ModelRenderer body_2;

    public ModelLodo() {
        this.textureWidth = 64;
        this.textureHeight = 16;
        this.bodyMiddle = new ModelRenderer(this, 16, 3);
        this.bodyMiddle.setRotationPoint(0.0f, 0.0f, 2.5f);
        this.bodyMiddle.addBox(-3.0f, -3.0f, -1.5f, 6, 4, 3, 0.0f);
        this.dec_4 = new ModelRenderer(this, 31, 2);
        this.dec_4.setRotationPoint(0.0f, -2.6f, -0.7f);
        this.dec_4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_4, -0.38397244f, 0.0f, 0.0f);
        this.dec_7 = new ModelRenderer(this, 31, 4);
        this.dec_7.setRotationPoint(2.6f, -1.7f, -0.7f);
        this.dec_7.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_7, 0.0f, -0.38397244f, 0.0f);
        this.dec_1 = new ModelRenderer(this, 26, 0);
        this.dec_1.setRotationPoint(-0.9f, -0.5f, -0.2f);
        this.dec_1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_1, -0.38397244f, 0.0f, 0.0f);
        this.dec_11 = new ModelRenderer(this, 8, 5);
        this.dec_11.setRotationPoint(-0.9f, -0.6f, 1.2f);
        this.dec_11.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_11, -0.38397244f, 0.0f, 0.0f);
        this.body = new ModelRenderer(this, 35, 0);
        this.body.setRotationPoint(0.0f, 0.0f, 1.5f);
        this.body.addBox(-2.5f, -2.0f, -1.0f, 5, 3, 3, 0.0f);
        this.dec_10 = new ModelRenderer(this, 0, 5);
        this.dec_10.setRotationPoint(-2.6f, -0.2f, -0.7f);
        this.dec_10.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_10, 0.0f, 0.38397244f, 0.0f);
        this.joint4 = new ModelRenderer(this, 2, 5);
        this.joint4.setRotationPoint(0.0f, 0.0f, 0.4f);
        this.joint4.addBox(-1.0f, 0.0f, 0.0f, 2, 1, 2, 0.0f);
        this.dec_2 = new ModelRenderer(this, 30, 0);
        this.dec_2.setRotationPoint(1.5f, 0.0f, -0.2f);
        this.dec_2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_2, 0.0f, -0.38397244f, 0.0f);
        this.dec_5 = new ModelRenderer(this, 0, 3);
        this.dec_5.setRotationPoint(1.8f, -2.6f, -0.7f);
        this.dec_5.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_5, -0.38397244f, 0.0f, 0.0f);
        this.joint3 = new ModelRenderer(this, 54, 1);
        this.joint3.setRotationPoint(0.0f, 0.0f, 0.4f);
        this.joint3.addBox(-1.0f, 0.0f, 0.0f, 2, 1, 2, 0.0f);
        this.joint5 = new ModelRenderer(this, 47, 6);
        this.joint5.setRotationPoint(0.0f, 0.0f, 0.4f);
        this.joint5.addBox(-1.0f, 0.0f, 0.0f, 2, 1, 2, 0.0f);
        this.dec = new ModelRenderer(this, 22, 0);
        this.dec.setRotationPoint(0.9f, -0.5f, -0.2f);
        this.dec.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec, -0.38397244f, 0.0f, 0.0f);
        this.bodyback = new ModelRenderer(this, 0, 8);
        this.bodyback.setRotationPoint(0.0f, 0.0f, 1.5f);
        this.bodyback.addBox(-2.0f, -1.0f, -2.0f, 4, 2, 4, 0.0f);
        this.dec_6 = new ModelRenderer(this, 51, 3);
        this.dec_6.setRotationPoint(-1.8f, -2.6f, -0.7f);
        this.dec_6.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_6, -0.38397244f, 0.0f, 0.0f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 23.0f, -3.0f);
        this.mainbody.addBox(-1.0f, 0.0f, -3.0f, 2, 1, 2, 0.0f);
        this.dec_8 = new ModelRenderer(this, 54, 4);
        this.dec_8.setRotationPoint(2.6f, -0.2f, -0.7f);
        this.dec_8.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_8, 0.0f, -0.38397244f, 0.0f);
        this.dec_3 = new ModelRenderer(this, 34, 0);
        this.dec_3.setRotationPoint(-1.5f, 0.0f, -0.2f);
        this.dec_3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_3, 0.0f, 0.38397244f, 0.0f);
        this.dec_14 = new ModelRenderer(this, 57, 6);
        this.dec_14.setRotationPoint(-1.6f, 0.0f, 1.2f);
        this.dec_14.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_14, 0.0f, 0.38397244f, 0.0f);
        this.body_2 = new ModelRenderer(this, 45, 9);
        this.body_2.setRotationPoint(0.0f, 0.0f, 1.5f);
        this.body_2.addBox(-1.0f, 0.0f, -2.0f, 2, 1, 5, 0.0f);
        this.dec_12 = new ModelRenderer(this, 12, 5);
        this.dec_12.setRotationPoint(0.9f, -0.6f, 1.2f);
        this.dec_12.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_12, -0.38397244f, 0.0f, 0.0f);
        this.dec_9 = new ModelRenderer(this, 58, 4);
        this.dec_9.setRotationPoint(-2.6f, -1.7f, -0.7f);
        this.dec_9.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_9, 0.0f, 0.38397244f, 0.0f);
        this.dec_13 = new ModelRenderer(this, 53, 6);
        this.dec_13.setRotationPoint(1.6f, 0.0f, 1.2f);
        this.dec_13.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.dec_13, 0.0f, -0.38397244f, 0.0f);
        this.bodyfront = new ModelRenderer(this, 5, 0);
        this.bodyfront.setRotationPoint(0.0f, 0.0f, -1.5f);
        this.bodyfront.addBox(-2.0f, -1.0f, -1.0f, 4, 2, 3, 0.0f);
        this.joint2 = new ModelRenderer(this, 48, 0);
        this.joint2.setRotationPoint(0.0f, 0.0f, -0.5f);
        this.joint2.addBox(-1.0f, 0.0f, 0.0f, 2, 1, 2, 0.0f);
        this.body_1 = new ModelRenderer(this, 34, 6);
        this.body_1.setRotationPoint(0.0f, 0.0f, 1.5f);
        this.body_1.addBox(-2.5f, -2.0f, -2.0f, 5, 3, 3, 0.0f);
        this.joint1 = new ModelRenderer(this, 16, 0);
        this.joint1.setRotationPoint(0.0f, 0.0f, 0.4f);
        this.joint1.addBox(-1.0f, 0.0f, 0.0f, 2, 1, 2, 0.0f);
        this.joint2.addChild(this.bodyMiddle);
        this.bodyMiddle.addChild(this.dec_4);
        this.bodyMiddle.addChild(this.dec_7);
        this.bodyfront.addChild(this.dec_1);
        this.bodyback.addChild(this.dec_11);
        this.joint1.addChild(this.body);
        this.bodyMiddle.addChild(this.dec_10);
        this.body_1.addChild(this.joint4);
        this.bodyfront.addChild(this.dec_2);
        this.bodyMiddle.addChild(this.dec_5);
        this.bodyMiddle.addChild(this.joint3);
        this.bodyback.addChild(this.joint5);
        this.bodyfront.addChild(this.dec);
        this.joint4.addChild(this.bodyback);
        this.bodyMiddle.addChild(this.dec_6);
        this.bodyMiddle.addChild(this.dec_8);
        this.bodyfront.addChild(this.dec_3);
        this.bodyback.addChild(this.dec_14);
        this.joint5.addChild(this.body_2);
        this.bodyback.addChild(this.dec_12);
        this.bodyMiddle.addChild(this.dec_9);
        this.bodyback.addChild(this.dec_13);
        this.mainbody.addChild(this.bodyfront);
        this.body.addChild(this.joint2);
        this.joint3.addChild(this.body_1);
        this.bodyfront.addChild(this.joint1);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.mainbody.render(scale);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        EntityLodo ven = (EntityLodo)entityIn;
        float f6 = (float)ven.getFloorTimer();
        if (f6 >= 0.0f) {
            float f2 = Mth.cos((float)(ageInTicks * 2.6f)) * 0.015f;
            float f1 = Mth.cos((float)(ageInTicks * 2.27f)) * 0.02f;
            this.mainbody.offsetX = f2;
            this.mainbody.offsetZ = f1;
            this.mainbody.rotateAngleX = 1.6f;
            this.mainbody.offsetZ = 0.2f;
            this.mainbody.offsetY = f6;
            f1 = Mth.cos((float)(ageInTicks * 0.643219f)) * 0.06510051f;
            f2 = Mth.cos((float)(ageInTicks * 0.643219f)) * 0.06510015f;
            this.joint1.rotateAngleY = f1;
            this.joint2.rotateAngleY = f1;
            this.joint3.rotateAngleY = f1;
            this.joint4.rotateAngleY = f1;
            this.joint5.rotateAngleY = f1;
            this.mainbody.rotateAngleZ = f2;
            this.bodyMiddle.rotateAngleZ = f2;
        } else {
            this.mainbody.offsetX = 0.0f;
            this.mainbody.offsetZ = 0.0f;
            this.mainbody.rotateAngleX = 0.0f;
            this.mainbody.offsetZ = 0.0f;
            this.mainbody.offsetY = 0.0f;
            float f1 = Mth.cos((float)(limbSwing * 1.5f)) * 1.0f * limbSwingAmount * 0.25f;
            float f2 = Mth.cos((float)(limbSwing * 1.5f)) * 1.0f * limbSwingAmount * 0.15f;
            this.joint1.rotateAngleY = f1;
            this.joint2.rotateAngleY = f1;
            this.joint3.rotateAngleY = f1;
            this.joint4.rotateAngleY = f1;
            this.joint5.rotateAngleY = f1;
            this.mainbody.rotateAngleZ = f2;
            this.bodyMiddle.rotateAngleZ = f2;
        }
    }

    public void setLivingAnimations(LivingEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTickTime) {
    }
}

