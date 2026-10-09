package com.dhanantry.scapeandrunparasites.client.renderer.entity.layer;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.LayerRenderer;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import net.minecraft.world.entity.Mob;

public class LayerGlowing<T extends EntityPMalleable>
implements LayerRenderer<T> {
    protected final RenderMalleable<T> entity;

    public LayerGlowing(RenderMalleable<T> parasite) {
        this.entity = parasite;
    }

    public void doRenderLayer(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.entity.bindTexture(this.entity.getGlowTexture(this.entity.getBaseTexture(entitylivingbaseIn)));
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        GlStateManager.disableLighting();
        GlStateManager.depthMask((!entitylivingbaseIn.isInvisible() ? 1 : 0) != 0);
        OpenGlHelper.setLightmapTextureCoords((int)OpenGlHelper.lightmapTexUnit, (float)61680.0f, (float)0.0f);
        GlStateManager.enableLighting();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        this.entity.getMainModel().render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        this.entity.setLightmap((Mob)entitylivingbaseIn);
        GlStateManager.disableBlend();
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}

