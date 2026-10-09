package com.dhanantry.scapeandrunparasites.client.renderer.entity.focused;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.focused.ModelBanoFocused;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderCosmical;
import com.dhanantry.scapeandrunparasites.entity.monster.focused.EntityBanoFocused;
import net.minecraft.resources.ResourceLocation;

public class RenderBanoFocused
extends RenderCosmical<EntityBanoFocused> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/banof.png");
    public static final ResourceLocation TEXTURESEC = ResourceLocation.parse("srparasites:textures/entity/monster/testb.png");

    public RenderBanoFocused(RenderManager manager) {
        super(manager, new ModelBanoFocused(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityBanoFocused entity) {
        return TEXTURES;
    }

    @Override
    protected ResourceLocation getEntityTextureCosmical(EntityBanoFocused entity) {
        return TEXTURESEC;
    }
}

