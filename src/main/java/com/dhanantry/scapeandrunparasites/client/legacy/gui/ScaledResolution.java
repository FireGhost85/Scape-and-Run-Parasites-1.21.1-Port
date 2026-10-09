package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import net.minecraft.client.Minecraft;

public class ScaledResolution {
    private final Minecraft mc;

    public ScaledResolution(Minecraft mc) {
        this.mc = mc;
    }

    public int getScaleFactor() {
        return (int)this.mc.getWindow().getGuiScale();
    }

    public int getScaledWidth() {
        return this.mc.getWindow().getGuiScaledWidth();
    }

    public int getScaledHeight() {
        return this.mc.getWindow().getGuiScaledHeight();
    }
}
