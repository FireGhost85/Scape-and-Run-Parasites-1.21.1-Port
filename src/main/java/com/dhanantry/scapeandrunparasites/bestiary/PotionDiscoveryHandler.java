package com.dhanantry.scapeandrunparasites.bestiary;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.network.BestiarySyncPayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

/** A player that gets one of the mod's effects discovers it in the bestiary. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class PotionDiscoveryHandler {
    @SubscribeEvent
    public static void onPotionAdded(MobEffectEvent.Added e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) {
            return;
        }
        MobEffectInstance pe = e.getEffectInstance();
        ResourceLocation rl = BuiltInRegistries.MOB_EFFECT.getKey(pe.getEffect().value());
        if (rl == null || !ScapeAndRunParasites.MODID.equals(rl.getNamespace())) {
            return;
        }
        IBestiaryProgress prog = BestiaryCapability.get(p);
        String id = rl.getPath();
        if (!prog.hasSeenEffect(id)) {
            prog.markEffectSeen(id);
            SRPSend.sendToPlayer(p, new BestiarySyncPayload(prog.serializeNBT()));
        }
    }
}
