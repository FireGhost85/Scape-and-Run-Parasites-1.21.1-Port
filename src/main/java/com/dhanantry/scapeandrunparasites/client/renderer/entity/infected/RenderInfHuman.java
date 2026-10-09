package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfHuman;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderInfHuman
extends RenderSRP<EntityInfHuman> {
    private static final ResourceLocation TEXTURE_DEFAULT = ResourceLocation.parse("srparasites:textures/entity/monster/human.png");
    private static final ResourceLocation TEXTURE_ALT = ResourceLocation.parse("srparasites:textures/entity/monster/human1.png");
    private static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/humanfrozen.png");
    private static final ResourceLocation TEXTURE_EATEN = ResourceLocation.parse("srparasites:textures/entity/monster/humaneaten.png");
    private static final ResourceLocation TEXTURE_FLOOD = ResourceLocation.parse("srparasites:textures/entity/monster/humanflood.png");
    private static final ResourceLocation TEXTURE_KIM = ResourceLocation.parse("srparasites:textures/entity/monster/humanforge.png");

    public RenderInfHuman(RenderManager manager) {
        super(manager, new ModelInfHuman(), 0.5f);
    }

    protected void preRenderCallback(EntityInfHuman entity, float partialTickTime) {
        float f = entity.getSelfeFlashIntensity(partialTickTime);
        float ff = entity.getSelfeFlashIntensity2();
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)(ff * f3), (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityInfHuman entity) {
        if (entity.hasCustomName() && "Kim".equalsIgnoreCase(SRPEntityUtil.getCustomNameTag(entity))) {
            return TEXTURE_KIM;
        }
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE_ALT;
            }
            case 2: {
                return TEXTURE_EATEN;
            }
            case 3: {
                return TEXTURE_FLOOD;
            }
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURE_DEFAULT;
    }
}

