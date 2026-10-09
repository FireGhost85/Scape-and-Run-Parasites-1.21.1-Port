package com.dhanantry.scapeandrunparasites.util;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

/** Small 1.12 helper equivalents used by the translated entity code. */
public final class SRPEntityUtil {
    private SRPEntityUtil() {}

    /** {@code world.loadedEntityList}: a snapshot of all loaded entities (server only; empty on the client). */
    public static List<Entity> allEntities(Level level) {
        List<Entity> out = new ArrayList<>();
        if (level instanceof ServerLevel server) {
            for (Entity e : server.getAllEntities()) {
                out.add(e);
            }
        }
        return out;
    }

    /** {@code entity.getCustomNameTag()}: empty string if the entity has no custom name. */
    public static String getCustomNameTag(Entity entity) {
        Component c = entity.getCustomName();
        return c == null ? "" : c.getString();
    }

    public static void setCustomNameTag(Entity entity, @Nullable String name) {
        entity.setCustomName(name == null || name.isEmpty() ? null : Component.literal(name));
    }

    /** {@code EntityList.getKey(entity)} as a string ("modid:name"). */
    public static String entityId(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    @Nullable
    public static EntityType<?> entityType(String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        return rl == null ? null : BuiltInRegistries.ENTITY_TYPE.getOptional(rl).orElse(null);
    }

    /** {@code EntityList.createEntityByIDFromName}: a new entity of the registered type, or null if unknown. */
    @Nullable
    public static Entity create(ResourceLocation id, Level level) {
        return BuiltInRegistries.ENTITY_TYPE.getOptional(id).map(type -> type.create(level)).orElse(null);
    }

    /** {@code Potion.getPotionFromResourceLocation}: the mob effect with that registry name, or null. */
    @Nullable
    public static Holder<MobEffect> effect(String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id.contains(":") ? id : "minecraft:" + id);
        return rl == null ? null : BuiltInRegistries.MOB_EFFECT.getHolder(rl).map(h -> (Holder<MobEffect>) h).orElse(null);
    }

    /** {@code world.addWeatherEffect(new EntityLightningBolt(world, x, y, z, effectOnly))}. */
    public static void lightning(Level level, double x, double y, double z, boolean visualOnly) {
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(x, y, z);
            bolt.setVisualOnly(visualOnly);
            level.addFreshEntity(bolt);
        }
    }

    /** Posts the MobDespawnEvent and returns its result (DEFAULT, ALLOW or DENY) without acting on it. */
    public static net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result despawnResult(net.minecraft.world.entity.Mob mob) {
        if (!(mob.level() instanceof ServerLevel server)) {
            return net.neoforged.neoforge.event.entity.living.MobDespawnEvent.Result.DEFAULT;
        }
        net.neoforged.neoforge.event.entity.living.MobDespawnEvent event = new net.neoforged.neoforge.event.entity.living.MobDespawnEvent(mob, server);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(event);
        return event.getResult();
    }

    /** {@code SRPEntityUtil.lightBrightness(World, pos)} of the overworld: light level 0..15 through the 1.12 brightness table. */
    public static float lightBrightness(Level level, net.minecraft.core.BlockPos pos) {
        int light = level.getMaxLocalRawBrightness(pos);
        float f1 = 1.0f - (float) light / 15.0f;
        return (1.0f - f1) / (f1 * 3.0f + 1.0f);
    }

    /** {@code SRPEntityUtil.rayTraceBlocks(world, from, to)}: the first solid block hit by the segment, or null. */
    @Nullable
    public static net.minecraft.world.phys.BlockHitResult rayTraceBlocks(Level level, net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to) {
        net.minecraft.world.phys.BlockHitResult hit = level.clip(new net.minecraft.world.level.ClipContext(from, to,
                net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty()));
        return hit.getType() == net.minecraft.world.phys.HitResult.Type.MISS ? null : hit;
    }

    /** Blocks of the 1.12 material "circuits" (torches, redstone parts, levers, buttons, plates, tripwire). */
    public static boolean isCircuits(net.minecraft.world.level.block.state.BlockState state) {
        net.minecraft.world.level.block.Block b = state.getBlock();
        return b instanceof net.minecraft.world.level.block.BaseTorchBlock || b instanceof net.minecraft.world.level.block.ButtonBlock
                || b instanceof net.minecraft.world.level.block.BasePressurePlateBlock || b instanceof net.minecraft.world.level.block.LeverBlock
                || b instanceof net.minecraft.world.level.block.RedStoneWireBlock || b instanceof net.minecraft.world.level.block.DiodeBlock
                || b instanceof net.minecraft.world.level.block.TripWireBlock || b instanceof net.minecraft.world.level.block.TripWireHookBlock
                || b instanceof net.minecraft.world.level.block.SkullBlock || b instanceof net.minecraft.world.level.block.WallSkullBlock;
    }

    /** {@code EntityAITarget.isSuitableTarget(attacker, target, includeInvincibles, checkSight)} of 1.12. */
    public static boolean isSuitableTarget(net.minecraft.world.entity.Mob attacker, @Nullable net.minecraft.world.entity.LivingEntity target, boolean includeInvincibles, boolean checkSight) {
        if (target == null || target == attacker || !target.isAlive()) {
            return false;
        }
        if (!attacker.canAttackType(target.getType())) {
            return false;
        }
        if (attacker.isAlliedTo(target)) {
            return false;
        }
        if (attacker instanceof net.minecraft.world.entity.OwnableEntity owned && owned.getOwnerUUID() != null) {
            if (target instanceof net.minecraft.world.entity.OwnableEntity other && owned.getOwnerUUID().equals(other.getOwnerUUID())) {
                return false;
            }
            if (target == owned.getOwner()) {
                return false;
            }
        }
        if (target instanceof net.minecraft.world.entity.player.Player player && !includeInvincibles && player.getAbilities().invulnerable) {
            return false;
        }
        return !checkSight || attacker.getSensing().hasLineOfSight(target);
    }

    /** {@code RayTraceResult.entityHit}: the entity of an entity hit result, otherwise null. */
    @Nullable
    public static Entity hitEntity(net.minecraft.world.phys.HitResult result) {
        return result instanceof net.minecraft.world.phys.EntityHitResult hit ? hit.getEntity() : null;
    }
}
