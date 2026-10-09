package com.dhanantry.scapeandrunparasites.world.spawner;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.world.level.Level;

public class MobCaps {
    Set<MobCap> mobCaps = new HashSet<MobCap>();

    public int getMobCapForDimension(String dimensionId) {
        for (MobCap mobCap : this.mobCaps) {
            if (!mobCap.getDimensionId().equals(dimensionId)) continue;
            return mobCap.getMobCap();
        }
        return SRPConfig.worldMobCap;
    }

    public void initializeMobCaps(Set<String> dimensionIds) {
        for (String dimensionId : dimensionIds) {
            this.mobCaps.add(new MobCap(SRPConfig.worldMobCap, dimensionId));
        }
    }

    public void initializeMobCapsForWorlds(Set<Level> worlds) {
        for (Level world : worlds) {
            this.mobCaps.add(new MobCap(SRPConfig.worldMobCap, DimKeys.of(world)));
        }
    }

    public void updateMobCapForDimension(String dimensionId, Level world) {
        for (MobCap mobCap : this.mobCaps) {
            if (!mobCap.getDimensionId().equals(dimensionId)) continue;
            mobCap.updateMobCap(world);
            return;
        }
        MobCap newMobCap = new MobCap(SRPConfig.worldMobCap, dimensionId);
        newMobCap.updateMobCap(world);
        this.mobCaps.add(newMobCap);
    }

    public void updateAllMobCaps(Set<Level> worlds) {
        for (Level world : worlds) {
            this.updateMobCapForDimension(DimKeys.of(world), world);
        }
    }

    public static class MobCap {
        private int mobCap;
        private String dimensionId;

        public MobCap(int mobCap, String dimensionId) {
            this.mobCap = mobCap;
            this.dimensionId = dimensionId;
        }

        public void updateMobCap(Level world) {
            int playerCount = world.players().size();
            this.setMobCap(SRPConfig.worldMobCap + playerCount * SRPConfig.worldMobCapPlusPlayer);
        }

        public String getDimensionId() {
            return this.dimensionId;
        }

        public void setDimensionId(String dimensionId) {
            this.dimensionId = dimensionId;
        }

        public int getMobCap() {
            return this.mobCap;
        }

        public void setMobCap(int mobCap) {
            this.mobCap = mobCap;
        }
    }
}

