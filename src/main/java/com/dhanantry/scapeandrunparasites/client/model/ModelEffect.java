package com.dhanantry.scapeandrunparasites.client.model;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import net.minecraft.util.Mth;

public abstract class ModelEffect
extends ModelSRP {
    public void renderTwo(ModelRenderer in, float ageInTicks, float f5, float width, float height, double n1, float n2, double n3, double n4) {
        double f1 = n1 + (double)Mth.sin((float)(ageInTicks * n2)) * n3 + n4;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)in.offsetX, (float)in.offsetY, (float)in.offsetZ);
        GlStateManager.translate((float)(in.rotationPointX * f5), (float)(in.rotationPointY * f5), (float)(in.rotationPointZ * f5));
        GlStateManager.scale((double)(f1 + (double)width), (double)(f1 + (double)height), (double)(f1 + (double)width));
        GlStateManager.translate((float)(-in.offsetX), (float)(-in.offsetY), (float)(-in.offsetZ));
        GlStateManager.translate((float)(-in.rotationPointX * f5), (float)(-in.rotationPointY * f5), (float)(-in.rotationPointZ * f5));
        in.render(f5);
        GlStateManager.popMatrix();
    }
}

