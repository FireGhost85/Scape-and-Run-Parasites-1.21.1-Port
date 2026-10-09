package com.dhanantry.scapeandrunparasites.client.renderer.entity.hijacked;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.hijacked.ModelHiGolem;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import net.minecraft.resources.ResourceLocation;

public class RenderHiGolem
extends RenderSRP<EntityHiGolem> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/higolem.png");

    public RenderHiGolem(RenderManager manager) {
        super(manager, new ModelHiGolem(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityHiGolem entity) {
        return TEXTURE;
    }

    protected void applyRotations(EntityHiGolem entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

