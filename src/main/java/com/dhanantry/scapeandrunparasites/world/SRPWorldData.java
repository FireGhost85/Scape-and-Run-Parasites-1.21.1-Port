package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockBiomeCore;
import com.dhanantry.scapeandrunparasites.block.BlockColonyCore;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class SRPWorldData
extends WorldSavedData {
    private static final String DATA_NAME = "srparasites_data";
    private ArrayList<Integer> nodeX = new ArrayList();
    private ArrayList<Integer> nodeY = new ArrayList();
    private ArrayList<Integer> nodeZ = new ArrayList();
    private ArrayList<Integer> nodeA = new ArrayList();
    private ArrayList<Byte> nodeT = new ArrayList();
    private ArrayList<Integer> originX = new ArrayList();
    private ArrayList<Integer> originY = new ArrayList();
    private ArrayList<Integer> originZ = new ArrayList();
    private ArrayList<Integer> originA = new ArrayList();
    private ArrayList<Integer> originH = new ArrayList();
    private ArrayList<Integer> colonyX = new ArrayList();
    private ArrayList<Integer> colonyY = new ArrayList();
    private ArrayList<Integer> colonyZ = new ArrayList();
    private ArrayList<Integer> colonyA = new ArrayList();
    private ArrayList<Integer> resistanceI = new ArrayList();
    private ArrayList<String> resistanceS = new ArrayList();
    private boolean dimMeteor;

    public SRPWorldData(Level world) {
        super(DATA_NAME);
        this.create(world);
    }

    public SRPWorldData(String name) {
        super(name);
    }

    public static SRPWorldData get(Level world) {
        if (world == null) {
            return null;
        }
        MapStorage storage = world.getPerWorldStorage();
        SRPWorldData instance = (SRPWorldData)storage.loadData(SRPWorldData.class, DATA_NAME);
        if (instance == null) {
            instance = new SRPWorldData(world);
            storage.setData(DATA_NAME, (WorldSavedData)instance);
        } else {
            instance.updateDays(world);
        }
        return instance;
    }

    private void create(Level world) {
        SRPMain.logger.debug("Creating SRPWorldData for dim {}", DimKeys.of(world));
        boolean meteorIsEnabled = Arrays.stream(SRPConfigWorld.meteorBlacklistDims).noneMatch(dim -> dim == DimKeys.of(world));
        this.setTriggerMet(meteorIsEnabled && (SRPWorldEntitySpawner.triggerSPAWNING || SRPConfigWorld.meteorActive));
        this.markDirty();
    }

    public void resetInstance(Level world) {
        world.getPerWorldStorage().setData(DATA_NAME, (WorldSavedData)new SRPWorldData(world));
    }

    public void readFromNBT(CompoundTag compound) {
        int type;
        CompoundTag tagT;
        int age;
        CompoundTag tagA;
        int coordz;
        CompoundTag tagZ;
        int coordy;
        CompoundTag tagY;
        int coordx;
        CompoundTag tagX;
        int i;
        ListTag tagListT;
        ListTag tagListA;
        ListTag tagListZ;
        ListTag tagListY;
        ListTag tagListX;
        if (compound.contains("srpmeteor")) {
            this.dimMeteor = compound.getBoolean("srpmeteor");
        }
        if (compound.contains("srpnodescoordsx")) {
            tagListX = compound.getList("srpnodescoordsx", 10);
            tagListY = compound.getList("srpnodescoordsy", 10);
            tagListZ = compound.getList("srpnodescoordsz", 10);
            tagListA = compound.getList("srpnodesages", 10);
            tagListT = compound.getList("srpnodestypes", 10);
            if (tagListX.size() != tagListY.size() || tagListX.size() != tagListZ.size() || tagListX.size() != tagListA.size() || tagListX.size() != tagListT.size()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading nodes coords");
            } else {
                for (i = 0; i < tagListX.size(); ++i) {
                    tagX = tagListX.getCompound(i);
                    coordx = tagX.getInt("nodex" + i);
                    this.nodeX.add(i, coordx);
                    tagY = tagListY.getCompound(i);
                    coordy = tagY.getInt("nodey" + i);
                    this.nodeY.add(i, coordy);
                    tagZ = tagListZ.getCompound(i);
                    coordz = tagZ.getInt("nodez" + i);
                    this.nodeZ.add(i, coordz);
                    tagA = tagListA.getCompound(i);
                    age = tagA.getInt("nodea" + i);
                    this.nodeA.add(i, age);
                    tagT = tagListT.getCompound(i);
                    type = tagT.getInt("nodet" + i);
                    this.nodeT.add(i, (byte)type);
                }
            }
        }
        if (compound.contains("srporiginscoordsx")) {
            tagListX = compound.getList("srporiginscoordsx", 10);
            tagListY = compound.getList("srporiginscoordsy", 10);
            tagListZ = compound.getList("srporiginscoordsz", 10);
            tagListA = compound.getList("srporiginsareas", 10);
            tagListT = compound.getList("srporiginshealths", 10);
            if (tagListX.size() != tagListY.size() || tagListX.size() != tagListZ.size() || tagListX.size() != tagListA.size() || tagListX.size() != tagListT.size()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading origins coords");
            } else {
                for (i = 0; i < tagListX.size(); ++i) {
                    tagX = tagListX.getCompound(i);
                    coordx = tagX.getInt("originx" + i);
                    this.originX.add(i, coordx);
                    tagY = tagListY.getCompound(i);
                    coordy = tagY.getInt("originy" + i);
                    this.originY.add(i, coordy);
                    tagZ = tagListZ.getCompound(i);
                    coordz = tagZ.getInt("originz" + i);
                    this.originZ.add(i, coordz);
                    tagA = tagListA.getCompound(i);
                    age = tagA.getInt("origina" + i);
                    this.originA.add(i, age);
                    tagT = tagListT.getCompound(i);
                    type = tagT.getInt("originh" + i);
                    this.originH.add(i, type);
                }
            }
        }
        if (compound.contains("srpcoloniescoordsx")) {
            tagListX = compound.getList("srpcoloniescoordsx", 10);
            tagListY = compound.getList("srpcoloniescoordsy", 10);
            tagListZ = compound.getList("srpcoloniescoordsz", 10);
            tagListA = compound.getList("srpcoloniesages", 10);
            if (tagListX.size() != tagListY.size() || tagListX.size() != tagListZ.size() || tagListX.size() != tagListA.size()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading colonies coords");
            } else {
                for (int i2 = 0; i2 < tagListX.size(); ++i2) {
                    CompoundTag tagX2 = tagListX.getCompound(i2);
                    int coordx2 = tagX2.getInt("colonyx" + i2);
                    this.colonyX.add(i2, coordx2);
                    CompoundTag tagY2 = tagListY.getCompound(i2);
                    int coordy2 = tagY2.getInt("colonyy" + i2);
                    this.colonyY.add(i2, coordy2);
                    CompoundTag tagZ2 = tagListZ.getCompound(i2);
                    int coordz2 = tagZ2.getInt("colonyz" + i2);
                    this.colonyZ.add(i2, coordz2);
                    CompoundTag tagA2 = tagListA.getCompound(i2);
                    int age2 = tagA2.getInt("colonya" + i2);
                    this.colonyA.add(i2, age2);
                }
            }
        }
        if (compound.contains("srpcolonyresistances")) {
            tagListX = compound.getList("srpcolonyresistancei", 10);
            tagListY = compound.getList("srpcolonyresistances", 10);
            if (tagListX.size() != tagListY.size()) {
                SRPMain.logger.log(Level.ERROR, "Problem while reading resistance");
            } else {
                for (int i3 = 0; i3 < tagListX.size(); ++i3) {
                    CompoundTag tagX3 = tagListX.getCompound(i3);
                    int coordx3 = tagX3.getInt("resistance" + i3);
                    this.resistanceI.add(i3, coordx3);
                    CompoundTag tagY3 = tagListY.getCompound(i3);
                    String coordy3 = tagY3.getString("resistance" + i3);
                    this.resistanceS.add(i3, coordy3);
                }
            }
        }
    }

    public CompoundTag writeToNBT(CompoundTag compound) {
        CompoundTag tagT;
        CompoundTag tagA;
        CompoundTag tagZ;
        CompoundTag tagY;
        CompoundTag tagX;
        int type;
        int age;
        int coordz;
        int coordy;
        int coordx;
        int i;
        ListTag tagListT;
        ListTag tagListA;
        ListTag tagListZ;
        ListTag tagListY;
        ListTag tagListX;
        compound.putString("srpversion", "1.10.9");
        compound.putBoolean("srpmeteor", this.dimMeteor);
        if (this.nodeX.size() != this.nodeY.size() || this.nodeX.size() != this.nodeZ.size() || this.nodeX.size() != this.nodeA.size() || this.nodeX.size() != this.nodeT.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing nodes coords");
        } else {
            tagListX = new ListTag();
            tagListY = new ListTag();
            tagListZ = new ListTag();
            tagListA = new ListTag();
            tagListT = new ListTag();
            for (i = 0; i < this.nodeX.size(); ++i) {
                coordx = this.nodeX.get(i);
                coordy = this.nodeY.get(i);
                coordz = this.nodeZ.get(i);
                age = this.nodeA.get(i);
                type = this.nodeT.get(i).byteValue();
                tagX = new CompoundTag();
                tagX.putInt("nodex" + i, coordx);
                tagListX.add((Tag)tagX);
                tagY = new CompoundTag();
                tagY.putInt("nodey" + i, coordy);
                tagListY.add((Tag)tagY);
                tagZ = new CompoundTag();
                tagZ.putInt("nodez" + i, coordz);
                tagListZ.add((Tag)tagZ);
                tagA = new CompoundTag();
                tagA.putInt("nodea" + i, age);
                tagListA.add((Tag)tagA);
                tagT = new CompoundTag();
                tagT.putInt("nodet" + i, type);
                tagListT.add((Tag)tagT);
            }
            compound.put("srpnodescoordsx", (Tag)tagListX);
            compound.put("srpnodescoordsy", (Tag)tagListY);
            compound.put("srpnodescoordsz", (Tag)tagListZ);
            compound.put("srpnodesages", (Tag)tagListA);
            compound.put("srpnodestypes", (Tag)tagListT);
        }
        if (this.originX.size() != this.originY.size() || this.originX.size() != this.originZ.size() || this.originX.size() != this.originA.size() || this.originX.size() != this.originH.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing origins coords");
        } else {
            tagListX = new ListTag();
            tagListY = new ListTag();
            tagListZ = new ListTag();
            tagListA = new ListTag();
            tagListT = new ListTag();
            for (i = 0; i < this.originX.size(); ++i) {
                coordx = this.originX.get(i);
                coordy = this.originY.get(i);
                coordz = this.originZ.get(i);
                age = this.originA.get(i);
                type = this.originH.get(i);
                tagX = new CompoundTag();
                tagX.putInt("originx" + i, coordx);
                tagListX.add((Tag)tagX);
                tagY = new CompoundTag();
                tagY.putInt("originy" + i, coordy);
                tagListY.add((Tag)tagY);
                tagZ = new CompoundTag();
                tagZ.putInt("originz" + i, coordz);
                tagListZ.add((Tag)tagZ);
                tagA = new CompoundTag();
                tagA.putInt("origina" + i, age);
                tagListA.add((Tag)tagA);
                tagT = new CompoundTag();
                tagT.putInt("originh" + i, type);
                tagListT.add((Tag)tagT);
            }
            compound.put("srporiginscoordsx", (Tag)tagListX);
            compound.put("srporiginscoordsy", (Tag)tagListY);
            compound.put("srporiginscoordsz", (Tag)tagListZ);
            compound.put("srporiginsareas", (Tag)tagListA);
            compound.put("srporiginshealths", (Tag)tagListT);
        }
        if (this.colonyX.size() != this.colonyY.size() || this.colonyX.size() != this.colonyZ.size() || this.colonyX.size() != this.colonyA.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing colonies coords");
        } else {
            tagListX = new ListTag();
            tagListY = new ListTag();
            tagListZ = new ListTag();
            tagListA = new ListTag();
            for (int i2 = 0; i2 < this.colonyX.size(); ++i2) {
                int coordx2 = this.colonyX.get(i2);
                int coordy2 = this.colonyY.get(i2);
                int coordz2 = this.colonyZ.get(i2);
                int age2 = this.colonyA.get(i2);
                CompoundTag tagX2 = new CompoundTag();
                tagX2.putInt("colonyx" + i2, coordx2);
                tagListX.add((Tag)tagX2);
                CompoundTag tagY2 = new CompoundTag();
                tagY2.putInt("colonyy" + i2, coordy2);
                tagListY.add((Tag)tagY2);
                CompoundTag tagZ2 = new CompoundTag();
                tagZ2.putInt("colonyz" + i2, coordz2);
                tagListZ.add((Tag)tagZ2);
                CompoundTag tagA2 = new CompoundTag();
                tagA2.putInt("colonya" + i2, age2);
                tagListA.add((Tag)tagA2);
            }
            compound.put("srpcoloniescoordsx", (Tag)tagListX);
            compound.put("srpcoloniescoordsy", (Tag)tagListY);
            compound.put("srpcoloniescoordsz", (Tag)tagListZ);
            compound.put("srpcoloniesages", (Tag)tagListA);
        }
        if (this.resistanceI.size() != this.resistanceS.size()) {
            SRPMain.logger.log(Level.ERROR, "Problem while writing resistance");
        } else {
            tagListX = new ListTag();
            tagListY = new ListTag();
            for (int i3 = 0; i3 < this.resistanceI.size(); ++i3) {
                int coordx3 = this.resistanceI.get(i3);
                String coordy3 = this.resistanceS.get(i3);
                CompoundTag tagX3 = new CompoundTag();
                tagX3.putInt("resistance" + i3, coordx3);
                tagListX.add((Tag)tagX3);
                CompoundTag tagY3 = new CompoundTag();
                tagY3.putString("resistance" + i3, coordy3);
                tagListY.add((Tag)tagY3);
            }
            compound.put("srpcolonyresistancei", (Tag)tagListX);
            compound.put("srpcolonyresistances", (Tag)tagListY);
        }
        return compound;
    }

    public void setTriggerMet(boolean in) {
        this.dimMeteor = in;
    }

    public boolean getTriggerMet() {
        return this.dimMeteor;
    }

    private long getDistanceSQ(double rootx, double rooty, double rootz, double standingx, double standingy, double standingz) {
        double d0 = rootx - standingx;
        double d1 = rooty - standingy;
        double d2 = rootz - standingz;
        return (long)(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public void clearOriginList() {
        this.originX = new ArrayList();
        this.originY = new ArrayList();
        this.originZ = new ArrayList();
        this.originA = new ArrayList();
        this.originH = new ArrayList();
        this.markDirty();
    }

    public ArrayList<Integer> getorigins(String i) {
        if (i.equals("x")) {
            return this.originX;
        }
        if (i.equals("y")) {
            return this.originY;
        }
        if (i.equals("z")) {
            return this.originZ;
        }
        if (i.equals("a")) {
            return this.originA;
        }
        if (i.equals("h")) {
            return this.originH;
        }
        return null;
    }

    public int setOrigin(Level world, int x, int y, int z, int heatlh, int radius) {
        if (!this.canOriginBeMade(BlockPos.containing(x, y, z))) {
            return 6;
        }
        SRPSaveData data = SRPSaveData.get(world);
        if (this.originX.size() >= this.getOriginCap(world, data)) {
            return 7;
        }
        int canAdd = 1;
        for (int i = 0; i < this.originX.size(); ++i) {
            if (this.originX.get(i) != x || this.originY.get(i) != y || this.originZ.get(i) != z) continue;
            canAdd = 10;
        }
        if (canAdd == 1) {
            this.originX.add(x);
            this.originY.add(y);
            this.originZ.add(z);
            this.originA.add(radius);
            this.originH.add(heatlh);
            this.markDirty();
            this.setEIVHealthToData(world, data);
        }
        if (data.getEvolutionPhase(DimKeys.of(world)) == -1) {
            canAdd = 2;
        }
        return canAdd;
    }

    private int getOriginCap(Level in, SRPSaveData data) {
        if (!SRPConfigSystems.useEvolution) {
            return SRPConfigWorld.originCap;
        }
        switch (data.getEvolutionPhase(DimKeys.of(in))) {
            case -1: {
                return SRPConfigSystems.phaseOriginMinusOne;
            }
            case 0: {
                return SRPConfigSystems.phaseOriginZero;
            }
            case 1: {
                return SRPConfigSystems.phaseOriginOne;
            }
            case 2: {
                return SRPConfigSystems.phaseOriginTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseOriginThree;
            }
            case 4: {
                return SRPConfigSystems.phaseOriginFour;
            }
            case 5: {
                return SRPConfigSystems.phaseOriginFive;
            }
            case 6: {
                return SRPConfigSystems.phaseOriginSix;
            }
            case 7: {
                return SRPConfigSystems.phaseOriginSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseOriginEight;
            }
            case 9: {
                return SRPConfigSystems.phaseOriginNine;
            }
            case 10: {
                return SRPConfigSystems.phaseOriginTen;
            }
        }
        return 0;
    }

    public boolean canOriginBeMade(BlockPos pos) {
        int distance = SRPConfigWorld.originMinimumDistance * SRPConfigWorld.originMinimumDistance;
        for (int i = 0; i < this.originX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ <= (double)distance)) continue;
            return false;
        }
        return true;
    }

    public boolean removeOrigin(int x, int y, int z, Level worldIn) {
        for (int i = 0; i < this.originX.size(); ++i) {
            if (this.originX.get(i) != x || this.originY.get(i) != y || this.originZ.get(i) != z) continue;
            this.originX.remove(i);
            this.originY.remove(i);
            this.originZ.remove(i);
            this.originA.remove(i);
            this.originH.remove(i);
            this.markDirty();
            this.setEIVHealthToData(worldIn, SRPSaveData.get(worldIn));
            return true;
        }
        return false;
    }

    public int nearestInfectionValue(BlockPos pos, boolean health) {
        int heart = this.nearestInfectionIndex(pos);
        if (heart > -1) {
            if (health) {
                return this.originH.get(heart);
            }
            return this.originA.get(heart);
        }
        return heart;
    }

    public int nearestInfectionValueArea(BlockPos pos, boolean health) {
        int heart = this.nearestInfectionPositionIndex(true, pos);
        if (heart > -1) {
            if (health) {
                return this.originH.get(heart);
            }
            return this.originA.get(heart);
        }
        return heart;
    }

    public BlockPos nearestInfectionPosition(Boolean outside, BlockPos pos) {
        BlockPos heart = null;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            long distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            long ageDistance = this.originA.get(i).intValue();
            if (distanceSQ > (ageDistance *= ageDistance) && (!outside.booleanValue() || distance != -1.0)) continue;
            if (distance == -1.0) {
                heart = BlockPos.containing(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue());
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= (double)distanceSQ)) continue;
            heart = BlockPos.containing(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue());
            distance = distanceSQ;
        }
        return heart;
    }

    public int nearestInfectionPositionIndex(Boolean outside, BlockPos pos) {
        int heart = -1;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            long distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            long ageDistance = this.originA.get(i).intValue();
            if (distanceSQ > (ageDistance *= ageDistance) && (!outside.booleanValue() || distance != -1.0)) continue;
            if (distance == -1.0) {
                heart = i;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= (double)distanceSQ)) continue;
            heart = i;
            distance = distanceSQ;
        }
        return heart;
    }

    public int nearestInfectionIndex(BlockPos pos) {
        int heart = -1;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int ageDistance = this.originA.get(i);
            if (!(distanceSQ <= (double)(ageDistance *= ageDistance))) continue;
            if (distance == -1.0) {
                heart = i;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            heart = i;
            distance = distanceSQ;
        }
        return heart;
    }

    public void updateOriginValues(Level world, int updates, SRPSaveData data) {
        int dim = DimKeys.of(world);
        byte phase = data.getEvolutionPhase(dim);
        while (updates > 0) {
            for (int i = 0; i < this.originA.size(); ++i) {
                int size = this.originA.get(i);
                int health = this.originH.get(i);
                size = (int)((double)size + (double)size * (SRPConfigWorld.originDailySize + SRPWorldData.getPhaseSize(phase)));
                health = (int)((double)health + (double)size * (SRPConfigWorld.originDailyHealth + SRPWorldData.getPhaseHealth(phase)));
                this.originA.set(i, Math.min(size, SRPConfigWorld.originRadiusCap));
                this.originH.set(i, Math.min(health, SRPConfigWorld.originHealthCap));
            }
            --updates;
        }
        this.markDirty();
        this.setEIVHealthToData(world, data);
    }

    public void setEIVHealthToData(Level in, SRPSaveData data) {
        int health = 0;
        int area = 0;
        for (int i = 0; i < this.originH.size(); ++i) {
            health += this.originH.get(i).intValue();
            area += this.originA.get(i).intValue();
        }
        data.setEIVHealthArea(DimKeys.of(in), health, area);
    }

    public static double getPhaseSize(int in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseOriginBonusSizeOne;
            }
            case 2: {
                return SRPConfigSystems.phaseOriginBonusSizeTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseOriginBonusSizeThree;
            }
            case 4: {
                return SRPConfigSystems.phaseOriginBonusSizeFour;
            }
            case 5: {
                return SRPConfigSystems.phaseOriginBonusSizeFive;
            }
            case 6: {
                return SRPConfigSystems.phaseOriginBonusSizeSix;
            }
            case 7: {
                return SRPConfigSystems.phaseOriginBonusSizeSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseOriginBonusSizeEight;
            }
            case 9: {
                return SRPConfigSystems.phaseOriginBonusSizeNine;
            }
            case 10: {
                return SRPConfigSystems.phaseOriginBonusSizeTen;
            }
        }
        return 0.0;
    }

    public static double getPhaseHealth(int in) {
        switch (in) {
            case 1: {
                return SRPConfigSystems.phaseOriginBonusHealthOne;
            }
            case 2: {
                return SRPConfigSystems.phaseOriginBonusHealthTwo;
            }
            case 3: {
                return SRPConfigSystems.phaseOriginBonusHealthThree;
            }
            case 4: {
                return SRPConfigSystems.phaseOriginBonusHealthFour;
            }
            case 5: {
                return SRPConfigSystems.phaseOriginBonusHealthFive;
            }
            case 6: {
                return SRPConfigSystems.phaseOriginBonusHealthSix;
            }
            case 7: {
                return SRPConfigSystems.phaseOriginBonusHealthSeven;
            }
            case 8: {
                return SRPConfigSystems.phaseOriginBonusHealthEight;
            }
            case 9: {
                return SRPConfigSystems.phaseOriginBonusHealthNine;
            }
            case 10: {
                return SRPConfigSystems.phaseOriginBonusHealthTen;
            }
        }
        return 0.0;
    }

    public boolean setOriginHealth(Level worldIn, BlockPos pos, int amount, boolean plus) {
        int healthID = -1;
        double distance = -1.0;
        for (int i = 0; i < this.originX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.originX.get(i).intValue(), this.originY.get(i).intValue(), this.originZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int ageDistance = this.originA.get(i);
            if (!(distanceSQ <= (double)(ageDistance *= ageDistance))) continue;
            if (distance == -1.0) {
                healthID = i;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            healthID = i;
            distance = distanceSQ;
        }
        if (healthID != -1) {
            SRPSaveData data = SRPSaveData.get(worldIn);
            if (plus) {
                int current;
                if (data.getEvolutionPhase(DimKeys.of(worldIn)) == -1 && amount > 0) {
                    amount = (int)((double)amount * SRPConfigSystems.phaseOriginMinusOnePenalty);
                }
                if ((current = this.originH.get(healthID) + amount) <= 0) {
                    this.originX.remove(healthID);
                    this.originY.remove(healthID);
                    this.originZ.remove(healthID);
                    this.originA.remove(healthID);
                    this.originH.remove(healthID);
                } else {
                    this.originH.set(healthID, current);
                }
            } else if (amount <= 0) {
                this.originX.remove(healthID);
                this.originY.remove(healthID);
                this.originZ.remove(healthID);
                this.originA.remove(healthID);
                this.originH.remove(healthID);
            } else {
                this.originH.set(healthID, amount);
            }
            this.markDirty();
            this.setEIVHealthToData(worldIn, data);
            return true;
        }
        return false;
    }

    public void clearNodeList() {
        this.nodeX = new ArrayList();
        this.nodeY = new ArrayList();
        this.nodeZ = new ArrayList();
        this.nodeA = new ArrayList();
        this.nodeT = new ArrayList();
        this.markDirty();
    }

    public ArrayList<Integer> getNodes(String i) {
        if (i.equals("x")) {
            return this.nodeX;
        }
        if (i.equals("y")) {
            return this.nodeY;
        }
        if (i.equals("z")) {
            return this.nodeZ;
        }
        if (i.equals("a")) {
            return this.nodeA;
        }
        if (i.equals("t")) {
            ArrayList<Integer> eee = new ArrayList<Integer>();
            for (byte b : this.nodeT) {
                eee.add(Integer.valueOf(b));
            }
            return eee;
        }
        return null;
    }

    public int setNode(int x, int y, int z, int type) {
        if (!this.canNodeBeMade(BlockPos.containing(x, y, z))) {
            return 8;
        }
        if (this.nodeX.size() >= SRPConfigWorld.maximumNumberNodes) {
            return 9;
        }
        int canAdd = 1;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            if (this.nodeX.get(i) != x || this.nodeY.get(i) != y || this.nodeZ.get(i) != z) continue;
            if (this.nodeA.get(i) != 1) {
                this.removeNode(x, y, z);
                this.setNode(x, y, z, type);
            }
            canAdd = 10;
        }
        if (canAdd == 1) {
            this.nodeX.add(x);
            this.nodeY.add(y);
            this.nodeZ.add(z);
            this.nodeA.add(1);
            this.nodeT.add((byte)type);
            this.markDirty();
        }
        return canAdd;
    }

    public boolean canNodeBeMade(BlockPos pos) {
        int distance = SRPConfigWorld.minimumDistanceBetweenNodes * SRPConfigWorld.minimumDistanceBetweenNodes;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ <= (double)distance)) continue;
            return false;
        }
        return true;
    }

    public boolean removeNode(int x, int y, int z) {
        for (int i = 0; i < this.nodeX.size(); ++i) {
            if (this.nodeX.get(i) != x || this.nodeY.get(i) != y || this.nodeZ.get(i) != z) continue;
            this.nodeX.remove(i);
            this.nodeY.remove(i);
            this.nodeZ.remove(i);
            this.nodeA.remove(i);
            this.nodeT.remove(i);
            this.markDirty();
            return true;
        }
        return false;
    }

    public int isInRangeOfHeart(BlockPos pos, double distance) {
        distance += 1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ < distance * distance)) continue;
            return (int)distanceSQ;
        }
        return -1;
    }

    public int nearestHeartAge(BlockPos pos, boolean spread, int currentDay) {
        int heart = -1;
        double distance = -1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int ageDistance;
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int currentage = ageDistance = this.convertDayToAgeNode(this.nodeA.get(i));
            ageDistance = spread ? this.getDistanceSpreadByAge(ageDistance, true) : this.getDistanceEffectByAge(ageDistance, true);
            if (!(distanceSQ <= (double)ageDistance)) continue;
            if (distance == -1.0) {
                heart = currentage;
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            heart = currentage;
            distance = distanceSQ;
        }
        return heart;
    }

    public int nearestHeartType(BlockPos pos, boolean spread, int currentDay) {
        int type = -1;
        double distance = -1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int ageDistance;
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int currentage = ageDistance = this.convertDayToAgeNode(this.nodeA.get(i));
            ageDistance = spread ? this.getDistanceSpreadByAge(ageDistance, true) : this.getDistanceEffectByAge(ageDistance, true);
            if (!(distanceSQ <= (double)ageDistance)) continue;
            if (distance == -1.0) {
                type = this.nodeT.get(i).byteValue();
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            type = this.nodeT.get(i).byteValue();
            distance = distanceSQ;
        }
        return type;
    }

    public BlockPos nearestHeartAgePosition(BlockPos pos, int currentDay) {
        BlockPos heart = null;
        double distance = -1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int ageDistance;
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int currentage = ageDistance = this.convertDayToAgeNode(this.nodeA.get(i));
            if (!(distanceSQ <= (double)(ageDistance = this.getDistanceEffectByAge(ageDistance, true)))) continue;
            if (distance == -1.0) {
                heart = BlockPos.containing(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            heart = BlockPos.containing(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
            distance = distanceSQ;
        }
        return heart;
    }

    private int convertDayToAgeNode(int nodeBirthday) {
        int age = nodeBirthday;
        if (age >= SRPConfigWorld.timeNeedeToNodeThree) {
            return 3;
        }
        if (age >= SRPConfigWorld.timeNeedeToNodeTwo) {
            return 2;
        }
        return 1;
    }

    public int getDistanceSpreadByAge(int age, boolean sq) {
        switch (age) {
            case 1: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeSpreadOne * SRPConfigWorld.nodeRangeSpreadOne;
                }
                return SRPConfigWorld.nodeRangeSpreadOne;
            }
            case 2: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeSpreadTwo * SRPConfigWorld.nodeRangeSpreadTwo;
                }
                return SRPConfigWorld.nodeRangeSpreadTwo;
            }
            case 3: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeSpreadThree * SRPConfigWorld.nodeRangeSpreadThree;
                }
                return SRPConfigWorld.nodeRangeSpreadThree;
            }
        }
        return -1;
    }

    public int getDistanceEffectByAge(int age, boolean sq) {
        switch (age) {
            case 1: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeEffectsOne * SRPConfigWorld.nodeRangeEffectsOne;
                }
                return SRPConfigWorld.nodeRangeEffectsOne;
            }
            case 2: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeEffectsTwo * SRPConfigWorld.nodeRangeEffectsTwo;
                }
                return SRPConfigWorld.nodeRangeEffectsTwo;
            }
            case 3: {
                if (sq) {
                    return SRPConfigWorld.nodeRangeEffectsThree * SRPConfigWorld.nodeRangeEffectsThree;
                }
                return SRPConfigWorld.nodeRangeEffectsThree;
            }
        }
        return -1;
    }

    public void checkHeartExistance(Level worldIn) {
        for (int i = this.nodeX.size() - 1; i >= 0; --i) {
            BlockState state;
            BlockPos pos = BlockPos.containing(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
            if (!worldIn.isAreaLoaded(pos, 3) || (state = worldIn.getBlockState(pos)).getBlock() == SRPBlocks.BiomeHeart.get() && state.getPropertyNames().contains(BlockBiomeCore.ACTIVE) && (Integer)state.getValue((Property)BlockBiomeCore.ACTIVE) > 0) continue;
            this.nodeX.remove(i);
            this.nodeY.remove(i);
            this.nodeZ.remove(i);
            this.nodeA.remove(i);
            this.nodeT.remove(i);
            this.markDirty();
        }
    }

    public int getHeartPocition(BlockPos pos, int currentDay) {
        for (int i = 0; i < this.nodeX.size(); ++i) {
            if (this.nodeX.get(i).intValue() != pos.getX() || this.nodeY.get(i).intValue() != pos.getY() || this.nodeZ.get(i).intValue() != pos.getZ()) continue;
            return this.convertDayToAgeNode(this.nodeA.get(i));
        }
        return -1;
    }

    public int totalNodePoints(int currentDay) {
        int points = 0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            int age = this.convertDayToAgeNode(this.nodeA.get(i));
            points += age;
        }
        return points;
    }

    public void clearColonyList() {
        this.colonyX = new ArrayList();
        this.colonyY = new ArrayList();
        this.colonyZ = new ArrayList();
        this.colonyA = new ArrayList();
        this.markDirty();
    }

    public ArrayList<Integer> getColonies(String i) {
        if (i.equals("x")) {
            return this.colonyX;
        }
        if (i.equals("y")) {
            return this.colonyY;
        }
        if (i.equals("z")) {
            return this.colonyZ;
        }
        if (i.equals("a")) {
            return this.colonyA;
        }
        return null;
    }

    public int setColony(int x, int y, int z) {
        if (!this.canColonyBeMade(BlockPos.containing(x, y, z))) {
            return 6;
        }
        if (this.colonyX.size() >= SRPConfigWorld.maximumNumberColonies) {
            return 7;
        }
        int canAdd = 1;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            if (this.colonyX.get(i) != x || this.colonyY.get(i) != y || this.colonyZ.get(i) != z) continue;
            if (this.colonyA.get(i) != 1) {
                this.removeColony(x, y, z);
                this.setColony(x, y, z);
            }
            canAdd = 10;
        }
        if (canAdd == 1) {
            this.colonyX.add(x);
            this.colonyY.add(y);
            this.colonyZ.add(z);
            this.colonyA.add(1);
            this.markDirty();
        }
        return canAdd;
    }

    public boolean canColonyBeMade(BlockPos pos) {
        int distance = SRPConfigWorld.minimumDistanceBetweenColonies * SRPConfigWorld.minimumDistanceBetweenColonies;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ <= (double)distance)) continue;
            return false;
        }
        return true;
    }

    public boolean removeColony(int x, int y, int z) {
        for (int i = 0; i < this.colonyX.size(); ++i) {
            if (this.colonyX.get(i) != x || this.colonyY.get(i) != y || this.colonyZ.get(i) != z) continue;
            this.colonyX.remove(i);
            this.colonyY.remove(i);
            this.colonyZ.remove(i);
            this.colonyA.remove(i);
            this.markDirty();
            return true;
        }
        return false;
    }

    public BlockPos nearestColonyPosition(BlockPos pos, boolean effect) {
        BlockPos posColony = null;
        double distance = -1.0;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            int ageDistance = this.convertDayToPointsColony(this.colonyA.get(i));
            if (!(distanceSQ <= (double)(ageDistance = this.getColonyDistanceSpreadByPoints(ageDistance, true, effect)))) continue;
            if (distance == -1.0) {
                posColony = BlockPos.containing(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue());
                distance = distanceSQ;
                continue;
            }
            if (!(distance <= distanceSQ)) continue;
            posColony = BlockPos.containing(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue());
            distance = distanceSQ;
        }
        return posColony;
    }

    private int convertDayToPointsColony(int nodeBirthday) {
        int age = nodeBirthday;
        return Math.min(age, SRPConfigWorld.colonyPointCap);
    }

    public int colonunumber() {
        return this.colonyA.size();
    }

    public int getColonyDistanceSpreadByPoints(int points, boolean sq, boolean effect) {
        if (effect) {
            int baseRange = SRPConfigWorld.colonyBaseEffectRadiusValue;
            int a = points / SRPConfigWorld.colonySpreadEffectPoint;
            int b = a * SRPConfigWorld.colonySpreadEffectValue + baseRange;
            if (sq) {
                return b * b;
            }
            return b;
        }
        int baseRange = SRPConfigWorld.colonyBaseRadiusValue;
        int a = points / SRPConfigWorld.colonySpreadPoint;
        int b = a * SRPConfigWorld.colonySpreadValue + baseRange;
        if (sq) {
            return b * b;
        }
        return b;
    }

    public int getColonyDistanceSpreadByPosition(BlockPos pos, boolean sq) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        for (int i = 0; i < this.colonyX.size(); ++i) {
            if (this.colonyX.get(i) != x || this.colonyY.get(i) != y || this.colonyZ.get(i) != z) continue;
            int ageDistance = this.convertDayToPointsColony(this.colonyA.get(i));
            ageDistance = this.getColonyDistanceSpreadByPoints(ageDistance, sq, false);
            return ageDistance;
        }
        return 0;
    }

    public BlockPos isInRangeOfColony(BlockPos pos, double distance) {
        distance += 1.0;
        for (int i = 0; i < this.nodeX.size(); ++i) {
            double distanceSQ = this.getDistanceSQ(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue(), pos.getX(), pos.getY(), pos.getZ());
            if (!(distanceSQ < distance * distance)) continue;
            return BlockPos.containing(this.nodeX.get(i).intValue(), this.nodeY.get(i).intValue(), this.nodeZ.get(i).intValue());
        }
        return null;
    }

    public int totalColonyPoints(int currentDay) {
        int points = 0;
        for (int i = 0; i < this.colonyX.size(); ++i) {
            int age = this.convertDayToPointsColony(this.colonyA.get(i));
            points += age;
        }
        return Math.min(points, SRPConfigWorld.colonyTotalPointCap);
    }

    public void checkColonyExistance(Level worldIn) {
        for (int i = this.colonyX.size() - 1; i >= 0; --i) {
            BlockState state;
            BlockPos pos = BlockPos.containing(this.colonyX.get(i).intValue(), this.colonyY.get(i).intValue(), this.colonyZ.get(i).intValue());
            if (!worldIn.isAreaLoaded(pos, 3) || ((state = worldIn.getBlockState(pos)).getBlock() == SRPBlocks.ColonyHeart.get() || state.getBlock() == SRPBlocks.ColonyOutpost.get()) && state.getPropertyNames().contains(BlockColonyCore.ACTIVE) && (Integer)state.getValue((Property)BlockColonyCore.ACTIVE) > 0) continue;
            this.colonyX.remove(i);
            this.colonyY.remove(i);
            this.colonyZ.remove(i);
            this.colonyA.remove(i);
            this.markDirty();
        }
    }

    public void addGlobalResistance(String damage) {
        boolean flag = true;
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            if (!this.resistanceS.get(i).equals(damage)) continue;
            int iiii = this.resistanceI.get(i) + 1;
            this.resistanceI.set(i, iiii);
            flag = false;
            break;
        }
        if (flag) {
            this.resistanceS.add(damage);
            this.resistanceI.add(1);
        }
        this.markDirty();
    }

    public String getMostCommonDamageS() {
        ArrayList<String> resistanceAAAS = this.resistanceS;
        ArrayList<Integer> resistanceAAAI = this.resistanceI;
        String atm = null;
        int times = 0;
        for (int i = 0; i < resistanceAAAI.size(); ++i) {
            if (resistanceAAAI.get(i) <= times) continue;
            times = resistanceAAAI.get(i);
            atm = resistanceAAAS.get(i);
        }
        return atm;
    }

    public int getMostCommonDamageI() {
        ArrayList<Integer> resistanceAAAI = this.resistanceI;
        int times = 0;
        for (int i = 0; i < resistanceAAAI.size(); ++i) {
            if (resistanceAAAI.get(i) <= times) continue;
            times = resistanceAAAI.get(i);
        }
        return times;
    }

    public void resetGlobalAdaptation() {
        this.resistanceI = new ArrayList();
        this.resistanceS = new ArrayList();
        this.markDirty();
    }

    public ArrayList<String> getAdaptationS() {
        return this.resistanceS;
    }

    public ArrayList<Integer> getAdaptationI() {
        return this.resistanceI;
    }

    public void updateDays(Level world) {
        int atm;
        int i;
        SRPSaveData data = SRPSaveData.get(world);
        int count = data.getUpdateNumber(DimKeys.of(world));
        if (count == 0) {
            return;
        }
        for (i = 0; i < this.nodeA.size(); ++i) {
            atm = this.nodeA.get(i) + count;
            this.nodeA.set(i, atm);
        }
        for (i = 0; i < this.colonyA.size(); ++i) {
            atm = this.colonyA.get(i) + count;
            this.colonyA.set(i, atm);
        }
        this.markDirty();
        this.updateOriginValues(world, count, data);
    }
}

