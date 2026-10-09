package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.dislodgment.Dislodgments;
import com.dhanantry.scapeandrunparasites.network.EvoPhaseCancelPayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.phase.PhaseConfig;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

/**
 * Per-dimension Evolution Phases / Evolution Points, generations, dislodgments, parasite locks and assimilation counters
 * (SRPSaveData of 1.10.9). Port notes:
 * <ul>
 * <li>One instance for the whole server, stored with the overworld's data (file {@code data/srparasites_global_data.dat}), holding one
 * entry per dimension. Dimensions are keyed by resource location instead of numeric id (see {@link DimKeys}).</li>
 * <li>The NBT layout is flattened (one compound per dimension) because no 1.12.2 data can be loaded.</li>
 * </ul>
 */
public class SRPSaveData extends SavedData {
    private static final Logger LOG = ScapeAndRunParasites.LOGGER;
    public static final String DATA_NAME = "srparasites_global_data";
    private static final String DISLO_R = "0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0";
    public static int falseLevel = 0;

    /** One dimension's entry (the parallel lists dimEP* / dimGeneration* / dimUpdates / dimEIV* of 1.10.9). */
    private static final class DimData {
        int totalKills;
        byte evolution;
        int timeEvolution;
        boolean canGain;
        boolean canLose;
        String codes = DISLO_R;
        String codesDur = DISLO_R;
        byte generation;
        int generationTime;
        int updates;
        int eivHealth;
        int eivArea;
    }

    private record LockedPara(byte phase, int paraId) {}

    private int choice;
    private final Map<String, DimData> dims = new LinkedHashMap<>();
    private final Map<String, List<LockedPara>> lockedParas = new LinkedHashMap<>();
    private final Map<Integer, Integer> assimCounts = new HashMap<>();
    private boolean loading;

    // ------------------------------------------------------------------ access / persistence

    /** Server-side instance (overworld storage), or null on the client / without a server. */
    @Nullable
    public static SRPSaveData get(@Nullable Level level) {
        if (level == null || level.isClientSide()) return null;
        MinecraftServer server = level.getServer();
        if (server == null) return null;
        return get(server);
    }

