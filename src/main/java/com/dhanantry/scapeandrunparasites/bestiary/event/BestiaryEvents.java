package com.dhanantry.scapeandrunparasites.bestiary.event;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/** Kills of a player count in the bestiary: kills, seen mob, seen tier. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class BestiaryEvents {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof Player p) || p.level().isClientSide) {
            return;
        }
        IBestiaryProgress prog = BestiaryCapability.get(p);
        LivingEntity mob = e.getEntity();
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        String mobId = key.toString();
        prog.addKill(mobId, 1);
        prog.markMobSeen(mobId);
        for (BestiaryEntry be : SRPBestiaryRegistry.all()) {
            if (mobId.equals(be.mobId)) {
                ParasiteTier tier = be.tier;
                if (tier != null) {
                    prog.markTierSeen(tier);
                }
                break;
            }
        }
    }
}
