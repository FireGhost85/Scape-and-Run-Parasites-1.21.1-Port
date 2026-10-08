package com.dhanantry.scapeandrunparasites.phase;

import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/**
 * Dimension identity. 1.12.2 used numeric dimension ids; this port keys everything by the dimension's
 * resource location string ("minecraft:overworld"). Config entries may still use the legacy numbers
 * (0 = overworld, -1 = nether, 1 = end); any other legacy number is kept as an unmapped key (e.g. "270").
 */
public final class DimKeys {
    public static final String OVERWORLD = "minecraft:overworld";
    public static final String NETHER = "minecraft:the_nether";
    public static final String END = "minecraft:the_end";

    private DimKeys() {}

    public static String of(Level level) {
        return level.dimension().location().toString();
    }

    /** Converts a config / command token (legacy number or resource location) to a dimension key. */
    public static String normalize(String token) {
        String s = token.trim();
        switch (s) {
            case "0": return OVERWORLD;
            case "-1": return NETHER;
            case "1": return END;
            default: break;
        }
        ResourceLocation rl = s.matches("-?\\d+") ? null : ResourceLocation.tryParse(s);
        return rl != null ? rl.toString() : s;
    }

    public static boolean matches(String[] configList, String key) {
        for (String entry : configList) {
            if (normalize(entry).equals(key)) return true;
        }
        return false;
    }

    /** Legacy id used in debug file names: 0 / -1 / 1 for the vanilla dimensions, otherwise a sanitised key. */
    public static String legacyId(String key) {
        switch (key) {
            case OVERWORLD: return "0";
            case NETHER: return "-1";
            case END: return "1";
            default: return key.replace(':', '_').replace('/', '_');
        }
    }

    @Nullable
    public static ServerLevel level(@Nullable MinecraftServer server, String key) {
        if (server == null) return null;
        ResourceLocation rl = ResourceLocation.tryParse(key);
        if (rl == null) return null;
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, rl));
    }
}
