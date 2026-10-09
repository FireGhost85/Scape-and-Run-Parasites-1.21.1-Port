package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelEffect;
import net.minecraft.world.entity.Entity;

public class ModelBombJinjo
extends ModelEffect {
    public ModelRenderer mainbody;
    public ModelRenderer b;
    public ModelRenderer b_1;
    public ModelRenderer b_2;
    public ModelRenderer b_3;
    public ModelRenderer b_4;
    public ModelRenderer b_5;
    public ModelRenderer b_6;
    public ModelRenderer b_7;
    public ModelRenderer b_8;
    public ModelRenderer b_9;

    public ModelBombJinjo() {
        this.textureWidth = 135;
        this.textureHeight = 150;
        this.b = new ModelRenderer(this, 0, 0);
        this.b.setRotationPoint(0.0f, 18.5f, 0.0f);
        this.b.addBox(-4.0f, -8.0f, -7.0f, 14, 14, 12, 0.0f);
        this.b_5 = new ModelRenderer(this, 0, 53);
        this.b_5.setRotationPoint(-1.1f, 6.5f, -6.0f);
        this.b_5.addBox(-4.0f, -8.0f, -7.0f, 14, 14, 12, 0.0f);
        this.setRotateAngle(this.b_5, 0.50265485f, 0.0f, 0.0f);
        this.b_4 = new ModelRenderer(this, 40, 30);
        this.b_4.setRotationPoint(1.0f, 2.0f, 0.0f);
        this.b_4.addBox(-4.0f, -12.0f, -3.5f, 15, 22, 6, 0.0f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(-3.0f, 0.0f, 0.0f);
        this.mainbody.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.b_1 = new ModelRenderer(this, 52, 0);
        this.b_1.setRotationPoint(-8.0f, 4.0f, -5.0f);
        this.b_1.addBox(-4.0f, 0.0f, -4.0f, 10, 15, 15, 0.0f);
        this.setRotateAngle(this.b_1, 0.0f, 0.0f, -0.43982297f);
        this.b_7 = new ModelRenderer(this, 0, 79);
        this.b_7.setRotationPoint(-1.1f, 1.5f, 9.0f);
        this.b_7.addBox(-3.0f, -16.0f, -7.0f, 14, 19, 12, 0.0f);
        this.setRotateAngle(this.b_7, 0.12566371f, 0.0f, 0.0f);
        this.b_3 = new ModelRenderer(this, 0, 26);
        this.b_3.setRotationPoint(-5.0f, -6.0f, 1.0f);
        this.b_3.addBox(-4.0f, -4.5f, -6.0f, 10, 17, 10, 0.0f);
        this.setRotateAngle(this.b_3, 0.0f, 0.0f, 0.12566371f);
        this.b_9 = new ModelRenderer(this, 90, 109);
        this.b_9.setRotationPoint(-1.4f, -8.5f, 2.0f);
        this.b_9.addBox(-1.0f, -16.0f, -7.0f, 8, 14, 10, 0.0f);
        this.setRotateAngle(this.b_9, 0.06283186f, 0.0f, 0.0f);
        this.b_8 = new ModelRenderer(this, 52, 88);
        this.b_8.setRotationPoint(-1.1f, -2.5f, -7.0f);
        this.b_8.addBox(-2.5f, -10.0f, -7.0f, 10, 17, 14, 0.0f);
        this.setRotateAngle(this.b_8, -0.12566371f, 0.0f, 0.0f);
        this.b_2 = new ModelRenderer(this, 92, 20);
        this.b_2.setRotationPoint(8.0f, 5.0f, -1.0f);
        this.b_2.addBox(-4.0f, -12.0f, -5.0f, 11, 27, 10, 0.0f);
        this.setRotateAngle(this.b_2, 0.0f, 0.0f, 0.18849556f);
        this.b_6 = new ModelRenderer(this, 70, 57);
        this.b_6.setRotationPoint(-1.1f, 16.5f, 5.0f);
        this.b_6.addBox(-1.0f, -16.0f, -7.0f, 11, 19, 12, 0.0f);
        this.setRotateAngle(this.b_6, -0.37699112f, 0.0f, 0.0f);
        this.mainbody.addChild(this.b);
        this.mainbody.addChild(this.b_5);
        this.mainbody.addChild(this.b_4);
        this.mainbody.addChild(this.b_1);
        this.mainbody.addChild(this.b_7);
        this.mainbody.addChild(this.b_3);
        this.mainbody.addChild(this.b_9);
        this.mainbody.addChild(this.b_8);
        this.mainbody.addChild(this.b_2);
        this.mainbody.addChild(this.b_6);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
        this.renderTwo(this.mainbody, ageInTicks, f5, 1.0f, 1.0f, 0.1, 0.8f, 0.05, 0.1);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }
}

