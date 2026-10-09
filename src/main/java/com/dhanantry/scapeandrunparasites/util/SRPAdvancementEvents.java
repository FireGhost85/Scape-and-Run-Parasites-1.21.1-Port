package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import com.dhanantry.scapeandrunparasites.network.SRPSend;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/**
 * The advancement triggers of 1.10.9 that were Java classes (the "impossible" criteria of the JSON files): monument (Beckon
 * Stage IV nearby), qliphoth_overload (Dispatcher Stage IV nearby), enemy_enemy (a creeper kills a parasite), hunt_season (50 kills
 * in a day), cut_roots (1000 rupter kills). Guerilla, sepeku, thornshade_self_destruct, tricked_me_did_you, dark_days, columbus
 * and stolas are granted where they happen.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPAdvancementEvents {
    private static final double DETECT_RANGE_SQ = 65536.0;
    private static final double CREEPER_RANGE_SQ = 4096.0;

    private SRPAdvancementEvents() {
    }

    /** Awards one criterion; returns true when it was newly granted. */
    public static boolean grant(ServerPlayer p, String advancement, String criterion) {
        MinecraftServer server = p.getServer();
        if (server == null) {
            return false;
        }
        AdvancementHolder adv = server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, advancement));
        return adv != null && p.getAdvancements().award(adv, criterion);
    }

    /** Awards every remaining criterion. */
    public static void grantAll(ServerPlayer p, String advancement) {
        MinecraftServer server = p.getServer();
        if (server == null) {
            return;
        }
        AdvancementHolder adv = server.getAdvancements().get(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, advancement));
        if (adv == null) {
            return;
        }
        for (String c : p.getAdvancements().getOrStartProgress(adv).getRemainingCriteria()) {
            p.getAdvancements().award(adv, c);
        }
    }

    private static void detected(ServerLevel level, Entity ent, String advancement, String criterion, SoundEvent sound) {
        for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {
            if (p.level() != level || p.distanceToSqr(ent) > DETECT_RANGE_SQ) {
                continue;
            }
            if (!grant(p, advancement, criterion)) {
                continue;
            }
            if (sound != null) {
                p.connection.send(new ClientboundSoundPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(sound), SoundSource.MASTER, p.getX(), p.getY(), p.getZ(), 1.0f, 1.0f, p.getRandom().nextLong()));
            }
            SRPSend.sendToPlayer(p, new QlipShakePayload(0, 20, true, true, 4.0f));
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent e) {
        if (!(e.getLevel() instanceof ServerLevel level)) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getEntity().getType());
        if (!ScapeAndRunParasites.MODID.equals(id.getNamespace())) {
            return;
        }
        if ("beckon_siv".equals(id.getPath())) {
            detected(level, e.getEntity(), "monument", "detected_beckon_siv", SRPSounds.DISLO_11.get());
        } else if ("dispatcher_siv".equals(id.getPath())) {
            detected(level, e.getEntity(), "qliphoth_overload", "detected_dispatcher_siv", SRPSounds.DISLO_28.get());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent e) {
        if (!(e.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity victim = e.getEntity();
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType());
        if (!ScapeAndRunParasites.MODID.equals(id.getNamespace())) {
            return;
        }
        Entity killer = e.getSource().getEntity();
        if (killer instanceof Creeper) {
            for (ServerPlayer p : level.getServer().getPlayerList().getPlayers()) {
                if (p.level() == level && p.distanceToSqr(victim) <= CREEPER_RANGE_SQ) {
                    grant(p, "enemy_enemy", "creeper_killed_parasite");
                }
            }
        }
        if (!(killer instanceof ServerPlayer player)) {
            return;
        }
        CompoundTag persisted = player.getPersistentData();
        long now = level.getGameTime();
        long start = persisted.getLong("srpHuntStart");
        int count = persisted.getInt("srpHuntCount");
        if (start == 0L || now - start > 24000L) {
            start = now;
            count = 1;
        } else {
            ++count;
        }
        persisted.putLong("srpHuntStart", start);
        persisted.putInt("srpHuntCount", count);
        if (count >= 50) {
            grant(player, "hunt_season", "fifty_in_day");
        }
        if ("rupter".equals(id.getPath())) {
            int rupters = persisted.getInt("srpRupterKills") + 1;
            persisted.putInt("srpRupterKills", rupters);
            if (rupters >= 1000) {
                grant(player, "cut_roots", "reached_1000_rupter_kills");
            }
        }
    }
}
