package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Thornshade berry: plants a thornshade on top of a block, or can be eaten (2 food, 4 magic damage). */
public class ItemThornshadeBerry extends ItemBase {
    public ItemThornshadeBerry() {
        super("thornshade_berry", 64, 30);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        if (context.getClickedFace() != Direction.UP || player == null) {
            return InteractionResult.FAIL;
        }
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);
        BlockPos placePos = clickedState.canBeReplaced() ? pos : pos.above();
        if (!player.mayUseItemAt(placePos, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }
        Block thornshade = SRPBlocks.Thornshade.get();
        BlockPlaceContext placeContext = new BlockPlaceContext(context);
        BlockState placeState = thornshade.getStateForPlacement(placeContext);
        if (placeState == null || !placeState.canSurvive(level, placePos)) {
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide) {
            if (!level.setBlock(placePos, placeState, 11)) {
                return InteractionResult.FAIL;
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            level.playSound(null, placePos, SRPSounds.BLOCKINFEST_BREAK.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 10;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.canEat(true)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!level.isClientSide) {
                player.getFoodData().eat(2, 0.1f);
                player.hurt(player.damageSources().magic(), 4.0f);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_HURT, player.getSoundSource(), 1.0f, 1.0f);
            player.playSound(SRPSounds.FLESH_GROW.get(), 10.0f, 1.0f);
        }
        return stack;
    }
}
