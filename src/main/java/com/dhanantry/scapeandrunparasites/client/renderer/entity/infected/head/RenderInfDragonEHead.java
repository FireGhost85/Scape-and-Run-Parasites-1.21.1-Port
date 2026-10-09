package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfDragonEHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfDragonEHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfDragonEHead
extends RenderSRP<EntityInfDragonEHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/dragoneh.png");

    public RenderInfDragonEHead(RenderManager manager) {
        super(manager, new ModelInfDragonEHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfDragonEHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfDragonEHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

