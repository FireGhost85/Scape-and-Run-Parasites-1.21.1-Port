package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfHumanHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHumanHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfHumanHead
extends RenderSRP<EntityInfHumanHead> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/humanh.png");
    public static final ResourceLocation TEXTURE1 = ResourceLocation.parse("srparasites:textures/entity/monster/humanh1.png");
    public static final ResourceLocation TEXTURE2 = ResourceLocation.parse("srparasites:textures/entity/monster/humanh2.png");
    public static final ResourceLocation TEXTURE3 = ResourceLocation.parse("srparasites:textures/entity/monster/humanhnocturn.png");

    public RenderInfHumanHead(RenderManager manager) {
        super(manager, new ModelInfHumanHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfHumanHead entity) {
        switch (entity.getSkin()) {
            case 10: {
                return TEXTURE3;
            }
            case 1: {
                return TEXTURE1;
            }
            case 2: {
                return TEXTURE;
            }
        }
        return TEXTURE;
    }

    protected void applyRotations(EntityInfHumanHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

