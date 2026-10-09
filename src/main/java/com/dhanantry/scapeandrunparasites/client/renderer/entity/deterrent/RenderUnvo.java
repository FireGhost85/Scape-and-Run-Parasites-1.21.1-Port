package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelUnvo;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import net.minecraft.resources.ResourceLocation;

public class RenderUnvo
extends RenderMalleable<EntityUnvo> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/unvo.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/sentryfrozen.png");

    public RenderUnvo(RenderManager manager) {
        super(manager, new ModelUnvo(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityUnvo entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }
}

