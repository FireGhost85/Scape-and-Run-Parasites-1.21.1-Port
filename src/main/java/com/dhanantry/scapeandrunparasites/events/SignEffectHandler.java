package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SignEffectHandler {
    private static final int DURATION_TICKS = 40;
    private static final String CHARM_ID = "srparasites:the_sign_charm";
    private static Item SIGN_ITEM;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.player.level().isClientSide) {
            return;
        }
        Player p = e.player;
        if (SIGN_ITEM == null) {
            SIGN_ITEM = BuiltInRegistries.ITEM.get(ResourceLocation.parse(CHARM_ID));
        }
        if (SIGN_ITEM == null) {
            return;
        }
        boolean hasCharm = SignEffectHandler.hasItemAnywhere(p, SIGN_ITEM);
        if (hasCharm) {
            p.addEffect(new MobEffectInstance(SRPPotions.THE_SIGN_E, 40, 0, false, false));
        }
    }

    private static boolean hasItemAnywhere(Player p, Item item) {
        for (ItemStack s : p.getInventory().items) {
            if (s.isEmpty() || s.getItem() != item) continue;
            return true;
        }
        for (ItemStack s : p.getInventory().offHandInventory) {
            if (s.isEmpty() || s.getItem() != item) continue;
            return true;
        }
        for (ItemStack s : p.getInventory().armor) {
            if (s.isEmpty() || s.getItem() != item) continue;
            return true;
        }
        return false;
    }

    private SignEffectHandler() {
    }
}

