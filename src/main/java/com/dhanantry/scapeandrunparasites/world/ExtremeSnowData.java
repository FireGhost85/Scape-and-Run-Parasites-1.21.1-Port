package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class ExtremeSnowData
extends WorldSavedData {
    private static final String KEY = "srp_extreme_snow";
    private boolean enabled = false;
    private float intensity = 1.0f;
    private boolean forceAnywhere = true;
    private float windDeg = 30.0f;
    private float windSpeed = 0.5f;

    public ExtremeSnowData() {
        super(KEY);
    }

    public ExtremeSnowData(String name) {
        super(name);
    }

    public static ExtremeSnowData get(Level world) {
        String dimKey;
        MapStorage storage = world.getPerWorldStorage();
        ExtremeSnowData data = (ExtremeSnowData)storage.loadData(ExtremeSnowData.class, dimKey = "srp_extreme_snow_" + DimKeys.of(world));
        if (data == null) {
            data = new ExtremeSnowData(dimKey);
            storage.setData(dimKey, (WorldSavedData)data);
        }
        return data;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
    }

    public void setIntensity(float i) {
        this.intensity = i;
    }

    public boolean isForceAnywhere() {
        return this.forceAnywhere;
    }

    public void setForceAnywhere(boolean b) {
        this.forceAnywhere = b;
    }

    public float getWindDeg() {
        return this.windDeg;
    }

    public void setWindDeg(float d) {
        this.windDeg = d;
    }

    public float getWindSpeed() {
        return this.windSpeed;
    }

    public void setWindSpeed(float s) {
        this.windSpeed = s;
    }

    public void readFromNBT(CompoundTag nbt) {
        this.enabled = nbt.getBoolean("enabled");
        this.intensity = nbt.getFloat("intensity");
        this.forceAnywhere = nbt.getBoolean("forceAnywhere");
        this.windDeg = nbt.getFloat("windDeg");
        this.windSpeed = nbt.getFloat("windSpeed");
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.putBoolean("enabled", this.enabled);
        nbt.putFloat("intensity", this.intensity);
        nbt.putBoolean("forceAnywhere", this.forceAnywhere);
        nbt.putFloat("windDeg", this.windDeg);
        nbt.putFloat("windSpeed", this.windSpeed);
        return nbt;
    }
}

