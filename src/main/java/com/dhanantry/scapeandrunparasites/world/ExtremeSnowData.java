package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** State of the extreme snow storm of a dimension (/srp_extremesnow). */
public class ExtremeSnowData extends SavedData {
    private boolean enabled = false;
    private float intensity = 1.0f;
    private boolean forceAnywhere = true;
    private float windDeg = 30.0f;
    private float windSpeed = 0.5f;

    public static ExtremeSnowData get(ServerLevel world) {
        String key = "srp_extreme_snow_" + DimKeys.of(world).replace(':', '_');
        return world.getDataStorage().computeIfAbsent(new SavedData.Factory<>(ExtremeSnowData::new, ExtremeSnowData::load, null), key);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
        this.setDirty();
    }

    public void setIntensity(float i) {
        this.intensity = i;
        this.setDirty();
    }

    public boolean isForceAnywhere() {
        return this.forceAnywhere;
    }

    public void setForceAnywhere(boolean b) {
        this.forceAnywhere = b;
        this.setDirty();
    }

    public float getWindDeg() {
        return this.windDeg;
    }

    public void setWindDeg(float d) {
        this.windDeg = d;
        this.setDirty();
    }

    public float getWindSpeed() {
        return this.windSpeed;
    }

    public void setWindSpeed(float s) {
        this.windSpeed = s;
        this.setDirty();
    }

    public static ExtremeSnowData load(CompoundTag nbt, HolderLookup.Provider registries) {
        ExtremeSnowData d = new ExtremeSnowData();
        d.enabled = nbt.getBoolean("enabled");
        d.intensity = nbt.contains("intensity") ? nbt.getFloat("intensity") : 1.0f;
        d.forceAnywhere = !nbt.contains("forceAnywhere") || nbt.getBoolean("forceAnywhere");
        d.windDeg = nbt.contains("windDeg") ? nbt.getFloat("windDeg") : 30.0f;
        d.windSpeed = nbt.contains("windSpeed") ? nbt.getFloat("windSpeed") : 0.5f;
        return d;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        nbt.putBoolean("enabled", this.enabled);
        nbt.putFloat("intensity", this.intensity);
        nbt.putBoolean("forceAnywhere", this.forceAnywhere);
        nbt.putFloat("windDeg", this.windDeg);
        nbt.putFloat("windSpeed", this.windSpeed);
        return nbt;
    }
}
