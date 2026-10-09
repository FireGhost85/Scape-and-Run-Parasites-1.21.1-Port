package com.dhanantry.scapeandrunparasites.client.model.entity;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

/** The armor model of the infected player (SRPModelBiped of 1.12): a biped with its own pose for the infected player (type 1). */
public class SRPModelBiped extends ModelBase {
    private byte parent;
    public ModelRenderer jointH;
    public ModelRenderer jointHW;
    public ModelRenderer body;
    public ModelRenderer rightA;
    public ModelRenderer leftA;
    public ModelRenderer rightL;
    public ModelRenderer leftL;

    public SRPModelBiped(float modelSize) {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.jointH = new ModelRenderer(this, 0, 0);
        this.jointH.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, modelSize);
        this.jointH.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.jointHW = new ModelRenderer(this, 32, 0);
        this.jointHW.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, modelSize + 0.5f);
        this.jointHW.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.body = new ModelRenderer(this, 16, 16);
        this.body.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, modelSize);
        this.body.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.rightA = new ModelRenderer(this, 40, 16);
        this.rightA.addBox(-3.0f, -2.0f, -2.0f, 4, 12, 4, modelSize);
        this.rightA.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.leftA = new ModelRenderer(this, 40, 16);
        this.leftA.mirror = true;
        this.leftA.addBox(-1.0f, -2.0f, -2.0f, 4, 12, 4, modelSize);
        this.leftA.setRotationPoint(5.0f, 2.0f, 0.0f);
        this.rightL = new ModelRenderer(this, 0, 16);
        this.rightL.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, modelSize);
        this.rightL.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.leftL = new ModelRenderer(this, 0, 16);
        this.leftL.mirror = true;
        this.leftL.addBox(-2.0f, 0.0f, -2.0f, 4, 12, 4, modelSize);
        this.leftL.setRotationPoint(1.9f, 12.0f, 0.0f);
    }

    public void setParent(byte in) {
        this.parent = in;
    }

    public void setInvisible(boolean visible) {
        this.jointH.showModel = visible;
        this.jointHW.showModel = visible;
        this.body.showModel = visible;
        this.rightA.showModel = visible;
        this.leftA.showModel = visible;
        this.rightL.showModel = visible;
        this.leftL.showModel = visible;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.pushMatrix();
        if (this.isChild) {
            GlStateManager.scale(0.75f, 0.75f, 0.75f);
            GlStateManager.translate(0.0f, 16.0f * scale, 0.0f);
            this.jointH.render(scale);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5f, 0.5f, 0.5f);
            GlStateManager.translate(0.0f, 24.0f * scale, 0.0f);
            this.body.render(scale);
            this.rightA.render(scale);
            this.leftA.render(scale);
            this.rightL.render(scale);
            this.leftL.render(scale);
            this.jointHW.render(scale);
        } else {
            if (entityIn.isShiftKeyDown()) {
                GlStateManager.translate(0.0f, 0.2f, 0.0f);
            }
            this.jointH.render(scale);
            this.body.render(scale);
            this.rightA.render(scale);
            this.leftA.render(scale);
            this.rightL.render(scale);
            this.leftL.render(scale);
            this.jointHW.render(scale);
        }
        GlStateManager.popMatrix();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.jointH.offsetY = 0.0f;
        this.jointH.offsetZ = 0.0f;
        this.jointH.rotateAngleX = 0.0f;
        this.leftL.rotateAngleX = 0.0f;
        this.rightL.rotateAngleX = 0.0f;
        if (this.parent == 1) {
            this.jointH.offsetY = -0.25f;
            this.jointH.offsetZ = 0.24000001f;
            float f4 = Mth.cos(ageInTicks * 0.08f) * 0.07f;
            this.jointH.rotateAngleX = 0.45f + f4;
            if (entityIn.xo == entityIn.getX() && entityIn.zo == entityIn.getZ()) {
                return;
            }
            float gs = 1.75f;
            float gd = 0.55f;
            this.swingX(this.leftL, 0.3f * gs, 5.0f * gd, -1, limbSwing, limbSwingAmount);
            this.swingX(this.rightL, 0.3f * gs, 5.0f * gd, 1, limbSwing, limbSwingAmount);
        }
    }

    public void swingX(ModelRenderer modelRenderer, float speed, float degree, int invert, float limbSwing, float limbSwingAmount) {
        modelRenderer.rotateAngleX = (float)((double)((float)invert * limbSwingAmount * degree) * Math.cos(limbSwing * speed) * (double)limbSwingAmount);
    }
}
