package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Maul: right click slams the ground (200 tick cooldown). The sentient maul charges like a bow and dashes up to 16 blocks in the
 * look direction, slams where it stops (dash and pending slam are tracked in the player's persistent data), shift + right click
 * while a slam is pending forces the drop.
 */
public class WeaponMeleeMaul extends WeaponToolMeleeBase {
    private static final int MAUL_COOLDOWN_TICKS = 200;
    private static final int DEBUFF_TICKS = 200;
    private static final double SLAM_RADIUS = 4.0;
    private static final double KNOCKUP = 0.85;
    private static final double PUSH = 1.25;
    private static final String NBT_DASH = "srp_maul_dash";
    private static final String NBT_DASH_TICKS = "srp_maul_dash_t";
    private static final String NBT_DASH_DX = "srp_maul_dash_dx";
    private static final String NBT_DASH_DZ = "srp_maul_dash_dz";
    private static final String NBT_DASH_ITEM = "srp_maul_dash_item";
    private static final String NBT_DASH_DY = "srp_maul_dash_dy";
    private static final String NBT_DASH_TOTAL = "srp_maul_dash_total";
    private static final String NBT_DASH_ORIG = "srp_maul_dash_orig";
    private static final String NBT_SLAM_PENDING = "srp_maul_slam_pending";
    private static final String NBT_SLAM_PENDING_T = "srp_maul_slam_pending_t";
    private static final String NBT_SLAM_DELAY = "srp_maul_slam_delay";
    private static final String NBT_CHARGED_AOE = "srp_maul_charged_aoe";
    private static final double DASH_MAX_UP = 0.95;
    private static final double DASH_MAX_DOWN = -0.25;
    private static final int SLAM_WAIT_MAX_TICKS = 200;
    private static final int SENTIENT_DASH_TICKS = 4;
    private static final double SENTIENT_DASH_DIST = 16.0;
    private static final int SENTIENT_COOLDOWN_TICKS = 500;
    private static final int SENTIENT_MIN_CHARGE_TICKS = 6;
    private static boolean inChargedAoe;

    public WeaponMeleeMaul(Tier material, String name, double attackspeed, float range, float attackD, boolean fear, int id) {
        super(material, name, attackspeed, range, attackD, fear, id);
    }

    @Override
    public Item getNext() {
        return this == SRPItems.weapon_maul.get() ? SRPItems.weapon_maulSentient.get() : null;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag && !inChargedAoe && this.calling && attacker instanceof Player player && target.getHealth() <= 0.0f) {
            if (!player.getPersistentData().getBoolean(NBT_CHARGED_AOE)) {
                return flag;
            }
            int times = 5;
            inChargedAoe = true;
            try {
                for (int i = 0; i <= times; ++i) {
                    player.attack(target);
                }
            } finally {
                inChargedAoe = false;
            }
            if (target.getHealth() <= 0.0f) {
                addKills(stack, target.getMaxHealth());
            }
        }
        return flag;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        this.addWeaponLines(tooltip);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return this.calling ? UseAnim.BOW : UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.calling ? 72000 : 0;
    }

    private void doMaulSlam(Level world, Player player, ItemStack stack) {
        AABB bb = player.getBoundingBox().inflate(SLAM_RADIUS, 1.5, SLAM_RADIUS);
        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, bb);
        float base = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        for (LivingEntity e : list) {
            if (e == player || !e.isAlive() || e.isAlliedTo(player) || player.isAlliedTo(e) || e instanceof TamableAnimal tame && tame.getOwnerUUID() != null) continue;
            float slamDamage = base * 2.0f;
            e.hurt(player.damageSources().playerAttack(player), slamDamage);
            double dx = e.getX() - player.getX();
            double dz = e.getZ() - player.getZ();
            double len = Math.sqrt(dx * dx + dz * dz);
            if (len < 1.0E-4) {
                dx = 0.0;
                dz = 0.0;
                len = 1.0;
            }
            double nx = dx / len;
            double nz = dz / len;
            Mot.setY(e, Math.max(e.getDeltaMovement().y, KNOCKUP));
            Mot.addX(e, nx * PUSH);
            Mot.addZ(e, nz * PUSH);
            e.hurtMarked = true;
            e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DEBUFF_TICKS, 0));
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DEBUFF_TICKS, 0));
        }
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, player.getX(), player.getY() + 1.0, player.getZ(), 16, 1.4, 0.25, 1.4, 0.0);
        }
        if (!world.isClientSide) {
            player.getPersistentData().putBoolean(NBT_CHARGED_AOE, false);
        }
        this.spawnGroundShake(world, player);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SRPSounds.VENGEANCE_ROCK.get(), SoundSource.PLAYERS, 0.9f, 0.9f);
        stack.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
    }

    private void armPendingSlam(CompoundTag ptag, int maxTicks) {
        ptag.putBoolean(NBT_SLAM_PENDING, true);
        ptag.putInt(NBT_SLAM_PENDING_T, maxTicks);
        ptag.putInt(NBT_SLAM_DELAY, 1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.calling) {
            if (!world.isClientSide) {
                CompoundTag ptag = player.getPersistentData();
                if (player.isShiftKeyDown() && ptag.getBoolean(NBT_SLAM_PENDING) && !player.onGround()) {
                    this.forceDrop(player);
                    return InteractionResultHolder.success(stack);
                }
            }
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.pass(stack);
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(stack);
        }
        player.swing(hand);
        if (!world.isClientSide) {
            this.doMaulSlam(world, player, stack);
            player.getCooldowns().addCooldown(this, MAUL_COOLDOWN_TICKS);
        }
        return InteractionResultHolder.success(stack);
    }

    private void forceDrop(Player player) {
        Mot.setY(player, -3.5);
        player.hurtMarked = true;
        player.fallDistance = 0.0f;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (this.calling) {
            return InteractionResult.PASS;
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.PASS;
        }
        player.swing(hand);
        if (!player.level().isClientSide) {
            this.doMaulSlam(player.level(), player, stack);
            player.getCooldowns().addCooldown(this, MAUL_COOLDOWN_TICKS);
        }
        return InteractionResult.SUCCESS;
    }

    private void spawnGroundShake(Level world, Player player) {
        if (!(world instanceof ServerLevel ws)) {
            return;
        }
        int r = (int) Math.ceil(SLAM_RADIUS);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -r; dx <= r; ++dx) {
            for (int dz = -r; dz <= r; ++dz) {
                double dist2 = dx * dx + dz * dz;
                if (dist2 > SLAM_RADIUS * SLAM_RADIUS) continue;
                int x = (int) Math.floor(player.getX()) + dx;
                int z = (int) Math.floor(player.getZ()) + dz;
                int baseY = (int) Math.floor(player.getBoundingBox().minY);
                int groundY = -1;
                for (int dy = 2; dy >= -6; --dy) {
                    int y = baseY + dy;
                    pos.set(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if (state.isAir() || !state.getFluidState().isEmpty()) continue;
                    groundY = y;
                    break;
                }
                if (groundY < 0) continue;
                pos.set(x, groundY, z);
                BlockState ground = world.getBlockState(pos);
                if (ground.isAir()) continue;
                ws.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, ground), x + 0.5, groundY + 1.02, z + 0.5, 6, 0.35, 0.06, 0.35, 0.15);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entityLiving, int timeLeft) {
        if (!this.calling || !(entityLiving instanceof Player player)) {
            return;
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return;
        }
        int used = this.getUseDuration(stack, entityLiving) - timeLeft;
        if (used < SENTIENT_MIN_CHARGE_TICKS) {
            return;
        }
        float charge = this.getBowCharge(used);
        if (charge <= 0.0f) {
            return;
        }
        Vec3 look = player.getLookAngle();
        if (look.length() < 0.001) {
            return;
        }
        double y = look.y;
        if (y > DASH_MAX_UP) {
            y = DASH_MAX_UP;
        }
        if (y < DASH_MAX_DOWN) {
            y = DASH_MAX_DOWN;
        }
        Vec3 dir = new Vec3(look.x, y, look.z);
        if (dir.length() < 0.001) {
            return;
        }
        dir = dir.normalize();
        double totalDist = SENTIENT_DASH_DIST * (double) charge;
        int dashTicks = Math.max(3, (int) Math.ceil((float) SENTIENT_DASH_TICKS * charge));
        if (!world.isClientSide) {
            CompoundTag ptag = player.getPersistentData();
            ptag.putBoolean(NBT_DASH, true);
            ptag.putInt(NBT_DASH_TICKS, dashTicks);
            ptag.putInt(NBT_DASH_ORIG, dashTicks);
            ptag.putBoolean(NBT_CHARGED_AOE, true);
            ptag.putDouble(NBT_DASH_DX, dir.x);
            ptag.putDouble(NBT_DASH_DY, dir.y);
            ptag.putDouble(NBT_DASH_DZ, dir.z);
            ptag.putDouble(NBT_DASH_TOTAL, totalDist);
            ptag.putString(NBT_DASH_ITEM, BuiltInRegistries.ITEM.getKey(this).toString());
            ptag.putBoolean(NBT_SLAM_PENDING, false);
            ptag.putInt(NBT_SLAM_PENDING_T, 0);
            ptag.putInt(NBT_SLAM_DELAY, 0);
            player.getCooldowns().addCooldown(this, SENTIENT_COOLDOWN_TICKS);
        }
    }

    private float getBowCharge(int chargeTicks) {
        float f = (float) chargeTicks / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    /** First entity (not the player) whose slightly enlarged box the segment start-end crosses. */
    private EntityHitResult rayTraceEntities(Level world, Player player, Vec3 start, Vec3 end) {
        Entity closest = null;
        Vec3 closestHit = null;
        double closestDist = start.distanceToSqr(end);
        AABB box = player.getBoundingBox().expandTowards(end.x - start.x, end.y - start.y, end.z - start.z).inflate(1.0);
        for (Entity e : world.getEntities(player, box)) {
            if (!e.canBeCollidedWith()) continue;
            AABB eb = e.getBoundingBox().inflate(0.3);
            java.util.Optional<Vec3> r = eb.clip(start, end);
            if (r.isEmpty()) continue;
            double d = start.distanceToSqr(r.get());
            if (!(d < closestDist)) continue;
            closestDist = d;
            closest = e;
            closestHit = r.get();
        }
        return closest == null ? null : new EntityHitResult(closest, closestHit);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!this.calling || !(entity instanceof Player player)) {
            return;
        }
        CompoundTag ptag = player.getPersistentData();
        ItemStack held = player.getMainHandItem();
        ItemStack slamStack = !held.isEmpty() && held.getItem() == this ? held : stack;
        if (!world.isClientSide && ptag.getBoolean(NBT_SLAM_PENDING)) {
            int delay = ptag.getInt(NBT_SLAM_DELAY);
            if (delay > 0) {
                ptag.putInt(NBT_SLAM_DELAY, delay - 1);
                player.fallDistance = 0.0f;
                return;
            }
            int t = ptag.getInt(NBT_SLAM_PENDING_T);
            boolean landed = player.onGround() || player.verticalCollision && player.getDeltaMovement().y <= 0.0 || player.isInLava() || player.isInWater();
            if (landed) {
                this.doMaulSlam(world, player, slamStack);
                ptag.putBoolean(NBT_SLAM_PENDING, false);
                ptag.putInt(NBT_SLAM_PENDING_T, 0);
                ptag.putInt(NBT_SLAM_DELAY, 0);
            } else if (t <= 0) {
                ptag.putBoolean(NBT_SLAM_PENDING, false);
                ptag.putInt(NBT_SLAM_PENDING_T, 0);
                ptag.putInt(NBT_SLAM_DELAY, 0);
            } else {
                ptag.putInt(NBT_SLAM_PENDING_T, t - 1);
                player.fallDistance = 0.0f;
            }
        }
        if (!ptag.getBoolean(NBT_DASH)) {
            return;
        }
        ItemStack heldMain = player.getMainHandItem();
        if (!selected || heldMain.isEmpty() || heldMain.getItem() != this || !ptag.getString(NBT_DASH_ITEM).equals(BuiltInRegistries.ITEM.getKey(this).toString())) {
            ptag.putBoolean(NBT_DASH, false);
            if (!world.isClientSide) {
                this.armPendingSlam(ptag, SLAM_WAIT_MAX_TICKS);
            }
            return;
        }
        int ticksLeft = ptag.getInt(NBT_DASH_TICKS);
        int origTicks = ptag.getInt(NBT_DASH_ORIG);
        if (ticksLeft <= 0 || origTicks <= 0) {
            if (!world.isClientSide) {
                this.armPendingSlam(ptag, SLAM_WAIT_MAX_TICKS);
            }
            ptag.putBoolean(NBT_DASH, false);
            return;
        }
        double dx = ptag.getDouble(NBT_DASH_DX);
        double dy = ptag.getDouble(NBT_DASH_DY);
        double dz = ptag.getDouble(NBT_DASH_DZ);
        double totalDist = ptag.getDouble(NBT_DASH_TOTAL);
        double step = totalDist / (double) origTicks;
        player.fallDistance = 0.0f;
        if (!world.isClientSide) {
            Vec3 start = new Vec3(player.getX(), player.getY() + (double) player.getEyeHeight(), player.getZ());
            Vec3 end = start.add(dx * step, dy * step, dz * step);
            EntityHitResult hitE = this.rayTraceEntities(world, player, start, end);
            if (hitE != null && hitE.getEntity() instanceof LivingEntity t) {
                this.doMaulSlam(world, player, slamStack);
                float base = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                t.hurt(player.damageSources().playerAttack(player), base * 2.0f);
                ptag.putBoolean(NBT_DASH, false);
                return;
            }
            BlockHitResult hitB = world.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
            if (hitB.getType() == HitResult.Type.BLOCK) {
                this.armPendingSlam(ptag, SLAM_WAIT_MAX_TICKS);
                ptag.putBoolean(NBT_DASH, false);
                return;
            }
            Mot.setX(player, dx * step);
            Mot.setZ(player, dz * step);
            Mot.setY(player, dy * step);
            if (player.getDeltaMovement().y < -1.25) {
                Mot.setY(player, -1.25);
            }
            if (player.getDeltaMovement().y > 1.25) {
                Mot.setY(player, 1.25);
            }
            player.hurtMarked = true;
            ptag.putInt(NBT_DASH_TICKS, ticksLeft - 1);
        }
    }
}
