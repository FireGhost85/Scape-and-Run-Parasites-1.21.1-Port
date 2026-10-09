package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.ModelInfDragonE;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import net.minecraft.resources.ResourceLocation;

public class RenderInfDragonE
extends RenderSRP<EntityInfDragonE> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/infdragone.png");

    public RenderInfDragonE(RenderManager manager) {
        super(manager, new ModelInfDragonE(), 1.2f);
    }

    protected ResourceLocation getEntityTexture(EntityInfDragonE entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityInfDragonE entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

