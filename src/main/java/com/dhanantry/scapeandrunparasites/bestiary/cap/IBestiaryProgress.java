package com.dhanantry.scapeandrunparasites.bestiary.cap;

import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface IBestiaryProgress {
    public int getKills(String var1);

    public void addKill(String var1, int var2);

    public boolean hasUnlockedProgressPhase();

    public boolean hasUnlockedProgressUD();

    public void setUnlockedProgressPhase(boolean var1);

    public void setUnlockedProgressUD(boolean var1);

    public boolean isMobSeen(String var1);

    public void markMobSeen(String var1);

    public boolean isTierSeen(ParasiteTier var1);

    public void markTierSeen(ParasiteTier var1);

    public boolean hasSeenCelestial(String var1);

    public void markCelestialSeen(String var1);

    public void unlockAll();

    public CompoundTag serializeNBT();

    public void deserializeNBT(CompoundTag var1);

    public boolean hasSeenBlock(ResourceLocation var1);

    public void markBlockSeen(ResourceLocation var1);

    public Set<String> getSeenBlocks();

    public Set<String> getSeenCelestials();

    public void setSeenBlocks(Set<String> var1);

    public boolean hasSeenEffect(String var1);

    public void markEffectSeen(String var1);

    public Set<String> getSeenEffects();

    public float getDamageToParasites();

    public float getDamageFromParasites();

    public int getDeathsByParasites();

    public void clearStatsPageData();

    public void addDamageToParasites(float var1);

    public void addDamageFromParasites(float var1);

    public void addDeathsByParasites(int var1);

    public void copyCombatStatsFrom(IBestiaryProgress var1);

    public void setDamageToParasites(float var1);

    public void setDamageFromParasites(float var1);

    public void setDeathsByParasites(int var1);
}

