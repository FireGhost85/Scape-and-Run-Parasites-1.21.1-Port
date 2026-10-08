package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteNexusProtection1;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class EntityPDispatcher
extends EntityPStationaryArchitect {
    protected ArrayList<String> mobNameId = new ArrayList();
    protected int crude;
    protected int inf;
    protected int mangler;
    protected int flesh;

    public EntityPDispatcher(EntityType<? extends EntityPDispatcher> type, Level worldIn) {
        super(type, worldIn);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.setScentHPMultiplier(0.5f);
    }

    public boolean storeLodo(EntityParasiteBase in, boolean upgrade) {
        if (in.getParasiteIDRegister() == 5 && upgrade) {
            this.mobNameId.add("srparasites:rupter");
            in.particleStatus((byte)7);
            in.discard();
            return true;
        }
        if (in.getParasiteIDRegister() == 23 && upgrade) {
            in.particleStatus((byte)7);
            in.discard();
            ++this.flesh;
            if (this.flesh >= 2) {
                this.flesh -= 2;
                this.addPrim();
                return true;
            }
            return true;
        }
        return false;
    }

    protected boolean storeInf(EntityParasiteBase in, boolean upgrade) {
        if ((in instanceof EntityPInfected || in.getParasiteIDRegister() == 23) && upgrade) {
            ++this.inf;
            this.mobNameId.add(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(in.getType())).toString());
            in.particleStatus((byte)7);
            in.discard();
            if (this.inf >= 4 && this.mobNameId != null) {
                this.inf -= 4;
                int current = 0;
                for (int i = 0; i < this.mobNameId.size(); ++i) {
                    if (this.mobNameId.get(i) == null) {
                        this.mobNameId.remove(i);
                        --i;
                        continue;
                    }
                    if (!this.mobNameId.get(i).contains("sim_")) continue;
                    this.mobNameId.remove(i);
                    i = -1;
                    if (++current < 4) continue;
                    this.addPrim();
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean storeCrude(EntityParasiteBase in, boolean upgrade) {
        if ((in.getParasiteIDRegister() == 43 || in.getParasiteIDRegister() == 39) && upgrade) {
            this.crude = in.getParasiteIDRegister() == 39 ? ++this.crude : (this.crude += 2);
            this.mobNameId.add(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(in.getType())).toString());
            in.particleStatus((byte)7);
            in.discard();
            if (this.crude >= 6 && this.mobNameId != null) {
                this.crude -= 6;
                int current = 0;
                for (int i = 0; i < this.mobNameId.size(); ++i) {
                    if (this.mobNameId.get(i) == null) {
                        this.mobNameId.remove(i);
                        --i;
                        continue;
                    }
                    if (!this.mobNameId.get(i).contains("incompleteform")) continue;
                    this.mobNameId.remove(i);
                    i = -1;
                    if (++current < 6) continue;
                    this.mobNameId.add("srparasites:heed");
                    break;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean storeMudo(EntityParasiteBase in, boolean upgrade) {
        if (in.getParasiteIDRegister() == 12 && upgrade) {
            this.mobNameId.add("srparasites:mangler");
            in.particleStatus((byte)7);
            in.discard();
            return true;
        }
        return false;
    }

    protected boolean storeMangler(EntityParasiteBase in, boolean upgrade) {
        if (in.getParasiteIDRegister() == 76 && upgrade) {
            ++this.mangler;
            this.mobNameId.add(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(in.getType())).toString());
            in.particleStatus((byte)7);
            in.discard();
            return true;
        }
        return false;
    }

    protected boolean storeAll(EntityParasiteBase in) {
        this.mobNameId.add(Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(in.getType())).toString());
        in.particleStatus((byte)7);
        in.discard();
        return true;
    }

    public boolean storeParasite(EntityParasiteBase in) {
        if (!in.isAlive()) {
            return true;
        }
        if (in.getParasiteIDRegister() == 36) {
            return true;
        }
        float bHard = 0.0f;
        BlockState state2 = this.level().getBlockState(this.blockPosition().below());
        float atm = state2.getDestroySpeed(this.level(), this.blockPosition().below());
        if (atm <= 0.0f) {
            return true;
        }
        bHard += atm;
        state2 = this.level().getBlockState(this.blockPosition().below(2));
        atm = state2.getDestroySpeed(this.level(), this.blockPosition().below(2));
        if (atm <= 0.0f) {
            return true;
        }
        bHard += atm;
        state2 = this.level().getBlockState(this.blockPosition().below(3));
        atm = state2.getDestroySpeed(this.level(), this.blockPosition().below(3));
        if (atm <= 0.0f) {
            return true;
        }
        return (bHard += atm) >= 10.0f;
    }

    private void addPrim() {
        int prim = this.getRandom().nextInt(7);
        boolean canAdd = true;
        for (int safe = 0; canAdd && safe < 10; ++safe) {
            switch (prim) {
                case 0: {
                    if (!SRPConfigMobs.emanaEnabled) break;
                    this.mobNameId.add("srparasites:pri_yelloweye");
                    canAdd = false;
                    break;
                }
                case 1: {
                    if (!SRPConfigMobs.canraEnabled) break;
                    this.mobNameId.add("srparasites:pri_summoner");
                    canAdd = false;
                    break;
                }
                case 2: {
                    if (!SRPConfigMobs.zetmoEnabled) break;
                    this.mobNameId.add("srparasites:pri_bolster");
                    canAdd = false;
                    break;
                }
                case 3: {
                    if (!SRPConfigMobs.shycoEnabled) break;
                    this.mobNameId.add("srparasites:pri_longarms");
                    canAdd = false;
                    break;
                }
                case 4: {
                    if (!SRPConfigMobs.arachnidaEnabled) break;
                    this.mobNameId.add("srparasites:pri_arachnida");
                    canAdd = false;
                    break;
                }
                case 5: {
                    if (!SRPConfigMobs.noglaEnabled) break;
                    this.mobNameId.add("srparasites:pri_reeker");
                    canAdd = false;
                    break;
                }
                case 6: {
                    if (!SRPConfigMobs.hullEnabled) break;
                    this.mobNameId.add("srparasites:pri_manducater");
                    canAdd = false;
                }
            }
            if (++prim < 7) continue;
            prim = 0;
        }
    }

    public String getParasiteStored() {
        if (this.mobNameId.isEmpty()) {
            return null;
        }
        int j = this.getRandom().nextInt(this.mobNameId.size());
        String name = this.mobNameId.get(j);
        if (name == null) {
            return null;
        }
        if (name.contains("sim_")) {
            --this.inf;
        } else if (name.contains("incompleteform_small")) {
            --this.crude;
        } else if (name.contains("incompleteform_medium")) {
            this.crude -= 2;
        } else if (name.contains("mangler")) {
            --this.mangler;
        }
        this.mobNameId.remove(j);
        return name;
    }

    public void addParaBack(String id) {
        this.mobNameId.add(id);
    }

    public ArrayList<String> getMobList() {
        return this.mobNameId;
    }

    @Override
    public void generateStructure() {
        if (SRPConfig.nexusStructures) {
            WorldGenParasiteNexusProtection1 p1 = new WorldGenParasiteNexusProtection1(false, 1);
            p1.generate(this.level(), RandomSource.create(), this.blockPosition().below());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("dodcrude", this.crude);
        compound.putInt("dodinf", this.inf);
        compound.putInt("dodflesh", this.flesh);
        ListTag allResS = new ListTag();
        for (int i = 0; i < this.mobNameId.size(); ++i) {
            String res = this.mobNameId.get(i);
            if (res == null) continue;
            CompoundTag resT = new CompoundTag();
            resT.putString("mobs" + i, res);
            allResS.add((Tag)resT);
        }
        compound.put("dodmobs", (Tag)allResS);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("dodcrude", 99)) {
            this.crude = compound.getInt("dodcrude");
        }
        if (compound.contains("dodinf", 99)) {
            this.inf = compound.getInt("dodinf");
        }
        if (compound.contains("dodflesh", 99)) {
            this.flesh = compound.getInt("dodflesh");
        }
        if (compound.contains("dodmobs")) {
            ListTag allResS = compound.getList("dodmobs", 10);
            for (int i = 0; i < allResS.size(); ++i) {
                CompoundTag resT = allResS.getCompound(i);
                String res = resT.getString("mobs" + i);
                this.mobNameId.add(i, res);
            }
        }
    }
}

