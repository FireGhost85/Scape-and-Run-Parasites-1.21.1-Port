package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/** The celestial objects / events of the sky (client.celestial.CelestialObjectRegistry of 1.10.9; also used by the server). Filled on first use, after the config is loaded. */
public class CelestialObjectRegistry {
    private static final Random RAND = new Random();
    private static final List<CelestialObjectDefinition> OBJECTS = new ArrayList<CelestialObjectDefinition>();
    private static final Set<String> FORCED_IDS = new HashSet<String>();
    private static boolean initialised;

    private static ResourceLocation tex(String path) {
        return ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path);
    }

    public static int getObjectCount() {
        return CelestialObjectRegistry.getObjects().size();
    }

    public static boolean isInitialized() {
        return !CelestialObjectRegistry.getObjects().isEmpty();
    }

    public static int getHalfDiscoveryThreshold() {
        int total = CelestialObjectRegistry.getObjectCount();
        return (total + 1) / 2;
    }

    public static List<String> getAllIds() {
        ArrayList<String> ids = new ArrayList<String>();
        for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
            ids.add(def.id);
        }
        return ids;
    }

    public static synchronized void init() {
        initialised = true;
        OBJECTS.clear();
        addIfEnabled(new CelestialObjectDefinition("mercury", tex("textures/celestial/solar/mercury.png"), 0, 10, 0.4f, false, true, 1.85f, 2.0f, 2.0f, false, 1.2f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("mars", tex("textures/celestial/solar/mars.png"), 0, 10, 0.35f, false, true, 1.0f, 2.0f, 2.0f, false, 1.0f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("jupiter", tex("textures/celestial/solar/jupiter.png"), 0, 10, 0.25f, false, true, 2.0f, 2.0f, 2.0f, false, 0.35f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("saturn", tex("textures/celestial/solar/saturn.png"), 0, 10, 0.22f, false, true, 1.55f, 2.0f, 2.0f, false, 0.32f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("uranus", tex("textures/celestial/solar/uranus.png"), 0, 10, 1.0f, false, true, 1.15f, 2.0f, 2.0f, false, 0.15f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("neptune", tex("textures/celestial/solar/neptune.png"), 0, 10, 0.15f, false, true, 1.1f, 2.0f, 2.0f, false, 0.22f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("pluto", tex("textures/celestial/solar/pluto.png"), 0, 10, 0.08f, false, true, 1.0f, 1.0f, 0.1f, false, 0.0f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("venus", tex("textures/celestial/solar/venus.png"), 0, 10, 0.75f, false, true, 2.1f, 2.0f, 2.0f, false, 0.18f, false, 1, 20, RAND.nextFloat() * 360.0f, 8.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("blip", tex("textures/celestial/bld_planet.png"), 0, 10, 1.0f, true, false, 4.0f, 1.0f, 0.1f, false, 0.0f, false, 1, 20, 0.0f, 60.0f, CelestialObjectDefinition.OrbitPath.RING, 360.0f, 60.0f, 60.0f, 9000.0f, false));
        addIfEnabled(new CelestialObjectDefinition("pulse", tex("textures/celestial/star1.png"), 3, 10, 1.0f, false, true, 10.0f, 1.0f, 0.1f, false, 0.0f, false, 1, 0, 40.0f, 45.0f));
        addIfEnabled(new CelestialObjectDefinition("eight", tex("textures/celestial/eight.png"), 4, 10, 0.005f, false, true, 20.0f, 1.0f, 0.1f, false, 0.0f, false, 1, 0, 180.0f, 30.0f));
        addIfEnabled(new CelestialObjectDefinition("twenty_seven", tex("textures/celestial/twenty_seven.png"), 2, 10, 0.01f, false, true, 90.0f, 1.0f, 1.0f, false, 0.0f, false, 1, 20, 250.0f, 50.0f));
        addIfEnabled(new CelestialObjectDefinition("three", tex("textures/celestial/three.png"), 5, 10, 0.15f, false, false, 6.0f, 1.0f, 0.1f, false, 4.0f, false, 1, 15, 60.0f, 40.0f, CelestialObjectDefinition.OrbitPath.RING, 140.0f, 30.0f, 45.0f, 12000.0f, false));
        addIfEnabled(new CelestialObjectDefinition("eighty_three", tex("textures/celestial/eighty_three.png"), 7, 10, 0.05f, false, true, 5.0f, 1.0f, 0.1f, false, 0.0f, true, 4, 20, 310.0f, 70.0f));
        addIfEnabled(new CelestialObjectDefinition("four_comet", tex("textures/celestial/four_comet.png"), 3, 10, 0.35f, false, false, 6.0f, 1.0f, 0.1f, true, 0.0f, false, 1, 3, 0.0f, 15.0f, CelestialObjectDefinition.OrbitPath.ARC, 180.0f, 5.0f, 25.0f, 900.0f, true));
        addIfEnabled(new CelestialObjectDefinition("arrow", tex("textures/celestial/tetrahedron.png"), 0, 10, 0.005f, false, true, 4.0f, 2.0f, 2.0f, false, 0.35f, false, 1, 20, 90.0f, 22.0f + RAND.nextFloat() * 50.0f));
        addIfEnabled(new CelestialObjectDefinition("dark_days", tex("textures/celestial/black_sky.png"), 0, 10, 5.0E-4f, false, true, 1.0f, 1.0f, 0.0f, false, 0.0f, false, 1, 20, 0.0f, 90.0f));
    }

    public static synchronized List<CelestialObjectDefinition> getObjects() {
        if (!initialised) {
            CelestialObjectRegistry.init();
        }
        return OBJECTS;
    }

    public static CelestialObjectDefinition getById(String id) {
        for (CelestialObjectDefinition d : CelestialObjectRegistry.getObjects()) {
            if (!d.id.equals(id)) continue;
            return d;
        }
        return null;
    }

    private static void addIfEnabled(CelestialObjectDefinition def) {
        if (def == null) {
            return;
        }
        if (CelestialObjectRegistry.isDisabledByConfig(def.id)) {
            return;
        }
        OBJECTS.add(def);
    }

    public static boolean isDisabledByConfig(String id) {
        if (id == null) {
            return true;
        }
        if ("dark_days".equals(id) && !SRPConfigWorld.darkDaysEnabled) {
            return true;
        }
        return SRPConfigWorld.isCelestialEventBlacklisted(id);
    }

    public static boolean isForced(String id) {
        return id != null && FORCED_IDS.contains(id);
    }

    public static Set<String> getForcedIds() {
        return Collections.unmodifiableSet(FORCED_IDS);
    }

    public static void clearForced() {
        FORCED_IDS.clear();
    }

    public static boolean rollsThisNight(Level world, CelestialObjectDefinition def) {
        if (world == null || def == null) {
            return false;
        }
        if (CelestialObjectRegistry.isDisabledByConfig(def.id)) {
            return false;
        }
        if ("dark_days".equals(def.id)) {
            return SRPConfigWorld.darkDaysEnabled && FORCED_IDS.contains(def.id);
        }
        if (FORCED_IDS.contains(def.id)) {
            return true;
        }
        long nightIndex = world.getGameTime() / 24000L;
        long seed = nightIndex * 918273L + (long)def.id.hashCode();
        RAND.setSeed(seed);
        return RAND.nextFloat() <= def.chancePerNight;
    }
}
