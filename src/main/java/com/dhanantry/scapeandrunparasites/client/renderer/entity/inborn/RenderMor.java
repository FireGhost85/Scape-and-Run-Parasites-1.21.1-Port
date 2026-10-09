package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelMor;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMor;
import net.minecraft.resources.ResourceLocation;

public class RenderMor
extends RenderLiving<EntityMor> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/test.png");

    public RenderMor(RenderManager manager) {
        super(manager, (ModelBase)new ModelMor(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityMor entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityMor entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

