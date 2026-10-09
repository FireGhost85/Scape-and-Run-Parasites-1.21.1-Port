package com.dhanantry.scapeandrunparasites.bestiary.cap;

import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class BestiaryProgress
implements IBestiaryProgress {
    private final Map<String, Integer> kills = new HashMap<String, Integer>();
    private final Set<String> seenMobs = new HashSet<String>();
    private final Set<String> seenTiers = new HashSet<String>();
    private float damageToParasites = 0.0f;
    private float damageFromParasites = 0.0f;
    private int deathsByParasites = 0;
    private final Set<String> seenBlocks = new HashSet<String>();
    private final Set<String> seenCelestials = new HashSet<String>();
    private final Set<String> seenEffects = new HashSet<String>();
    private boolean unlockedProgressPhase = false;
    private boolean unlockedProgressUD = false;

    @Override
    public void clearStatsPageData() {
        this.kills.clear();
        this.damageToParasites = 0.0f;
        this.damageFromParasites = 0.0f;
        this.deathsByParasites = 0;
    }

    @Override
    public int getKills(String mobId) {
        return this.kills.getOrDefault(mobId, 0);
    }

    @Override
    public void addKill(String mobId, int amount) {
        if (amount <= 0) {
            return;
        }
        this.kills.merge(mobId, amount, Integer::sum);
        this.seenMobs.add(mobId);
    }

    @Override
    public void copyCombatStatsFrom(IBestiaryProgress other) {
        if (other == null) {
            return;
        }
        this.damageToParasites = other.getDamageToParasites();
        this.damageFromParasites = other.getDamageFromParasites();
        this.deathsByParasites = other.getDeathsByParasites();
    }

    @Override
    public boolean isMobSeen(String mobId) {
        return this.seenMobs.contains(mobId);
    }

    @Override
    public void markMobSeen(String mobId) {
        if (mobId != null) {
            this.seenMobs.add(mobId);
        }
    }

    @Override
    public boolean isTierSeen(ParasiteTier tier) {
        return this.seenTiers.contains(tier.name());
    }

    @Override
    public void markTierSeen(ParasiteTier tier) {
        if (tier != null) {
            this.seenTiers.add(tier.name());
        }
    }

    @Override
    public boolean hasSeenCelestial(String id) {
        return id != null && this.seenCelestials.contains(id);
    }

    @Override
    public void markCelestialSeen(String id) {
        if (id != null) {
            this.seenCelestials.add(id);
        }
    }

    @Override
    public void unlockAll() {
        for (ParasiteTier t : ParasiteTier.values()) {
            this.seenTiers.add(t.name());
        }
    }

    @Override
    public boolean hasSeenBlock(ResourceLocation id) {
        return id != null && this.seenBlocks.contains(id.toString());
    }

    @Override
    public void markBlockSeen(ResourceLocation id) {
        if (id != null) {
            this.seenBlocks.add(id.toString());
        }
    }

    @Override
    public Set<String> getSeenBlocks() {
        return this.seenBlocks;
    }

    @Override
    public void setSeenBlocks(Set<String> ids) {
        this.seenBlocks.clear();
        if (ids != null) {
            this.seenBlocks.addAll(ids);
        }
    }

    @Override
    public Set<String> getSeenCelestials() {
        return this.seenCelestials;
    }

    @Override
    public boolean hasSeenEffect(String id) {
        return id != null && this.seenEffects.contains(id);
    }

    @Override
    public void markEffectSeen(String id) {
        if (id != null) {
            this.seenEffects.add(id);
        }
    }

    @Override
    public Set<String> getSeenEffects() {
        return this.seenEffects;
    }

    @Override
    public float getDamageToParasites() {
        return this.damageToParasites;
    }

    @Override
    public float getDamageFromParasites() {
        return this.damageFromParasites;
    }

    @Override
    public int getDeathsByParasites() {
        return this.deathsByParasites;
    }

    @Override
    public boolean hasUnlockedProgressPhase() {
        return this.unlockedProgressPhase;
    }

    @Override
    public boolean hasUnlockedProgressUD() {
        return this.unlockedProgressUD;
    }

    @Override
    public void setUnlockedProgressPhase(boolean value) {
        this.unlockedProgressPhase = value;
    }

    @Override
    public void setUnlockedProgressUD(boolean value) {
        this.unlockedProgressUD = value;
    }

    @Override
    public void addDamageToParasites(float amount) {
        if (amount > 0.0f) {
            this.damageToParasites += amount;
        }
    }

    @Override
    public void addDamageFromParasites(float amount) {
        if (amount > 0.0f) {
            this.damageFromParasites += amount;
        }
    }

    @Override
    public void addDeathsByParasites(int amount) {
        if (amount > 0) {
            this.deathsByParasites += amount;
        }
    }

    @Override
    public void setDamageToParasites(float amount) {
        this.damageToParasites = Math.max(0.0f, amount);
    }

    @Override
    public void setDamageFromParasites(float amount) {
        this.damageFromParasites = Math.max(0.0f, amount);
    }

    @Override
    public void setDeathsByParasites(int amount) {
        this.deathsByParasites = Math.max(0, amount);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag k = new CompoundTag();
        for (Map.Entry<String, Integer> entry : this.kills.entrySet()) {
            k.putInt(entry.getKey(), entry.getValue().intValue());
        }
        tag.put("kills", (Tag)k);
        ListTag sm = new ListTag();
        for (String string : this.seenMobs) {
            sm.add((Tag)net.minecraft.nbt.StringTag.valueOf(string));
        }
        tag.put("seenMobs", (Tag)sm);
        ListTag nBTTagList = new ListTag();
        for (String string : this.seenTiers) {
            nBTTagList.add((Tag)net.minecraft.nbt.StringTag.valueOf(string));
        }
        tag.put("seenTiers", (Tag)nBTTagList);
        ListTag nBTTagList2 = new ListTag();
        for (String string : this.seenBlocks) {
            nBTTagList2.add((Tag)net.minecraft.nbt.StringTag.valueOf(string));
        }
        tag.put("seenBlocks", (Tag)nBTTagList2);
        ListTag nBTTagList3 = new ListTag();
        for (String s : this.seenCelestials) {
            nBTTagList3.add((Tag)net.minecraft.nbt.StringTag.valueOf(s));
        }
        tag.put("seenCelestials", (Tag)nBTTagList3);
        ListTag nBTTagList4 = new ListTag();
        for (String s : this.seenEffects) {
            nBTTagList4.add((Tag)net.minecraft.nbt.StringTag.valueOf(s));
        }
        tag.put("seenEffects", (Tag)nBTTagList4);
        tag.putFloat("damageToParasites", this.damageToParasites);
        tag.putFloat("damageFromParasites", this.damageFromParasites);
        tag.putInt("deathsByParasites", this.deathsByParasites);
        tag.putBoolean("unlockedProgressPhase", this.unlockedProgressPhase);
        tag.putBoolean("unlockedProgressUD", this.unlockedProgressUD);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.kills.clear();
        this.seenMobs.clear();
        this.seenTiers.clear();
        this.seenBlocks.clear();
        this.seenCelestials.clear();
        this.seenEffects.clear();
        this.damageToParasites = 0.0f;
        this.damageFromParasites = 0.0f;
        this.deathsByParasites = 0;
        this.unlockedProgressPhase = false;
        this.unlockedProgressUD = false;
        CompoundTag k = tag.getCompound("kills");
        for (String key : k.getAllKeys()) {
            this.kills.put(key, k.getInt(key));
        }
        ListTag sm = tag.getList("seenMobs", 8);
        for (int i = 0; i < sm.size(); ++i) {
            this.seenMobs.add(sm.getString(i));
        }
        ListTag st = tag.getList("seenTiers", 8);
        for (int i = 0; i < st.size(); ++i) {
            this.seenTiers.add(st.getString(i));
        }
        ListTag sb = tag.getList("seenBlocks", 8);
        for (int i = 0; i < sb.size(); ++i) {
            this.seenBlocks.add(sb.getString(i));
        }
        ListTag sc = tag.getList("seenCelestials", 8);
        for (int i = 0; i < sc.size(); ++i) {
            this.seenCelestials.add(sc.getString(i));
        }
        ListTag se = tag.getList("seenEffects", 8);
        for (int i = 0; i < se.size(); ++i) {
            this.seenEffects.add(se.getString(i));
        }
        this.damageToParasites = tag.getFloat("damageToParasites");
        this.damageFromParasites = tag.getFloat("damageFromParasites");
        this.deathsByParasites = tag.getInt("deathsByParasites");
        this.unlockedProgressPhase = tag.getBoolean("unlockedProgressPhase");
        this.unlockedProgressUD = tag.getBoolean("unlockedProgressUD");
    }
}

