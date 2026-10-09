package com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.inborn.ModelMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import net.minecraft.resources.ResourceLocation;

public class RenderMudo
extends RenderLiving<EntityMudo> {
    public static final ResourceLocation TEXTURES = ResourceLocation.parse("srparasites:textures/entity/monster/mudo.png");
    public static final ResourceLocation TEXTUREV = ResourceLocation.parse("srparasites:textures/entity/monster/mudov.png");
    public static final ResourceLocation TEXTUREB = ResourceLocation.parse("srparasites:textures/entity/monster/mudob.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/mudo_snowy.png");
    public static final ResourceLocation TEXTURE_CLASSIC = ResourceLocation.parse("srparasites:textures/entity/monster/mudo_classic.png");
    public static final ResourceLocation TEXTURE_GOLD = ResourceLocation.parse("srparasites:textures/entity/monster/mudo_golden.png");
    public static final ResourceLocation TEXTURE_STRIPED = ResourceLocation.parse("srparasites:textures/entity/monster/mudo_striped.png");
    public static final ResourceLocation TEXTURE_FLUFFY = ResourceLocation.parse("srparasites:textures/entity/monster/mudo_fluffy.png");
    public static final ResourceLocation TEXTURE_WEIRD = ResourceLocation.parse("srparasites:textures/entity/monster/mudo_weird.png");

    public RenderMudo(RenderManager manager) {
        super(manager, (ModelBase)new ModelMudo(), 0.5f);
    }

    protected ResourceLocation getEntityTexture(EntityMudo entity) {
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE_CLASSIC;
            }
            case 2: {
                return TEXTURE_GOLD;
            }
            case 3: {
                return TEXTURE_STRIPED;
            }
            case 4: {
                return TEXTURE_FLUFFY;
            }
            case 5: {
                return TEXTUREV;
            }
            case 6: {
                return TEXTUREB;
            }
            case 7: {
                return TEXTURE_WEIRD;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURES;
    }

    protected void applyRotations(EntityMudo entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
        super.rotateCorpse(entityLiving, ageInTicks, rotationYaw, partialTicks);
    }
}

