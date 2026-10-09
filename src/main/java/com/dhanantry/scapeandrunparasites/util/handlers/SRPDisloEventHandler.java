package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SRPDisloEventHandler {
    @SubscribeEvent
    public static void eventDislo1(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase((net.minecraft.world.level.Level)event.getLevel(), SRPAttributes.EVENTRIGHTCLICKBLOCK, SRPConfigSystems.chanceEventRightClickB, SRPConfigSystems.disloCOTHSpy, event.getPos());
    }

    @SubscribeEvent
    public static void eventDislo2(PlayerXpEvent.PickupXp event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTXPPICKUP, SRPConfigSystems.chanceEventXPPickUp, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public static void eventDislo3(ItemEntityPickupEvent.Pre event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getPlayer().level(), SRPAttributes.EVENTITEMPICKUP, SRPConfigSystems.chanceEventItemPickUp, SRPConfigSystems.disloCOTHSpy, event.getPlayer().blockPosition());
    }

    @SubscribeEvent
    public static void eventDislo4(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTHEALING, SRPConfigSystems.chanceEventHealing, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public static void eventDislo5(LivingEntityUseItemEvent.Finish event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTUSEITEM, SRPConfigSystems.chanceEventUsteItem, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public static void eventDislo6(PlayerContainerEvent.Close event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTMENUCLOSE, SRPConfigSystems.chanceEventMEnuClose, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }
}

