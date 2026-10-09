package com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.head;

import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.infected.head.ModelInfVillagerHead;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderSRP;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfVillagerHead;
import net.minecraft.resources.ResourceLocation;

public class RenderInfVillagerHead
extends RenderSRP<EntityInfVillagerHead> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/villagerh.png");
    public static final ResourceLocation TEXTURE1 = ResourceLocation.parse("srparasites:textures/entity/monster/villagerh1.png");

    public RenderInfVillagerHead(RenderManager manager) {
        super(manager, new ModelInfVillagerHead(), 0.6f);
    }

    protected ResourceLocation getEntityTexture(EntityInfVillagerHead entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE1;
            }
        }
        return TEXTURE;
    }

    protected void applyRotations(EntityInfVillagerHead entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

