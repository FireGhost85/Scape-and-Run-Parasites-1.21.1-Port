package com.dhanantry.scapeandrunparasites.bestiary.effects;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

/** Every status effect of the mod, sorted by id (the status effects page of the bestiary). */
public final class SRPStatusEffectRegistry {
    private static final List<Entry> ENTRIES = new ArrayList<>();
    private static boolean built = false;

    private SRPStatusEffectRegistry() {
    }

    public static List<Entry> all() {
        if (!built) {
            build();
        }
        return ENTRIES;
    }

    private static void build() {
        built = true;
        ENTRIES.clear();
        for (MobEffect effect : BuiltInRegistries.MOB_EFFECT) {
            ResourceLocation rl = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (rl != null && ScapeAndRunParasites.MODID.equals(rl.getNamespace())) {
                ENTRIES.add(new Entry(rl.getPath(), effect));
            }
        }
        ENTRIES.sort(Comparator.comparing(a -> a.id));
    }

    public static final class Entry {
        public final String id;
        public final MobEffect potion;

        public Entry(String id, MobEffect potion) {
            this.id = id;
            this.potion = potion;
        }
    }
}
