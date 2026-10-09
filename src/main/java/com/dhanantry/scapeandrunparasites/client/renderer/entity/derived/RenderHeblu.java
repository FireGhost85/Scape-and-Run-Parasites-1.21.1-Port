package com.dhanantry.scapeandrunparasites.client.renderer.entity.derived;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.derived.ModelHeblu;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderCosmical;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import net.minecraft.resources.ResourceLocation;

public class RenderHeblu
extends RenderCosmical<EntityHeblu> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/heblu.png");
    public static final ResourceLocation TEXTURESEC = ResourceLocation.parse("srparasites:textures/entity/monster/heblumc.png");

    public RenderHeblu(RenderManager manager) {
        super(manager, new ModelHeblu(), 1.3f);
    }

    protected ResourceLocation getEntityTexture(EntityHeblu entity) {
        return TEXTURES;
    }

    @Override
    protected ResourceLocation getEntityTextureCosmical(EntityHeblu entity) {
        return TEXTURESEC;
    }
}

