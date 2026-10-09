package com.dhanantry.scapeandrunparasites.client.renderer.entity.crude;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.crude.ModelInhooM;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import net.minecraft.resources.ResourceLocation;

public class RenderInhooM
extends RenderSRP<EntityInhooM> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/inhoom.png");
    public static final ResourceLocation TEXTURE_FROZEN = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/test.png");

    public RenderInhooM(RenderManager manager) {
        super(manager, new ModelInhooM(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityInhooM entity) {
        switch (entity.getSkin()) {
            case 120: {
                return TEXTURE_FROZEN;
            }
        }
        return TEXTURES;
    }
}

