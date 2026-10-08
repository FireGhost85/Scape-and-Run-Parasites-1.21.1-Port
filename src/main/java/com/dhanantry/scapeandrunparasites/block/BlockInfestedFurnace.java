package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityInfestedFurnace;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Infested furnace: a furnace with the model and name of the mod. Hardness 3.5, stone sounds, light level 14 while lit.
 * Facing is the opposite of the placer's, like the 1.12 block.
 */
public class BlockInfestedFurnace extends AbstractFurnaceBlock {
    public static final MapCodec<BlockInfestedFurnace> CODEC = simpleCodec(p -> new BlockInfestedFurnace());

    public BlockInfestedFurnace() {
        super(SRPMaterial.ROCK.props(3.5f).sound(SoundType.STONE).lightLevel(state -> state.getValue(LIT) ? 14 : 0));
    }

    @Override
    protected MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityInfestedFurnace(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createFurnaceTicker(level, type, SRPBlockEntities.INFESTED_FURNACE.get());
    }

    @Override
    protected void openContainer(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof TileEntityInfestedFurnace furnace) {
            player.openMenu(furnace);
        }
    }
}
