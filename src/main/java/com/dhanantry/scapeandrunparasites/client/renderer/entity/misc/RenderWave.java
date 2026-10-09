package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelNULL;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import net.minecraft.resources.ResourceLocation;

public class RenderWave
extends RenderLiving<EntityWave> {
    public static final ResourceLocation TEXTURESS = ResourceLocation.parse("srparasites:textures/entity/monster/tendrilshyco.png");

    public RenderWave(RenderManager manager) {
        super(manager, (ModelBase)new ModelNULL(), 0.0f);
    }

    protected ResourceLocation getEntityTexture(EntityWave entity) {
        return TEXTURESS;
    }
}

