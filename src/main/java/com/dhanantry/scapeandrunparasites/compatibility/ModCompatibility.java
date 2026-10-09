package com.dhanantry.scapeandrunparasites.compatibility;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import java.util.HashMap;

public class ModCompatibility {
    public static HashMap<String, Boolean> modCompatModules = new HashMap();
    public static boolean FLUIDLOGGED_API;

    public static void preInit() {
        modCompatModules.put("ReachFix", Loader.isModLoaded((String)"reachfix"));
        modCompatModules.put("REID", Loader.isModLoaded((String)"JustEnoughIDs"));
        SRPMain.logger.info(String.format("[MOD COMPATIBILITY] Checked for %d mods that may be in the list that need patches or alternative code", modCompatModules.size()));
    }

    public static void postInit() {
        if (modCompatModules.get("REID").booleanValue() && SRPConfigWorld.biomeRegster && SRPConfigWorld.nodesActivated) {
            SRPMain.logger.error("[MOD COMPATIBILITY] Scape and Run: Parasites is NOT compatible with Roughly Enough IDs or Just Enough IDs. Nodes and biome spread will not function properly as a consequence.");
        }
        SRPMain.logger.info(String.format("[MOD COMPATIBILITY] %d of %d modules activated", ModCompatibility.modulesActive(), modCompatModules.size()));
    }

    public static int modulesActive() {
        int i = 0;
        for (boolean b : modCompatModules.values()) {
            if (!b) continue;
            ++i;
        }
        return i;
    }

    public static void warnAddonDeprecatedFunction(String oldFunction, String newFunction) {
        SRPMain.logger.warn(String.format("[MOD COMPATIBILITY] An addon is currently using a function that is now deprecated! The old function is %s, and replaced with %s. Failure to update will cause crashes once backwards compatibility is removed. For now, the old function is similar in function to the older version as a failsafe. Please report this to the addon author ASAP.", oldFunction, newFunction));
    }
}

