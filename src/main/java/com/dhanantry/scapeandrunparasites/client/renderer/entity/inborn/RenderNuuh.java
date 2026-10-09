package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelNuuh;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import net.minecraft.resources.ResourceLocation;

public class RenderNuuh
extends RenderMalleable<EntityNuuh> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/nuuh.png");
    public static final ResourceLocation TEXTUREV = ResourceLocation.parse("srparasites:textures/entity/monster/nuuhv.png");
    public static final ResourceLocation TEXTUREB = ResourceLocation.parse("srparasites:textures/entity/monster/nuuhb.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/snuuh.png");

    public RenderNuuh(RenderManager manager) {
        super(manager, new ModelNuuh(), 0.7f);
    }

    protected ResourceLocation getEntityTexture(EntityNuuh entity) {
        switch (entity.getSkin()) {
            case 5: {
                return TEXTUREV;
            }
            case 6: {
                return TEXTUREB;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityNuuh entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

