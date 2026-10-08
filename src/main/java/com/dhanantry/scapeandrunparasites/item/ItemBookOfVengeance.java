package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.List;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Book of vengeance: right click pulls the player to the living thing under the crosshair (32 blocks, line of sight; see
 * {@link VengeanceGrappleHandler}); shift + right click is a knockback burst around the player (80 tick cooldown kept in the stack).
 */
public class ItemBookOfVengeance extends Item {
    private static final int RANGE = 32;
    private static final int COOLDOWN_TICKS = 60;
    private static final int KB_COOLDOWN_TICKS = 80;
    private static final String NBT_KB_NEXT = "srp_kb_next";
    private static final double KB_RADIUS = 5.0;
    private static final double KB_STRENGTH = 2.2;
    private static final double KB_UP = 0.4;
    private static final double KB_SELF_JUMP_Y = 0.42;

    public ItemBookOfVengeance() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String base = "item.srparasites.book_of_vengeance.";
        tooltip.add(Component.translatable(base + "vengeance0"));
        tooltip.add(Component.translatable(base + "vengeance1"));
        tooltip.add(Component.translatable(base + "vengeance3"));
        tooltip.add(Component.translatable(base + "vengeance5"));
        tooltip.add(Component.translatable(base + "vengeance6"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide) {
                if (!(world instanceof ServerLevel ws)) {
                    return InteractionResultHolder.fail(stack);
                }
                if (isKnockbackOnCooldown(stack, ws.getGameTime())) {
                    return InteractionResultHolder.fail(stack);
                }
                doKnockbackBurst(ws, player);
                setKnockbackCooldown(stack, ws.getGameTime() + KB_COOLDOWN_TICKS);
                player.swing(hand);
            }
            return InteractionResultHolder.success(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!world.isClientSide) {
            LivingEntity target = findLookTargetLiving(player, RANGE);
            if (target == null || !player.hasLineOfSight(target)) {
                return InteractionResultHolder.fail(stack);
            }
            VengeanceGrappleHandler.start(player, target);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            player.swing(hand);
        }
        return InteractionResultHolder.success(stack);
    }

    private static void doKnockbackBurst(ServerLevel ws, Player player) {
        ws.playSound(null, player.getX(), player.getY(), player.getZ(), SRPSounds.VENGEANCE_CHAIN_IMPACT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        ws.playSound(null, player.getX(), player.getY(), player.getZ(), SRPSounds.VENGEANCE_IMPACT.get(), SoundSource.PLAYERS, 1.0f, 1.0f);
        spawnPinkBurst(ws, new Vec3(player.getX(), player.getY() + 0.9, player.getZ()), 26);
        AABB box = player.getBoundingBox().expandTowards(KB_RADIUS, 1.5, KB_RADIUS);
        for (LivingEntity ent : ws.getEntitiesOfClass(LivingEntity.class, box)) {
            if (ent == null || ent.isRemoved() || ent == player) continue;
            double dx = ent.getX() - player.getX();
            double dz = ent.getZ() - player.getZ();
            double distSq = dx * dx + dz * dz;
            if (distSq < 1.0E-4) {
                dx = (ws.random.nextDouble() - 0.5) * 0.2;
                dz = (ws.random.nextDouble() - 0.5) * 0.2;
                distSq = dx * dx + dz * dz;
            }
            double dist = Math.sqrt(distSq);
            double nx = dx / dist;
            double nz = dz / dist;
            double falloff = 1.0 - Math.min(1.0, dist / KB_RADIUS);
            double push = KB_STRENGTH * (0.45 + 0.55 * falloff);
            Mot.addX(ent, nx * push);
            Mot.addZ(ent, nz * push);
            Mot.setY(ent, Math.max(ent.getDeltaMovement().y, KB_UP + 0.15 * falloff));
            ent.hurtMarked = true;
        }
        Mot.setY(player, Math.max(player.getDeltaMovement().y, KB_SELF_JUMP_Y));
        player.hurtMarked = true;
        player.setOnGround(false);
        player.fallDistance = 0.0f;
        ws.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.6f, 1.6f);
        ws.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY() + 0.1, player.getZ(), 8, 0.25, 0.05, 0.25, 0.02);
    }

    private static void spawnPinkBurst(ServerLevel ws, Vec3 center, int points) {
        DustParticleOptions pink = new DustParticleOptions(new org.joml.Vector3f(1.0f, 0.25f, 0.85f), 1.0f);
        for (int i = 0; i < points; ++i) {
            double ox = (ws.random.nextDouble() - 0.5) * 1.1;
            double oy = (ws.random.nextDouble() - 0.5) * 0.6;
            double oz = (ws.random.nextDouble() - 0.5) * 1.1;
            ws.sendParticles(pink, center.x + ox, center.y + oy, center.z + oz, 1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private static boolean isKnockbackOnCooldown(ItemStack stack, long worldTime) {
        return ReportData.read(stack).getLong(NBT_KB_NEXT) > worldTime;
    }

    private static void setKnockbackCooldown(ItemStack stack, long nextUseTick) {
        ReportData.update(stack, (CompoundTag tag) -> tag.putLong(NBT_KB_NEXT, nextUseTick));
    }

    private static LivingEntity findLookTargetLiving(Player player, double range) {
        Level world = player.level();
        Vec3 eye = player.getEyePosition(1.0f);
        Vec3 look = player.getViewVector(1.0f);
        Vec3 end = eye.add(look.x * range, look.y * range, look.z * range);
        BlockHitResult blockHit = world.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        if (blockHit.getType() == HitResult.Type.BLOCK) {
            end = blockHit.getLocation();
        }
        AABB searchBox = player.getBoundingBox().expandTowards(look.x * range, look.y * range, look.z * range).inflate(1.0);
        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : world.getEntities(player, searchBox)) {
            if (!(e instanceof LivingEntity living) || !e.canBeCollidedWith() || living == player || living.isRemoved()) continue;
            java.util.Optional<Vec3> hit = living.getBoundingBox().inflate(0.3).clip(eye, end);
            if (hit.isEmpty()) continue;
            double dist = eye.distanceTo(hit.get());
            if (dist < bestDist) {
                bestDist = dist;
                best = living;
            }
        }
        if (best != null && !player.hasLineOfSight(best)) {
            return null;
        }
        return best;
    }
}
