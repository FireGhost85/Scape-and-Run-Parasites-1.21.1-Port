package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelMes;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.layer.LayerSnow;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderMes
extends RenderMalleable<EntityMes> {
    public static final ResourceLocation TEXTUREM = ResourceLocation.parse("srparasites:textures/entity/monster/mes.png");
    public static final ResourceLocation TEXTURE1 = ResourceLocation.parse("srparasites:textures/entity/monster/mes1.png");
    public static final ResourceLocation STEXTURE1 = ResourceLocation.parse("srparasites:textures/entity/monster/smes1.png");
    public static final ResourceLocation STEXTURE2 = ResourceLocation.parse("srparasites:textures/entity/monster/smes2.png");
    public static final ResourceLocation STEXTURE3 = ResourceLocation.parse("srparasites:textures/entity/monster/smes3.png");
    public static final ResourceLocation STEXTURE4 = ResourceLocation.parse("srparasites:textures/entity/monster/smes4.png");
    public static final ResourceLocation STEXTURE5 = ResourceLocation.parse("srparasites:textures/entity/monster/smes5.png");

    public RenderMes(RenderManager manager) {
        super(manager, new ModelMes(), 0.7f);
        this.addLayer(new LayerSnow(this, ResourceLocation.parse("srparasites:textures/entity/layer/messnow.png"), 1.0, 1.0, 1.0));
    }

    protected void preRenderCallback(EntityMes entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityMes entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE1;
            }
            case 121: {
                return STEXTURE1;
            }
            case 122: {
                return STEXTURE2;
            }
            case 123: {
                return STEXTURE3;
            }
            case 124: {
                return STEXTURE4;
            }
            case 125: {
                return STEXTURE5;
            }
        }
        return TEXTUREM;
    }
}
