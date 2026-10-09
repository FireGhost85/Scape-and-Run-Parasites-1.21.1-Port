package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class DeadBloodBottleHandler {
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (e.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = e.getEntityPlayer();
        Level world = e.getLevel();
        ItemStack held = e.getItemStack();
        if (held.isEmpty() || held.getItem() != Items.GLASS_BOTTLE) {
            return;
        }
        HitResult rt = DeadBloodBottleHandler.rayTrace(world, player, true);
        if (rt == null || rt.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = rt.getBlockPos();
        if (((BlockFluidBase)SRPFluids.DEADBLOOD_FLUID.getBlock()).getQuantaValue((BlockGetter)world, pos) <= 0) {
            return;
        }
        if (!world.isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            ItemStack filled = new ItemStack(SRPItems.DEADBLOOD_FLUID.get());
            if (held.isEmpty()) {
                player.setHeldItem(InteractionHand.MAIN_HAND, filled);
            } else if (!player.getInventory().addItemStackToInventory(filled)) {
                player.dropPlayerItemWithRandomChoice(filled, false);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.9f, 1.0f);
        }
        e.setCanceled(true);
        e.setCancellationResult(EnumActionResult.SUCCESS);
    }

    private static HitResult rayTrace(Level world, Player player, boolean useLiquids) {
        float pitch = player.getXRot();
        float yaw = player.getYRot();
        Vec3 eye = player.getEyePosition(1.0f);
        float f2 = Mth.cos((float)(-yaw * ((float)Math.PI / 180) - (float)Math.PI));
        float f3 = Mth.sin((float)(-yaw * ((float)Math.PI / 180) - (float)Math.PI));
        float f4 = -Mth.cos((float)(-pitch * ((float)Math.PI / 180)));
        float f5 = Mth.sin((float)(-pitch * ((float)Math.PI / 180)));
        float x = f3 * f4;
        float y = f5;
        float z = f2 * f4;
        double reach = player.getAttribute(Player.REACH_DISTANCE).getValue();
        Vec3 look = eye.add((double)x * reach, (double)y * reach, (double)z * reach);
        return SRPEntityUtil.rayTraceBlocks(world, eye, look);
    }
}

