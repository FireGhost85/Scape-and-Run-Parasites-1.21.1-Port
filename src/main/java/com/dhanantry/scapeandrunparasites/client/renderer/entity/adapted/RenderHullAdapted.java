package com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.adapted.ModelHullAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class RenderHullAdapted
extends RenderMalleable<EntityHullAdapted> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/hulla_old.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/hullah_old.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");
    private float secShadow = 1.0f;

    public RenderHullAdapted(RenderManager manager) {
        super(manager, new ModelHullAdapted(), 1.0f);
    }

    protected void preRenderCallback(EntityHullAdapted entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityHullAdapted entity) {
        switch (entity.getSkin()) {
            case 7: {
                return TEXTUREH;
            }
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }

    protected void renderModel(EntityHullAdapted entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean flag1 = !flag;
        boolean flag2 = entitylivingbaseIn.getSSS();
        if (flag || flag1 || flag2) {
            float f1 = 0.15f;
            if (flag2) {
                f1 = 0.02f;
            }
            if (!this.bindEntityTexture(entitylivingbaseIn)) {
                return;
            }
            if (flag1 || flag2) {
                this.shadowSize = 0.0f;
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)f1);
                GlStateManager.depthMask((boolean)false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.alphaFunc((int)516, (float)0.003921569f);
            }
            this.mainModel.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            if (flag1 || flag2) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc((int)516, (float)0.1f);
                GlStateManager.depthMask((boolean)true);
            } else {
                this.shadowSize = this.secShadow;
            }
        }
    }
}

