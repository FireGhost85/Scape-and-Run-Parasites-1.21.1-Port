package com.dhanantry.scapeandrunparasites.client.renderer.entity.focused;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.focused.ModelShycoFocused;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderCosmical;
import com.dhanantry.scapeandrunparasites.entity.monster.focused.EntityShycoFocused;
import net.minecraft.resources.ResourceLocation;

public class RenderShycoFocused
extends RenderCosmical<EntityShycoFocused> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/shycof.png");
    public static final ResourceLocation TEXTURESEC = ResourceLocation.parse("srparasites:textures/entity/monster/testb.png");

    public RenderShycoFocused(RenderManager manager) {
        super(manager, new ModelShycoFocused(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityShycoFocused entity) {
        return TEXTURES;
    }

    @Override
    protected ResourceLocation getEntityTextureCosmical(EntityShycoFocused entity) {
        return TEXTURESEC;
    }
}

