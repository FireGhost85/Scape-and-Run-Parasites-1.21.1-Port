package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelVenkrolSVMid;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSV;
import net.minecraft.resources.ResourceLocation;

public class RenderVenkrolSV
extends RenderMalleable<EntityVenkrolSV> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/venkrolsvmid.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/svenkrolsvmid.png");

    public RenderVenkrolSV(RenderManager manager) {
        super(manager, new ModelVenkrolSVMid(), 0.4f);
    }

    protected ResourceLocation getEntityTexture(EntityVenkrolSV entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityVenkrolSV entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

