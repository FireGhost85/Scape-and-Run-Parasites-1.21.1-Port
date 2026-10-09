package com.dhanantry.scapeandrunparasites.client.renderer.entity.derived;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.derived.ModelKirin;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderCosmical;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class RenderKirin
extends RenderCosmical<EntityKirin> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/kirin.png");
    public static final ResourceLocation TEXTURESEC = ResourceLocation.parse("srparasites:textures/entity/monster/testb.png");

    public RenderKirin(RenderManager manager) {
        super(manager, new ModelKirin(), 1.3f);
    }

    protected ResourceLocation getEntityTexture(EntityKirin entity) {
        return TEXTURES;
    }

    @Override
    protected ResourceLocation getEntityTextureCosmical(EntityKirin entity) {
        return TEXTURESEC;
    }

    @Override
    protected void preRenderCallback(EntityKirin entitylivingbaseIn, float partialTickTime) {
        super.preRenderCallback(entitylivingbaseIn, partialTickTime);
        GlStateManager.translate((float)0.0f, (float)-1.75f, (float)0.0f);
    }

    @Override
    protected void renderModel(EntityKirin entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        super.renderModel(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        if (entity.isChargingJudgementCut()) {
            this.renderJudgementCutAura(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        }
    }

    private void renderJudgementCutAura(EntityKirin entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        float scale;
        float alpha;
        int chargeTicks = entity.getJudgementCutChargeTicks();
        int endTicks = entity.getJudgementCutAuraEndTicks();
        if (chargeTicks <= 0 && endTicks <= 0) {
            return;
        }
        if (chargeTicks > 0) {
            float progress = 1.0f - Mth.clamp((float)((float)chargeTicks / 60.0f), (float)0.0f, (float)1.0f);
            float fadeIn = Mth.clamp((float)(progress / 0.28f), (float)0.0f, (float)1.0f);
            fadeIn = fadeIn * fadeIn * (3.0f - 2.0f * fadeIn);
            float pulse = 0.5f + 0.5f * Mth.sin((float)(((float)entity.tickCount + progress * 20.0f) * 0.45f));
            alpha = (0.06f + pulse * 0.11f) * fadeIn;
            scale = 1.015f + progress * 0.085f + pulse * 0.01f;
        } else {
            float endProgress = 1.0f - Mth.clamp((float)((float)endTicks / 24.0f), (float)0.0f, (float)1.0f);
            endProgress = endProgress * endProgress * (3.0f - 2.0f * endProgress);
            alpha = 0.22f * (1.0f - endProgress);
            scale = 1.1f + endProgress * 0.9f;
        }
        if (alpha <= 0.01f) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.scale((float)scale, (float)scale, (float)scale);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask((boolean)false);
        GlStateManager.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        GlStateManager.disableTexture2D();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        this.mainModel.render((Entity)entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
        GlStateManager.enableTexture2D();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.depthMask((boolean)true);
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}

