package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelKol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import net.minecraft.resources.ResourceLocation;

public class RenderKol
extends RenderLiving<EntityKol> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/kol.png");

    public RenderKol(RenderManager manager) {
        super(manager, (ModelBase)new ModelKol(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityKol entity) {
        return TEXTURES;
    }

    protected void applyRotations(EntityKol entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

