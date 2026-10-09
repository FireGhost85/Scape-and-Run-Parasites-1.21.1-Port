package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfEndermanHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfEndermanHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfEndermanHead
extends RenderSRP<EntityInfEndermanHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/endermanh.png");

    public RenderInfEndermanHead(RenderManager manager) {
        super(manager, new ModelInfEndermanHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfEndermanHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfEndermanHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

