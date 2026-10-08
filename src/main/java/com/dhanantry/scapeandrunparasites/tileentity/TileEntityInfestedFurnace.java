package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity of the infested furnace: a plain furnace (3 slots, smelting recipes, the vanilla slot faces and stack limit)
 * under the name {@code tile.srparasites.infested_furnace.name}. The 1.12 class re-implemented the furnace with a fixed cook
 * time of 200 ticks; the recipe cooking time (200 for smelting recipes) is used now.
 */
public class TileEntityInfestedFurnace extends AbstractFurnaceBlockEntity {
    public TileEntityInfestedFurnace(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.INFESTED_FURNACE.get(), pos, state, RecipeType.SMELTING);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.srparasites.infested_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new FurnaceMenu(id, inventory, this, this.dataAccess);
    }
}
