package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Crafting table whose menu stays valid as long as the block is any crafting table (1.12
 * {@code ContainerConsumedWorkbench.canInteractWith}), without a distance check.
 */
public class BlockSRPWorkbench extends CraftingTableBlock {
    public BlockSRPWorkbench(float hardness, float resistance) {
        super(SRPMaterial.WOOD.props(hardness, resistance).sound(SoundType.WOOD));
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((id, inventory, player) -> new CraftingMenu(id, inventory, ContainerLevelAccess.create(level, pos)) {
            @Override
            public boolean stillValid(Player player) {
                return level.getBlockState(pos).getBlock() instanceof CraftingTableBlock;
            }
        }, Component.translatable("container.crafting"));
    }
}
