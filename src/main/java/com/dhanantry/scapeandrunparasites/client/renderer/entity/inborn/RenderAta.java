package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelAta;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import net.minecraft.resources.ResourceLocation;

public class RenderAta
extends RenderSRP<EntityAta> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/gnat.png");

    public RenderAta(RenderManager manager) {
        super(manager, new ModelAta(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityAta entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityAta entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

