package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfCowHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfCowHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfCowHead
extends RenderSRP<EntityInfCowHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/cowh.png");

    public RenderInfCowHead(RenderManager manager) {
        super(manager, new ModelInfCowHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfCowHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfCowHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

