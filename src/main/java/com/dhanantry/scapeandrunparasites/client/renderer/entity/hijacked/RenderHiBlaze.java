package com.dhanantry.scapeandrunparasites.client.renderer.entity.hijacked;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.hijacked.ModelHiBlaze;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiBlaze;
import net.minecraft.resources.ResourceLocation;

public class RenderHiBlaze
extends RenderSRP<EntityHiBlaze> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/hiblaze.png");

    public RenderHiBlaze(RenderManager manager) {
        super(manager, new ModelHiBlaze(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityHiBlaze entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityHiBlaze entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

