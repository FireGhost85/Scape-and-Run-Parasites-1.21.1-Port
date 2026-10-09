package com.dhanantry.scapeandrunparasites.client.celestial;

import net.minecraft.resources.ResourceLocation;



public class CelestialObjectDefinition {
    public final String id;
    public final ResourceLocation texture;
    public final int minPhase;
    public final int maxPhase;
    public final float chancePerNight;
    public final boolean followsStars;
    public final boolean stationary;
    public final float size;
    public final float baseOpacity;
    public final float extraOpacity;
    public final boolean fastStreak;
    public final float rotationSpeedDeg;
    public final boolean animated;
    public final int frameCount;
    public final int frameTimeTicks;
    public final float yawDeg;
    public final float pitchDeg;
    public final boolean oneShotOrbit;
    public final OrbitPath orbitPath;
    public final float orbitYawRangeDeg;
    public final float orbitPitchMinDeg;
    public final float orbitPitchMaxDeg;
    public final float orbitPeriodTicks;

    public CelestialObjectDefinition(String id, ResourceLocation texture, int minPhase, int maxPhase, float chancePerNight, boolean followsStars, boolean stationary, float size, float baseOpacity, float extraOpacity, boolean fastStreak, float rotationSpeedDeg, boolean animated, int frameCount, int frameTimeTicks, float yawDeg, float pitchDeg, OrbitPath orbitPath, float orbitYawRangeDeg, float orbitPitchMinDeg, float orbitPitchMaxDeg, float orbitPeriodTicks, boolean oneShotOrbit) {
        this.id = id;
        this.texture = texture;
        this.minPhase = minPhase;
        this.maxPhase = maxPhase;
        this.chancePerNight = chancePerNight;
        this.followsStars = followsStars;
        this.stationary = stationary;
        this.size = size;
        this.baseOpacity = baseOpacity;
        this.extraOpacity = extraOpacity;
        this.fastStreak = fastStreak;
        this.rotationSpeedDeg = rotationSpeedDeg;
        this.animated = animated;
        this.frameCount = frameCount;
        this.frameTimeTicks = frameTimeTicks;
        this.yawDeg = yawDeg;
        this.pitchDeg = pitchDeg;
        this.orbitPath = orbitPath;
        this.orbitYawRangeDeg = orbitYawRangeDeg;
        this.orbitPitchMinDeg = orbitPitchMinDeg;
        this.orbitPitchMaxDeg = orbitPitchMaxDeg;
        this.orbitPeriodTicks = orbitPeriodTicks;
        this.oneShotOrbit = oneShotOrbit;
    }

    public CelestialObjectDefinition(String id, ResourceLocation texture, int minPhase, int maxPhase, float chancePerNight, boolean followsStars, boolean stationary, float size, float baseOpacity, float extraOpacity, boolean fastStreak, float rotationSpeedDeg, boolean animated, int frameCount, int frameTimeTicks, float yawDeg, float pitchDeg) {
        this(id, texture, minPhase, maxPhase, chancePerNight, followsStars, stationary, size, baseOpacity, extraOpacity, fastStreak, rotationSpeedDeg, animated, frameCount, frameTimeTicks, yawDeg, pitchDeg, OrbitPath.NONE, 0.0f, pitchDeg, pitchDeg, -1.0f, false);
    }

    public boolean isPhaseAllowed(int phase) {
        return phase >= this.minPhase && phase <= this.maxPhase;
    }

    public static enum OrbitPath {
        NONE,
        RING,
        ARC;

    }
}

