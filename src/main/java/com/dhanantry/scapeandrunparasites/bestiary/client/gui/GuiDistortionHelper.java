package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

public final class GuiDistortionHelper {
    public static final String PLAYER_DISABLE_TAG = "SRPGuiDistortionDisabled";
    public static final String PLAYER_CREATIVE_OVERRIDE_TAG = "SRPGuiDistortionCreativeOverride";
    public static boolean clientDistortionDisabled = false;
    public static boolean clientCreativeOverride = false;
    public static boolean itemHighlightHookAvailable = true;
    public static boolean subtitleHookAvailable = true;
    public static boolean chatHookAvailable = true;
    public static boolean signHookAvailable = true;

    private GuiDistortionHelper() {
    }

    private static Set<String> getConfiguredMobIds() {
        HashSet<String> ids = new HashSet<String>();
        if (SRPConfigSystems.guiDistortionMobList == null || SRPConfigSystems.guiDistortionMobList.length == 0) {
            ids.add("srparasites:kirin");
            ids.add("srparasites:draconite");
            return ids;
        }
        for (String s : SRPConfigSystems.guiDistortionMobList) {
            if (s == null || (s = s.trim()).isEmpty()) continue;
            ids.add(s.toLowerCase(Locale.ROOT));
        }
        if (ids.isEmpty()) {
            ids.add("srparasites:kirin");
            ids.add("srparasites:draconite");
        }
        return ids;
    }

    public static boolean isDistortionActive(Minecraft mc) {
        if (!SRPConfigSystems.guiDistortionEnabled) {
            return false;
        }
        if (mc == null || mc.level == null || mc.player == null) {
            return false;
        }
        if (clientDistortionDisabled) {
            return false;
        }
        if (GuiDistortionHelper.isCreativeOrSpectator(mc) && !clientCreativeOverride) {
            return false;
        }
        if (GuiDistortionHelper.isProtectedFromDistortion(mc)) {
            return false;
        }
        int range = SRPConfigSystems.guiDistortionRange > 0 ? SRPConfigSystems.guiDistortionRange : 100;
        Set<String> validIds = GuiDistortionHelper.getConfiguredMobIds();
        if (validIds.isEmpty()) {
            return false;
        }
        double maxDistSq = (double)range * (double)range;
        for (Entity ent : SRPEntityUtil.allEntities(mc.level)) {
            ResourceLocation key;
            if (ent == null || (key = BuiltInRegistries.ENTITY_TYPE.getKey(ent.getType())) == null || !validIds.contains(key.toString().toLowerCase(Locale.ROOT)) || !(mc.player.distanceToSqr(ent) <= maxDistSq)) continue;
            return true;
        }
        return false;
    }

    private static boolean isCreativeOrSpectator(Minecraft mc) {
        if (mc == null || mc.player == null) {
            return false;
        }
        if (mc.player.isSpectator()) {
            return true;
        }
        if (mc.player.getAbilities() != null && mc.player.getAbilities().instabuild) {
            return true;
        }
                return false;
    }

    public static String jamText(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        String clean = ChatFormatting.stripFormatting((String)s);
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < clean.length(); ++i) {
            char c = clean.charAt(i);
            if (Character.isWhitespace(c)) {
                out.append(c);
                continue;
            }
            out.append("\u00a7k").append(c).append("\u00a7r").append("\u00a78");
        }
        return "\u00a78" + out.toString();
    }

    public static String jamTextIfNeeded(String s, boolean jammed) {
        return jammed ? GuiDistortionHelper.jamText(s) : s;
    }

    private static boolean isProtectedFromDistortion(Minecraft mc) {
        return mc != null && mc.player != null && mc.player.hasEffect(SRPPotions.THE_SIGN_E);
    }

    public static int getJammedValue(int seedBase) {
        long t = GuiContext.systemTime() / 120L;
        int a = Math.abs((int)((t * 37L + (long)seedBase * 131L) % 997L));
        int b = Math.abs((int)((t * 19L + (long)seedBase * 73L) % 211L));
        return a + b + 1;
    }

    public static int getDisplayValue(int realValue, boolean jammed, int seedBase) {
        return jammed ? GuiDistortionHelper.getJammedValue(seedBase) : realValue;
    }

    public static float getDisplayRatio(int realValue, int realMax, boolean jammed, int seedBase) {
        if (jammed) {
            int fakeValue = Math.max(1, GuiDistortionHelper.getJammedValue(seedBase));
            int fakeMax = Math.max(fakeValue, GuiDistortionHelper.getJammedValue(seedBase + 999));
            return (float)fakeValue / (float)fakeMax;
        }
        if (realMax <= 0) {
            return 0.0f;
        }
        return (float)realValue / (float)realMax;
    }

    public static boolean isExtendedWorldTextDistortionActive(Minecraft mc) {
        if (!SRPConfigSystems.guiDistortionAffectsWorldText) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static boolean shouldDistortChat(Minecraft mc) {
        if (!chatHookAvailable) {
            return false;
        }
        if (!SRPConfigSystems.guiDistortionAffectsChat) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static boolean shouldDistortItemTooltips(Minecraft mc) {
        if (!SRPConfigSystems.guiDistortionAffectsItemTooltips) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static boolean shouldDistortPotionHud(Minecraft mc) {
        if (!SRPConfigSystems.guiDistortionAffectsPotionHud) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static boolean shouldDistortSigns(Minecraft mc) {
        if (!signHookAvailable) {
            return false;
        }
        if (!SRPConfigSystems.guiDistortionAffectsSigns) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static boolean shouldDistortItemHighlight(Minecraft mc) {
        if (!itemHighlightHookAvailable) {
            return false;
        }
        if (!SRPConfigSystems.guiDistortionAffectsItemHighlight) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static boolean shouldDistortSubtitles(Minecraft mc) {
        if (!subtitleHookAvailable) {
            return false;
        }
        if (!SRPConfigSystems.guiDistortionAffectsSubtitles) {
            return false;
        }
        return GuiDistortionHelper.isDistortionActive(mc);
    }

    public static String toneDownTextColors(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\u00a7e", "\u00a78").replace("\u00a76", "\u00a78").replace("\u00a77", "\u00a78");
    }
}

