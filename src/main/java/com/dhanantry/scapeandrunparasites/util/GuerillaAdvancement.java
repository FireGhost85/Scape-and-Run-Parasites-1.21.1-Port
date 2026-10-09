package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class GuerillaAdvancement {
    private static final ResourceLocation ADV_ID = ResourceLocation.fromNamespaceAndPath("srparasites", "guerilla");
    private static final String CRITERION = "kill_fear_splashed";
    private static final String TAG_APPLIER = "srpFearApplier";
    private static final String TAG_UNTIL = "srpFearUntil";

    private GuerillaAdvancement() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        Entity trueSrc = event.getSource().getEntity();
        if (!(trueSrc instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer victim = (ServerPlayer)event.getEntity();
        ServerPlayer killer = (ServerPlayer)trueSrc;
        CompoundTag tag = victim.getPersistentData();
        if (!tag.contains(TAG_APPLIER)) {
            return;
        }
        if (!killer.getUUID().toString().equals(tag.getString(TAG_APPLIER))) {
            return;
        }
        long until = tag.getLong(TAG_UNTIL);
        if (victim.level().getGameTime() > until) {
            return;
        }
        if (!victim.hasEffect(SRPPotions.FEAR_E)) {
            return;
        }
        AdvancementHolder adv = killer.getServer().getAdvancements().get(ADV_ID);
        if (adv == null) {
            return;
        }
        AdvancementProgress prog = killer.getAdvancements().getOrStartProgress(adv);
        if (!prog.isDone()) {
            killer.getAdvancements().award(adv, CRITERION);
        }
        tag.remove(TAG_APPLIER);
        tag.remove(TAG_UNTIL);
    }
}

