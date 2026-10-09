package com.dhanantry.scapeandrunparasites.client.renderer.entity.awakened;

import com.dhanantry.scapeandrunparasites.client.legacy.LayerRenderer;
import com.dhanantry.scapeandrunparasites.client.model.ModelSRP;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.awakened.RenderOroncoAW;
import com.dhanantry.scapeandrunparasites.entity.monster.awakened.EntityOroncoAW;
import net.minecraft.resources.ResourceLocation;

public class LayerOroncoAW
implements LayerRenderer<EntityOroncoAW> {
    public static final ResourceLocation TEXTUREO = ResourceLocation.parse("srparasites:textures/entity/monster/test.png");
    private int part;
    private final RenderOroncoAW parent;
    private ModelSRP model;

    public LayerOroncoAW(RenderOroncoAW in) {
        this.parent = in;
    }

    public void doRenderLayer(EntityOroncoAW entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    }

    public boolean shouldCombineTextures() {
        return false;
    }
}

