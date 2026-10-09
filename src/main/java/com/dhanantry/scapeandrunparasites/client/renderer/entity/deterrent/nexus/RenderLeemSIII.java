package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelLeemSIII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIII;
import net.minecraft.resources.ResourceLocation;

public class RenderLeemSIII
extends RenderMalleable<EntityLeemSIII> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/leemsiii.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderLeemSIII(RenderManager manager) {
        super(manager, new ModelLeemSIII(), 0.4f);
    }

    protected ResourceLocation getEntityTexture(EntityLeemSIII entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityLeemSIII entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

