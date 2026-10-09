package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolMeleeBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

/** SRPEventHandlerBus.onMouse of 1.10.9: a left click does nothing while a melee weapon of the mod is on cooldown. (The extended reach is the item attribute {@code ENTITY_INTERACTION_RANGE} of the weapon.) */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientInput {
    private ClientInput() {
    }

    @SubscribeEvent
    public static void onAttackClick(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack()) {
            return;
        }
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) {
            return;
        }
        Item item = p.getMainHandItem().getItem();
        if (!(item instanceof WeaponToolMeleeBase)) {
            return;
        }
        if (p.getCooldowns().isOnCooldown(item)) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }
}
