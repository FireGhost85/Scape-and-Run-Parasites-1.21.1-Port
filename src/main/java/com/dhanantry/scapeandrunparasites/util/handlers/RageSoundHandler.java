package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/** Raging mobs growl every 3 to 6 seconds (1.12 LivingUpdateEvent handler). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class RageSoundHandler {
    private static final String RAGE_SOUND_TIMER = "SRPRageSoundTimer";
    private static final int RAGE_SOUND_MIN_INTERVAL = 60;
    private static final int RAGE_SOUND_MAX_INTERVAL = 120;
    private static final Set<String> BLACKLIST = Set.of("minecraft:player", "minecraft:armor_stand", "srparasites:buglin", "srparasites:biomass",
            "srparasites:beckon_si", "srparasites:beckon_sii", "srparasites:beckon_siii", "srparasites:beckon_siv",
            "srparasites:rooter_si", "srparasites:rooter_sii", "srparasites:rooter_siii", "srparasites:rooter_siv",
            "srparasites:dispatcher_si", "srparasites:dispatcher_sii", "srparasites:dispatcher_siii", "srparasites:dispatcher_siv",
            "srparasites:movingflesh", "srparasites:worker", "srparasites:dispatcherten", "srparasites:rooterball",
            "srparasites:incompleteform_small", "srparasites:incompleteform_medium", "srparasites:crux_incomplete",
            "srparasites:carrier_worm", "srparasites:succor", "srparasites:anc_pod");

    private RageSoundHandler() {
    }

    @SubscribeEvent
    public static void aiStep(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity) || entity.level().isClientSide) {
            return;
        }
        if (entity.isRemoved() || entity.getHealth() <= 0.0f) {
            return;
        }
        if (RageSoundHandler.isBlacklisted(entity)) {
            return;
        }
        MobEffectInstance rage = entity.getEffect(SRPPotions.RAGE_E);
        CompoundTag data = entity.getPersistentData();
        if (rage == null) {
            if (data.contains(RAGE_SOUND_TIMER)) {
                data.remove(RAGE_SOUND_TIMER);
            }
            return;
        }
        int timer = data.getInt(RAGE_SOUND_TIMER);
        if (timer > 0) {
            data.putInt(RAGE_SOUND_TIMER, timer - 1);
            return;
        }
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SRPSounds.RAGE.get(), SoundSource.HOSTILE, 0.3f, 1.0f + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1f);
        data.putInt(RAGE_SOUND_TIMER, RageSoundHandler.getRandomInterval(entity));
    }

    private static int getRandomInterval(LivingEntity entity) {
        int min = Math.max(1, RAGE_SOUND_MIN_INTERVAL);
        int max = Math.max(min, RAGE_SOUND_MAX_INTERVAL);
        return min + entity.getRandom().nextInt(max - min + 1);
    }

    private static boolean isBlacklisted(LivingEntity entity) {
        if (entity instanceof Player || entity instanceof ArmorStand) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        return BLACKLIST.contains(id.toString());
    }
}
