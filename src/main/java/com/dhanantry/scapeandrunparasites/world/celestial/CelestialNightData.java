package com.dhanantry.scapeandrunparasites.world.celestial;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

/** Per dimension state of the celestial events (CelestialNightData of 1.10.9, stored with the overworld data, dimensions keyed by resource location). */
public class CelestialNightData extends SavedData {
    public static final String DATA_NAME = "srp_celestial_night_v2";
    private final Map<String, DimState> states = new HashMap<String, DimState>();

    public static CelestialNightData get(Level world) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            return new CelestialNightData();
        }
        return CelestialNightData.get(server);
    }

    public static CelestialNightData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(new SavedData.Factory<>(CelestialNightData::new, CelestialNightData::load, null), DATA_NAME);
    }

    public DimState getOrCreate(String dim) {
        return this.states.computeIfAbsent(dim, k -> new DimState());
    }

    public DimState getState(String dim) {
        return this.states.get(dim);
    }

    public void markDirty() {
        this.setDirty();
    }

    public static CelestialNightData load(CompoundTag nbt, HolderLookup.Provider registries) {
        CelestialNightData data = new CelestialNightData();
        ListTag list = nbt.getList("dims", 10);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag d = list.getCompound(i);
            DimState s = new DimState();
            s.nightIndex = d.getLong("night");
            s.phase = d.getInt("phase");
            s.darkDaysLastRollDay = d.contains("darkDaysLastRollDay") ? d.getLong("darkDaysLastRollDay") : -1L;
            s.darkDaysEndTime = d.contains("darkDaysEndTime") ? d.getLong("darkDaysEndTime") : -1L;
            s.darkDaysStartTime = d.contains("darkDaysStartTime") ? d.getLong("darkDaysStartTime") : -1L;
            s.darkDaysEndingSoundPlayed = d.getBoolean("darkDaysEndingSoundPlayed");
            ListTag active = d.getList("active", 8);
            for (int j = 0; j < active.size(); ++j) {
                s.active.add(active.getString(j));
            }
            ListTag forced = d.getList("forced", 8);
            for (int j = 0; j < forced.size(); ++j) {
                s.forced.add(forced.getString(j));
            }
            data.states.put(d.getString("dim"), s);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag list = new ListTag();
        for (Map.Entry<String, DimState> e : this.states.entrySet()) {
            CompoundTag d = new CompoundTag();
            d.putString("dim", e.getKey());
            DimState s = e.getValue();
            d.putLong("night", s.nightIndex);
            d.putInt("phase", s.phase);
            d.putLong("darkDaysLastRollDay", s.darkDaysLastRollDay);
            d.putLong("darkDaysStartTime", s.darkDaysStartTime);
            d.putLong("darkDaysEndTime", s.darkDaysEndTime);
            d.putBoolean("darkDaysEndingSoundPlayed", s.darkDaysEndingSoundPlayed);
            ListTag active = new ListTag();
            for (String id : s.active) {
                active.add(StringTag.valueOf(id));
            }
            d.put("active", (Tag)active);
            ListTag forced = new ListTag();
            for (String id : s.forced) {
                forced.add(StringTag.valueOf(id));
            }
            d.put("forced", (Tag)forced);
            list.add((Tag)d);
        }
        nbt.put("dims", (Tag)list);
        return nbt;
    }

    public static class DimState {
        public long nightIndex = -1L;
        public int phase = 0;
        public final Set<String> active = new HashSet<String>();
        public final Set<String> forced = new HashSet<String>();
        public long darkDaysStartTime = -1L;
        public long darkDaysEndTime = -1L;
        public boolean darkDaysEndingSoundPlayed = false;
        public long darkDaysLastRollDay = -1L;
    }
}
