package com.dhanantry.scapeandrunparasites.client.model.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.world.entity.Entity;

public class ModelNade
extends ModelSRP {
    public ModelRenderer mainbody;

    public ModelNade() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.mainbody = new ModelRenderer(this, 0, 0);
        this.mainbody.setRotationPoint(0.0f, 20.0f, 0.0f);
        this.mainbody.addBox(-8.0f, -12.0f, -8.0f, 16, 16, 16, 0.0f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        double scale = 0.5;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.mainbody.offsetX, (float)this.mainbody.offsetY, (float)this.mainbody.offsetZ);
        GlStateManager.translate((float)(this.mainbody.rotationPointX * f5), (float)(this.mainbody.rotationPointY * f5), (float)(this.mainbody.rotationPointZ * f5));
        GlStateManager.scale((double)scale, (double)scale, (double)scale);
        GlStateManager.translate((float)(-this.mainbody.offsetX), (float)(-this.mainbody.offsetY), (float)(-this.mainbody.offsetZ));
        GlStateManager.translate((float)(-this.mainbody.rotationPointX * f5), (float)(-this.mainbody.rotationPointY * f5), (float)(-this.mainbody.rotationPointZ * f5));
        this.mainbody.render(f5);
        GlStateManager.popMatrix();
    }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.mainbody.offsetY = 0.14f;
    }
}

