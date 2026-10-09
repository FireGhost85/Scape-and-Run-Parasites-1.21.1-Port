package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelEffect;
import net.minecraft.world.entity.Entity;

public class ModelBombOmboo
extends ModelEffect {
    public ModelRenderer mainbody;
    public ModelRenderer dec;
    public ModelRenderer dec_1;
    public ModelRenderer dec_2;
    public ModelRenderer dec_3;
    public ModelRenderer dec_4;

    public ModelBombOmboo() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 17.0f, 0.0f);
        this.mainbody.addBox(-3.5f, 0.0f, -5.0f, 7, 7, 10, 0.0f);
        this.dec = new ModelRenderer(this, 24, 0);
        this.dec.setRotationPoint(4.0f, 4.0f, 0.0f);
        this.dec.addBox(-2.0f, -2.0f, -3.0f, 4, 4, 6, 0.0f);
        this.setRotateAngle(this.dec, 0.0f, 0.0f, 0.43633232f);
        this.dec_2 = new ModelRenderer(this, 30, 14);
        this.dec_2.setRotationPoint(0.0f, 4.0f, -5.5f);
        this.dec_2.addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4, 0.0f);
        this.setRotateAngle(this.dec_2, 0.4712389f, 0.0f, 0.0f);
        this.dec_1 = new ModelRenderer(this, 38, 4);
        this.dec_1.setRotationPoint(-4.0f, 4.0f, 0.0f);
        this.dec_1.addBox(-2.0f, -2.0f, -3.0f, 4, 4, 6, 0.0f);
        this.setRotateAngle(this.dec_1, 0.0f, 0.0f, -0.43633232f);
        this.dec_4 = new ModelRenderer(this, 0, 17);
        this.dec_4.setRotationPoint(0.0f, 0.3f, 0.0f);
        this.dec_4.addBox(-2.0f, -2.0f, -3.0f, 4, 4, 6, 0.0f);
        this.setRotateAngle(this.dec_4, 0.0f, 0.0f, 0.7853982f);
        this.dec_3 = new ModelRenderer(this, 46, 14);
        this.dec_3.setRotationPoint(0.0f, 4.0f, 5.5f);
        this.dec_3.addBox(-2.0f, -2.0f, -2.0f, 4, 4, 4, 0.0f);
        this.setRotateAngle(this.dec_3, -0.4712389f, 0.0f, 0.0f);
        this.mainbody.addChild(this.dec);
        this.mainbody.addChild(this.dec_2);
        this.mainbody.addChild(this.dec_1);
        this.mainbody.addChild(this.dec_4);
        this.mainbody.addChild(this.dec_3);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
        this.renderTwo(this.mainbody, ageInTicks, f5, 1.0f, 1.0f, 0.1, 0.8f, 0.05, 0.1);
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }
}

