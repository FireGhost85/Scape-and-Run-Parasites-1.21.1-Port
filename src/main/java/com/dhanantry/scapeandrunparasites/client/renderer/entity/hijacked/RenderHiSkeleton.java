package com.dhanantry.scapeandrunparasites.client.renderer.entity.hijacked;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.hijacked.ModelHiSkeleton;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiSkeleton;
import net.minecraft.resources.ResourceLocation;

public class RenderHiSkeleton
extends RenderSRP<EntityHiSkeleton> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/hiskeleton.png");

    public RenderHiSkeleton(RenderManager manager) {
        super(manager, new ModelHiSkeleton(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityHiSkeleton entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityHiSkeleton entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

