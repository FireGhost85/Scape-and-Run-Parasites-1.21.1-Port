package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPDispatcher;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Tile entity of the Dod nexus block: collects the kill count of nearby parasites and spawns a Dod. */
public class TileEntityDod extends BlockEntity {
    private int ticksSinceSync;
    private int killC;

    public TileEntityDod(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.DOD.get(), pos, state);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        if (compound.contains("parasitekillc", 3)) {
            this.killC = compound.getInt("parasitekillc");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putInt("parasitekillc", this.killC);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileEntityDod te) {
        te.update();
    }

    public void update() {
        Level level = this.level;
        if (level == null || level.isClientSide) {
            return;
        }
        BlockPos pos = this.worldPosition;
        if (level.getBlockState(pos).getBlock() == Blocks.AIR) {
            this.killSelfTile();
            return;
        }
        ++this.ticksSinceSync;
        if (this.ticksSinceSync % 20 != 0) {
            return;
        }
        double coll = 0.0;
        AABB axisalignedbb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(16.0);
        List<EntityParasiteBase> moblist = level.getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (EntityParasiteBase mob : moblist) {
            if (!(mob.getKillC() > 1.0)) {
                continue;
            }
            coll += mob.getKillC();
            mob.setKillC(0.0);
        }
        if (coll != 0.0) {
            this.killC += (int) coll;
        }
        if (this.killC > SRPConfig.reinforcerNidusValue) {
            int ccc = 0;
            axisalignedbb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(SRPConfig.turretFollow);
            List<EntityPStationaryArchitect> architects = level.getEntitiesOfClass(EntityPStationaryArchitect.class, axisalignedbb);
            for (EntityParasiteBase mob : architects) {
                if (!(mob instanceof EntityDod) && !(mob instanceof EntityDodSII) && !(mob instanceof EntityDodSIII) && !(mob instanceof EntityDodSIV)) {
                    continue;
                }
                ++ccc;
            }
            if (ccc == 0) {
                int count = 0;
                for (Entity entity : this.allEntities(level)) {
                    if (!(entity instanceof EntityPDispatcher) || ++count <= SRPConfig.nexusDodCap && !(this.getDistanceSq(entity) < (double) (SRPConfig.nexusDodDis * SRPConfig.nexusDodDis))) {
                        continue;
                    }
                    if (SRPConfigSystems.useEvolution) {
                        SRPSaveData data = SRPSaveData.get(level);
                        data.setTotalKills(DimKeys.of(level), this.killC * SRPConfig.reinforcerNidusMult, true, level, true, 45);
                    }
                    return;
                }
                if (SRPConfigSystems.useEvolution) {
                    SRPSaveData data = SRPSaveData.get(level);
                    if (ParasiteEventEntity.getRSchance(level) == 0.0) {
                        data.setTotalKills(DimKeys.of(level), this.killC * SRPConfig.reinforcerNidusMult, true, level, true, 46);
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        this.killSelfTile();
                        return;
                    }
                }
                EntityDod dod = SRPEntities.DISPATCHER_SI.get().create(level);
                dod.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                if (!level.noCollision(dod)) {
                    dod.discard();
                } else {
                    level.addFreshEntity(dod);
                    dod.particleStatus((byte) 7);
                    if (SRPConfigSystems.rsSounds) {
                        if (SRPConfigSystems.disloGrowlNoise) {
                            if (SRPSaveData.get(level).getCurrentCode(DimKeys.of(level), 15) == 0) {
                                dod.playSound(SRPSounds.DODSI.get(), 4.0f, 1.0f);
                            }
                        } else {
                            dod.playSound(SRPSounds.DODSI.get(), 4.0f, 1.0f);
                        }
                    }
                }
            }
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            this.killSelfTile();
        }
    }

    private Iterable<Entity> allEntities(Level level) {
        if (level instanceof net.minecraft.server.level.ServerLevel server) {
            return server.getAllEntities();
        }
        return List.of();
    }

    private void killSelfTile() {
        if (this.level == null) {
            return;
        }
        this.setRemoved();
        this.level.removeBlockEntity(this.worldPosition);
    }

    /** Squared distance of the block corner to the entity position (the original did not use block centres). */
    public double getDistanceSq(Entity entityIn) {
        double d0 = this.worldPosition.getX() - entityIn.getX();
        double d1 = this.worldPosition.getY() - entityIn.getY();
        double d2 = this.worldPosition.getZ() - entityIn.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
}
