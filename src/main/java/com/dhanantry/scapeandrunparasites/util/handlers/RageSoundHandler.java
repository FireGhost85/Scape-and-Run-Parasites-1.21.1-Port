package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@Mod.EventBusSubscriber
public final class RageSoundHandler {
    private static final String RAGE_SOUND_TIMER = "SRPRageSoundTimer";
    private static final int RAGE_SOUND_MIN_INTERVAL = 60;
    private static final int RAGE_SOUND_MAX_INTERVAL = 120;
    private static final float RAGE_SOUND_VOLUME = 0.3f;
    private static final float RAGE_SOUND_PITCH = 1.0f;

    private RageSoundHandler() {
    }

    @SubscribeEvent
    public static void aiStep(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level() == null || entity.level().isClientSide) {
            return;
        }
        if (entity.isRemoved() || entity.getHealth() <= 0.0f) {
            return;
        }
        if (RageSoundHandler.isBlacklisted(entity)) {
            return;
        }
        MobEffectInstance rage = entity.getEffect(SRPPotions.RAGE_E);
        if (rage == null) {
            entity.getPersistentData().remove(RAGE_SOUND_TIMER);
            return;
        }
        CompoundTag data = entity.getPersistentData();
        int timer = data.getInt(RAGE_SOUND_TIMER);
        if (timer > 0) {
            data.putInt(RAGE_SOUND_TIMER, timer - 1);
            return;
        }
        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SRPSounds.RAGE.get(), SoundSource.HOSTILE, 0.3f, 1.0f + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.1f);
        data.putInt(RAGE_SOUND_TIMER, RageSoundHandler.getRandomInterval(entity));
    }

    private static int getRandomInterval(LivingEntity entity) {
        int min = Math.max(1, 60);
        int max = Math.max(min, 120);
        return min + entity.getRandom().nextInt(max - min + 1);
    }

    private static boolean isBlacklisted(LivingEntity entity) {
        if (entity instanceof Player) {
            return true;
        }
        if (entity instanceof EntityArmorStand) {
            return true;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        if (id == null) {
            return true;
        }
        String name = id.toString();
        return name.equals("minecraft:player") || name.equals("minecraft:armor_stand") || name.equals("srparasites:buglin") || name.equals("srparasites:biomass") || name.equals("srparasites:beckon_si") || name.equals("srparasites:beckon_sii") || name.equals("srparasites:beckon_siii") || name.equals("srparasites:beckon_siv") || name.equals("srparasites:rooter_si") || name.equals("srparasites:rooter_sii") || name.equals("srparasites:rooter_siii") || name.equals("srparasites:rooter_siv") || name.equals("srparasites:dispatcher_si") || name.equals("srparasites:dispatcher_sii") || name.equals("srparasites:dispatcher_siii") || name.equals("srparasites:dispatcher_siv") || name.equals("srparasites:movingflesh") || name.equals("srparasites:worker") || name.equals("srparasites:dispatcherten") || name.equals("srparasites:rooterball") || name.equals("srparasites:incompleteform_small") || name.equals("srparasites:incompleteform_medium") || name.equals("srparasites:crux_incomplete") || name.equals("srparasites:carrier_worm") || name.equals("srparasites:succor") || name.equals("srparasites:anc_pod");
    }
}

