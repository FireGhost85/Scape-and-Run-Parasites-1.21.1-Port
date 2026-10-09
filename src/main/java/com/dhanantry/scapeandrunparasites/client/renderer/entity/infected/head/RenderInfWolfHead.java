package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfWolfHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfWolfHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfWolfHead
extends RenderSRP<EntityInfWolfHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/wolfh.png");

    public RenderInfWolfHead(RenderManager manager) {
        super(manager, new ModelInfWolfHead(), 0.4f);
    }

    protected ResourceLocation getEntityTexture(EntityInfWolfHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfWolfHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

