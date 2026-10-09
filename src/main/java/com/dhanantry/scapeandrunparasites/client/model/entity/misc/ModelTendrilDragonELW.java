package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.world.entity.Entity;

public class ModelTendrilDragonELW
extends ModelSRP {
    public ModelRenderer mainbody;
    public ModelRenderer jointLW1;
    public ModelRenderer jd;
    public ModelRenderer jointLW1_1;
    public ModelRenderer w;
    public ModelRenderer skin;
    public ModelRenderer jointLW2;
    public ModelRenderer dec;
    public ModelRenderer dec_1;
    public ModelRenderer dec_2;
    public ModelRenderer w_1;
    public ModelRenderer skin_1;
    public ModelRenderer dec_3;

    public ModelTendrilDragonELW() {
        this.textureWidth = 200;
        this.textureHeight = 150;
        this.jointLW1 = new ModelRenderer(this, 4, 0);
        this.jointLW1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.jointLW1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.w_1 = new ModelRenderer(this, 160, 6);
        this.w_1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.w_1.addBox(-15.0f, -2.0f, -2.0f, 15, 4, 4, 0.0f);
        this.setRotateAngle(this.w_1, 0.0f, 0.0f, -0.9536479f);
        this.skin = new ModelRenderer(this, -49, 24);
        this.skin.setRotationPoint(0.0f, 0.0f, 2.0f);
        this.skin.addBox(-60.0f, 0.0f, 0.0f, 60, 0, 58, 0.0f);
        this.w = new ModelRenderer(this, 8, 0);
        this.w.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.w.addBox(-38.0f, -4.0f, -4.0f, 38, 8, 8, 0.0f);
        this.setRotateAngle(this.w, -0.29670596f, 0.0f, -0.08726646f);
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(-23.0f, 22.0f, -22.0f);
        this.mainbody.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.mainbody, -0.25132743f, 0.0f, 0.0f);
        this.jointLW2 = new ModelRenderer(this, 92, 0);
        this.jointLW2.setRotationPoint(-60.0f, 0.0f, 0.0f);
        this.jointLW2.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.jd = new ModelRenderer(this, 8, 0);
        this.jd.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.jd.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.setRotateAngle(this.jd, 0.0f, 0.24993114f, (float)Math.PI);
        this.skin_1 = new ModelRenderer(this, 16, 85);
        this.skin_1.setRotationPoint(-56.0f, 0.0f, 0.0f);
        this.skin_1.addBox(-56.0f, 0.0f, 2.0f, 60, 0, 60, 0.0f);
        this.setRotateAngle(this.skin_1, 0.0f, 0.0f, (float)(-Math.PI));
        this.dec_1 = new ModelRenderer(this, 154, 0);
        this.dec_1.setRotationPoint(-6.0f, -4.6f, -0.5f);
        this.dec_1.addBox(-1.5f, -1.0f, -1.0f, 3, 5, 2, 0.0f);
        this.setRotateAngle(this.dec_1, 1.5707964f, -1.8221238f, 0.0f);
        this.dec_2 = new ModelRenderer(this, 164, 0);
        this.dec_2.setRotationPoint(-4.0f, -0.6f, -4.5f);
        this.dec_2.addBox(-1.5f, -1.0f, -1.0f, 3, 4, 2, 0.0f);
        this.setRotateAngle(this.dec_2, 0.0f, 0.0f, 0.50265485f);
        this.jointLW1_1 = new ModelRenderer(this, 12, 0);
        this.jointLW1_1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.jointLW1_1.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1, 0.0f);
        this.dec_3 = new ModelRenderer(this, 97, 14);
        this.dec_3.setRotationPoint(-16.0f, 0.0f, 0.0f);
        this.dec_3.addBox(-1.0f, -1.0f, -2.0f, 47, 2, 3, 0.0f);
        this.setRotateAngle(this.dec_3, 0.0f, (float)Math.PI, 0.0f);
        this.dec = new ModelRenderer(this, 100, 0);
        this.dec.setRotationPoint(-39.0f, 0.0f, 0.0f);
        this.dec.addBox(-1.0f, -1.0f, -2.5f, 22, 4, 5, 0.0f);
        this.setRotateAngle(this.dec, 0.0f, (float)Math.PI, 0.0f);
        this.mainbody.addChild(this.jointLW1);
        this.jointLW2.addChild(this.w_1);
        this.w.addChild(this.skin);
        this.jointLW1_1.addChild(this.w);
        this.w.addChild(this.jointLW2);
        this.jointLW1.addChild(this.jd);
        this.w_1.addChild(this.skin_1);
        this.w.addChild(this.dec_1);
        this.w.addChild(this.dec_2);
        this.jd.addChild(this.jointLW1_1);
        this.w_1.addChild(this.dec_3);
        this.w.addChild(this.dec);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.mainbody.render(scale);
    }
}

