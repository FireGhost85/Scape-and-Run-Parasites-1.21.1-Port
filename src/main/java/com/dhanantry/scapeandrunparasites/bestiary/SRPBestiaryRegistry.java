package com.dhanantry.scapeandrunparasites.bestiary;

import com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.SRPBlockCompendiumRegistry;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public final class SRPBestiaryRegistry {
    private static final Gson GSON = new Gson();
    private static final Map<String, BestiaryEntry> ENTRIES = new LinkedHashMap<String, BestiaryEntry>();
    private static final Map<String, Float> RENDER_SCALE = new HashMap<String, Float>();

    public static BestiaryEntry get(String id) {
        return ENTRIES.get(id);
    }

    public static Collection<BestiaryEntry> all() {
        return ENTRIES.values();
    }

    public static float getRenderScale(String id) {
        return RENDER_SCALE.getOrDefault(id, Float.valueOf(1.0f)).floatValue();
    }

    public static void registerDefaults() {
        ENTRIES.clear();
        RENDER_SCALE.clear();
        ClassLoader cl = SRPBestiaryRegistry.class.getClassLoader();
        String base = "assets/srparasites/bestiary/";
        IndexJson index = SRPBestiaryRegistry.fromJson(cl, "assets/srparasites/bestiary/index.json", IndexJson.class);
        if (index == null) {
            SRPBestiaryRegistry.log("[Bestiary] index.json not found or failed to parse. No entries loaded.", new Object[0]);
            return;
        }
        SRPBlockCompendiumRegistry.registerDefaults();
        if (index.scales != null) {
            for (Map.Entry<String, Double> e : index.scales.entrySet()) {
                if (e.getKey() == null || e.getValue() == null) continue;
                RENDER_SCALE.put(e.getKey(), Float.valueOf(e.getValue().floatValue()));
            }
        }
        int loaded = 0;
        if (index.mobs != null) {
            for (String name : index.mobs) {
                String id;
                if (name == null || name.isEmpty()) continue;
                MobJson j = SRPBestiaryRegistry.fromJson(cl, "assets/srparasites/bestiary/" + name + ".json", MobJson.class);
                if (j == null) {
                    SRPBestiaryRegistry.log("[Bestiary] Skipping '%s' (missing or invalid JSON).", name);
                    continue;
                }
                String string = id = j.id != null && !j.id.isEmpty() ? j.id : j.nameKey;
                if (id == null || id.isEmpty()) {
                    SRPBestiaryRegistry.log("[Bestiary] Skipping profile '%s' (no id/nameKey).", name);
                    continue;
                }
                ParasiteTier tier = SRPBestiaryRegistry.parseTier(j.tier);
                if (tier == null) {
                    SRPBestiaryRegistry.log("[Bestiary] Skipping '%s' (invalid tier '%s').", id, j.tier);
                    continue;
                }
                int hp = -1;
                float dmg = -1.0f;
                if (j.stats != null && "override".equalsIgnoreCase(j.stats.source)) {
                    if (j.stats.hp != null) {
                        hp = j.stats.hp.intValue();
                    }
                    if (j.stats.damage != null) {
                        dmg = j.stats.damage.floatValue();
                    }
                }
                int loreMin = j.minLoreKill != null ? j.minLoreKill : 10;
                int statMin = j.minStatKill != null ? j.minStatKill : 3;
                BestiaryEntry entry = new BestiaryEntry(id, tier, j.nameKey != null ? j.nameKey : SRPBestiaryRegistry.makeDefaultNameKey(id), hp, dmg, j.loreKey != null ? j.loreKey : SRPBestiaryRegistry.makeDefaultLoreKey(id), loreMin, statMin);
                ENTRIES.put(entry.mobId, entry);
                ++loaded;
            }
        }
        SRPBestiaryRegistry.log("[Bestiary] Loaded %d entries. Scales: %d", loaded, RENDER_SCALE.size());
        if (loaded == 0) {
            SRPBestiaryRegistry.log("[Bestiary] WARNING: 0 entries loaded. Check your index.json, profile filenames, and tier strings.", new Object[0]);
        }
    }

    private static ParasiteTier parseTier(String raw) {
        if (raw == null) {
            return null;
        }
        String s = raw.trim();
        if (s.isEmpty()) {
            return null;
        }
        if ((s = s.replace(' ', '_').toUpperCase(Locale.ROOT)).startsWith("TIER_")) {
            s = s.replace("TIER_", "STAGE_");
        }
        try {
            return ParasiteTier.valueOf(s);
        }
        catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static String makeDefaultNameKey(String id) {
        ResourceLocation rl = ResourceLocation.parse(id);
        return "entity." + rl.getNamespace() + "." + rl.getPath();
    }

    private static String makeDefaultLoreKey(String id) {
        ResourceLocation rl = ResourceLocation.parse(id);
        return "bestiary.lore." + rl.getNamespace() + "." + rl.getPath();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static <T> T fromJson(ClassLoader cl, String path, Class<T> type) {
        try (InputStream in = cl.getResourceAsStream(path);){
            if (in == null) {
                T t = null;
                return t;
            }
            InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
            Object object = GSON.fromJson((Reader)r, type);
            return (T)object;
        }
        catch (Exception e) {
            return null;
        }
    }

    private static void log(String fmt, Object ... args) {
        System.out.println(String.format(Locale.ROOT, fmt, args));
    }

    private static final class MobJson {
        @SerializedName(value="id")
        String id;
        @SerializedName(value="nameKey")
        String nameKey;
        @SerializedName(value="tier")
        String tier;
        @SerializedName(value="loreKey")
        String loreKey;
        @SerializedName(value="stats")
        Stats stats;
        @SerializedName(value="minlorekill")
        Integer minLoreKill;
        @SerializedName(value="minstatkill")
        Integer minStatKill;

        private MobJson() {
        }

        private static final class Stats {
            @SerializedName(value="source")
            String source;
            @SerializedName(value="hp")
            Double hp;
            @SerializedName(value="damage")
            Double damage;

            private Stats() {
            }
        }
    }

    private static final class IndexJson {
        @SerializedName(value="mobs")
        List<String> mobs;
        @SerializedName(value="scales")
        Map<String, Double> scales;

        private IndexJson() {
        }
    }
}

