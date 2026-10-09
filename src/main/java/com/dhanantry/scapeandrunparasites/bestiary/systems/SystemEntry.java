package com.dhanantry.scapeandrunparasites.bestiary.systems;

import net.minecraft.resources.ResourceLocation;

public final class SystemEntry {
    public final String id;
    public final ResourceLocation icon;

    public SystemEntry(String id, ResourceLocation icon) {
        this.id = id;
        this.icon = icon;
    }

    public String nameKey() {
        return "bestiary.system." + this.id + ".name";
    }

    public String descKey() {
        return "bestiary.system." + this.id + ".desc";
    }
}

