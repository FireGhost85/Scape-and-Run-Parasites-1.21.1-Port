package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDod;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import net.minecraft.resources.ResourceLocation;

public class RenderDod
extends RenderMalleable<EntityDod> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/dod.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/dispatcher1snowy.png");

    public RenderDod(RenderManager manager) {
        super(manager, new ModelDod(), 0.4f);
    }

    protected ResourceLocation getEntityTexture(EntityDod entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }
}

