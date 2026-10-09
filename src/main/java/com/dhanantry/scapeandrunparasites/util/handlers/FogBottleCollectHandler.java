package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class FogBottleCollectHandler {
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        this.tryCollectFog(event.getLevel(), event.getEntity(), event.getHand(), (PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        this.tryCollectFog(event.getLevel(), event.getEntity(), event.getHand(), (PlayerInteractEvent)event);
    }

    private void tryCollectFog(Level world, Player player, InteractionHand hand, PlayerInteractEvent event) {
        ItemStack fogBottle;
        if (world == null || player == null || hand == null) {
            return;
        }
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || held.getItem() != Items.GLASS_BOTTLE) {
            return;
        }
        BlockPos fogPos = this.findFogInLook(world, player);
        if (fogPos == null) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (world.isClientSide) {
            return;
        }
        if (!player.getAbilities().instabuild) {
            held.shrink(1);
        }
        if (!player.getInventory().addItemStackToInventory(fogBottle = new ItemStack(SRPItems.FOG_BOTTLE.get()))) {
            player.dropPlayerItemWithRandomChoice(fogBottle, false);
        }
        world.playSound(null, fogPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
        world.setBlock(fogPos, Blocks.AIR.defaultBlockState(), 3);
    }

    private BlockPos findFogInLook(Level world, Player player) {
        double reach = 5.0;
        if (player instanceof ServerPlayer) {
            reach = ((ServerPlayer)player).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 eyes = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        BlockPos lastPos = null;
        for (double d = 0.0; d <= reach; d += 0.15) {
            Vec3 point = eyes.add(look.x * d, look.y * d, look.z * d);
            BlockPos pos = BlockPos.containing(point.x, point.y, point.z);
            if (pos.equals(lastPos)) continue;
            lastPos = pos;
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() != SRPBlocks.ParasiteFog.get()) continue;
            return pos;
        }
        return null;
    }
}

