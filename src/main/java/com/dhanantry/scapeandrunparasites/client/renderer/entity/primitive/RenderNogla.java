package com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.primitive.ModelNogla;
import com.dhanantry.scapeandrunparasites.client.renderer.RenderMalleable;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.text.Normalizer;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class RenderNogla
extends RenderMalleable<EntityNogla> {
    public static final ResourceLocation TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/nogla.png");
    public static final ResourceLocation TEXTURE2 = ResourceLocation.parse("srparasites:textures/entity/monster/noglasp1.png");
    public static final ResourceLocation TEXTUREV = ResourceLocation.parse("srparasites:textures/entity/monster/noglav.png");
    public static final ResourceLocation TEXTUREB = ResourceLocation.parse("srparasites:textures/entity/monster/noglab.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/noglah.png");
    public static final ResourceLocation STEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/snogla.png");
    public static final ResourceLocation RICARDO_TEX = ResourceLocation.parse("srparasites:textures/entity/monster/ricardo.png");
    public static final ResourceLocation RICARDO_BALD_TEX = ResourceLocation.parse("srparasites:textures/entity/monster/ricardo_bald.png");
    public static final ResourceLocation FROZEN_TEXTURE = ResourceLocation.parse("srparasites:textures/entity/monster/snowvariants/primitivereekerfrozen.png");

    public RenderNogla(RenderManager manager) {
        super(manager, new ModelNogla(), 1.3f);
    }

    protected void preRenderCallback(EntityNogla entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getSelfeFlashIntensity(partialTickTime);
        float f1 = 1.0f + Mth.sin((float)(f * 100.0f)) * f * 0.01f;
        f = Mth.clamp((float)f, (float)0.0f, (float)1.0f);
        f *= f;
        f *= f;
        float f2 = (1.0f + f * 0.4f) * f1;
        float f3 = (1.0f + f * 0.1f) / f1;
        GlStateManager.scale((float)f2, (float)f3, (float)f2);
    }

    protected ResourceLocation getEntityTexture(EntityNogla entity) {
        if (entity.isRicardoBald()) {
            return RICARDO_BALD_TEX;
        }
        if (this.isRicardoName(entity)) {
            return RICARDO_TEX;
        }
        switch (entity.getSkin()) {
            case 1: {
                return TEXTURE2;
            }
            case 5: {
                return TEXTUREV;
            }
            case 6: {
                return TEXTUREB;
            }
            case 7: {
                return TEXTUREH;
            }
            case 120: {
                return STEXTURE;
            }
        }
        return TEXTURE;
    }

    private boolean isRicardoName(EntityNogla entity) {
        if (!SRPConfigMobs.noglaRicardoVariantEnabled) {
            return false;
        }
        if (!entity.hasCustomName()) {
            return false;
        }
        String raw = ChatFormatting.stripFormatting(SRPEntityUtil.getCustomNameTag(entity));
        if (raw == null) {
            return false;
        }
        String name = RenderNogla.normalizeLower(raw);
        if ("ricardo".equals(name)) {
            return true;
        }
        String localized = net.minecraft.network.chat.Component.translatable("entity.srparasites.nametag.ricardo").getString();
        return localized != null && !localized.isEmpty() && name.equals(RenderNogla.normalizeLower(localized));
    }

    private static String normalizeLower(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.trim().toLowerCase(Locale.ROOT);
    }
}

