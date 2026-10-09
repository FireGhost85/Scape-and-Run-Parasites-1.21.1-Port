package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class SRPDisloEventHandler {
    @SubscribeEvent
    public void eventDislo(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase(event.getLevel(), SRPAttributes.EVENTRIGHTCLICKBLOCK, SRPConfigSystems.chanceEventRightClickB, SRPConfigSystems.disloCOTHSpy, event.getPos());
    }

    @SubscribeEvent
    public void eventDislo(PlayerPickupXpEvent event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTXPPICKUP, SRPConfigSystems.chanceEventXPPickUp, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public void eventDislo(EntityItemPickupEvent event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTITEMPICKUP, SRPConfigSystems.chanceEventItemPickUp, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public void eventDislo(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTHEALING, SRPConfigSystems.chanceEventHealing, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public void eventDislo(LivingEntityUseItemEvent.Finish event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTUSEITEM, SRPConfigSystems.chanceEventUsteItem, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }

    @SubscribeEvent
    public void eventDislo(PlayerContainerEvent.Close event) {
        ParasiteEventWorld.setDisloWorldPhase(event.getEntity().level(), SRPAttributes.EVENTMENUCLOSE, SRPConfigSystems.chanceEventMEnuClose, SRPConfigSystems.disloCOTHSpy, event.getEntity().blockPosition());
    }
}

