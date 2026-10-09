package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class GuerillaAdvancement {
    private static final ResourceLocation ADV_ID = ResourceLocation.fromNamespaceAndPath("srparasites", "guerilla");
    private static final String CRITERION = "kill_fear_splashed";
    private static final String TAG_APPLIER = "srpFearApplier";
    private static final String TAG_UNTIL = "srpFearUntil";

    private GuerillaAdvancement() {
    }

    public static void register(FMLInitializationEvent e) {
        MinecraftForge.EVENT_BUS.register(GuerillaAdvancement.class);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        if (event.getEntity().level().isClientSide) {
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
        Advancement adv = killer.getServer().getAdvancementManager().getAdvancement(ADV_ID);
        if (adv == null) {
            return;
        }
        AdvancementProgress prog = killer.getAdvancements().getProgress(adv);
        if (!prog.isDone()) {
            prog.grantCriterion(CRITERION);
        }
        tag.remove(TAG_APPLIER);
        tag.remove(TAG_UNTIL);
    }
}