    public static SRPSaveData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(() -> create(overworld), SRPSaveData::load, null), DATA_NAME);
    }

    private static SRPSaveData create(ServerLevel world) {
        SRPSaveData data = new SRPSaveData();
        data.choice = SRPPace.choiceNUMBER;
        data.createData(world);
        return data;
    }

    /** Replaces the stored instance with a freshly configured one. */
    public static SRPSaveData resetInstance(ServerLevel world) {
        ServerLevel overworld = world.getServer().overworld();
        SRPSaveData data = create(overworld);
        overworld.getDataStorage().set(DATA_NAME, data);
        data.setDirty();
        return data;
    }

    private void createData(ServerLevel world) {
        LOG.debug("creating SRPSaveData");
        this.resetLock();
        for (String line : SRPConfigSystems.evolutionDimStart) {
            try {
                String[] split = line.split(";");
                String dim = DimKeys.normalize(split[0]);
                byte phase = Byte.parseByte(split[1].trim());
                int points = Integer.parseInt(split[2].trim());
                this.addDim(dim);
                this.setEvolutionPhase(dim, phase, true, world);
                switch (phase) {
                    case -1 -> this.setTotalKills(dim, -points, false, world, true, 3);
                    case -2 -> {
                        this.setGaining(false, dim);
                        this.setLoss(false, dim);
                    }
                    default -> this.setTotalKills(dim, points, false, world, true, 2);
                }
            } catch (Exception e) {
                LOG.warn("Config line in \"Evolution Phases Dimension Starting Phase List\" is malformed, skipping ({})", line);
            }
        }
        for (String line : SRPConfigSystems.generationDimStart) {
            try {
                String[] split = line.split(";");
                String dim = DimKeys.normalize(split[0]);
                byte gen = Byte.parseByte(split[1].trim());
                this.addDim(dim);
                this.setGeneration(gen, dim);
            } catch (Exception e) {
                LOG.warn("Config line in \"Generation Dimension Starting List\" is malformed, skipping ({})", line);
            }
        }
        this.setDirty();
    }

    private DimData addDim(String id) {
        DimData existing = this.dims.get(id);
        if (existing != null) return existing;
        DimData d = new DimData();
        d.canGain = SRPConfigSystems.evolutionDimGainInverted == DimKeys.matches(SRPConfigSystems.evolutionDimGain, id);
        d.canLose = SRPConfigSystems.evolutionDimLossInverted == DimKeys.matches(SRPConfigSystems.evolutionDimLoss, id);
        d.evolution = SRPConfigSystems.defaultEvoPhase;
        d.totalKills = SRPConfigSystems.defaultEvoPoints;
        d.generation = SRPConfigSystems.generationDefa;
        this.dims.put(id, d);
        this.setDirty();
        return d;
    }

    /** Entry for a dimension; created with the config defaults when missing (the "not found -> addDim -> retry" idiom of the original). */
    private DimData dim(String id) {
        return this.addDim(id);
    }

    public static SRPSaveData load(CompoundTag tag, HolderLookup.Provider registries) {
        SRPSaveData data = new SRPSaveData();
        data.loading = true;
        if (tag.contains("srpchoice")) data.choice = tag.getInt("srpchoice");
        ListTag list = tag.getList("dimensions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag t = list.getCompound(i);
            DimData d = new DimData();
            d.totalKills = t.getInt("kills");
            d.timeEvolution = t.getInt("time");
            d.evolution = (byte) t.getInt("ev");
            d.canGain = t.getBoolean("gain");
            d.canLose = t.getBoolean("canLose");
            d.codes = t.getString("code");
            d.codesDur = t.getString("codedur");
            d.generation = t.getByte("generation");
            d.generationTime = t.getInt("generationtime");
            d.updates = t.getInt("update");
            d.eivHealth = t.getInt("eivh");
            d.eivArea = t.getInt("eiva");
            data.dims.put(t.getString("dimid"), d);
        }
        ListTag locks = tag.getList("lockedParasites", Tag.TAG_COMPOUND);
        for (int i = 0; i < locks.size(); i++) {
            CompoundTag t = locks.getCompound(i);
            data.lockedParas.computeIfAbsent(t.getString("dimId"), k -> new ArrayList<>()).add(new LockedPara(t.getByte("phase"), t.getInt("parasiteId")));
        }
        ListTag assim = tag.getList("srpassimilated", Tag.TAG_COMPOUND);
        for (int i = 0; i < assim.size(); i++) {
            CompoundTag t = assim.getCompound(i);
            data.assimCounts.put(t.getInt("id"), t.getInt("times"));
        }
        data.loading = false;
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt("srpchoice", this.choice);
        tag.putString("srpversion", "1.10.9");
        ListTag list = new ListTag();
        for (Map.Entry<String, DimData> e : this.dims.entrySet()) {
            DimData d = e.getValue();
            CompoundTag t = new CompoundTag();
            t.putString("dimid", e.getKey());
            t.putInt("kills", d.totalKills);
            t.putInt("time", d.timeEvolution);
            t.putInt("ev", d.evolution);
            t.putBoolean("gain", d.canGain);
            t.putBoolean("canLose", d.canLose);
            t.putString("code", d.codes);
            t.putString("codedur", d.codesDur);
            t.putByte("generation", d.generation);
            t.putInt("generationtime", d.generationTime);
            t.putInt("update", d.updates);
            t.putInt("eivh", d.eivHealth);
            t.putInt("eiva", d.eivArea);
            list.add(t);
        }
        tag.put("dimensions", list);
        ListTag locks = new ListTag();
        for (Map.Entry<String, List<LockedPara>> e : this.lockedParas.entrySet()) {
            for (LockedPara p : e.getValue()) {
                CompoundTag t = new CompoundTag();
                t.putString("dimId", e.getKey()); // original wrote "dimensionId" but read "dimId"; one key is used here
                t.putByte("phase", p.phase());
                t.putInt("parasiteId", p.paraId());
                locks.add(t);
            }
        }
        tag.put("lockedParasites", locks);
        ListTag assim = new ListTag();
        for (Map.Entry<Integer, Integer> e : this.assimCounts.entrySet()) {
            CompoundTag t = new CompoundTag();
            t.putInt("id", e.getKey());
            t.putInt("times", e.getValue());
            assim.add(t);
        }
        tag.put("srpassimilated", assim);
        return tag;
    }

    @Override
    public void setDirty() {
        if (!this.loading) super.setDirty();
    }

    public int getChoice() {
        return this.choice;
    }

    // ------------------------------------------------------------------ parasite locks

    public boolean checkParasiteID(int paraId) {
        return this.lockedParas.values().stream().anyMatch(l -> l.stream().anyMatch(p -> p.paraId() == paraId));
    }

    public List<Integer> getLockedList() {
        List<Integer> out = new ArrayList<>();
        this.lockedParas.values().forEach(l -> l.forEach(p -> out.add(p.paraId())));
        return out;
    }

    public boolean unlockParasite(int paraId) {
        boolean removed = false;
        for (List<LockedPara> l : this.lockedParas.values()) removed |= l.removeIf(p -> p.paraId() == paraId);
        this.setDirty();
        return removed;
    }

    public void unlockAllParasite() {
        this.lockedParas.clear();
        this.setDirty();
    }

    public void resetLock() {
        this.lockedParas.clear();
        for (String line : SRPConfigSystems.evolutionParasiteLock) {
            try {
                String[] split = line.split(";");
                String dim = DimKeys.normalize(split[0]);
                byte phase = Byte.parseByte(split[1].trim());
                int paraId = Integer.parseInt(split[2].trim());
                this.lockedParas.computeIfAbsent(dim, k -> new ArrayList<>()).add(new LockedPara(phase, paraId));
            } catch (Exception e) {
                LOG.warn("Config line in \"Evolution Parasite Lock List\" is malformed, skipping ({})", line);
            }
        }
        this.setDirty();
    }

    private void checkForUnlock(byte phase, String dimId, @Nullable Level world) {
        List<LockedPara> pairs = this.lockedParas.get(dimId);
        if (pairs != null) {
            Iterator<LockedPara> it = pairs.iterator();
            while (it.hasNext()) {
                LockedPara pair = it.next();
                if (phase != pair.phase()) continue;
                it.remove();
                ParasiteEventEntity.alertAllPlayerSer(world, SRPConfigSystems.evolutionParasiteLockMessage);
            }
        }
        this.setDirty();
    }

    // ------------------------------------------------------------------ vectors (EIV) and daily update

    public int getEIVHealth(String id) {
        DimData d = this.dims.get(id);
        return d == null ? 0 : d.eivHealth;
    }

    public int getEIVArea(String id) {
        DimData d = this.dims.get(id);
        return d == null ? 0 : d.eivArea;
    }

    public void setEIVHealthArea(String id, int health, int area) {
        DimData d = this.dims.get(id);
        if (d == null) return;
        d.eivHealth = health;
        d.eivArea = area;
        this.setDirty();
    }

    /** Daily tick: grows every dimension's vector and feeds its evolution points. */
    public void addUpdateNumber(int in, MinecraftServer server) {
        if (SRPConfigWorld.originActivated) {
            for (Map.Entry<String, DimData> e : new ArrayList<>(this.dims.entrySet())) {
                String id = e.getKey();
                DimData d = e.getValue();
                d.updates = Math.max(d.updates + in, 0);
                int health = d.eivHealth;
                int size = d.eivArea;
                long newHealth = health;
                long newSize = size;
                newHealth = (long) ((double) newHealth + (double) size * (SRPConfigWorld.originDailyHealth + PhaseConfig.originBonusHealth(d.evolution)));
                newSize = (long) ((double) newSize + (double) size * (SRPConfigWorld.originDailySize + PhaseConfig.originBonusSize(d.evolution)));
                health = newHealth > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newHealth;
                size = newSize > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newSize;
                d.eivHealth = Math.min(health, SRPConfigWorld.originHealthCap);
                d.eivArea = Math.min(size, SRPConfigWorld.originRadiusCap);
                if (!debugPointCsv()) {
                    LOG.debug("[SRP EP DEBUG] VECTOR_DAY dim={} phase={} eivHealth={} eivArea={}", id, d.evolution, d.eivHealth, d.eivArea);
                }
                long points = d.evolution == -1 ? (long) (int) ((double) d.eivHealth * SRPConfigWorld.originDailyEPPointsOutBreak)
                        : (long) (int) ((double) d.eivHealth * SRPConfigWorld.originDailyEPPoints);
                int pointToAdd = points > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) points;
                pointToAdd = Math.min(pointToAdd, PhaseConfig.vectorPointCap(d.evolution));
                this.setTotalKills(id, pointToAdd, true, DimKeys.level(server, id), true, true, 1);
            }
        }
        this.setDirty();
    }

    /** Pending daily updates for a dimension (reset on read). Unknown dimensions are created and report their generation, as in 1.10.9. */
    public int getUpdateNumber(String id) {
        DimData d = this.dims.get(id);
        if (d != null) {
            int u = d.updates;
            d.updates = 0;
            this.setDirty();
            return u;
        }
        return this.dim(id).generation;
    }

    // ------------------------------------------------------------------ gain / loss / phase / cooldown

    public void setGaining(boolean in, String id) {
        DimData d = this.dims.get(id);
        if (d == null) return;
        d.canGain = in;
        this.setDirty();
    }

    public boolean getCanGain(String id) {
        return this.dim(id).canGain;
    }

    public void setLoss(boolean in, String id) {
        DimData d = this.dims.get(id);
        if (d == null) return;
        d.canLose = in;
        this.setDirty();
    }

    public boolean getCanLoss(String id) {
        return this.dim(id).canLose;
    }

    public byte getEvolutionPhase(String id) {
        return this.dim(id).evolution;
    }

    public void setCooldown(int in, Level worldIn, String id, boolean adding) {
        DimData d = this.dims.get(id);
        if (d == null) return;
        int currentWT = (int) worldIn.getGameTime();
        int timer = currentWT - PhaseConfig.delayTicks(this.getEvolutionPhase(id)) * 20 + in * 20;
        d.timeEvolution = adding ? timer + this.getCooldown(worldIn, id) * 20 : timer;
        this.setDirty();
    }

    public int getCooldown(Level worldIn, String id) {
        DimData d = this.dims.get(id);
        if (d == null || d.timeEvolution == 0) return 0;
        int currentWT = (int) worldIn.getGameTime();
        return Math.max((PhaseConfig.delayTicks(this.getEvolutionPhase(id)) * 20 - (currentWT - d.timeEvolution)) / 20, 0);
    }

    public int getTotalKills(String id) {
        return this.dim(id).totalKills;
    }

    public boolean setTotalKills(String dimID, int in, boolean plus, @Nullable Level worldIn, boolean canChangePhase, int srcID) {
        return this.setTotalKills(dimID, in, plus, worldIn, canChangePhase, false, srcID);
    }

    /**
     * Adds (plus) or sets (!plus) evolution points. {@code in} is scaled by the world's pace choice and capped per call by difficulty
     * (integer division as in 1.10.9: Peaceful/Easy = 0, Normal/Hard = evolutionPointCap) unless it comes from sleeping / the x3 and x10 paces.
     */
    public boolean setTotalKills(String dimID, int in, boolean plus, @Nullable Level worldIn, boolean canChangePhase, boolean isFromSleep, int srcID) {
        if (!SRPConfigSystems.useEvolution) return false;
        int requestedAmount = in;
        boolean requestedPlus = plus;
        boolean requestedCanChangePhase = canChangePhase;
        if (in > 0) {
            switch (this.choice) {
                case 0 -> in = (int) ((double) in * 0.5);
                case 2 -> {
                    in *= 3;
                    isFromSleep = true;
                }
                case 3 -> {
                    in *= 10;
                    isFromSleep = true;
                }
                default -> { }
            }
        }
        if (worldIn != null) {
            if (SRPConfigSystems.evolutionNoPlayerMultipler && worldIn.players().isEmpty()) return false;
            int difficultyCap = worldIn.getDifficulty().getId() / 2 * SRPConfigSystems.evolutionPointCap;
            if (!isFromSleep && plus) in = Math.min(in, difficultyCap);
        }
        DimData d = this.dims.get(dimID);
        if (d == null) {
            this.addDim(dimID);
            this.setDirty();
            return true;
        }
        if (!d.canGain && plus && in > 0) {
            logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, d.totalKills, d.evolution, canChangePhase, isFromSleep, "GAIN_DISABLED");
            return false;
        }
        if (!d.canLose && plus && in < 0) {
            logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, d.totalKills, d.evolution, canChangePhase, isFromSleep, "LOSS_DISABLED");
            return false;
        }
        if (d.evolution == -2) {
            logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, d.totalKills, d.evolution, canChangePhase, isFromSleep, "PHASE_NEGATIVE_TWO");
            return false;
        }
        if (PhaseConfig.delayTicks(d.evolution) != 0 && worldIn != null && this.choice != 3 && this.getCooldown(worldIn, dimID) != 0 && plus) {
            logPointRejected(worldIn, dimID, srcID, plus, requestedAmount, in, d.totalKills, d.evolution, canChangePhase, isFromSleep, "COOLDOWN");
            return false;
        }
        if (worldIn != null) this.checkGeneration(dimID, worldIn);
        int beforePoints = d.totalKills;
        byte phaseBefore = d.evolution;
        if (plus) {
            int kills = d.totalKills;
            byte phase = d.evolution;
            boolean top = kills >= 0 && in > 0;
            if (in < 0 && phase < 0) return false;
            kills += in;
            if (SRPConfigSystems.debugEvolutionPointsConsole) {
                LOG.debug("[SRP EP DEBUG] calculated points dim={} src={} before={} change={} after={} phase={} canChangePhase={} sleepBypass={}", dimID, srcID, beforePoints, in, kills, phase, canChangePhase, isFromSleep);
            }
            if (top && kills < 0) kills = d.totalKills;
            if (!canChangePhase && phase >= 0) {
                int lower = PhaseConfig.neededPoints(this.getEvolutionPhase(dimID));
                if (kills < lower) kills = lower;
            }
            if (kills < 0 && phase >= 0) kills = 0;
            if (kills > SRPConfigSystems.phaseTenTotalPoints) kills = SRPConfigSystems.phaseTenTotalPoints;
            this.checkKills(dimID, kills, worldIn, d.evolution);
            d.totalKills = kills;
            logPointChange(worldIn, dimID, srcID, requestedPlus, requestedAmount, in, beforePoints, kills, phaseBefore, d.evolution, requestedCanChangePhase, isFromSleep, "APPLIED");
        } else {
            d.totalKills = in;
            logPointChange(worldIn, dimID, srcID, requestedPlus, requestedAmount, in, beforePoints, in, phaseBefore, d.evolution, requestedCanChangePhase, isFromSleep, "SET_DIRECT");
        }
        this.setDirty();
        return true;
    }

    private static final String PHASE_DECREASED = "Phase decreased";

    private static String warningFor(int phase) {
        return PhaseConfig.warning(phase);
    }

    /** Promotes / demotes the phase when the points cross a threshold (SRPSaveData.checkKills). Recurses upward while promoting. */
    private boolean checkKills(String id, int in, @Nullable Level worldIn, byte evoPhase) {
        boolean changed = false;
        if (evoPhase == -1) {
            if (in > 0 && this.setEvolutionPhase(id, (byte) 0, false, worldIn)) {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, warningFor(0), 0);
                changed = true;
            }
        } else if (evoPhase == 0) {
            if (in > SRPConfigSystems.phaseKillsOne && this.setEvolutionPhase(id, (byte) 1, false, worldIn)) {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, warningFor(1), 1);
                changed = true;
            }
        } else if (evoPhase >= 1 && evoPhase <= 9) {
            int next = evoPhase + 1;
            if (in > PhaseConfig.neededPoints((byte) next)) {
                if (this.setEvolutionPhase(id, (byte) next, false, worldIn)) {
                    ParasiteEventEntity.alertAllPlayerDim(worldIn, warningFor(next), next);
                    changed = true;
                }
            } else if (in < PhaseConfig.neededPoints(evoPhase) && this.setEvolutionPhase(id, (byte) (evoPhase - 1), false, worldIn)) {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, PHASE_DECREASED, -7);
            }
        } else if (evoPhase == 10) {
            if (in < SRPConfigSystems.phaseKillsTen && this.setEvolutionPhase(id, (byte) 9, false, worldIn)) {
                ParasiteEventEntity.alertAllPlayerDim(worldIn, PHASE_DECREASED, -7);
            }
        }
        if (in > 0 && changed && evoPhase > -3 && evoPhase < 11) {
            return this.checkKills(id, in, worldIn, (byte) (evoPhase + 1));
        }
        return true;
    }

    public boolean setEvolutionPhase(String id, byte in, boolean override, @Nullable Level worldIn) {
        DimData d = this.dims.get(id);
        if (d != null) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            ServerLevel target = DimKeys.level(server, id);
            if (target != null) com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersInDimension(target, new EvoPhaseCancelPayload(in));
            d.evolution = in;
            if (override) this.checkPhase(id, in, worldIn);
            if (worldIn != null) d.timeEvolution = (int) worldIn.getGameTime();
            this.checkForUnlock(this.getEvolutionPhase(id), id, worldIn);
        }
        this.setDirty();
        return true;
    }

    /** Snaps the points to the phase's threshold after an override (SRPSaveData.checkPhase). */
    private void checkPhase(String id, byte in, @Nullable Level worldIn) {
        switch (in) {
            case -1 -> this.setTotalKills(id, -10, false, worldIn, true, 4);
            case 0 -> this.setTotalKills(id, 0, false, worldIn, true, 5);
            case 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 -> this.setTotalKills(id, PhaseConfig.neededPoints(in), false, worldIn, true, 5 + in);
            default -> { }
        }
    }

    // ------------------------------------------------------------------ assimilation counters

    public int getNumberIDDataSpawn(int id) {
        return this.assimCounts.getOrDefault(id, 0);
    }

    public void addNumberIDDataSpawn(int id) {
        this.assimCounts.merge(id, 1, Integer::sum);
        this.setDirty();
    }

    // ------------------------------------------------------------------ dislodgments

    public String getCurrentCodeU(String id) {
        return this.dim(id).codes;
    }

    public String getCurrentCodeUD(String id) {
        return this.dim(id).codesDur;
    }

    public int getCurrentCode(String id, int position) {
        return Integer.parseInt(this.dim(id).codes.split(";")[position]);
    }

    public int[] getDisloValues(String id) {
        int[] values = new int[30];
        DimData d = this.dims.get(id);
        if (d != null) {
            String[] here = d.codes.split(";");
            for (int k = 0; k < here.length; k++) values[k] = Integer.parseInt(here[k]);
        }
        return values;
    }

    public int getCurrentCodeDuration(String id, int position) {
        return Integer.parseInt(this.dim(id).codesDur.split(";")[position]);
    }

    public boolean setCurrentCode(String id, int position, int code, int time, @Nullable Level worldIn, boolean start, int pointCost) {
        if (position > 29 || position < 0) return false;
        if (code < 0 || time < 0 || pointCost < 0) return false;
        if (code == 0) time = 0;
        DimData d = this.dims.get(id);
        if (d == null) {
            this.setDirty();
            return true;
        }
        if (this.getCurrentCodeDuration(id, position) != 0) return false;
        if (pointCost > 0 && SRPConfigSystems.useEvolution) {
            int beforePoints = d.totalKills;
            int point = beforePoints - pointCost;
            if (point < PhaseConfig.neededPoints(d.evolution)) return false;
            byte phaseBefore = d.evolution;
            d.totalKills = point;
            logPointChange(worldIn, id, 70 + position, true, -pointCost, -pointCost, beforePoints, point, phaseBefore, d.evolution, false, false, "DISLODGEMENT_COST");
        }
        String[] here = d.codes.split(";");
        here[position] = String.valueOf(code);
        d.codes = String.join(";", here);
        here = d.codesDur.split(";");
        here[position] = String.valueOf(time);
        d.codesDur = String.join(";", here);
        if (worldIn != null) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, PhaseConfig.disloMessage(position, start), 200 + position);
            if (start) Dislodgments.start(worldIn, position, time);
        }
        this.setDirty();
        return true;
    }

    /** Counts every active dislodgment down by {@code amount} seconds, ends finished ones and starts their cooldown (negative duration). */
    public void reduceCodesCooldown(String id, int amount, @Nullable Level worldIn) {
        DimData d = this.dims.get(id);
        if (d != null) {
            String[] here = d.codesDur.split(";");
            for (int k = 0; k < here.length; k++) {
                int cool = Integer.parseInt(here[k]);
                if (cool == 0) continue;
                boolean positive = cool > 0;
                if (positive) {
                    Dislodgments.mid(worldIn, k, this.getCurrentCode(id, k));
                    cool = Math.max(cool - amount, 0);
                } else {
                    cool = Math.min(cool + amount, 0);
                }
                here[k] = String.valueOf(cool);
                if (cool != 0 || !positive) continue;
                int cooldown = -PhaseConfig.disloCooldown(k);
                cooldown *= (int) PhaseConfig.disloPhaseCooldown(d.evolution);
                here[k] = String.valueOf(cooldown);
                String[] codes = d.codes.split(";");
                codes[k] = "0";
                d.codes = String.join(";", codes);
                if (worldIn == null) continue;
                ParasiteEventEntity.alertAllPlayerDim(worldIn, PhaseConfig.disloMessage(k, false), 200 + k);
                Dislodgments.end(worldIn, k);
            }
            d.codesDur = String.join(";", here);
        }
        this.setDirty();
    }

    // ------------------------------------------------------------------ generations

    public void setGeneration(byte in, String id) {
        DimData d = this.dims.get(id);
        if (d != null) d.generation = in;
        this.setDirty();
    }

    public byte getGeneration(String id) {
        return this.dim(id).generation;
    }

    public void setGenerationTime(int in, String id) {
        DimData d = this.dims.get(id);
        if (d != null) d.generationTime = in;
        this.setDirty();
    }

    public int getGenerationTime(String id) {
        return this.dim(id).generationTime;
    }

    public int getGenerationNeededTime(Level worldIn, String id) {
        DimData d = this.dims.get(id);
        if (d == null) return 0;
        int timer = (int) worldIn.getGameTime() - d.generationTime;
        int needed = this.getGenerationNeededTime(d.generation);
        if (!this.generationPhaseNeeded(d.generation, d.evolution)) {
            needed = (int) ((float) needed * SRPConfigSystems.generationPhasePenalty);
        }
        return Math.max(needed - timer, 0);
    }

    public boolean checkGeneration(String id, Level worldIn) {
        DimData d = this.dims.get(id);
        if (d == null) {
            this.addDim(id);
        } else {
            if (d.generation == 5) return false;
            int currentWT = (int) worldIn.getGameTime();
            int timer = currentWT - d.generationTime;
            int needed = this.getGenerationNeededTime(d.generation);
            if (!this.generationPhaseNeeded(d.generation, d.evolution)) {
                needed = (int) ((float) needed * SRPConfigSystems.generationPhasePenalty);
            }
            if (timer > needed) {
                d.generation = (byte) (d.generation + 1);
                d.generationTime = currentWT;
            }
        }
        this.setDirty();
        return true;
    }

    private boolean generationPhaseNeeded(byte gene, byte phase) {
        byte[] phases = switch (gene) {
            case 0 -> SRPConfigSystems.generationPhases1;
            case 1 -> SRPConfigSystems.generationPhases2;
            case 2 -> SRPConfigSystems.generationPhases3;
            case 3 -> SRPConfigSystems.generationPhases4;
            case 4 -> SRPConfigSystems.generationPhases5;
            default -> null;
        };
        if (phases == null) return false;
        for (byte p : phases) {
            if (p == phase) return true;
        }
        return false;
    }

    private int getGenerationNeededTime(byte in) {
        double bonus = 1.0;
        switch (this.choice) {
            case 0 -> bonus *= 0.5;
            case 2 -> bonus *= 3.0;
            case 3 -> bonus *= 10.0;
            default -> { }
        }
        if (bonus <= 0.0) bonus = 1.0;
        int base = switch (in) {
            case 0 -> SRPConfigSystems.generationTime1;
            case 1 -> SRPConfigSystems.generationTime2;
            case 2 -> SRPConfigSystems.generationTime3;
            case 3 -> SRPConfigSystems.generationTime4;
            case 4 -> SRPConfigSystems.generationTime5;
            default -> -1;
        };
        if (base < 0) return 0;
        return (int) Math.max(1L, Math.round((double) base / bonus));
    }

    // ------------------------------------------------------------------ generation modifiers (generated)

    /** Generation boolean modifiers: miniDamage, damageCap, lookWalls, sprinting, waterLeap, specialM, adaptation, blockSearch, residue, orbbox. */
    public boolean[] getGeneModi(String id) {
        DimData d = this.dims.get(id);
        if (d == null) return new boolean[]{true};
        switch (d.generation) {
            case 0: return new boolean[]{SRPConfigSystems.generationMiniDamage0, SRPConfigSystems.generationDamageCap0, SRPConfigSystems.generationLookWalls0, SRPConfigSystems.generationSprinting0, SRPConfigSystems.generationWaterLeap0, SRPConfigSystems.generationSpecialM0, SRPConfigSystems.generationAdaptation0, SRPConfigSystems.generationBlockSearch0, SRPConfigSystems.generationResidue0, SRPConfigSystems.generationOrbbox0};
            case 1: return new boolean[]{SRPConfigSystems.generationMiniDamage1, SRPConfigSystems.generationDamageCap1, SRPConfigSystems.generationLookWalls1, SRPConfigSystems.generationSprinting1, SRPConfigSystems.generationWaterLeap1, SRPConfigSystems.generationSpecialM1, SRPConfigSystems.generationAdaptation1, SRPConfigSystems.generationBlockSearch1, SRPConfigSystems.generationResidue1, SRPConfigSystems.generationOrbbox1};
            case 2: return new boolean[]{SRPConfigSystems.generationMiniDamage2, SRPConfigSystems.generationDamageCap2, SRPConfigSystems.generationLookWalls2, SRPConfigSystems.generationSprinting2, SRPConfigSystems.generationWaterLeap2, SRPConfigSystems.generationSpecialM2, SRPConfigSystems.generationAdaptation2, SRPConfigSystems.generationBlockSearch2, SRPConfigSystems.generationResidue2, SRPConfigSystems.generationOrbbox2};
            case 3: return new boolean[]{SRPConfigSystems.generationMiniDamage3, SRPConfigSystems.generationDamageCap3, SRPConfigSystems.generationLookWalls3, SRPConfigSystems.generationSprinting3, SRPConfigSystems.generationWaterLeap3, SRPConfigSystems.generationSpecialM3, SRPConfigSystems.generationAdaptation3, SRPConfigSystems.generationBlockSearch3, SRPConfigSystems.generationResidue3, SRPConfigSystems.generationOrbbox3};
            case 4: return new boolean[]{SRPConfigSystems.generationMiniDamage4, SRPConfigSystems.generationDamageCap4, SRPConfigSystems.generationLookWalls4, SRPConfigSystems.generationSprinting4, SRPConfigSystems.generationWaterLeap4, SRPConfigSystems.generationSpecialM4, SRPConfigSystems.generationAdaptation4, SRPConfigSystems.generationBlockSearch4, SRPConfigSystems.generationResidue4, SRPConfigSystems.generationOrbbox4};
            case 5: return new boolean[]{SRPConfigSystems.generationMiniDamage5, SRPConfigSystems.generationDamageCap5, SRPConfigSystems.generationLookWalls5, SRPConfigSystems.generationSprinting5, SRPConfigSystems.generationWaterLeap5, SRPConfigSystems.generationSpecialM5, SRPConfigSystems.generationAdaptation5, SRPConfigSystems.generationBlockSearch5, SRPConfigSystems.generationResidue5, SRPConfigSystems.generationOrbbox5};
            default: return new boolean[]{true, true, true, true, true, true, true, true, true, true};
        }
    }

    /** Generation float modifiers: poisonHeal, mobHealing, attackSpeed. */
    public float[] getGeneModi2(String id) {
        DimData d = this.dims.get(id);
        if (d == null) return new float[]{0.0f};
        switch (d.generation) {
            case 0: return new float[]{SRPConfigSystems.generationPoisonHeal0, SRPConfigSystems.generationMobHealing0, SRPConfigSystems.generationAttackSpeed0};
            case 1: return new float[]{SRPConfigSystems.generationPoisonHeal1, SRPConfigSystems.generationMobHealing1, SRPConfigSystems.generationAttackSpeed1};
            case 2: return new float[]{SRPConfigSystems.generationPoisonHeal2, SRPConfigSystems.generationMobHealing2, SRPConfigSystems.generationAttackSpeed2};
            case 3: return new float[]{SRPConfigSystems.generationPoisonHeal3, SRPConfigSystems.generationMobHealing3, SRPConfigSystems.generationAttackSpeed3};
            case 4: return new float[]{SRPConfigSystems.generationPoisonHeal4, SRPConfigSystems.generationMobHealing4, SRPConfigSystems.generationAttackSpeed4};
            case 5: return new float[]{SRPConfigSystems.generationPoisonHeal5, SRPConfigSystems.generationMobHealing5, SRPConfigSystems.generationAttackSpeed5};
            default: return new float[]{2.5f, 3.0f, 0.5f};
        }
    }

    // ------------------------------------------------------------------ Ubiquitous Development / colonies

    /** {sum of phases of dimensions above phase 0, number of such dimensions}. */
    public int[] getDeveDimsPhases() {
        int points = 0;
        int count = 0;
        for (DimData d : this.dims.values()) {
            if (d.evolution <= 0) continue;
            points += d.evolution;
            count++;
        }
        return new int[]{points, count};
    }

    public int getDeveLevel() {
        if (falseLevel != 0) return falseLevel;
        int[] check = this.getDeveDimsPhases();
        int value = check[0];
        int count = check[1];
        if (value >= SRPConfigSystems.devePointsFour && count >= SRPConfigSystems.deveMiniDimsFour) return 4;
        if (value >= SRPConfigSystems.devePointsThree && count >= SRPConfigSystems.deveMiniDimsThree) return 3;
        if (value >= SRPConfigSystems.devePointsTwo && count >= SRPConfigSystems.deveMiniDimsTwo) return 2;
        if (value >= SRPConfigSystems.devePointsOne && count >= SRPConfigSystems.deveMiniDimsOne) return 1;
        return 0;
    }

    /** Known dimension keys in storage order (getColonies("d")). */
    public List<String> getDimensionKeys() {
        return new ArrayList<>(this.dims.keySet());
    }

    /** Evolution points per dimension, same order as {@link #getDimensionKeys()} (getColonies("k")). */
    public List<Integer> getKillsList() {
        List<Integer> out = new ArrayList<>();
        this.dims.values().forEach(d -> out.add(d.totalKills));
        return out;
    }

    /** Phases per dimension (getColonies("p")). */
    public List<Integer> getPhasesList() {
        List<Integer> out = new ArrayList<>();
        this.dims.values().forEach(d -> out.add((int) d.evolution));
        return out;
    }

    /** Generations per dimension (getColonies("g")). */
    public List<Integer> getGenerationsList() {
        List<Integer> out = new ArrayList<>();
        this.dims.values().forEach(d -> out.add((int) d.generation));
        return out;
    }

    // ------------------------------------------------------------------ evolution point debug output

    private static boolean debugPointCsv() {
        return SRPConfigSystems.debugEvolutionPointsCsv;
    }

    private static String getPointSourceName(int srcID) {
        return switch (srcID) {
            case 1 -> "EIV_DAILY_GROWTH";
            case 2 -> "DIM_START_DEFAULT";
            case 3 -> "DIM_START_OUTBREAK";
            case 4 -> "KILL";
            case 5 -> "ASSIMILATION";
            case 6 -> "COMMAND";
            case 7 -> "EVENT";
            case 8 -> "COLONY";
            case 9 -> "PENALTY_OR_LOSS";
            case 52 -> "COTH_DURATION_REFRESH";
            default -> "UNKNOWN_" + srcID;
        };
    }

    private static String[] getPointCallerInfo() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        boolean found = false;
        for (StackTraceElement element : stack) {
            String className = element.getClassName();
            String methodName = element.getMethodName();
            if (className.endsWith("SRPSaveData") && ("setTotalKills".equals(methodName) || "setCurrentCode".equals(methodName))) {
                found = true;
                continue;
            }
            if (!found || className.endsWith("SRPSaveData")) continue;
            return new String[]{className, methodName, String.valueOf(element.getLineNumber())};
        }
        return new String[]{"unknown", "unknown", "-1"};
    }

    private static String csvSafe(@Nullable String in) {
        return in == null ? "" : "\"" + in.replace("\"", "\"\"") + "\"";
    }

    private static File getPointDebugFile(@Nullable Level worldIn, String dimID) {
        File root = null;
        if (worldIn != null && worldIn.getServer() != null) root = worldIn.getServer().getWorldPath(LevelResource.ROOT).toFile();
        if (root == null) root = new File(".");
        File folder = new File(root, "srparasites_debug");
        if (!folder.exists()) folder.mkdirs();
        return new File(folder, "evolution_points_dim" + DimKeys.legacyId(dimID) + ".csv");
    }

    private static void writePointCsv(@Nullable Level worldIn, String dimID, int srcID, String srcName, boolean plus, int requestedAmount, int processedAmount, int appliedDelta,
                                      int before, int after, int phaseBefore, int phaseAfter, boolean canChangePhase, boolean isFromSleep, String result) {
        if (!debugPointCsv()) return;
        File file = getPointDebugFile(worldIn, dimID);
        boolean writeHeader = !file.exists() || file.length() == 0L;
        long worldTime = worldIn != null ? worldIn.getGameTime() : -1L;
        long day = worldTime >= 0L ? worldTime / 24000L : -1L;
        String callerClass = "";
        String callerMethod = "";
        String callerLine = "";
        if (SRPConfigSystems.debugEvolutionPointsCallerTrace) {
            String[] caller = getPointCallerInfo();
            callerClass = caller[0];
            callerMethod = caller[1];
            callerLine = caller[2];
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (writeHeader) {
                writer.write("real_time_ms,world_time,day,dim,src_id,src_name,caller_class,caller_method,caller_line,plus,requested_amount,processed_amount,applied_delta,before,after,phase_before,phase_after,can_change_phase,is_from_sleep,result");
                writer.newLine();
            }
            writer.write(System.currentTimeMillis() + "," + worldTime + "," + day + "," + DimKeys.legacyId(dimID) + "," + srcID + "," + csvSafe(srcName) + "," + csvSafe(callerClass) + "," + csvSafe(callerMethod) + ","
                    + callerLine + "," + plus + "," + requestedAmount + "," + processedAmount + "," + appliedDelta + "," + before + "," + after + "," + phaseBefore + "," + phaseAfter + ","
                    + canChangePhase + "," + isFromSleep + "," + csvSafe(result));
            writer.newLine();
        } catch (IOException e) {
            LOG.warn("[SRP EP DEBUG] Failed to write point CSV for dim {} src {}.", dimID, srcName, e);
        }
    }

    private static void logPointChange(@Nullable Level worldIn, String dimID, int srcID, boolean plus, int requestedAmount, int processedAmount, int before, int after, int phaseBefore,
                                       int phaseAfter, boolean canChangePhase, boolean isFromSleep, String result) {
        String srcName = getPointSourceName(srcID);
        int appliedDelta = after - before;
        if (SRPConfigSystems.debugEvolutionPointsConsole) {
            String callerClass = "";
            String callerMethod = "";
            String callerLine = "";
            if (SRPConfigSystems.debugEvolutionPointsCallerTrace) {
                String[] caller = getPointCallerInfo();
                callerClass = caller[0];
                callerMethod = caller[1];
                callerLine = caller[2];
            }
            long worldTime = worldIn != null ? worldIn.getGameTime() : -1L;
            long day = worldTime >= 0L ? worldTime / 24000L : -1L;
            LOG.debug("[SRP EP DEBUG] result={} dim={} day={} time={} src={}({}) caller={}.{}:{} plus={} requested={} processed={} appliedDelta={} before={} after={} phase={}=>{} canChangePhase={} sleepBypass={}",
                    result, dimID, day, worldTime, srcID, srcName, callerClass, callerMethod, callerLine, plus, requestedAmount, processedAmount, appliedDelta, before, after, phaseBefore, phaseAfter, canChangePhase, isFromSleep);
        }
        writePointCsv(worldIn, dimID, srcID, srcName, plus, requestedAmount, processedAmount, appliedDelta, before, after, phaseBefore, phaseAfter, canChangePhase, isFromSleep, result);
    }

    private static void logPointRejected(@Nullable Level worldIn, String dimID, int srcID, boolean plus, int requestedAmount, int processedAmount, int before, int phaseBefore,
                                         boolean canChangePhase, boolean isFromSleep, String reason) {
        logPointChange(worldIn, dimID, srcID, plus, requestedAmount, processedAmount, before, before, phaseBefore, phaseBefore, canChangePhase, isFromSleep, "REJECTED_" + reason);
    }
}
