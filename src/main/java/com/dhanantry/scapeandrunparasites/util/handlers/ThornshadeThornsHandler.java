package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.world.SRPExplosion;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

/**
 * Thornshade thorns effect: limits how often it can be applied (two uses, a cooldown, no parasites, no big mobs), reflects part
 * of the damage and makes a victim that reached the limit explode, infecting the ones around.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class ThornshadeThornsHandler {
    private static final String TAG_ROOT = "srp_thornshade_thorns";
    private static final String TAG_USES = "Uses";
    private static final String TAG_COOLDOWN_UNTIL = "CooldownUntil";
    private static final String TAG_EXPLODE_DELAY = "ExplodeDelay";
    private static final float MAX_HP_ALLOWED = 120.0f;
    private static final String TAG_HAS_EXPLODED = "HasExplodedOnce";

    private static boolean isThorns(MobEffectInstance effect) {
        return effect.getEffect().value() == SRPPotions.THORNSHADE_THORNS_E.get();
    }

    @SubscribeEvent
    public static void onPotionApplicable(MobEffectEvent.Applicable event) {
        LivingEntity living = event.getEntity();
        if (living.level().isClientSide) {
            return;
        }
        MobEffectInstance incoming = event.getEffectInstance();
        if (!isThorns(incoming)) {
            return;
        }
        if (living instanceof EntityParasiteBase) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        if (living.getMaxHealth() > MAX_HP_ALLOWED) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        if (living.hasEffect(SRPPotions.THORNSHADE_THORNS_E)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        if (isInfiniteDuration(incoming)) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        Level world = living.level();
        long now = world.getGameTime();
        CompoundTag data = getThornshadeData(living);
        int uses = data.getInt(TAG_USES);
        if (uses >= 2) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            if (!data.contains(TAG_EXPLODE_DELAY)) {
                scheduleExplosion(living, data);
            }
            setThornshadeData(living, data);
            return;
        }
        long cooldownUntil = data.getLong(TAG_COOLDOWN_UNTIL);
        if (cooldownUntil > now) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            return;
        }
        data.putInt(TAG_USES, ++uses);
        int durationTicks = incoming.getDuration();
        long extraCooldownTicks = (long)durationTicks / 2L;
        data.putLong(TAG_COOLDOWN_UNTIL, now + extraCooldownTicks);
        setThornshadeData(living, data);
        event.setResult(MobEffectEvent.Applicable.Result.DEFAULT);
    }

    @SubscribeEvent
    public static void aiStep(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living) || living.level().isClientSide) {
            return;
        }
        if (!living.getPersistentData().contains(TAG_ROOT)) {
            return;
        }
        CompoundTag data = getThornshadeData(living);
        if (!data.contains(TAG_EXPLODE_DELAY)) {
            return;
        }
        int delay = data.getInt(TAG_EXPLODE_DELAY);
        if (delay > 0) {
            spawnBloodParticles(living.level(), living, 15);
            data.putInt(TAG_EXPLODE_DELAY, --delay);
            setThornshadeData(living, data);
            return;
        }
        data.remove(TAG_EXPLODE_DELAY);
        setThornshadeData(living, data);
        doExplosion(living);
    }

    private static void spawnBloodParticles(Level world, LivingEntity entity, int count) {
        if (!(world instanceof ServerLevel ws)) {
            return;
        }
        double x = entity.getX();
        double y = entity.getY() + (double)entity.getBbHeight() * 0.5;
        double z = entity.getZ();
        BlockState redDustId = Blocks.REDSTONE_BLOCK.defaultBlockState();
        for (int i = 0; i < count; ++i) {
            double offsetX = (world.random.nextDouble() - 0.5) * 0.6;
            double offsetY = world.random.nextDouble() * 0.8;
            double offsetZ = (world.random.nextDouble() - 0.5) * 0.6;
            double motionX = (world.random.nextDouble() - 0.5) * 0.3;
            double motionY = world.random.nextDouble() * 0.4 + 0.1;
            double motionZ = (world.random.nextDouble() - 0.5) * 0.3;
            ws.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, redDustId), x + offsetX, y + offsetY, z + offsetZ, 0, motionX, motionY, motionZ, 0.0);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) {
            return;
        }
        MobEffectInstance eff = target.getEffect(SRPPotions.THORNSHADE_THORNS_E);
        if (eff == null || isInfiniteDuration(eff)) {
            return;
        }
        Entity trueSourceEntity = event.getSource().getEntity();
        if (!(trueSourceEntity instanceof LivingEntity attacker)) {
            return;
        }
        float incoming = event.getNewDamage();
        if (incoming <= 0.0f) {
            return;
        }
        CompoundTag data = getThornshadeData(target);
        int uses = data.getInt(TAG_USES);
        float reflectFactor = uses <= 1 ? 0.25f : 0.5f;
        float reflected = incoming * reflectFactor;
        if (reflected <= 0.0f) {
            return;
        }
        attacker.hurt(target.damageSources().thorns(target), reflected);
    }

    private static void scheduleExplosion(LivingEntity living, CompoundTag data) {
        if (data.getBoolean(TAG_HAS_EXPLODED)) {
            return;
        }
        data.putInt(TAG_EXPLODE_DELAY, 20);
        Level world = living.level();
        world.playSound(null, living.getX(), living.getY(), living.getZ(), SRPSounds.ADAPTATION_P.get(), SoundSource.PLAYERS, 1.5f, 0.8f + world.random.nextFloat() * 0.4f);
    }

    private static void doExplosion(LivingEntity center) {
        Level world = center.level();
        if (world.isClientSide) {
            return;
        }
        CompoundTag centerData = getThornshadeData(center);
        centerData.putBoolean(TAG_HAS_EXPLODED, true);
        if (center instanceof ServerPlayer player) {
            AdvancementHolder adv = player.getServer().getAdvancements().get(ResourceLocation.fromNamespaceAndPath("srparasites", "thornshade_self_destruct"));
            if (adv != null) {
                player.getAdvancements().award(adv, "exploded");
            }
        }
        setThornshadeData(center, centerData);
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        float innerRadius = 3.0f;
        float outerRadius = 10.0f;
        AABB outerBox = new AABB(x - outerRadius, y - outerRadius, z - outerRadius, x + outerRadius, y + outerRadius, z + outerRadius);
        new SRPExplosion(world, null, x, y, z, 3.0f, false, false).doExplosionA();
        world.playSound(null, x, y, z, SRPSounds.BUTHOL_BOOM.get(), SoundSource.PLAYERS, 2.0f, 1.0f);
        spawnRadialParticles(world, x, y + (double)center.getBbHeight() * 0.5, z, innerRadius, 50, ParticleTypes.CLOUD);
        spawnRadialParticles(world, x, y + (double)center.getBbHeight() * 0.5, z, outerRadius, 120, ParticleTypes.WITCH);
        center.hurt(world.damageSources().magic(), Float.MAX_VALUE);
        AABB innerBox = new AABB(x - innerRadius, y - innerRadius, z - innerRadius, x + innerRadius, y + innerRadius, z + innerRadius);
        for (LivingEntity other : world.getEntitiesOfClass(LivingEntity.class, innerBox)) {
            double distSq;
            CompoundTag data;
            if (other == center || !other.isAlive() || other instanceof EntityParasiteBase || (distSq = other.distanceToSqr(x, y, z)) > (double)(innerRadius * innerRadius) || !other.hasEffect(SRPPotions.THORNSHADE_THORNS_E) || other.getMaxHealth() > MAX_HP_ALLOWED || (data = getThornshadeData(other)).getBoolean(TAG_HAS_EXPLODED) || data.contains(TAG_EXPLODE_DELAY)) continue;
            data.putInt(TAG_USES, Math.max(2, data.getInt(TAG_USES)));
            scheduleExplosion(other, data);
            setThornshadeData(other, data);
        }
        for (LivingEntity other : world.getEntitiesOfClass(LivingEntity.class, outerBox)) {
            double distSq;
            if (other == center || !other.isAlive() || other instanceof EntityParasiteBase || (distSq = other.distanceToSqr(x, y, z)) <= (double)(innerRadius * innerRadius) || other.getMaxHealth() > MAX_HP_ALLOWED || other.hasEffect(SRPPotions.THORNSHADE_THORNS_E)) continue;
            other.addEffect(new MobEffectInstance(SRPPotions.THORNSHADE_THORNS_E, 600, 0, false, true));
            spawnRadialParticles(world, other.getX(), other.getY() + (double)other.getBbHeight() * 0.5, other.getZ(), 1.0f, 20, ParticleTypes.WITCH);
        }
    }

    private static void spawnRadialParticles(Level world, double x, double y, double z, float radius, int count, ParticleOptions type) {
        if (!(world instanceof ServerLevel ws)) {
            return;
        }
        BlockState bloodStateId = Blocks.REDSTONE_BLOCK.defaultBlockState();
        for (int i = 0; i < count; ++i) {
            double angle = world.random.nextDouble() * 2.0 * Math.PI;
            double ringRadius = (double)radius * (0.7 + world.random.nextDouble() * 0.3);
            double px = x + ringRadius * Math.cos(angle);
            double pz = z + ringRadius * Math.sin(angle);
            double py = y + (world.random.nextDouble() - 0.5) * ((double)radius * 0.2);
            double dirX = px - x;
            double dirZ = pz - z;
            double len = Math.sqrt(dirX * dirX + dirZ * dirZ);
            if (len == 0.0) {
                dirX = 1.0;
                dirZ = 0.0;
                len = 1.0;
            }
            dirX /= len;
            dirZ /= len;
            double baseOut = 0.4 + world.random.nextDouble() * 0.5;
            double mistVy = 0.05 + world.random.nextDouble() * 0.15;
            if (type == ParticleTypes.WITCH) {
                ws.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, bloodStateId), px, py, pz, 0, dirX * baseOut * 0.6, mistVy, dirZ * baseOut * 0.6, 0.0);
            } else {
                ws.sendParticles(type, px, py, pz, 1, dirX * baseOut * 0.3, mistVy, dirZ * baseOut * 0.3, 0.1);
            }
            double chunkScale = 0.6 + world.random.nextDouble() * 0.8;
            double gx = dirX * chunkScale;
            double gz = dirZ * chunkScale;
            double gy = 0.25 + world.random.nextDouble() * 0.6;
            ws.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, bloodStateId), px, py, pz, 0, gx, gy, gz, 0.0);
        }
    }

    private static CompoundTag getThornshadeData(LivingEntity entity) {
        CompoundTag root = entity.getPersistentData();
        if (!root.contains(TAG_ROOT, 10)) {
            CompoundTag data = new CompoundTag();
            root.put(TAG_ROOT, data);
            return data;
        }
        return root.getCompound(TAG_ROOT);
    }

    private static void setThornshadeData(LivingEntity entity, CompoundTag data) {
        entity.getPersistentData().put(TAG_ROOT, data);
    }

    private static boolean isInfiniteDuration(MobEffectInstance effect) {
        return effect.isInfiniteDuration() || effect.getDuration() >= 72000 || effect.getDuration() == Integer.MAX_VALUE;
    }
}
