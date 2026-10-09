package com.dhanantry.scapeandrunparasites.client.legacy;

import net.minecraft.world.entity.LivingEntity;

/** 1.12 LayerRenderer. */
public interface LayerRenderer<E extends LivingEntity> {
    void doRenderLayer(E entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale);

    boolean shouldCombineTextures();
}
