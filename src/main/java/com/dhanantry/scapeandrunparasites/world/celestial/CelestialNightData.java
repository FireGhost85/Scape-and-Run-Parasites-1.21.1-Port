package com.dhanantry.scapeandrunparasites.world.celestial;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public class CelestialNightData
extends WorldSavedData {
    public static final String DATA_NAME = "srp_celestial_night_v2";
    private final Map<Integer, DimState> states = new HashMap<Integer, DimState>();

    public CelestialNightData() {
        super(DATA_NAME);
    }

    public CelestialNightData(String name) {
        super(name);
    }

    public static CelestialNightData get(Level world) {
        MapStorage storage = world.getMapStorage();
        CelestialNightData data = (CelestialNightData)storage.loadData(CelestialNightData.class, DATA_NAME);
        if (data == null) {
            data = new CelestialNightData(DATA_NAME);
            storage.setData(DATA_NAME, (WorldSavedData)data);
        }
        return data;
    }

    public DimState getOrCreate(int dim) {
        DimState s = this.states.get(dim);
        if (s == null) {
            s = new DimState();
            this.states.put(dim, s);
        }
        return s;
    }

    public DimState getState(int dim) {
        return this.states.get(dim);
    }

    public void readFromNBT(CompoundTag nbt) {
        this.states.clear();
        ListTag list = nbt.getList("dims", 10);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag d = list.getCompound(i);
            int dim = d.getInt("dim");
            DimState s = new DimState();
            s.nightIndex = d.getLong("night");
            s.phase = d.getInt("phase");
            s.darkDaysLastRollDay = d.contains("darkDaysLastRollDay") ? d.getLong("darkDaysLastRollDay") : -1L;
            s.darkDaysEndTime = d.contains("darkDaysEndTime") ? d.getLong("darkDaysEndTime") : -1L;
            s.darkDaysStartTime = d.contains("darkDaysStartTime") ? d.getLong("darkDaysStartTime") : -1L;
            s.darkDaysEndingSoundPlayed = d.getBoolean("darkDaysEndingSoundPlayed");
            ListTag active = d.getList("active", 8);
            for (int j = 0; j < active.size(); ++j) {
                s.active.add(active.getStringTagAt(j));
            }
            ListTag forced = d.getList("forced", 8);
            for (int j = 0; j < forced.size(); ++j) {
                s.forced.add(forced.getStringTagAt(j));
            }
            this.states.put(dim, s);
        }
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (Map.Entry<Integer, DimState> e : this.states.entrySet()) {
            CompoundTag d = new CompoundTag();
            d.putInt("dim", e.getKey().intValue());
            DimState s = e.getValue();
            d.putLong("night", s.nightIndex);
            d.putInt("phase", s.phase);
            d.putLong("darkDaysLastRollDay", s.darkDaysLastRollDay);
            d.putLong("darkDaysStartTime", s.darkDaysStartTime);
            d.putLong("darkDaysEndTime", s.darkDaysEndTime);
            d.putBoolean("darkDaysEndingSoundPlayed", s.darkDaysEndingSoundPlayed);
            ListTag active = new ListTag();
            for (String id : s.active) {
                active.add((Tag)new NBTTagString(id));
            }
            d.put("active", (Tag)active);
            ListTag forced = new ListTag();
            for (String id : s.forced) {
                forced.add((Tag)new NBTTagString(id));
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

