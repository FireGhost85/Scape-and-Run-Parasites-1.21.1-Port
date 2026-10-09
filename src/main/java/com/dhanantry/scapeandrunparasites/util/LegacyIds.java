package com.dhanantry.scapeandrunparasites.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Entity ids of 1.12 that changed in 1.21 ({@code minecraft:villager_golem} is {@code minecraft:iron_golem} now). The defaults of the
 * config files use the new ids; a config file written before (or copied from 1.12) may still hold the old ones, so the baked values
 * are translated after every load.
 */
public final class LegacyIds {
    private static final Map<String, Pattern> ENTITY_RENAMES = new LinkedHashMap<>();
    private static final Map<String, String> ENTITY_NEW = new LinkedHashMap<>();

    static {
        rename("villager_golem", "iron_golem");
        rename("snowman", "snow_golem");
        rename("zombie_pigman", "zombified_piglin");
        rename("evocation_illager", "evoker");
        rename("vindication_illager", "vindicator");
        rename("illusion_illager", "illusioner");
        rename("ozelot", "ocelot");
        rename("xp_orb", "experience_orb");
        rename("xp_bottle", "experience_bottle");
        rename("ender_crystal", "end_crystal");
        rename("fireworks_rocket", "firework_rocket");
        rename("evocation_fangs", "evoker_fangs");
        rename("eye_of_ender_signal", "eye_of_ender");
        rename("commandblock_minecart", "command_block_minecart");
    }

    private LegacyIds() {
    }

    private static void rename(String oldName, String newName) {
        ENTITY_RENAMES.put(oldName, Pattern.compile("minecraft:" + oldName + "(?![a-z0-9_])"));
        ENTITY_NEW.put(oldName, "minecraft:" + newName);
    }

    /** The text with the 1.12 entity ids replaced by the 1.21 ones. */
    public static String fixEntityIds(String s) {
        if (s == null || s.indexOf("minecraft:") < 0) {
            return s;
        }
        for (Map.Entry<String, Pattern> e : ENTITY_RENAMES.entrySet()) {
            s = e.getValue().matcher(s).replaceAll(ENTITY_NEW.get(e.getKey()));
        }
        return s;
    }

    /** Translates the String and String[] fields of a baked config class. */
    public static void fixConfig(Class<?> config) {
        for (Field f : config.getDeclaredFields()) {
            int m = f.getModifiers();
            if (!Modifier.isStatic(m) || Modifier.isFinal(m) || !Modifier.isPublic(m)) {
                continue;
            }
            try {
                if (f.getType() == String[].class) {
                    String[] list = (String[]) f.get(null);
                    if (list != null) {
                        String[] fixed = new String[list.length];
                        for (int i = 0; i < list.length; ++i) {
                            fixed[i] = fixEntityIds(list[i]);
                        }
                        f.set(null, fixed);
                    }
                } else if (f.getType() == String.class) {
                    f.set(null, fixEntityIds((String) f.get(null)));
                }
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}
