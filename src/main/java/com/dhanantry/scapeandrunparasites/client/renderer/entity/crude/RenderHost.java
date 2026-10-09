package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelHost;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import net.minecraft.resources.ResourceLocation;

public class RenderHost
extends RenderMalleable<EntityHost> {
    public static final ResourceLocation TEXTUREM = ResourceLocation.parse("srparasites:textures/entity/monster/host.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderHost(RenderManager manager) {
        super(manager, new ModelHost(), 0.0f);
    }

    protected ResourceLocation getEntityTexture(EntityHost entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTUREM;
    }
}

