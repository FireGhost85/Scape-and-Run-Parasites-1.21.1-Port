package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** Wooden button with the click volume 0.3 and the pitches 0.6 (pressed) and 0.5 (released) of the original. */
public class BlockSRPButton extends ButtonBlock {
    public BlockSRPButton() {
        super(BlockSetType.OAK, 30, SRPMaterial.WOOD.props(0.5f).sound(SoundType.WOOD).noCollission().pushReaction(net.minecraft.world.level.material.PushReaction.DESTROY));
    }

    @Override
    protected void playSound(Player player, LevelAccessor level, BlockPos pos, boolean hitOn) {
        if (hitOn) {
            level.playSound(player, pos, SoundEvents.WOODEN_BUTTON_CLICK_ON, SoundSource.BLOCKS, 0.3f, 0.6f);
        } else {
            level.playSound(null, pos, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, 0.3f, 0.5f);
        }
    }
}
