package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import net.minecraft.resources.ResourceLocation;

public class RenderLodo
extends RenderLiving<EntityLodo> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/lodo.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/slodo.png");

    public RenderLodo(RenderManager manager) {
        super(manager, (ModelBase)new ModelLodo(), 0.2f);
    }

    protected ResourceLocation getEntityTexture(EntityLodo entity) {
        switch (entity.getSkin()) {
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityLodo entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

