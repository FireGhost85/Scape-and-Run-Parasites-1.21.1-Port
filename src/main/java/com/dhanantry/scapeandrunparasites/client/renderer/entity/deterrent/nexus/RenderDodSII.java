package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.nexus.ModelDodSII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import net.minecraft.resources.ResourceLocation;

public class RenderDodSII
extends RenderMalleable<EntityDodSII> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/dodsii.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/dispatcher2snowy.png");

    public RenderDodSII(RenderManager manager) {
        super(manager, new ModelDodSII(), 0.4f);
    }

    protected ResourceLocation getEntityTexture(EntityDodSII entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }
}

