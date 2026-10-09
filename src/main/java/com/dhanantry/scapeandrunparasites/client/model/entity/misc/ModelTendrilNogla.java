package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class ModelTendrilNogla
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer taclejointL;
    public ModelRenderer tentacle;
    public ModelRenderer taclejointL1;
    public ModelRenderer tentacle_1;
    public ModelRenderer taclejointL2;
    public ModelRenderer tentacle_2;
    public ModelRenderer taclejointL3;
    public ModelRenderer tentacle_3;

    public ModelTendrilNogla() {
        this.textureWidth = 80;
        this.textureHeight = 32;
        this.taclejointL2 = new ModelRenderer(this, 32, 0);
        this.taclejointL2.setRotationPoint(0.0f, -0.2f, 9.0f);
        this.taclejointL2.addBox(-1.0f, -1.0f, 0.0f, 2, 2, 1, 0.0f);
        this.tentacle_1 = new ModelRenderer(this, 38, 0);
        this.tentacle_1.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.tentacle_1.addBox(-2.0f, -2.0f, 0.0f, 4, 4, 11, 0.0f);
        this.setRotateAngle(this.tentacle_1, -0.61086524f, 0.0f, 0.0f);
        this.tentacle_3 = new ModelRenderer(this, 42, 16);
        this.tentacle_3.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.tentacle_3.addBox(-1.0f, -1.0f, 0.0f, 2, 2, 13, 0.0f);
        this.setRotateAngle(this.tentacle_3, -0.7853982f, 0.0f, 0.0f);
        this.taclejointL1 = new ModelRenderer(this, 24, 0);
        this.taclejointL1.setRotationPoint(0.0f, -0.2f, 11.0f);
        this.taclejointL1.addBox(-1.5f, -1.5f, 0.0f, 3, 3, 1, 0.0f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 21.5f, 6.0f);
        this.mainbody.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.tentacle_2 = new ModelRenderer(this, 27, 15);
        this.tentacle_2.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.tentacle_2.addBox(-1.5f, -1.5f, 0.0f, 3, 3, 11, 0.0f);
        this.setRotateAngle(this.tentacle_2, -0.8552113f, 0.0f, 0.0f);
        this.taclejointL3 = new ModelRenderer(this, 38, 0);
        this.taclejointL3.setRotationPoint(0.0f, -0.2f, 9.0f);
        this.taclejointL3.addBox(-0.5f, -0.5f, 0.0f, 1, 1, 1, 0.0f);
        this.taclejointL = new ModelRenderer(this, 4, 0);
        this.taclejointL.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.taclejointL.addBox(-2.0f, -2.0f, 0.0f, 4, 4, 1, 0.0f);
        this.setRotateAngle(this.taclejointL, 0.0f, (float)Math.PI, (float)Math.PI);
        this.tentacle = new ModelRenderer(this, 0, 0);
        this.tentacle.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.tentacle.addBox(-2.5f, -2.5f, -1.0f, 5, 5, 14, 0.0f);
        this.tentacle_1.addChild(this.taclejointL2);
        this.taclejointL1.addChild(this.tentacle_1);
        this.taclejointL3.addChild(this.tentacle_3);
        this.tentacle.addChild(this.taclejointL1);
        this.taclejointL2.addChild(this.tentacle_2);
        this.tentacle_2.addChild(this.taclejointL3);
        this.mainbody.addChild(this.taclejointL);
        this.taclejointL.addChild(this.tentacle);
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
        this.taclejointL1.rotateAngleX = f1;
        this.taclejointL2.rotateAngleX = f2;
        this.taclejointL3.rotateAngleX = f3;
    }
}

