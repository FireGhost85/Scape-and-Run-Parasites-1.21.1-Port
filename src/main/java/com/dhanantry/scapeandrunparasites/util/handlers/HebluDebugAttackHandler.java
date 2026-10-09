package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public class HebluDebugAttackHandler {
    private static final ResourceLocation DEBUG_ITEM_ID = ResourceLocation.fromNamespaceAndPath("srparasites", "itemmobspawner_heblu");
    public static boolean DEBUG_FORCE_ENABLE_HEBLU_ITEM = false;
    public static boolean DEBUG_ALLOW_CREATIVE_PLAYER_TARGET = false;
    public static boolean DEBUG_TARGET_PLAYER_IF_NO_TARGET = false;

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        HebluDebugAttackHandler.tryTrigger(event.getEntity(), event.getHand(), (PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        HebluDebugAttackHandler.tryTrigger(event.getEntity(), event.getHand(), (PlayerInteractEvent)event);
    }

    private static void tryTrigger(Player player, InteractionHand hand, PlayerInteractEvent event) {
        if (!SRPConfigMobs.hebluDebugSpecialAttack && !DEBUG_FORCE_ENABLE_HEBLU_ITEM) {
            return;
        }
        if (player == null || player.level().isClientSide) {
            return;
        }
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation heldId = stack.getItem().builtInRegistryHolder().key().location();
        if (heldId == null || !DEBUG_ITEM_ID.equals(heldId)) {
            return;
        }
        EntityHeblu closestHeblu = HebluDebugAttackHandler.findClosestHeblu(player);
        if (closestHeblu == null) {
            return;
        }
        LivingEntity target = HebluDebugAttackHandler.findClosestValidTarget(closestHeblu, player);
        if (target == null && DEBUG_TARGET_PLAYER_IF_NO_TARGET) {
            target = player;
        }
        if (!closestHeblu.getFlyingState()) {
            closestHeblu.changeStateTo(true);
        }
        if (target != null) {
            closestHeblu.setTarget(target);
        }
        closestHeblu.spawnLightBarrage(target, true);
        closestHeblu.resetIdleTime();
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static EntityHeblu findClosestHeblu(Player player) {
        AABB box = player.getBoundingBox().expandTowards(48.0, 32.0, 48.0);
        List<? extends EntityHeblu> list = player.level().getEntitiesOfClass(EntityHeblu.class, box);
        EntityHeblu closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityHeblu heblu : list) {
            double dist;
            if (heblu == null || heblu.isRemoved() || !((dist = heblu.distanceToSqr((Entity)player)) < closestDist)) continue;
            closestDist = dist;
            closest = heblu;
        }
        return closest;
    }

    private static LivingEntity findClosestValidTarget(EntityHeblu heblu, Player playerUsingItem) {
        AABB box = heblu.getBoundingBox().expandTowards(48.0, 32.0, 48.0);
        List<? extends LivingEntity> list = heblu.level().getEntitiesOfClass(LivingEntity.class, box);
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (LivingEntity living : list) {
            double dist;
            Player targetPlayer;
            if (living == null || living.isRemoved() || living == heblu || HebluDebugAttackHandler.isSRParasitesMob(living) || living instanceof Player && ((targetPlayer = (Player)living).isSpectator() || targetPlayer.isCreative() && !DEBUG_ALLOW_CREATIVE_PLAYER_TARGET) || !heblu.hasLineOfSight((Entity)living) || !((dist = living.distanceToSqr((Entity)heblu)) < closestDist)) continue;
            closestDist = dist;
            closest = living;
        }
        return closest;
    }

    private static boolean isSRParasitesMob(LivingEntity living) {
        String className = living.getClass().getName();
        return className.startsWith("com.dhanantry.scapeandrunparasites.entity.monster.");
    }
}

