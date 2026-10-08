package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Remaining uses of a fog nullifier. */
public class TileEntityFogNullifier extends BlockEntity {
    private int usesRemaining = 0;

    public TileEntityFogNullifier(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.FOG_NULLIFIER.get(), pos, state);
    }

    public int getUsesRemaining() {
        return this.usesRemaining;
    }

    public void setUsesRemaining(int uses) {
        this.usesRemaining = Math.max(0, uses);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.saveAdditional(compound, registries);
        compound.putInt("UsesRemaining", this.usesRemaining);
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider registries) {
        super.loadAdditional(compound, registries);
        this.usesRemaining = compound.getInt("UsesRemaining");
    }
}
