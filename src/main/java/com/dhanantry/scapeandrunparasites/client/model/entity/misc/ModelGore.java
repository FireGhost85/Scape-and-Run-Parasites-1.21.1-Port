package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import net.minecraft.world.entity.Entity;

public class ModelGore
extends ModelSRP {
    public ModelRenderer sim;
    public ModelRenderer pri;
    public ModelRenderer ada;
    public ModelRenderer pure;
    public ModelRenderer dec;
    public ModelRenderer dec_1;
    public ModelRenderer dec_2;
    public ModelRenderer dec_3;
    public ModelRenderer dec_4;
    public ModelRenderer dec_5;
    public ModelRenderer dec_6;
    public ModelRenderer dec_7;

    public ModelGore() {
        this.textureWidth = 128;
        this.textureHeight = 128;
        this.dec = new ModelRenderer(this, 80, 4);
        this.dec.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec.addBox(-0.5f, -8.0f, -8.0f, 1, 16, 16, 0.0f);
        this.setRotateAngle(this.dec, 0.7853982f, 0.0f, 0.0f);
        this.dec_1 = new ModelRenderer(this, 48, 20);
        this.dec_1.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_1.addBox(-0.5f, -8.0f, -8.0f, 1, 16, 16, 0.0f);
        this.setRotateAngle(this.dec_1, 0.7853982f, 1.5707964f, 0.0f);
        this.dec_4 = new ModelRenderer(this, 38, 52);
        this.dec_4.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_4.addBox(-0.5f, -10.0f, -10.0f, 1, 20, 20, 0.0f);
        this.setRotateAngle(this.dec_4, 0.7853982f, 1.5707964f, 0.0f);
        this.sim = new ModelRenderer(this, 0, 0);
        this.sim.setRotationPoint(0.0f, 21.0f, 0.0f);
        this.sim.addBox(-3.0f, -3.0f, -3.0f, 6, 6, 6, 0.0f);
        this.setRotateAngle(this.sim, 0.0f, -0.7853982f, 0.0f);
        this.dec_6 = new ModelRenderer(this, 58, 72);
        this.dec_6.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_6.addBox(-0.5f, -11.0f, -11.0f, 1, 22, 22, 0.0f);
        this.setRotateAngle(this.dec_6, 0.7853982f, 1.5707964f, 0.0f);
        this.dec_3 = new ModelRenderer(this, 0, 40);
        this.dec_3.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_3.addBox(-0.5f, -9.0f, -9.0f, 1, 18, 18, 0.0f);
        this.setRotateAngle(this.dec_3, 0.7853982f, 0.0f, 0.0f);
        this.dec_5 = new ModelRenderer(this, 82, 52);
        this.dec_5.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_5.addBox(-0.5f, -10.0f, -10.0f, 1, 20, 20, 0.0f);
        this.setRotateAngle(this.dec_5, 0.7853982f, 0.0f, 0.0f);
        this.pure = new ModelRenderer(this, 0, 16);
        this.pure.setRotationPoint(0.0f, 18.0f, 0.0f);
        this.pure.addBox(-6.0f, -6.0f, -6.0f, 12, 12, 12, 0.0f);
        this.setRotateAngle(this.pure, 0.0f, -0.7853982f, 0.0f);
        this.dec_2 = new ModelRenderer(this, 64, 36);
        this.dec_2.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_2.addBox(-0.5f, -9.0f, -9.0f, 1, 18, 18, 0.0f);
        this.setRotateAngle(this.dec_2, 0.7853982f, 1.5707964f, 0.0f);
        this.pri = new ModelRenderer(this, 24, 0);
        this.pri.setRotationPoint(0.0f, 20.0f, 0.0f);
        this.pri.addBox(-4.0f, -4.0f, -4.0f, 8, 8, 8, 0.0f);
        this.setRotateAngle(this.pri, 0.0f, -0.7853982f, 0.0f);
        this.ada = new ModelRenderer(this, 56, 0);
        this.ada.setRotationPoint(0.0f, 19.0f, 0.0f);
        this.ada.addBox(-5.0f, -5.0f, -5.0f, 10, 10, 10, 0.0f);
        this.setRotateAngle(this.ada, 0.0f, -0.7853982f, 0.0f);
        this.dec_7 = new ModelRenderer(this, 0, 76);
        this.dec_7.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.dec_7.addBox(-0.5f, -11.0f, -11.0f, 1, 22, 22, 0.0f);
        this.setRotateAngle(this.dec_7, 0.7853982f, 0.0f, 0.0f);
        this.sim.addChild(this.dec);
        this.sim.addChild(this.dec_1);
        this.ada.addChild(this.dec_4);
        this.pure.addChild(this.dec_6);
        this.pri.addChild(this.dec_3);
        this.ada.addChild(this.dec_5);
        this.pri.addChild(this.dec_2);
        this.pure.addChild(this.dec_7);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5, entityIn);
        EntityGore in = (EntityGore)entityIn;
        switch (in.getSkin()) {
            case 1: {
                this.sim.render(f5);
                break;
            }
            case 2: {
                this.pri.render(f5);
                break;
            }
            case 3: {
                this.ada.render(f5);
                break;
            }
            case 4: {
                this.pure.render(f5);
                break;
            }
            case 10: {
                this.sim.render(f5);
            }
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }
}

