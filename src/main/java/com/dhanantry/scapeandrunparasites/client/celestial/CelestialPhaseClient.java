package com.dhanantry.scapeandrunparasites.client.celestial;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Client copy of the celestial state of each dimension (phase, night index, active and forced events). */
public final class CelestialPhaseClient {
    private static final Map<String, Integer> PHASES = new HashMap<String, Integer>();
    private static final Map<String, Long> NIGHT_INDEX = new HashMap<String, Long>();
    private static final Map<String, Set<String>> ACTIVE_IDS = new HashMap<String, Set<String>>();
    private static final Map<String, Set<String>> FORCED_IDS = new HashMap<String, Set<String>>();

    private CelestialPhaseClient() {
    }

    public static void setPhase(String dim, int phase) {
        PHASES.put(dim, phase);
    }

    public static int getPhase(String dim) {
        Integer p = PHASES.get(dim);
        return p != null ? p : 0;
    }

    public static void setServerNightState(String dim, int phase, long nightIndex, Set<String> active, Set<String> forced) {
        PHASES.put(dim, phase);
        NIGHT_INDEX.put(dim, nightIndex);
        if (active == null) {
            active = Collections.emptySet();
        }
        if (forced == null) {
            forced = Collections.emptySet();
        }
        ACTIVE_IDS.put(dim, new HashSet<String>(active));
        FORCED_IDS.put(dim, new HashSet<String>(forced));
    }

    public static boolean isActiveTonight(String dim, String id) {
        Set<String> s = ACTIVE_IDS.get(dim);
        return s != null && s.contains(id);
    }

    public static boolean isForcedTonight(String dim, String id) {
        Set<String> s = FORCED_IDS.get(dim);
        return s != null && s.contains(id);
    }

    public static long getNightIndex(String dim) {
        Long n = NIGHT_INDEX.get(dim);
        return n != null ? n : -1L;
    }

    public static Set<String> getActiveIds(String dim) {
        Set<String> s = ACTIVE_IDS.get(dim);
        return s == null ? Collections.emptySet() : new HashSet<String>(s);
    }

    public static Set<String> getForcedIds(String dim) {
        Set<String> s = FORCED_IDS.get(dim);
        return s == null ? Collections.emptySet() : new HashSet<String>(s);
    }

    public static void clearDim(String dim) {
        PHASES.remove(dim);
        NIGHT_INDEX.remove(dim);
        ACTIVE_IDS.remove(dim);
        FORCED_IDS.remove(dim);
    }

    public static void clearAll() {
        PHASES.clear();
        NIGHT_INDEX.clear();
        ACTIVE_IDS.clear();
        FORCED_IDS.clear();
    }
}
