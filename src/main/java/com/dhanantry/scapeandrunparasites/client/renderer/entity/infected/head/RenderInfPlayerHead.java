package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfPlayerHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfPlayerHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfPlayerHead
extends RenderSRP<EntityInfPlayerHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/playerh.png");

    public RenderInfPlayerHead(RenderManager manager) {
        super(manager, new ModelInfPlayerHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfPlayerHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfPlayerHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

