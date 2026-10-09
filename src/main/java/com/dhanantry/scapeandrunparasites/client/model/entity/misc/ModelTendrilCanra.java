package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.world.entity.Entity;

public class ModelTendrilCanra
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer taclejointL1;
    public ModelRenderer tentacle;
    public ModelRenderer taclejointL2;
    public ModelRenderer tentacle_1;
    public ModelRenderer taclejointL3;
    public ModelRenderer tentacle_2;
    public ModelRenderer taclejointL4;
    public ModelRenderer tentacle_3;

    public ModelTendrilCanra() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.tentacle_3 = new ModelRenderer(this, 30, 16);
        this.tentacle_3.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.tentacle_3.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 12, 0.0f);
        this.setRotateAngle(this.tentacle_3, 0.0f, -0.80285144f, 0.0f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 21.7f, 5.0f);
        this.mainbody.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.mainbody, 0.0f, (float)Math.PI, -1.5707964f);
        this.taclejointL1 = new ModelRenderer(this, 4, 0);
        this.taclejointL1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.taclejointL1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.taclejointL3 = new ModelRenderer(this, 24, 0);
        this.taclejointL3.setRotationPoint(0.0f, 0.0f, 9.0f);
        this.taclejointL3.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.tentacle_2 = new ModelRenderer(this, 0, 15);
        this.tentacle_2.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.tentacle_2.addBox(-1.5f, -1.5f, -1.0f, 3, 3, 12, 0.0f);
        this.setRotateAngle(this.tentacle_2, 0.0f, -0.62831855f, 0.0f);
        this.tentacle = new ModelRenderer(this, 0, 0);
        this.tentacle.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tentacle.addBox(-2.5f, -2.5f, -1.0f, 5, 5, 10, 0.0f);
        this.tentacle_1 = new ModelRenderer(this, 30, 0);
        this.tentacle_1.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.tentacle_1.addBox(-2.0f, -2.0f, -1.0f, 4, 4, 12, 0.0f);
        this.setRotateAngle(this.tentacle_1, 0.0f, -0.54105204f, 0.0f);
        this.taclejointL4 = new ModelRenderer(this, 28, 0);
        this.taclejointL4.setRotationPoint(0.0f, 0.0f, 9.0f);
        this.taclejointL4.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.taclejointL2 = new ModelRenderer(this, 20, 0);
        this.taclejointL2.setRotationPoint(0.0f, 0.0f, 7.0f);
        this.taclejointL2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.taclejointL4.addChild(this.tentacle_3);
        this.mainbody.addChild(this.taclejointL1);
        this.tentacle_1.addChild(this.taclejointL3);
        this.taclejointL3.addChild(this.tentacle_2);
        this.taclejointL1.addChild(this.tentacle);
        this.taclejointL2.addChild(this.tentacle_1);
        this.tentacle_2.addChild(this.taclejointL4);
        this.tentacle.addChild(this.taclejointL2);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.mainbody.render(scale);
    }
}

