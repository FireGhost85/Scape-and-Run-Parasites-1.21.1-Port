package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfHorseHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHorseHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfHorseHead
extends RenderSRP<EntityInfHorseHead> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/horseh.png");

    public RenderInfHorseHead(RenderManager manager) {
        super(manager, new ModelInfHorseHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfHorseHead entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityInfHorseHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

