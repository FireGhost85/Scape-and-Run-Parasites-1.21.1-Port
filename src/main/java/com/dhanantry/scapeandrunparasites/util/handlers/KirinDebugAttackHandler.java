package com.dhanantry.scapeandrunparasites.util.handlers;

import net.neoforged.fml.common.EventBusSubscriber;
import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
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

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class KirinDebugAttackHandler {
    private static final ResourceLocation DEBUG_ITEM_ID = ResourceLocation.fromNamespaceAndPath("srparasites", "itemmobspawner_kirin");
    public static boolean DEBUG_FORCE_ENABLE_KIRIN_ITEM = false;

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        KirinDebugAttackHandler.tryTrigger(event.getEntity(), event.getHand(), (PlayerInteractEvent)event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        KirinDebugAttackHandler.tryTrigger(event.getEntity(), event.getHand(), (PlayerInteractEvent)event);
    }

    private static void tryTrigger(Player player, InteractionHand hand, PlayerInteractEvent event) {
        if (!SRPConfigMobs.kirinDebugSpecialAttack && !DEBUG_FORCE_ENABLE_KIRIN_ITEM) {
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
        EntityKirin closestKirin = KirinDebugAttackHandler.findClosestKirin(player);
        if (closestKirin == null) {
            return;
        }
        LivingEntity target = closestKirin.getTarget();
        if (!KirinDebugAttackHandler.isValidTargetForDebug(closestKirin, target)) {
            target = KirinDebugAttackHandler.findClosestValidTarget(closestKirin, player);
        }
        if (target == null && SRPConfigMobs.kirinDebugTargetPlayerIfNoTarget) {
            target = player;
        }
        if (target == null) {
            return;
        }
        closestKirin.setTarget(target);
        closestKirin.spawnJudgementCuts(target);
        closestKirin.resetIdleTime();
        if (event instanceof PlayerInteractEvent.RightClickItem ri) {
            ri.setCanceled(true);
            ri.setCancellationResult(InteractionResult.SUCCESS);
        } else if (event instanceof PlayerInteractEvent.RightClickBlock rb) {
            rb.setCanceled(true);
            rb.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    private static boolean isValidTargetForDebug(EntityKirin kirin, LivingEntity living) {
        if (living == null || living.isRemoved()) {
            return false;
        }
        if (living == kirin) {
            return false;
        }
        if (KirinDebugAttackHandler.isSRParasitesMob(living)) {
            return false;
        }
        if (living instanceof Player) {
            Player targetPlayer = (Player)living;
            if (targetPlayer.isSpectator()) {
                return false;
            }
            if (targetPlayer.isCreative() && !SRPConfigMobs.kirinDebugTargetCreativePlayers) {
                return false;
            }
        }
        return kirin.hasLineOfSight((Entity)living);
    }

    private static EntityKirin findClosestKirin(Player player) {
        AABB box = player.getBoundingBox().expandTowards(48.0, 32.0, 48.0);
        List<? extends EntityKirin> list = player.level().getEntitiesOfClass(EntityKirin.class, box);
        EntityKirin closest = null;
        double closestDist = Double.MAX_VALUE;
        for (EntityKirin kirin : list) {
            double dist;
            if (kirin == null || kirin.isRemoved() || !((dist = kirin.distanceToSqr((Entity)player)) < closestDist)) continue;
            closestDist = dist;
            closest = kirin;
        }
        return closest;
    }

    private static LivingEntity findClosestValidTarget(EntityKirin kirin, Player playerUsingItem) {
        AABB box = kirin.getBoundingBox().expandTowards(48.0, 32.0, 48.0);
        List<? extends LivingEntity> list = kirin.level().getEntitiesOfClass(LivingEntity.class, box);
        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (LivingEntity living : list) {
            double dist;
            Player targetPlayer;
            if (living == null || living.isRemoved() || living == kirin || KirinDebugAttackHandler.isSRParasitesMob(living) || living instanceof Player && ((targetPlayer = (Player)living).isSpectator() || targetPlayer.isCreative() && !SRPConfigMobs.kirinDebugTargetCreativePlayers) || !kirin.hasLineOfSight((Entity)living) || !((dist = living.distanceToSqr((Entity)kirin)) < closestDist)) continue;
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

