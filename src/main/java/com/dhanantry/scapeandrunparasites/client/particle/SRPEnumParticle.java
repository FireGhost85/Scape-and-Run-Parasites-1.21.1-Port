package com.dhanantry.scapeandrunparasites.client.particle;

import com.dhanantry.scapeandrunparasites.init.SRPParticles;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public enum SRPEnumParticle {
    FOG("fog", 0, false, () -> SRPParticles.FOG.get()),
    SPORE("spore", 1, false, () -> SRPParticles.SPORE.get()),
    GCLOUD("gcloud", 2, false, () -> SRPParticles.GCLOUD.get()),
    GSPLASH("gsplash", 3, false, () -> SRPParticles.GSPLASH.get()),
    RHAPPY("rhappy", 4, false, () -> SRPParticles.RHAPPY.get()),
    BIOMASS("biomass", 5, false, () -> SRPParticles.BIOMASS.get()),
    EEN("een", 6, false, () -> SRPParticles.EEN.get()),
    FLASH("flash", 7, false, () -> SRPParticles.FLASH.get()),
    DOT("dot", 8, false, () -> SRPParticles.DOT.get()),
    WIND("wind", 9, false, () -> SRPParticles.WIND.get()),
    COOLER_FOG("coolerfog", 10, false, () -> SRPParticles.COOLER_FOG.get()),
    RAGE("rage", 11, false, () -> SRPParticles.RAGE.get()),
    BLOOD("blood", 12, false, () -> SRPParticles.BLOOD.get());

    private final String particleName;
    private final int particleID;
    private final boolean shouldIgnoreRange;
    private final int argumentCount;
    private final Supplier<SRPParticleType> type;
    private static final Map<Integer, SRPEnumParticle> PARTICLES;
    private static final Map<String, SRPEnumParticle> BY_NAME;

    private SRPEnumParticle(String particleNameIn, int particleIDIn, boolean shouldIgnoreRangeIn, int argumentCountIn, Supplier<SRPParticleType> type) {
        this.particleName = particleNameIn;
        this.particleID = particleIDIn;
        this.shouldIgnoreRange = shouldIgnoreRangeIn;
        this.argumentCount = argumentCountIn;
        this.type = type;
    }

    private SRPEnumParticle(String particleNameIn, int particleIDIn, boolean shouldIgnoreRangeIn, Supplier<SRPParticleType> type) {
        this(particleNameIn, particleIDIn, shouldIgnoreRangeIn, 0, type);
    }

    public static Set<String> getParticleNames() {
        return BY_NAME.keySet();
    }

    public String getParticleName() {
        return this.particleName;
    }

    public int getParticleID() {
        return this.particleID;
    }

    public int getArgumentCount() {
        return this.argumentCount;
    }

    public boolean getShouldIgnoreRange() {
        return this.shouldIgnoreRange;
    }

    public SRPParticleType getParticleType() {
        return this.type.get();
    }

    /** Options for Level#addParticle / ServerLevel#sendParticles. */
    public SRPParticleOptions options(int r, int g, int b) {
        return new SRPParticleOptions(this.type.get(), r, g, b);
    }

    @Nullable
    public static SRPEnumParticle getParticleFromId(int particleId) {
        return PARTICLES.get(particleId);
    }

    @Nullable
    public static SRPEnumParticle getByName(String nameIn) {
        return BY_NAME.get(nameIn);
    }

    static {
        PARTICLES = new HashMap<>();
        BY_NAME = new HashMap<>();
        for (SRPEnumParticle enumparticletypes : SRPEnumParticle.values()) {
            PARTICLES.put(enumparticletypes.getParticleID(), enumparticletypes);
            BY_NAME.put(enumparticletypes.getParticleName(), enumparticletypes);
        }
    }
}
