package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.VengeanceFxPayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * The grapple of the book of vengeance, run from the server tick: wind-up (20 ticks, chain particles), pull (up to 40 ticks, the
 * player is dragged to the target), impact (blast of 14 damage within 5 blocks, launches and debuffs everything hit), bounce (14
 * ticks) and the second impact with lightning on everything that was hit. The target cannot hurt the player meanwhile, and
 * lightning / explosion damage is cancelled for the player.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class VengeanceGrappleHandler {
    private static final Map<UUID, GrappleData> ACTIVE = new HashMap<>();

    private VengeanceGrappleHandler() {}

    private static void fxAroundTracking(ServerLevel ws, Vec3 pos, VengeanceFxPayload pkt) {
        for (ServerPlayer p : ws.players()) {
            if (p.distanceToSqr(pos.x, pos.y, pos.z) > 4096.0) continue;
            PacketDistributor.sendToPlayer(p, pkt);
        }
    }

    private static VengeanceFxPayload fx(int type, Vec3 pos, float a, int count) {
        return new VengeanceFxPayload((byte) type, pos.x, pos.y, pos.z, a, count);
    }

    public static void start(Player player, LivingEntity target) {
        if (player == null || target == null || player.isRemoved() || target.isRemoved() || !(player.level() instanceof ServerLevel ws)) {
            return;
        }
        GrappleData d = new GrappleData(player.getUUID(), target.getUUID(), DimKeys.of(ws));
        d.stage = Stage.WINDUP;
        d.timer = 20;
        ACTIVE.put(player.getUUID(), d);
        ws.playSound(null, player.getX(), player.getY(), player.getZ(), SRPSounds.VENGEANCE_PAPER.get(), SoundSource.PLAYERS, 2.0f, 1.0f);
    }

    @SubscribeEvent
    public static void onPlayerDamaged(LivingIncomingDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }
        GrappleData d = ACTIVE.get(p.getUUID());
        if (d == null) {
            return;
        }
        Entity src = e.getSource().getEntity();
        if (src != null && src.getUUID().equals(d.targetId)) {
            e.setCanceled(true);
            p.invulnerableTime = 20;
            return;
        }
        if (e.getSource().is(DamageTypes.LIGHTNING_BOLT) || e.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            e.setCanceled(true);
        }
    }

    private static void applyVengeanceDebuffs(LivingEntity ent) {
        if (ent == null || ent.isRemoved()) {
            return;
        }
        ent.addEffect(new MobEffectInstance(SRPPotions.DOD_SMOKE_TRAIL_E, 100, 0, false, true));
        ent.addEffect(new MobEffectInstance(SRPPotions.BLEED_E, 100, 0, false, true));
        ent.addEffect(new MobEffectInstance(SRPPotions.DEBAR_E, 100, 0, false, true));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post e) {
        if (ACTIVE.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, GrappleData>> it = ACTIVE.entrySet().iterator();
        while (it.hasNext()) {
            GrappleData d = it.next().getValue();
            Player player = d.getPlayer();
            LivingEntity target = d.getTarget();
            if (player == null || target == null || player.isRemoved() || target.isRemoved() || !(player.level() instanceof ServerLevel ws)
                    || !DimKeys.of(ws).equals(d.dimension)) {
                it.remove();
                continue;
            }
            if (!player.hasLineOfSight(target)) {
                ws.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.6f, 1.4f);
                it.remove();
                continue;
            }
            switch (d.stage) {
                case WINDUP -> {
                    spawnChainParticles(ws, player, target, ParticleTypes.WITCH);
                    if (d.timer % 4 == 0) {
                        Vec3 c = new Vec3(player.getX(), player.getY() + 0.2, player.getZ());
                        fxAroundTracking(ws, c, fx(0, c, 1.2f, 16));
                    }
                    if (d.timer % 6 == 0) {
                        ws.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.25f, 1.8f);
                    }
                    --d.timer;
                    if (d.timer <= 0) {
                        ws.playSound(null, target.getX(), target.getY() + (double) target.getBbHeight() * 0.5, target.getZ(), SRPSounds.VENGEANCE_CHAIN_IMPACT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        d.stage = Stage.PULL;
                        d.ticks = 0;
                    }
                }
                case PULL -> {
                    ++d.ticks;
                    spawnChainParticles(ws, player, target, ParticleTypes.ENCHANTED_HIT);
                    pullPlayerToward(player, target, 1.8);
                    double dist = player.distanceTo(target);
                    if (dist <= 2.2 || d.ticks > 40) {
                        impact1(ws, player, target, d);
                    }
                }
                case BOUNCE -> {
                    if (d.timer % 4 == 0) {
                        for (UUID id : d.affected) {
                            if (!(ws.getEntity(id) instanceof LivingEntity ent2) || ent2.isRemoved()) continue;
                            Vec3 c = new Vec3(ent2.getX(), ent2.getY() + (double) ent2.getBbHeight() * 0.5, ent2.getZ());
                            fxAroundTracking(ws, c, fx(2, c, 0.0f, 14));
                        }
                    }
                    int t = d.timer;
                    if (t >= 12) {
                        Mot.setY(player, Math.max(player.getDeltaMovement().y, 0.55));
                        Mot.mulX(player, 0.4);
                        Mot.mulZ(player, 0.4);
                        player.hurtMarked = true;
                    } else if (t >= 6) {
                        Mot.setY(player, Math.max(player.getDeltaMovement().y, -0.02));
                        Mot.mulX(player, 0.65);
                        Mot.mulZ(player, 0.65);
                        player.hurtMarked = true;
                    } else {
                        pullPlayerToward(player, target, 2.35);
                    }
                    player.fallDistance = 0.0f;
                    --d.timer;
                    if (d.timer <= 0) {
                        impact2AndLightning(ws, player, target, d);
                        it.remove();
                    }
                }
            }
        }
    }

    private static void pullPlayerToward(Player player, LivingEntity target, double speed) {
        Vec3 to = new Vec3(target.getX() - player.getX(), target.getY() + (double) target.getEyeHeight() * 0.5 - (player.getY() + (double) player.getEyeHeight()), target.getZ() - player.getZ());
        double len = to.length();
        if (len < 1.0E-4) {
            return;
        }
        Vec3 dir = to.scale(1.0 / len);
        Mot.setX(player, Mth.clamp(dir.x * speed, -2.8, 2.8));
        Mot.setY(player, Mth.clamp(dir.y * speed, -2.2, 2.2));
        Mot.setZ(player, Mth.clamp(dir.z * speed, -2.8, 2.8));
        player.hurtMarked = true;
        player.fallDistance = 0.0f;
    }

    private static void impact1(ServerLevel ws, Player player, LivingEntity target, GrappleData d) {
        ws.playSound(null, target.getX(), target.getY(), target.getZ(), SRPSounds.VENGEANCE_IMPACT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        ws.playSound(null, target.getX(), target.getY(), target.getZ(), SRPSounds.VENGEANCE_ROCK.get(), SoundSource.PLAYERS, 0.9f, 0.9f);
        Vec3 hitPos = new Vec3(target.getX(), target.getY(), target.getZ());
        fxAroundTracking(ws, hitPos, fx(1, hitPos, 0.0f, 28));
        ws.sendParticles(ParticleTypes.EXPLOSION_EMITTER, target.getX(), target.getY() + 0.2, target.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        float blastDamage = 14.0f;
        float radius = 5.0f;
        AABB aabb = new AABB(target.getX() - radius, target.getY() - 2.0, target.getZ() - radius, target.getX() + radius, target.getY() + 4.0, target.getZ() + radius);
        d.affected.clear();
        for (LivingEntity ent : ws.getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (ent == null || ent.isRemoved() || ent == player || (double) ent.distanceTo(target) > (double) radius) continue;
            d.affected.add(ent.getUUID());
            applyVengeanceDebuffs(ent);
            ws.playSound(null, ent.getX(), ent.getY(), ent.getZ(), SRPSounds.VENGEANCE_WHOOSH.get(), SoundSource.PLAYERS, 0.8f, 1.2f);
            ent.hurt(player.damageSources().playerAttack(player), blastDamage);
            Vec3 look = player.getLookAngle();
            double l = look.length();
            if (l > 1.0E-4) {
                look = look.scale(1.0 / l);
            }
            double forward = 1.9;
            double upward = 0.65;
            Mot.setX(ent, look.x * forward);
            Mot.setZ(ent, look.z * forward);
            Mot.setY(ent, Math.max(ent.getDeltaMovement().y, look.y * forward + upward));
            ent.hurtMarked = true;
        }
        Mot.mulX(player, 0.15);
        Mot.mulZ(player, 0.15);
        player.fallDistance = 0.0f;
        player.hurtMarked = true;
        d.stage = Stage.BOUNCE;
        d.timer = 14;
    }

    private static void impact2AndLightning(ServerLevel ws, Player player, LivingEntity target, GrappleData d) {
        ws.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.7f, 1.2f);
        Vec3 p = new Vec3(target.getX(), target.getY() + 0.1, target.getZ());
        fxAroundTracking(ws, p, fx(3, p, 0.0f, 0));
        ws.playSound(null, target.getX(), target.getY(), target.getZ(), SRPSounds.VENGEANCE_ROCK.get(), SoundSource.PLAYERS, 0.9f, 0.9f);
        ws.sendParticles(ParticleTypes.EXPLOSION, target.getX(), target.getY() + 0.15, target.getZ(), 3, 0.15, 0.1, 0.15, 0.0);
        if (!target.isRemoved()) {
            target.hurt(player.damageSources().playerAttack(player), 6.0f);
            Mot.setY(target, Math.max(target.getDeltaMovement().y, 0.35));
            target.hurtMarked = true;
        }
        float radius = 3.5f;
        float maxDmg = 10.0f;
        AABB aabb = new AABB(target.getX() - radius, target.getY() - 1.5, target.getZ() - radius, target.getX() + radius, target.getY() + 2.5, target.getZ() + radius);
        for (LivingEntity ent : ws.getEntitiesOfClass(LivingEntity.class, aabb)) {
            double dist;
            if (ent == null || ent.isRemoved() || ent == player || (dist = ent.distanceTo(target)) > radius) continue;
            float scale = 1.0f - (float) (dist / radius);
            ent.hurt(ws.damageSources().explosion(player, player), maxDmg * scale);
            Vec3 push = new Vec3(ent.getX() - target.getX(), 0.0, ent.getZ() - target.getZ());
            double len = push.length();
            if (!(len > 1.0E-4)) continue;
            push = push.scale(1.0 / len);
            double kb = 0.85 * (double) scale;
            Mot.addX(ent, push.x * kb);
            Mot.addZ(ent, push.z * kb);
            Mot.setY(ent, Math.max(ent.getDeltaMovement().y, 0.25 + 0.2 * (double) scale));
            ent.hurtMarked = true;
        }
        player.fallDistance = 0.0f;
        lightningStrike(ws, player, d);
    }

    private static void lightningStrike(ServerLevel ws, Player player, GrappleData d) {
        ws.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 0.9f, 1.0f);
        for (UUID id : d.affected) {
            if (!(ws.getEntity(id) instanceof LivingEntity ent) || ent.isRemoved()) continue;
            SRPEntityUtil.lightning(ws, ent.getX(), ent.getY(), ent.getZ(), true);
            Vec3 p = new Vec3(ent.getX(), ent.getY(), ent.getZ());
            fxAroundTracking(ws, p, fx(3, p, 0.0f, 0));
            ent.hurt(ws.damageSources().lightningBolt(), 16.0f);
            ws.playSound(null, ent.getX(), ent.getY(), ent.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.HOSTILE, 0.8f, 1.0f);
        }
    }

    private static void spawnChainParticles(ServerLevel ws, Player player, LivingEntity target, ParticleOptions type) {
        Vec3 start = new Vec3(player.getX(), player.getY() + (double) player.getEyeHeight() - 0.15, player.getZ());
        Vec3 end = new Vec3(target.getX(), target.getY() + (double) target.getEyeHeight() * 0.5, target.getZ());
        Vec3 delta = end.subtract(start);
        double len = delta.length();
        if (len < 1.0E-4) {
            return;
        }
        Vec3 step = delta.scale(1.0 / len);
        int count = Mth.clamp((int) (len / 0.6), 6, 60);
        for (int i = 0; i < count; ++i) {
            Vec3 p = start.add(step.scale((double) i * (len / (double) count)));
            ws.sendParticles(type, p.x, p.y, p.z, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private static final class GrappleData {
        final UUID playerId;
        final UUID targetId;
        final String dimension;
        Stage stage = Stage.WINDUP;
        int ticks = 0;
        int timer = 0;
        final List<UUID> affected = new ArrayList<>();

        GrappleData(UUID playerId, UUID targetId, String dimension) {
            this.playerId = playerId;
            this.targetId = targetId;
            this.dimension = dimension;
        }

        Player getPlayer() {
            var server = ServerLifecycleHooks.getCurrentServer();
            return server == null ? null : server.getPlayerList().getPlayer(this.playerId);
        }

        LivingEntity getTarget() {
            Player p = this.getPlayer();
            if (p == null || !(p.level() instanceof ServerLevel level)) {
                return null;
            }
            return level.getEntity(this.targetId) instanceof LivingEntity living ? living : null;
        }
    }

    private enum Stage {
        WINDUP, PULL, BOUNCE
    }
}
