package com.dhanantry.scapeandrunparasites.bestiary;

import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;

public final class BestiaryEntry {
    public final String mobId;
    public final ParasiteTier tier;
    public final String nameKey;
    public final int baseHp;
    public final float baseDamage;
    public final String loreKey;
    public final int minLoreKill;
    public final int minStatKill;

    public BestiaryEntry(String mobId, ParasiteTier tier, String nameKey, int baseHp, float baseDamage, String loreKey, int minLoreKill, int minStatKill) {
        this.mobId = mobId;
        this.tier = tier;
        this.nameKey = nameKey;
        this.baseHp = baseHp;
        this.baseDamage = baseDamage;
        this.loreKey = loreKey;
        this.minLoreKill = minLoreKill;
        this.minStatKill = minStatKill;
    }
}

