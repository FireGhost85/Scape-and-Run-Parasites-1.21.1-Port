package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfPigHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfPigHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfPigHead
extends RenderSRP<EntityInfPigHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/pigh.png");

    public RenderInfPigHead(RenderManager manager) {
        super(manager, new ModelInfPigHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfPigHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfPigHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

