package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelNULL;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWaveShock;
import net.minecraft.resources.ResourceLocation;

public class RenderWaveShock
extends RenderLiving<EntityWaveShock> {
    public static final ResourceLocation TEXTURESS = ResourceLocation.parse("srparasites:textures/entity/monster/tendrilshyco.png");

    public RenderWaveShock(RenderManager manager) {
        super(manager, (ModelBase)new ModelNULL(), 0.0f);
    }

    protected ResourceLocation getEntityTexture(EntityWaveShock entity) {
        return TEXTURESS;
    }
}

