package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfSheepHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfSheepHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfSheepHead
extends RenderSRP<EntityInfSheepHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/sheeph.png");

    public RenderInfSheepHead(RenderManager manager) {
        super(manager, new ModelInfSheepHead(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityInfSheepHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfSheepHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

