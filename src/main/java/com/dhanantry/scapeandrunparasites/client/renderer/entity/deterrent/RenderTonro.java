package com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.deterrent.ModelTonro;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import net.minecraft.resources.ResourceLocation;

public class RenderTonro
extends RenderMalleable<EntityTonro> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/tonro.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/kyphosisfrozen.png");

    public RenderTonro(RenderManager manager) {
        super(manager, new ModelTonro(), 0.4f);
    }

    protected ResourceLocation getEntityTexture(EntityTonro entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }
}

