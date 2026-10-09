package com.dhanantry.scapeandrunparasites.bestiary.systems;

import com.dhanantry.scapeandrunparasites.bestiary.systems.SystemEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public final class SRPSystemsRegistry {
    private static final List<SystemEntry> ENTRIES = new ArrayList<SystemEntry>();
    private static boolean built = false;

    private SRPSystemsRegistry() {
    }

    private static void add(String id, String iconPath) {
        ENTRIES.add(new SystemEntry(id, ResourceLocation.fromNamespaceAndPath("srparasites", iconPath)));
    }

    private static void build() {
        if (built) {
            return;
        }
        built = true;
        SRPSystemsRegistry.add("reinforcement", "textures/gui/systems/reinforcement.png");
        SRPSystemsRegistry.add("merge", "textures/gui/systems/merge.png");
        SRPSystemsRegistry.add("status_effects", "textures/gui/systems/status_effects.png");
        SRPSystemsRegistry.add("eiv", "textures/gui/potion_cold_utr.png");
        SRPSystemsRegistry.add("evolution", "textures/gui/systems/evolution.png");
        SRPSystemsRegistry.add("collective_consciousness", "textures/gui/systems/collective_consciousness.png");
        SRPSystemsRegistry.add("scent", "textures/gui/systems/scent.png");
        SRPSystemsRegistry.add("ubiquitous_development", "textures/gui/systems/ubiquitous_development.png");
        SRPSystemsRegistry.add("dislodgment", "textures/gui/systems/dislodgment.png");
        SRPSystemsRegistry.add("generations", "textures/gui/systems/generations.png");
        SRPSystemsRegistry.add("vectors", "textures/gui/systems/vectors.png");
        SRPSystemsRegistry.add("colonies", "textures/gui/systems/colonies.png");
        SRPSystemsRegistry.add("nodes", "textures/gui/systems/nodes.png");
        SRPSystemsRegistry.add("hives", "textures/gui/systems/hives.png");
        SRPSystemsRegistry.add("nests", "textures/gui/systems/nests.png");
        SRPSystemsRegistry.add("variants", "textures/gui/systems/variants.png");
        SRPSystemsRegistry.add("derived_distortion", "textures/gui/systems/derived_distortion.png");
        ENTRIES.sort(Comparator.comparing(e -> e.id));
    }

    public static List<SystemEntry> all() {
        SRPSystemsRegistry.build();
        return Collections.unmodifiableList(ENTRIES);
    }
}

