package com.dhanantry.scapeandrunparasites.client.renderer.entity.layer;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.LayerRenderer;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.resources.ResourceLocation;

public class LayerSnow<T extends EntityParasiteBase>
implements LayerRenderer<T> {
    private ResourceLocation LAYER;
    private final RenderLiving parasite;

    public LayerSnow(RenderLiving in, ResourceLocation l, double x, double y, double z) {
        this.parasite = in;
        this.LAYER = l;
    }

    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!((EntityParasiteBase)entitylivingbaseIn).getColdL()) {
            return;
        }
        this.parasite.bindTexture(this.LAYER);
        GlStateManager.enableBlend();
        if (entitylivingbaseIn.isInvisible()) {
            GlStateManager.depthMask((boolean)false);
        } else {
            GlStateManager.depthMask((boolean)true);
        }
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.parasite.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale + 0.0f);
        this.parasite.setLightmap(entitylivingbaseIn);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}

