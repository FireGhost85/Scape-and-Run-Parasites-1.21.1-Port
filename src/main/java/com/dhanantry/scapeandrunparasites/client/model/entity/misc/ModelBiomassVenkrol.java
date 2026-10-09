package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelEffect;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import net.minecraft.world.entity.Entity;

public class ModelBiomassVenkrol
extends ModelEffect {
    public ModelRenderer mainbodysi;
    public ModelRenderer mainbodysii;
    public ModelRenderer mainbodysiii;
    public ModelRenderer core1;
    public ModelRenderer dec;
    public ModelRenderer dec_1;
    public ModelRenderer dec_2;
    public ModelRenderer dec_3;
    public ModelRenderer core2;
    public ModelRenderer core3;
    public ModelRenderer core2_1;
    public ModelRenderer dec_4;
    public ModelRenderer dec_5;
    public ModelRenderer dec_6;
    public ModelRenderer dec_7;
    public ModelRenderer core3_1;
    public ModelRenderer core4;
    public ModelRenderer core2_2;
    public ModelRenderer core5;
    public ModelRenderer dec_8;
    public ModelRenderer dec_9;
    public ModelRenderer dec_10;
    public ModelRenderer dec_11;
    public ModelRenderer core3_2;
    public ModelRenderer core4_1;
    public ModelRenderer core6;

    public ModelBiomassVenkrol() {
        this.textureWidth = 80;
        this.textureHeight = 64;
        this.core2_2 = new ModelRenderer(this, 4, 14);
        this.core2_2.setRotationPoint(0.0f, -2.5f, 0.0f);
        this.core2_2.addBox(-4.0f, -3.0f, -4.0f, 8, 3, 8, 0.0f);
        this.dec_1 = new ModelRenderer(this, 64, 0);
        this.dec_1.setRotationPoint(-2.3f, -1.0f, 0.0f);
        this.dec_1.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2, 0.0f);
        this.setRotateAngle(this.dec_1, 0.0f, 0.0f, -1.1170107f);
        this.core3_1 = new ModelRenderer(this, 71, 11);
        this.core3_1.setRotationPoint(0.0f, -1.0f, 0.0f);
        this.core3_1.addBox(-1.0f, -3.0f, -1.0f, 2, 2, 2, 0.0f);
        this.dec_7 = new ModelRenderer(this, 0, 12);
        this.dec_7.setRotationPoint(-2.5f, -1.2f, 0.0f);
        this.dec_7.addBox(-1.5f, -1.0f, -1.5f, 3, 2, 3, 0.0f);
        this.setRotateAngle(this.dec_7, 0.0f, 0.0f, 0.55850536f);
        this.core5 = new ModelRenderer(this, 26, 15);
        this.core5.setRotationPoint(0.0f, -0.5f, 0.0f);
        this.core5.addBox(-5.0f, -3.0f, -5.0f, 10, 2, 10, 0.0f);
        this.setRotateAngle(this.core5, (float)(-Math.PI), 0.0f, 0.0f);
        this.mainbodysiii = new ModelRenderer(this, 22, 0);
        this.mainbodysiii.setRotationPoint(0.0f, 19.5f, 0.0f);
        this.mainbodysiii.addBox(-5.0f, -3.0f, -5.0f, 10, 4, 10, 0.0f);
        this.setRotateAngle(this.mainbodysiii, 0.0f, 0.7853982f, 0.0f);
        this.dec_11 = new ModelRenderer(this, 48, 31);
        this.dec_11.setRotationPoint(0.0f, -1.0f, -4.6f);
        this.dec_11.addBox(-2.5f, -3.5f, -1.5f, 5, 7, 3, 0.0f);
        this.setRotateAngle(this.dec_11, 0.31415927f, 0.0f, 0.0f);
        this.core1 = new ModelRenderer(this, 52, 0);
        this.core1.setRotationPoint(0.0f, -1.5f, 0.0f);
        this.core1.addBox(-1.5f, -3.0f, -1.5f, 3, 2, 3, 0.0f);
        this.core3_2 = new ModelRenderer(this, 0, 33);
        this.core3_2.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.core3_2.addBox(-3.0f, -3.0f, -3.0f, 6, 2, 6, 0.0f);
        this.core4_1 = new ModelRenderer(this, 56, 16);
        this.core4_1.setRotationPoint(0.0f, -2.5f, 0.0f);
        this.core4_1.addBox(-2.0f, -3.0f, -2.0f, 4, 3, 4, 0.0f);
        this.core4 = new ModelRenderer(this, 28, 0);
        this.core4.setRotationPoint(0.0f, -1.0f, 0.0f);
        this.core4.addBox(-0.5f, -3.0f, -0.5f, 1, 2, 1, 0.0f);
        this.mainbodysi = new ModelRenderer(this, 0, 0);
        this.mainbodysi.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.mainbodysi.addBox(-2.0f, -3.0f, -2.0f, 4, 3, 4, 0.0f);
        this.setRotateAngle(this.mainbodysi, 0.0f, 0.7853982f, 0.0f);
        this.core3 = new ModelRenderer(this, 0, 0);
        this.core3.setRotationPoint(0.0f, -1.0f, 0.0f);
        this.core3.addBox(-0.5f, -3.0f, -0.5f, 1, 2, 1, 0.0f);
        this.dec = new ModelRenderer(this, 12, 0);
        this.dec.setRotationPoint(2.3f, -1.0f, 0.0f);
        this.dec.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2, 0.0f);
        this.setRotateAngle(this.dec, 0.0f, 0.0f, 1.1170107f);
        this.dec_2 = new ModelRenderer(this, 70, 2);
        this.dec_2.setRotationPoint(0.0f, -1.0f, 2.3f);
        this.dec_2.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2, 0.0f);
        this.setRotateAngle(this.dec_2, -1.1170107f, 0.0f, 0.0f);
        this.dec_8 = new ModelRenderer(this, 0, 25);
        this.dec_8.setRotationPoint(-4.6f, -1.0f, 0.0f);
        this.dec_8.addBox(-3.5f, -1.5f, -2.5f, 7, 3, 5, 0.0f);
        this.setRotateAngle(this.dec_8, 0.0f, 0.0f, 1.2566371f);
        this.dec_9 = new ModelRenderer(this, 24, 27);
        this.dec_9.setRotationPoint(4.6f, -1.0f, 0.0f);
        this.dec_9.addBox(-3.5f, -1.5f, -2.5f, 7, 3, 5, 0.0f);
        this.setRotateAngle(this.dec_9, 0.0f, 0.0f, -1.2566371f);
        this.core2_1 = new ModelRenderer(this, 67, 6);
        this.core2_1.setRotationPoint(0.0f, -1.5f, 0.0f);
        this.core2_1.addBox(-1.5f, -3.0f, -1.5f, 3, 2, 3, 0.0f);
        this.dec_10 = new ModelRenderer(this, 63, 24);
        this.dec_10.setRotationPoint(0.0f, -1.0f, 4.6f);
        this.dec_10.addBox(-2.5f, -3.5f, -1.5f, 5, 7, 3, 0.0f);
        this.setRotateAngle(this.dec_10, -0.31415927f, 0.0f, 0.0f);
        this.core2 = new ModelRenderer(this, 52, 5);
        this.core2.setRotationPoint(0.0f, -1.0f, 0.0f);
        this.core2.addBox(-1.0f, -3.0f, -1.0f, 2, 2, 2, 0.0f);
        this.dec_5 = new ModelRenderer(this, 10, 7);
        this.dec_5.setRotationPoint(0.0f, -1.2f, 2.5f);
        this.dec_5.addBox(-1.5f, -1.5f, -1.0f, 3, 3, 2, 0.0f);
        this.setRotateAngle(this.dec_5, -1.1170107f, 0.0f, 0.0f);
        this.mainbodysii = new ModelRenderer(this, 16, 0);
        this.mainbodysii.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.mainbodysii.addBox(-2.0f, -3.0f, -2.0f, 4, 3, 4, 0.0f);
        this.setRotateAngle(this.mainbodysii, 0.0f, 0.7853982f, 0.0f);
        this.dec_6 = new ModelRenderer(this, 59, 11);
        this.dec_6.setRotationPoint(2.5f, -1.2f, 0.0f);
        this.dec_6.addBox(-1.5f, -1.0f, -1.5f, 3, 2, 3, 0.0f);
        this.setRotateAngle(this.dec_6, 0.0f, 0.0f, -0.55850536f);
        this.core6 = new ModelRenderer(this, 16, 35);
        this.core6.setRotationPoint(0.0f, -2.0f, 0.0f);
        this.core6.addBox(-4.0f, -3.0f, -4.0f, 8, 2, 8, 0.0f);
        this.dec_3 = new ModelRenderer(this, 62, 4);
        this.dec_3.setRotationPoint(0.0f, -1.0f, -2.3f);
        this.dec_3.addBox(-1.0f, -1.0f, -1.0f, 2, 2, 2, 0.0f);
        this.setRotateAngle(this.dec_3, 1.1170107f, 0.0f, 0.0f);
        this.dec_4 = new ModelRenderer(this, 0, 7);
        this.dec_4.setRotationPoint(0.0f, -1.2f, -2.5f);
        this.dec_4.addBox(-1.5f, -1.5f, -1.0f, 3, 3, 2, 0.0f);
        this.setRotateAngle(this.dec_4, 1.1170107f, 0.0f, 0.0f);
        this.mainbodysiii.addChild(this.core2_2);
        this.mainbodysi.addChild(this.dec_1);
        this.core2_1.addChild(this.core3_1);
        this.mainbodysii.addChild(this.dec_7);
        this.mainbodysiii.addChild(this.core5);
        this.mainbodysiii.addChild(this.dec_11);
        this.mainbodysi.addChild(this.core1);
        this.core2_2.addChild(this.core3_2);
        this.core3_2.addChild(this.core4_1);
        this.core3_1.addChild(this.core4);
        this.core2.addChild(this.core3);
        this.mainbodysi.addChild(this.dec);
        this.mainbodysi.addChild(this.dec_2);
        this.mainbodysiii.addChild(this.dec_8);
        this.mainbodysiii.addChild(this.dec_9);
        this.mainbodysii.addChild(this.core2_1);
        this.mainbodysiii.addChild(this.dec_10);
        this.core1.addChild(this.core2);
        this.mainbodysii.addChild(this.dec_5);
        this.mainbodysii.addChild(this.dec_6);
        this.core5.addChild(this.core6);
        this.mainbodysi.addChild(this.dec_3);
        this.mainbodysii.addChild(this.dec_4);
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float f5) {
        super.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5);
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, f5, entityIn);
        EntityBiomass pod = (EntityBiomass)entityIn;
        switch (pod.getSkin()) {
            case 1: {
                this.renderTwo(this.mainbodysi, ageInTicks, f5, pod.getGrowW(), pod.getGrowHeight(), 0.6, 0.8f, 0.05, 0.8);
                break;
            }
            case 2: {
                this.renderTwo(this.mainbodysii, ageInTicks, f5, pod.getGrowW(), pod.getGrowHeight(), 0.6, 0.8f, 0.05, 0.8);
                break;
            }
            case 3: {
                this.renderTwo(this.mainbodysiii, ageInTicks, f5, pod.getGrowW(), pod.getGrowHeight(), 0.6, 0.8f, 0.05, 0.8);
            }
        }
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
    }
}

