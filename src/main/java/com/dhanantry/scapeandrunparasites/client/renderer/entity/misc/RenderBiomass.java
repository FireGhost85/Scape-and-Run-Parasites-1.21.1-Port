package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBiomassPod;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBiomassVenkrol;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class RenderBiomass
extends RenderSRP<EntityBiomass> {
    protected static ModelBase modelV = new ModelBiomassVenkrol();
    protected static ModelBase modelP = new ModelBiomassPod();
    public static final ResourceLocation TEXTUREV = ResourceLocation.parse("srparasites:textures/entity/monster/biomassvenkrol.png");
    public static final ResourceLocation TEXTUREP = ResourceLocation.parse("srparasites:textures/entity/monster/biomasspod.png");

    public RenderBiomass(RenderManager p_i47208_1_) {
        super(p_i47208_1_, modelV, 0.5f);
    }

    protected void preRenderCallback(EntityBiomass entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityBiomass entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTUREV;
            }
            case 2: {
                return TEXTUREV;
            }
            case 3: {
                return TEXTUREV;
            }
            case 4: {
                return TEXTUREP;
            }
            case 5: {
                return TEXTUREP;
            }
            case 6: {
                return TEXTUREP;
            }
        }
        return TEXTUREP;
    }

    protected void applyRotations(EntityBiomass entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }

    protected void renderModel(EntityBiomass entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean flag1;
        boolean flag = this.isVisible(entitylivingbaseIn);
        boolean bl = flag1 = !flag && !entitylivingbaseIn.isInvisibleTo((Player)Minecraft.getInstance().player);
        if (flag || flag1) {
            if (!this.bindEntityTexture(entitylivingbaseIn)) {
                return;
            }
            if (flag1) {
            }
            switch (entitylivingbaseIn.getSkin()) {
                case 1: {
                    modelV.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 2: {
                    modelV.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 3: {
                    modelV.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 4: {
                    modelP.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 5: {
                    modelP.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                    break;
                }
                case 6: {
                    modelP.render((Entity)entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                }
            }
            if (flag1) {
            }
        }
    }
}

