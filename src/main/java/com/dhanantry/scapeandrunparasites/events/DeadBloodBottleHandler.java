package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

/** A glass bottle used on dead blood fills it (the fluid is not removed, as in the original). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class DeadBloodBottleHandler {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem e) {
        if (e.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Player player = e.getEntity();
        Level world = e.getLevel();
        ItemStack held = e.getItemStack();
        if (held.isEmpty() || held.getItem() != Items.GLASS_BOTTLE) {
            return;
        }
        BlockHitResult rt = rayTrace(world, player);
        if (rt.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos pos = rt.getBlockPos();
        if (!world.getFluidState(pos).getType().isSame(SRPFluids.DEADBLOOD_FLUID.get())) {
            return;
        }
        if (!world.isClientSide) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
            }
            ItemStack filled = new ItemStack(SRPItems.DEADBLOOD_FLUID.get());
            if (held.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, filled);
            } else if (!player.getInventory().add(filled)) {
                player.drop(filled, false);
            }
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 0.9f, 1.0f);
        }
        e.setCanceled(true);
        e.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static BlockHitResult rayTrace(Level world, Player player) {
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 end = eye.add(player.getViewVector(1.0f).scale(player.blockInteractionRange()));
        return world.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, player));
    }
}
