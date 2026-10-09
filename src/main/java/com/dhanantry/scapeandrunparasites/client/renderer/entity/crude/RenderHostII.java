package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelHostII;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import net.minecraft.resources.ResourceLocation;

public class RenderHostII
extends RenderMalleable<EntityHostII> {
    public static final ResourceLocation TEXTUREM = ResourceLocation.parse("srparasites:textures/entity/monster/hostii.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderHostII(RenderManager manager) {
        super(manager, new ModelHostII(), 0.0f);
    }

    protected ResourceLocation getEntityTexture(EntityHostII entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTUREM;
    }
}

